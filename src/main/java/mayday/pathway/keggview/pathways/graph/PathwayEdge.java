package mayday.pathway.keggview.pathways.graph;

import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Node;

public abstract class PathwayEdge extends Edge
{
	public PathwayEdge(Node source, Node target) 
	{
		super(source, target);		
	}
	
	public PathwayEdge(PathwayNode source, PathwayNode target) 
	{
		super(source, target);		
	}
		
}
