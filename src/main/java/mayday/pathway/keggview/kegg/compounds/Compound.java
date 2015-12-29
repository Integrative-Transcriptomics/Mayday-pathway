package mayday.pathway.keggview.kegg.compounds;

import java.util.ArrayList;
import java.util.List;

import mayday.pathway.keggview.kegg.KEGGObject;

public class Compound extends KEGGObject
{
	private List<String> names;
	private String formula;
	private double mass;
	private List<String> pathways;
	
	public Compound()
	{
		this.names=new ArrayList<String>();
	}
	
	public Compound(String entry, String name)
	{
		setEntry(entry);
		this.names=new ArrayList<String>();
		names.add(name);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return names.get(0);
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		if(names.isEmpty()) 	
			names.add(name);			
		else
			names.set(0, name);
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
	 * @return the names
	 */
	public List<String> getNames() {
		return names;
	}

	/**
	 * @param names the names to set
	 */
	public void setNames(List<String> names) {
		this.names = names;
	}

	public void addName(String name)
	{
		names.add(name);
	}

	/**
	 * @return the mass
	 */
	public double getMass() {
		return mass;
	}

	/**
	 * @param mass the mass to set
	 */
	public void setMass(double mass) {
		this.mass = mass;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return getName();
	}

	/**
	 * @return the pathways
	 */
	public List<String> getPathways() {
		return pathways;
	}

	/**
	 * @param pathways the pathways to set
	 */
	public void setPathways(List<String> pathways) {
		this.pathways = pathways;
	}
	
	
	


}
