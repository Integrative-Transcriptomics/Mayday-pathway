package mayday.graphviewer.plugins.connect;

import java.util.HashMap;
import java.util.List;

import mayday.core.Probe;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Edge;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.gui.ProbeDistanceDialog;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.GraphModel;

public class ConnectSimilar  extends AbstractGraphViewerPlugin
{
	public void run(final GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		if(components.isEmpty())
		{
			return;
		}
		
		List<Probe> probes=collectProbes(components);
		
		ProbeDistanceDialog pdd=new ProbeDistanceDialog(probes);
		pdd.setTitle("Connect Nodes with similar Probes");
		pdd.setVisible(true);
		if(pdd.isCancelled())
			return;
		DistanceMeasurePlugin distance=pdd.getDistanceMeasure();
		boolean isLimited=pdd.isLimitNeighbors();
		int limit=pdd.getNumberOfNeighbors();
		double tol=pdd.getTolerance();
		
		MultiHashMap<Probe, CanvasComponent> probeMap=new MultiHashMap<Probe, CanvasComponent>();
		for(CanvasComponent comp:components)
		{
			if(comp instanceof MultiProbeComponent)
			{
				for(Probe p: ((MultiProbeComponent) comp).getProbes())
				{
					probeMap.put(p, comp);
				}
			}			
		}
		model.setSilent(true);
		for(CanvasComponent cc: components)
		{
			if(cc instanceof MultiProbeComponent)
			{
				int c=0;
				for(Probe p: ((MultiProbeComponent) cc).getProbes())
				{
					for(Probe q: probeMap.keySet())
					{
						if(p==q) continue;
						double d=distance.getDistance(p.getValues(), q.getValues());
						if(d < tol)
						{
							for(CanvasComponent tc:probeMap.get(q))
							{
								Edge e= model.connect(cc, tc);
								e.setWeight(d);
							}	
							c++;
						}						
					}
					if( c > limit && isLimited)
						break;
				}
			}			
		}
		model.setSilent(false);
		canvas.revalidateEdges();		
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.ConnectSimilar",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Connect all nodes that are similar with respect to some distance measure",
				"Connect Similar"				
		);
		pli.addCategory(CONNECT_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/connect_similar.png");
		return pli;	

	}
}