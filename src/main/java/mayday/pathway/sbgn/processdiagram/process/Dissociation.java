package mayday.pathway.sbgn.processdiagram.process;

import java.awt.Shape;

import mayday.core.structures.graph.Graph;
import mayday.pathway.sbgn.processdiagram.ProcessDiagram;

public class Dissociation extends Transition 
{
	public Dissociation(Graph graph, String name) 
	{
		super(graph, name);	
		role=ProcessDiagram.DISSOCIATION_ROLE;
	}
	
	@Override
	public Shape getGlyph() 
	{
		return ProcessDiagram.getGlyph(role);
	}
}
