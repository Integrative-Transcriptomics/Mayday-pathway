package mayday.graphviewer.linlog;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.core.bag.ComponentBag;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class ModularityClustering extends AbstractGraphViewerPlugin
{
	public void run(final GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		SuperModel sm=(SuperModel)model;
		Graph g=model.getGraph();
		Map<Node,String> names=LinLogWrapper.buildNames(g.getNodes());
		Map<String,Map<String,Double>> graph=LinLogWrapper.buildLLGraph(g, names);
		
		// LinLogLayout way of building the layout:
		Map<String, Integer> clust=LinLogWrapper.cluster(graph);
		int maxCluster=new HashSet<Integer>(clust.values()).size();
		
		Map<Integer, ComponentBag> bags=new HashMap<Integer, ComponentBag>();
		for(Integer i: clust.values())
		{
			float hue = i / (float)(maxCluster+1);  
			Color c=Color.getHSBColor(hue, 1.0f, 1.0f);
			ComponentBag bag=new ComponentBag(sm);
			bag.setColor(c);
			bag.setName("Cluster" +i);
			bags.put(i, bag);			
		}
		
		ComponentBag unclustered=new ComponentBag(sm);
		unclustered.setColor(Color.lightGray);
		unclustered.setName("Unclustered");
		
	
		for(Node n: g.getNodes())
		{
			if(clust.containsKey(names.get(n)))
			{
				int cl=clust.get(names.get(n));				
				bags.get(cl).addComponent(model.getComponent(n));				
			}else
			{
				unclustered.addComponent(model.getComponent(n)); // add node to unclustered bag; 
			}
			
		}
		
		for(ComponentBag bag: bags.values())
			sm.addBag(bag);	
		sm.addBag(unclustered);
		
		canvas.updatePlotNow();
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Group.Modularity",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Annotate all edges with a distance measure",
				"Modularity Clustering"				
		);
		pli.setIcon("mayday/pathway/gvicons/modularity.png");
		pli.addCategory(GROUP_CATEGORY+"/"+MaydayDefaults.Plugins.CATEGORY_CLUSTERING);
		return pli;	
	}
}
