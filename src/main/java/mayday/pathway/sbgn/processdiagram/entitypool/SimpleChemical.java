package mayday.pathway.sbgn.processdiagram.entitypool;

import java.awt.Shape;
import java.awt.geom.Area;

import mayday.core.structures.graph.Graph;
import mayday.pathway.sbgn.processdiagram.ProcessDiagram;

public class SimpleChemical extends EntityPoolNode
{
	protected static Area cloneMarkerGlyph;
		
	public SimpleChemical(Graph graph, String name) 
	{
		super(graph, name);		
		role=ProcessDiagram.SIMPLE_CHEMICAL_ROLE;
	}

	@Override
	public Shape getGlyph() 
	{
		return ProcessDiagram.getGlyph(role);
	}
	

	
	
	
}
