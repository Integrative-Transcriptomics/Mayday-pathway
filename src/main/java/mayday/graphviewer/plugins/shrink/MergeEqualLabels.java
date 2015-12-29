package mayday.graphviewer.plugins.shrink;

import java.util.HashMap;
import java.util.List;

import mayday.core.Probe;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class MergeEqualLabels extends AbstractGraphViewerPlugin
{



	public void run(final GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		MultiHashMap<String, CanvasComponent> comps=new MultiHashMap<String, CanvasComponent>();
		for(CanvasComponent cc: components)
		{
			comps.put(cc.getLabel(),cc);
		}
		
		SuperModel sm=(SuperModel)model;
		Graph g=sm.getGraph();
		
		for(String s: comps.keySet())
		{
			if(comps.get(s).size() > 1)
			{
				CanvasComponent comp1=comps.get(s).get(0);
				MultiProbeNode node=(MultiProbeNode)sm.getNode(comp1);
				for(CanvasComponent cc: comps.get(s))
				{
					if(cc==comp1)
						continue;
					
					Node n=model.getNode(cc);
					for(Edge e: g.getInEdges(n))
					{
						e.setTarget(node);
					}
					for(Edge e: g.getOutEdges(n))
					{
						e.setSource(node);
					}
					if(n instanceof DefaultNode)
					{
						node.getProperties().putAll(((DefaultNode) n).getProperties());
					}
					if(n instanceof MultiProbeNode)
					{
						for(Probe p:((MultiProbeNode) n).getProbes())
							node.addProbe(p);
					}					
					cc.setVisible(false);
				}
			}
		}

	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.MergeEqual",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Combine Nodes with the same label",
				"Merge Nodes with identical label"				
		);
		pli.setIcon("mayday/pathway/gvicons/mergeidentical.png");
		pli.addCategory(SHRINK_CATEGORY);
		return pli;	

	}
	
}
