package mayday.graphviewer.plugins.shrink;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mayday.core.Probe;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.components.NodeComponent;
import mayday.vis3.graph.model.GraphModel;

public class UnifyLabel extends AbstractGraphViewerPlugin
{
	public void run(final GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		if(components.isEmpty())
		{
			return;
		}
		MultiHashMap<String,CanvasComponent> labelMap=new MultiHashMap<String, CanvasComponent>();
		for(CanvasComponent cc: components)
		{
			labelMap.put(cc.getLabel(), cc);
		}
		
		Graph g=model.getGraph();
		for(String s: labelMap.keySet())
		{
			List<CanvasComponent> comps=labelMap.get(s);
			if(comps.size() > 1)
			{
				CanvasComponent core=comps.get(0);
				Set<Probe> coreP=new HashSet<Probe>(((MultiProbeComponent) core).getProbes());
				for(int i=1; i!= comps.size(); ++i)
				{
					CanvasComponent cc=comps.get(i);
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
				
			}
		}		
		canvas.revalidateEdges();		
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.UnifyLabels",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Combine all nodes that have the same label",
				"Unify Labels"				
		);
		pli.addCategory(SHRINK_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/unifynames.png");
		return pli;	

	}
}
