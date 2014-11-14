package mayday.pathway.sbgn.processdiagram.entitypool;

import mayday.core.structures.graph.Graph;
import mayday.pathway.sbgn.processdiagram.container.Complex;



public class MultimerComplex extends Complex
{
	/** Number of identical subunits */
	int numberOfSubunits;
	
	public MultimerComplex(Graph graph, String name) 
	{
		super(graph, name);		
		role="Multimer Complex";
	}
	
}
