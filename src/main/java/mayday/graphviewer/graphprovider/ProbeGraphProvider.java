package mayday.graphviewer.graphprovider;

import java.util.HashMap;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.maps.MultiHashMap;
import mayday.vis3.graph.layout.CanvasLayouter;
import mayday.vis3.graph.layout.GridLayouter;

public class ProbeGraphProvider extends AbstractPlugin implements GraphProvider {

	@Override
	public Graph createGraph(MultiHashMap<DataSet, ProbeList> probeLists) 
	{
		Graph graph=new Graph();
		for(DataSet ds: probeLists.keySet())
		{
			for(ProbeList pl: probeLists.get(ds))
			{
				for(Probe p: pl)
				{
					MultiProbeNode n=new MultiProbeNode(graph,p);
					graph.addNode(n);
				}
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
		return "Probes";
	}

	@Override
	public String toString() 
	{
		return getName();
	}
	
	@Override
	public void init() {}
		
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"mayday.graphprovider.probes",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Create a new graph from of all selected probes",
				getName()				
		);
		pli.addCategory(PROBES);
		return pli;			
	}
		
}
