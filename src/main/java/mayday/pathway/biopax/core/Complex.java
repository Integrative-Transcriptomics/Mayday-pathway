package mayday.pathway.biopax.core;

import java.util.ArrayList;
import java.util.List;

import mayday.pathway.biopax.parser.MasterObject;

public class Complex extends PhysicalEntity
{

	private List<Participant> components;
	private String organism;
	
	public Complex(MasterObject molecule) 
	{
		super(molecule);		
	}
	
	
	@Override
	public void init(MasterObject object) 
	{
		components=new ArrayList<Participant>();
		if(object.hasMember("COMPONENTS"))
		{
			for(MasterObject l:object.getMembers("COMPONENTS"))
			{
				PhysicalEntity part=null;
				MasterObject entity=l.getMembers("PHYSICAL-ENTITY").get(0);
				if(entity.getObjectType()=="smallMolecule")
					part=new SmallMolecule(l.getMembers("PHYSICAL-ENTITY").get(0));
				
				if(entity.getObjectType()=="protein")
					part=new Protein(l.getMembers("PHYSICAL-ENTITY").get(0));
				
				if(entity.getObjectType()=="rna")
					part=new RNA(l.getMembers("PHYSICAL-ENTITY").get(0));
				
				if(entity.getObjectType()=="dna")
					part=new DNA(l.getMembers("PHYSICAL-ENTITY").get(0));
				
				if(entity.getObjectType()=="complex")
					part=new Complex(l.getMembers("PHYSICAL-ENTITY").get(0));
				
				if(entity.getObjectType()=="physicalEntity")
					part=new PhysicalEntity(l.getMembers("PHYSICAL-ENTITY").get(0));
				if(part!=null)
					components.add(new Participant(part,l));
			}
		}
		if(object.hasMember("ORGANISM"))
		{
			MasterObject o=object.getMembers("ORGANISM").get(0);
			organism=o.getFirstValue("NAME");
		}			
	}


	/**
	 * @return the organism
	 */
	public String getOrganism() {
		return organism;
	}


	/**
	 * @return the components
	 */
	public List<Participant> getComponents() {
		return components;
	}


	/**
	 * @param components the components to set
	 */
	public void setComponents(List<Participant> components) {
		this.components = components;
	}
	
	
}
