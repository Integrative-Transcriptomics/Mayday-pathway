package mayday.pathway.keggview.kegg.reaction;

public class Stoichometry 
{
	private int coefficient;
	private String varCoefficient=null;
	private String substance;

	
	public Stoichometry()
	{		
	}
	
	public Stoichometry(int coefficient, String substance)
	{		
		this.coefficient=coefficient;
		this.substance=substance;
	}

	/**
	 * @return the coefficient
	 */
	public int getCoefficient() {
		return coefficient;
	}

	/**
	 * @param coefficient the coefficient to set
	 */
	public void setCoefficient(int coefficient) {
		this.coefficient = coefficient;
	}

	/**
	 * @return the substance
	 */
	public String getSubstance() {
		return substance;
	}

	/**
	 * @param substance the substance to set
	 */
	public void setSubstance(String substance) {
		this.substance = substance;
	}

	/**
	 * @return the varCoefficient
	 */
	public String getVarCoefficient() {
		return varCoefficient;
	}

	/**
	 * @param varCoefficient the varCoefficient to set
	 */
	public void setVarCoefficient(String varCoefficient) {
		this.varCoefficient = varCoefficient;
	}
	
	public String toString()
	{
		return (varCoefficient==null?""+coefficient:varCoefficient)+" "+substance;
	}
	
	
}
