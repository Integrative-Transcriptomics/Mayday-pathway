package mayday.graphviewer.plugins.shrink;


import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import mayday.core.Probe;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.gui.ProbeDistanceDialog;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.components.NodeComponent;
import mayday.vis3.graph.model.GraphModel;

public class MergeProbesWithSimilar extends AbstractGraphViewerPlugin
{
	public void run(final GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		if(components.isEmpty())
		{
			return;
		}

		if(components.size()!=1)
		{
			JOptionPane.showMessageDialog(canvas, "Please select excactly one component", "Merge similar", JOptionPane.ERROR_MESSAGE);
			return;
		}

		List<Probe> probes=collectProbes(components);

		ProbeDistanceDialog pdd=new ProbeDistanceDialog(probes);
		pdd.setVisible(true);
		if(pdd.isCancelled())
			return;
		DistanceMeasurePlugin distance=pdd.getDistanceMeasure();
		double tol=pdd.getTolerance();

		MultiProbeComponent core=(MultiProbeComponent)components.get(0);
		Set<CanvasComponent> targets=new HashSet<CanvasComponent>();	
		
		for(Probe p: core.getProbes())
		{
			for(CanvasComponent cc:model.getComponents())
			{
				if(cc instanceof MultiProbeComponent)
				{
					if( ((MultiProbeComponent) cc).getProbes().size() == 0 ||
						((MultiProbeComponent) cc).getProbes().get(0).getMasterTable().getDataSet()!=p.getMasterTable().getDataSet() )	
							continue;
						
					for(Probe q:((MultiProbeComponent) cc).getProbes())
					{
						if(p==q) continue;
						
						double d=distance.getDistance(p.getValues(), q.getValues());
						if(d < tol)
						{
							targets.add(cc);
							break;
						}
					}												
				}
			}			
		}
		if(targets.isEmpty())
			return;
		Graph g=model.getGraph();
		Set<Probe> coreP=new HashSet<Probe>(((MultiProbeComponent) core).getProbes());
		for(CanvasComponent cc:targets)
		{
			Node n=((NodeComponent) cc).getNode();
			for(Node nn: g.getOutNeighbors(n))
			{
				Edge template=g.getEdge(n, nn);						
				model.connect(core, model.getComponent(nn),template);
			}
			for(Node nn: g.getInNeighbors(n))
			{
				Edge template=g.getEdge(nn, n);	
				model.connect(model.getComponent(nn),core,template);
			}
			// steal additional probes
			coreP.addAll(((MultiProbeComponent) cc).getProbes());					
			// remove comp.
			model.remove(cc);					
		}
		((MultiProbeComponent) core).setProbes(coreP);
		canvas.revalidateEdges();		
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.MergeProbesWithSimilar",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Combine the selected component with all similar components wrt a distance measure.",
				"Merge with similar components"				
		);
		pli.addCategory(SHRINK_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/mergesimilar.png");
		return pli;	

	}
}
