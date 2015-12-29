package mayday.pathway.sbgn.processdiagram.entitypool;

import java.awt.Shape;

import mayday.core.structures.graph.Graph;
import mayday.pathway.sbgn.processdiagram.ProcessDiagram;


public class Observable  extends EntityPoolNode{

	@SuppressWarnings("deprecation")
	public Observable(Graph graph, String name) 
	{
		super(graph, name);		
		role=ProcessDiagram.OBSERVALBE_ROLE;
	}
	
	@Override
	public Shape getCloneMarker() 
	{
		return null;
	}

	@Override
	public Shape getGlyph() 
	{
		return ProcessDiagram.getGlyph(role);
	}

}
