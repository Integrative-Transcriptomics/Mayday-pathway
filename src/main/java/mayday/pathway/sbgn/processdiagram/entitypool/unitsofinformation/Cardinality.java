package mayday.pathway.sbgn.processdiagram.entitypool.unitsofinformation;

import mayday.pathway.sbgn.processdiagram.entitypool.UnitOfInformation;

/**
 * This is a convenience class for Cardinality units of information.
 * @author symons
 *
 */
public class Cardinality extends UnitOfInformation
{
	private int cardinality;

	public Cardinality(int cardinality) 
	{
		super("N",String.valueOf(cardinality));
		this.cardinality=cardinality;
	}

	/**
	 * @return the cardinality
	 */
	public int getCardinality() {
		return cardinality;
	}

	/**
	 * @param cardinality the cardinality to set
	 */
	public void setCardinality(int cardinality)
	{
		this.cardinality = cardinality;
		setAnnotation(String.valueOf(cardinality));
	}
	
	

}
