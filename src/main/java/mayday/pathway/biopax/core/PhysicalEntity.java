package mayday.pathway.biopax.core;

import mayday.pathway.biopax.parser.MasterObject;

public class PhysicalEntity extends Entity
{

	public PhysicalEntity(MasterObject object) 
	{
		super(object);
		
	}

	@Override
	public void init(MasterObject object) 
	{		
	}
	
	public static PhysicalEntity fromObject(MasterObject entity)
	{
		PhysicalEntity res=null;
		
		if(entity.getObjectType().equals("smallMolecule"))
			res=new SmallMolecule(entity);
		
		if(entity.getObjectType().equals("protein"))
			res=new Protein(entity);
		
		if(entity.getObjectType().equals("rna"))
			res=new RNA(entity);
		
		if(entity.getObjectType().equals("dna"))
			res=new DNA(entity);
		
		if(entity.getObjectType().equals("complex"))
			res=new Complex(entity);
		
		if(entity.getObjectType().equals("physicalEntity"))
			res=new PhysicalEntity(entity);

		return res;
	
	}

}
