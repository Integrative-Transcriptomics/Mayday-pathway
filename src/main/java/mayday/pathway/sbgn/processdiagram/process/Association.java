package mayday.pathway.sbgn.processdiagram.process;

import java.awt.Shape;

import mayday.core.structures.graph.Graph;
import mayday.pathway.sbgn.processdiagram.ProcessDiagram;

public class Association extends Transition 
{
	public Association(Graph graph, String name) 
	{
		super(graph, name);
		role=ProcessDiagram.ASSOCIATION_ROLE;
	}
	
	@Override
	public Shape getGlyph() 
	{
		return ProcessDiagram.getGlyph(role);
	}
}
