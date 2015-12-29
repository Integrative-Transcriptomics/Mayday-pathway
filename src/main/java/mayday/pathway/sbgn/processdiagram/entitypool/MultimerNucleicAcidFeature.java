package mayday.pathway.sbgn.processdiagram.entitypool;

import mayday.core.structures.graph.Graph;

public class MultimerNucleicAcidFeature extends NucleicAcidFeature
{
	/** Number of identical subunits */
	int numberOfSubunits;
	
	public MultimerNucleicAcidFeature(Graph graph, String name) 
	{
		super(graph, name);		
		role="Multimer Nucleic Acid Feature";
	}
	
}
