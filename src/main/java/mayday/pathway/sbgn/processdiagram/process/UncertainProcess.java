package mayday.pathway.sbgn.processdiagram.process;

import java.awt.Shape;

import mayday.core.structures.graph.Graph;
import mayday.pathway.sbgn.processdiagram.ProcessDiagram;

public class UncertainProcess extends Transition 
{
	public UncertainProcess(Graph graph, String name) 
	{
		super(graph, name);	
		role=ProcessDiagram.UNCERTAIN_PROCESS_ROLE;
	}
	
	@Override
	public Shape getGlyph() 
	{
		return ProcessDiagram.getGlyph(role);
	}		
}
