package mayday.pathway.biopax.core;

import java.util.ArrayList;
import java.util.List;

import mayday.pathway.biopax.parser.MasterObject;

public class BiochemicalReaction extends Interaction 
{
	private double deltaG;
	private double deltaH;
	private double deltaS;
	private String ecNumber;
	private String interactionType;
	
	private List<Participant> left;
	private List<Participant> right;

	public BiochemicalReaction( MasterObject object) 
	{
		super(object);
	}

	@Override
	public void init(MasterObject object) 
	{
		if(object.hasValue("DELTA-S"))
			deltaS=Double.parseDouble(object.getFirstValue("DELTA-S"));
		if(object.hasValue("DELTA-H"))
			deltaH=Double.parseDouble(object.getFirstValue("DELTA-H"));
		if(object.hasValue("DELTA-G"))
			deltaG=Double.parseDouble(object.getFirstValue("DELTA-G"));
		if(object.hasValue("EC-NUMBER"))
			ecNumber=object.getFirstValue("EC-NUMBER");
		if(object.hasValue("INTERACTION-TYPE"))
			interactionType=object.getFirstValue("INTERACTION-TYPE");
		
		left=new ArrayList<Participant>();
		right=new ArrayList<Participant>();

		if(object.hasMember("LEFT"))
		{
			for(MasterObject l:object.getMembers("LEFT"))
			{
				if(!l.getMembers("PHYSICAL-ENTITY").isEmpty())
				{
					PhysicalEntity e=PhysicalEntity.fromObject(l.getMembers("PHYSICAL-ENTITY").get(0));
					Participant p=new Participant(e,l);
					left.add(p);
				}
			}
			for(MasterObject l:object.getMembers("RIGHT"))
			{
				PhysicalEntity e=PhysicalEntity.fromObject(l.getMembers("PHYSICAL-ENTITY").get(0));
				Participant p=new Participant(e,l);
				right.add(p);
			}
		}
	}

	/**
	 * @return the deltaG
	 */
	public double getDeltaG() {
		return deltaG;
	}

	/**
	 * @return the deltaH
	 */
	public double getDeltaH() {
		return deltaH;
	}

	/**
	 * @return the deltaS
	 */
	public double getDeltaS() {
		return deltaS;
	}

	/**
	 * @return the ecNumber
	 */
	public String getEcNumber() {
		return ecNumber;
	}

	/**
	 * @return the left
	 */
	public List<Participant> getLeft() {
		return left;
	}

	/**
	 * @return the right
	 */
	public List<Participant> getRight() {
		return right;
	}

	/**
	 * @return the interactionType
	 */
	public String getInteractionType() {
		return interactionType;
	}

	/**
	 * @param interactionType the interactionType to set
	 */
	public void setInteractionType(String interactionType) {
		this.interactionType = interactionType;
	}
	
	




}
