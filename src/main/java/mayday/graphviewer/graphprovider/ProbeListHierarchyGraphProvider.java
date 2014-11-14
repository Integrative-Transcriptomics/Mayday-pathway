package mayday.graphviewer.graphprovider;

import java.util.HashMap;
import java.util.Map;

import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.maps.MultiHashMap;
import mayday.vis3.graph.layout.CanvasLayouter;
import mayday.vis3.graph.layout.SugiyamaLayout;

public class ProbeListHierarchyGraphProvider extends AbstractPlugin implements GraphProvider {

	@Override
	public Graph createGraph(MultiHashMap<DataSet, ProbeList> probeLists) 
	{
		Graph graph=new Graph();
		Map<ProbeList, Node> plMap=new HashMap<ProbeList, Node>();
		
		for(DataSet ds: probeLists.keySet())
		{
			for(ProbeList pl:probeLists.get(ds))
			{
				MultiProbeNode n=new MultiProbeNode(graph,pl);
				graph.addNode(n);
				plMap.put(pl, n);
			}
		}
		
		for(ProbeList pl:plMap.keySet())
		{
			ProbeList parent=pl.getParent();
			if(parent!=null && plMap.containsKey(parent))
			{
				graph.connect(plMap.get(parent),plMap.get(pl));
			}
		}
		
		return graph;
	}

	@Override
	public CanvasLayouter defaultLayouter() 
	{
		return new SugiyamaLayout();
	}

	@Override
	public String getName() 
	{
		return "ProbeLists (Hierarchy)";
	}
			
	@Override
	public void init() {}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"mayday.graphprovider.probelist.hierarchy",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Create a graph from a probe list hierarchy",
				getName()				
		);
		pli.addCategory(PROBE_LISTS);
		return pli;			
	}

	@Override
	public String toString() 
	{
		return getName();
	}

}
