package mayday.pathway.sbgn.processdiagram.entitypool;

import mayday.core.structures.graph.Graph;
import mayday.pathway.sbgn.processdiagram.entitypool.unitsofinformation.Cardinality;

public class MultimerMacromolecule extends Macromolecule
{
	/** Number of identical subunits */
	int numberOfSubunits;
	
	public MultimerMacromolecule(Graph graph, String name, int numberOfSubunits) 
	{
		super(graph, name);	
		role="Multimer Macromolecule";
		this.numberOfSubunits=numberOfSubunits;
		addUnitOfInformation(new Cardinality(numberOfSubunits));
	}
		
}
