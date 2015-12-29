package mayday.graphviewer.linlog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import linloglayout.Edge;
import linloglayout.LinLogLayout;
import linloglayout.MinimizerBarnesHut;
import linloglayout.MinimizerClassic;
import linloglayout.Node;
import linloglayout.OptimizerModularity;
import mayday.core.structures.graph.Graph;

public class LinLogWrapper 
{
	public static Map<String,double[]> position(Map<String,Map<String,Double>> graph, int minimizer, int numIter)
	{
		graph = LinLogLayout.makeSymmetricGraph(graph);
		Map<String,Node> nameToNode = LinLogLayout.makeNodes(graph);
		List<Node> nodes = new ArrayList<Node>(nameToNode.values());
		List<Edge> edges = LinLogLayout.makeEdges(graph,nameToNode);
		Map<Node,double[]> nodeToPosition = LinLogLayout.makeInitialPositions(nodes, false);	
		if(minimizer==0)
		{
			new MinimizerBarnesHut(nodes, edges, 0.0, 1.0, 0.01).minimizeEnergy(nodeToPosition, numIter);
		}else
		{
			new MinimizerClassic(nodes, edges, 0.0, 1.0, 0.01,2).minimizeEnergy(nodeToPosition, numIter);
		}
			
		Map<String, double[]> res=new HashMap<String, double[]>();
		for(Node n: nodeToPosition.keySet())
		{
			res.put(n.name,nodeToPosition.get(n));
		}		
		return res;
	}

	public static Map<String,Integer> cluster(Map<String,Map<String,Double>> graph)
	{
		graph = LinLogLayout.makeSymmetricGraph(graph);
		Map<String,Node> nameToNode = LinLogLayout.makeNodes(graph);
		List<Node> nodes = new ArrayList<Node>(nameToNode.values());
		List<Edge> edges = LinLogLayout.makeEdges(graph,nameToNode);

		Map<Node,Integer> nodeToCluster = new OptimizerModularity().execute(nodes, edges, true, false);
		
		Map<String, Integer> res=new HashMap<String, Integer>();
		for(Node n: nodeToCluster.keySet())
		{
			res.put(n.name,nodeToCluster.get(n));
		}		
		return res;		
	}
	
	public static Map<mayday.core.structures.graph.Node, String> buildNames(Collection<mayday.core.structures.graph.Node> g)
	{
		Map<mayday.core.structures.graph.Node, String> res=new HashMap<mayday.core.structures.graph.Node, String>();		
		for(mayday.core.structures.graph.Node n: g)
		{
			res.put(n,"node"+n.hashCode());
		}		
		return res;
	}
	
	public static Map<String, Map<String,Double>> buildLLGraph(Graph g, Map<mayday.core.structures.graph.Node,String> names)
	{
		Map<String, Map<String,Double>> res=new HashMap<String, Map<String,Double>>();
		for(mayday.core.structures.graph.Node n:g.getNodes())
		{
			Map<String, Double> nres=new HashMap<String, Double>();
			for(mayday.core.structures.graph.Edge e:g.getOutEdges(n))
			{
				nres.put(names.get(e.getTarget()),e.getWeight()==0?1:e.getWeight());
			}
			res.put(names.get(n), nres);
		}	
		return res;
	}
}
