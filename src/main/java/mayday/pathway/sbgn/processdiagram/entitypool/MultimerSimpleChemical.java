package mayday.pathway.sbgn.processdiagram.entitypool;

import mayday.core.structures.graph.Graph;
import mayday.pathway.sbgn.processdiagram.entitypool.unitsofinformation.Cardinality;

/**
 * This class is the graphical representation of a multimer consisting of simple chemicals.
 * @author Stephan Symons
 *
 */
public class MultimerSimpleChemical extends SimpleChemical
{
	/** Number of identical subunits */
	private int numberOfSubunits;
	
	/**
	 * @param graph
	 * @param name
	 * @param numberOfSubunits
	 */
	public MultimerSimpleChemical(Graph graph, String name, int numberOfSubunits) 
	{
		super(graph, name);
		this.numberOfSubunits=numberOfSubunits;
		addUnitOfInformation(new Cardinality(numberOfSubunits));
		role="Multimer Simple Chemical";
	}
	
	public MultimerSimpleChemical(Graph graph, String name) 
	{
		super(graph, name);
		this.numberOfSubunits=1;
		role="Multimer Simple Chemical";
	}

	/**
	 * @return the numberOfSubunits
	 */
	public int getNumberOfSubunits() {
		return numberOfSubunits;
	}

	/**
	 * @param numberOfSubunits the numberOfSubunits to set
	 */
	public void setNumberOfSubunits(int numberOfSubunits) {
		this.numberOfSubunits = numberOfSubunits;
	}


	
	
	
}
