package mayday.pathway.biopax.core;

import mayday.pathway.biopax.parser.MasterObject;

public class Modulation extends Control {

	public Modulation(MasterObject object, Participant controller,	Interaction controlled) 
	{
		super(object, controller, controlled);
		if(controlType==null)
			controlType="modulation";
	}

	public static Modulation processModulation(MasterObject cat)
	{
		Participant controller=new Participant(PhysicalEntity.fromObject(cat.getMembers("CONTROLLER").get(0).getMembers("PHYSICAL-ENTITY").get(0)),cat.getMembers("CONTROLLER").get(0));
		BiochemicalReaction reaction=new BiochemicalReaction(cat.getMembers("CONTROLLED").get(0));		
		Modulation modulation=new Modulation(cat,controller,reaction);
		return modulation;
	}

}
