package mayday.pathway.biopax.core;

import mayday.pathway.biopax.parser.MasterObject;

/**
 * @author symons
 *
 */
public class Protein extends PhysicalEntity
{
	private String sequence;
	private String organism;
	
	public Protein(MasterObject molecule) 
	{
		super(molecule);		
	}
	
	
	@Override
	public void init(MasterObject object) 
	{
		if(object.hasValue("SEQUENCE"))
			sequence=object.getFirstValue("SEQUENCE");
		if(object.hasMember("ORGANISM"))
		{
			MasterObject o=object.getMembers("ORGANISM").get(0);
			if(object.hasMember("NAME"))
				organism=o.getFirstValue("NAME");
		}			
	}


	/**
	 * @return the sequence
	 */
	public String getSequence() {
		return sequence;
	}


	/**
	 * @return the organism
	 */
	public String getOrganism() {
		return organism;
	}
	
	
	
	
}
