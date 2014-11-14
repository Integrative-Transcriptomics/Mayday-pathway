package mayday.pathway.biopax.core;

import mayday.pathway.biopax.parser.MasterObject;

public class SmallMolecule extends PhysicalEntity 
{

	private String formula;

	private double weight;

	private String structure;

	public SmallMolecule( MasterObject molecule) 
	{
		super(molecule);		
	}


	@Override
	public void init(MasterObject object) 
	{
		if(object.hasValue("CHEMICAL-FORMULA"))
			formula=object.getFirstValue("CHEMICAL-FORMULA");
		if(object.hasValue("MOLECULAR-WEIGHT"))
			weight=Double.parseDouble(object.getFirstValue("MOLECULAR-WEIGHT"));	
		if(object.hasValue("STRUCTURE"))
			structure=object.getFirstValue("STRUCTURE");	

	}


	/**
	 * @return the formula
	 */
	public String getFormula() {
		return formula;
	}

	/**
	 * @param formula the formula to set
	 */
	public void setFormula(String formula) {
		this.formula = formula;
	}

	/**
	 * @return the weight
	 */
	public double getWeight() {
		return weight;
	}

	/**
	 * @param weight the weight to set
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}

	/**
	 * @return the structure
	 */
	public String getStructure() {
		return structure;
	}

	/**
	 * @param structure the structure to set
	 */
	public void setStructure(String structure) {
		this.structure = structure;
	}





}
