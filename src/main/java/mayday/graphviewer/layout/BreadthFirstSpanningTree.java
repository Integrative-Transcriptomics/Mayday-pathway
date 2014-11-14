package mayday.graphviewer.layout;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;

public class BreadthFirstSpanningTree 
{
	private Map<Node,Boolean> seen;
	private Queue<Node> queue;
	
	public Graph getSpanningTree(Graph g, Node root)
	{
		seen=new HashMap<Node, Boolean>();
		queue=new LinkedList<Node>();
		
		queue.add(root);
		Graph res=new Graph();
		res.setName(g.getName());
		res.addNode(root);
		seen.put(root, true);	
		while(!queue.isEmpty() )
		{
			Node current=queue.poll();			
			process(g,res,current);			
		}
		return res;
	}
	
	public Graph getSpanningTree(Graph g, Collection<Node> nodes, Node root)
	{
		seen=new HashMap<Node, Boolean>();
		queue=new LinkedList<Node>();
		
		queue.add(root);
		Graph res=new Graph();
		res.setName(g.getName());
		res.addNode(root);
		seen.put(root, true);	
		while(!queue.isEmpty() )
		{
			Node current=queue.poll();			
			process(g,nodes,res,current);			
		}
		return res;
	}
	
	private void process(Graph g, Collection<Node> nodes, Graph tree,  Node root)
	{
			
		for(Node n: g.getNeighbors(root))
		{
			if(!nodes.contains(n)) continue;
			if(seen.containsKey(n))
				continue;
			tree.addNode(n);
			queue.add(n);
			tree.connect(root,n);
			seen.put(n, true);
		}
	}
	
	private void process(Graph g, Graph tree,  Node root)
	{
			
		for(Node n: g.getNeighbors(root))
		{
			if(seen.containsKey(n))
				continue;
			tree.addNode(n);
			queue.add(n);
			tree.connect(root,n);
			seen.put(n, true);
		}
	}
	
}
