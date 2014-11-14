package mayday.graphviewer.util;

import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;

public abstract class Trees 
{
	public static int numberOfLeaves(Graph g, Node n)
	{
		int c=0;
		for(Node nc: g.getOutNeighbors(n))
		{
			if(g.getOutDegree(nc)==0)
			{
				c++;
			}else
			{
				c+=numberOfLeaves(g, nc);
			}
		}
		return c;
	}
	
	
	
//	public static int height(Graph g, Node n)
//	{
//		return 0;
//	}
}
