package mayday.pathway.sbgn.processdiagram.entitypool;

import java.awt.Shape;

import mayday.core.structures.graph.Graph;
import mayday.pathway.sbgn.processdiagram.ProcessDiagram;

public class Perturbation extends EntityPoolNode
{
	@SuppressWarnings("deprecation")
	public Perturbation(Graph graph, String name) 
	{
		super(graph, name);		
		role=ProcessDiagram.PERTUBATION_ROLE;
	}
	
	@Override
	public Shape getGlyph() 
	{
		return ProcessDiagram.getGlyph(role);
	}
}
