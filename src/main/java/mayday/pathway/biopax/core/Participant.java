package mayday.pathway.biopax.core;

import mayday.pathway.biopax.parser.MasterObject;


public class Participant implements Comparable<Participant>
{

	protected String stoichometricCoefficient="1";
	protected String cellularLocation="";
	protected String comment;
	
	protected PhysicalEntity entity;
	
	public Participant(PhysicalEntity n, String s, String cellLoc) 
	{
		entity=n;
		stoichometricCoefficient=s;
		cellularLocation=cellLoc;
	}
	
	public Participant(PhysicalEntity n, MasterObject p) 
	{
		entity=n;
		if(p.hasValue("STOICHIOMETRIC-COEFFICIENT"))
		{
			stoichometricCoefficient=(p.getFirstValue("STOICHIOMETRIC-COEFFICIENT"));
		}
			
		if(p.hasMember("CELLULAR-LOCATION"))
		{
			MasterObject ocv=p.getMembers("CELLULAR-LOCATION").get(0);
			if(ocv.hasValue("TERM"))
				cellularLocation=ocv.getFirstValue("TERM");
		}
	}



	/**
	 * @return the stoichometricCoefficient
	 */
	public String getStoichometricCoefficient() {
		return stoichometricCoefficient;
	}

	/**
	 * @param stoichometricCoefficient the stoichometricCoefficient to set
	 */
	public void setStoichometricCoefficient(String stoichometricCoefficient) {
		this.stoichometricCoefficient = stoichometricCoefficient;
	}

	/**
	 * @return the cellularLocation
	 */
	public String getCellularLocation() {
		return cellularLocation;
	}

	/**
	 * @param cellularLocation the cellularLocation to set
	 */
	public void setCellularLocation(String cellularLocation) {
		this.cellularLocation = cellularLocation;
	}



	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}



	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}



	/**
	 * @return the entity
	 */
	public PhysicalEntity getEntity() {
		return entity;
	}

	public int compareTo(Participant o) 
	{
		return getEntity().compareTo(o.getEntity());
	}
	
	
	

}
