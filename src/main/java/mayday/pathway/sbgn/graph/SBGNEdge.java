package mayday.pathway.sbgn.graph;

import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Node;
import mayday.vis3.graph.arrows.ArrowSettings;

public abstract class SBGNEdge extends Edge 
{
	public SBGNEdge(Node n1, Node n2) 
	{
		super(n1,n2);
	}
	
	public ArrowSettings getArrowSettings()
	{
		ArrowSettings res=new ArrowSettings();	
		return res;
	}
}
