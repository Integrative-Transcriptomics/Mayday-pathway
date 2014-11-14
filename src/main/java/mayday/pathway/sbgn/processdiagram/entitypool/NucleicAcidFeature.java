package mayday.pathway.sbgn.processdiagram.entitypool;

import java.awt.Shape;

import mayday.core.structures.graph.Graph;
import mayday.pathway.sbgn.processdiagram.ProcessDiagram;

public class NucleicAcidFeature extends StatefulEntityPoolNode
{
	public NucleicAcidFeature(Graph graph, String name) 
	{
		super(graph, name);
		role=ProcessDiagram.NUCLEIC_ACID_FEATURE_ROLE;
	}

	@Override
	public Shape getGlyph() 
	{
		return ProcessDiagram.getGlyph(role);
	}

}
