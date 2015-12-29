package mayday.pathway.biopax.core;

import java.util.ArrayList;
import java.util.List;

import mayday.pathway.biopax.parser.MasterObject;

public class Catalysis extends Control
{
	private String direction;
	private List<PhysicalEntity> cofactors;

	public Catalysis(MasterObject object, Participant controller, Interaction controlled) 
	{
		super(object,controller,controlled);		
		controlType="catalysis";
	}

	@Override
	public void init(MasterObject object)
	{
		super.init(object);
		if(object.hasValue("DIRECTION"))
			direction=object.getFirstValue("DIRECTION");
		
		cofactors=new ArrayList<PhysicalEntity>();
		
		if(object.hasMember("COFACTOR"))
		{
			for(MasterObject o:object.getMembers("COFACTOR"))
			{
				cofactors.add(PhysicalEntity.fromObject(o));
			}
		}
	}
	
	public static Catalysis processCatalysis(MasterObject cat)
	{
		Participant controller=new Participant(PhysicalEntity.fromObject(cat.getMembers("CONTROLLER").get(0).getMembers("PHYSICAL-ENTITY").get(0)),cat.getMembers("CONTROLLER").get(0));
		BiochemicalReaction reaction=new BiochemicalReaction(cat.getMembers("CONTROLLED").get(0));
		Catalysis catalysis=new Catalysis(cat,controller,reaction);
		return catalysis;
	}

	/**
	 * @return the direction
	 */
	public String getDirection() {
		return direction;
	}

	/**
	 * @return the cofactors
	 */
	public List<PhysicalEntity> getCofactors() {
		return cofactors;
	}
	
	

}
