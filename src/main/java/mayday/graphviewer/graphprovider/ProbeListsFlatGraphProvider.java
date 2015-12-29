package mayday.graphviewer.graphprovider;

import java.util.HashMap;

import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.maps.MultiHashMap;
import mayday.vis3.graph.layout.CanvasLayouter;
import mayday.vis3.graph.layout.GridLayouter;

public class ProbeListsFlatGraphProvider extends AbstractPlugin implements GraphProvider {

	@Override
	public Graph createGraph(MultiHashMap<DataSet, ProbeList> probeLists) 
	{
		Graph graph=new Graph();
		for(DataSet ds: probeLists.keySet())
		{
			for(ProbeList pl:probeLists.get(ds))
			{
				MultiProbeNode n=new MultiProbeNode(graph,pl);
				graph.addNode(n);
			}
		}
		return graph;
	}

	@Override
	public CanvasLayouter defaultLayouter() 
	{
		return new GridLayouter(GridLayouter.FILL);
	}

	@Override
	public String getName() 
	{
		return "ProbeLists (flat)"; 
	}
	
	@Override
	public void init() {}
		
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"mayday.graphprovider.probelist.flat",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Create a graph from  probe lists ",
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
