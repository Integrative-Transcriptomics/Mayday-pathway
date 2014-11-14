package mayday.pathway.biopax.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.pathway.biopax.core.BiochemicalReaction;
import mayday.pathway.biopax.core.Catalysis;
import mayday.pathway.biopax.core.Control;
import mayday.pathway.biopax.core.Interaction;
import mayday.pathway.biopax.core.Modulation;
import mayday.pathway.biopax.core.Participant;
import mayday.pathway.biopax.core.PhysicalEntity;
import mayday.pathway.biopax.parser.MasterObject;

public class PathwayStep implements Comparable<PathwayStep>
{
	private List<Interaction> interactions;

	public PathwayStep(MasterObject o) 
	{		
		interactions=new ArrayList<Interaction>();
		for(MasterObject m: o.getMembers("STEP-INTERACTIONS"))
		{
			if(m.getObjectType().equals("catalysis"))
			{
				interactions.add(processCatalysis(m));
			}
			if(m.getObjectType().equals("modulation"))
			{
				interactions.add(processModulation(m));
			}
			if(m.getObjectType().equals("biochemicalReaction"))
			{
				interactions.add(processBiochemicalReaction(m));
			}
		}
	}
	
	public Map<String, Participant> getAllParticipants()
	{
		Map<String, Participant> res =new HashMap<String, Participant>();
		for(Interaction i:interactions)			
		{
			if(i instanceof BiochemicalReaction)
			{
				for(Participant p:((BiochemicalReaction) i).getLeft())
				{
					res.put(p.getEntity().getName().trim(), p);
				}
				for(Participant p:((BiochemicalReaction) i).getRight())
				{
					res.put(p.getEntity().getName().trim(), p);
				}
			}
			if(i instanceof Control)
			{
				for(Participant p:((Control) i).getController())
				{
					res.put(p.getEntity().getName().trim(), p);
				}
				BiochemicalReaction reaction=(BiochemicalReaction) ((Control) i).getControlled();
				for(Participant p:reaction.getLeft())
				{
					res.put(p.getEntity().getName().trim(), p);
				}
				for(Participant p:reaction.getRight())
				{
					res.put(p.getEntity().getName().trim(), p);
				}
			}
		}
		
		return res;
	}
	

	private Modulation processModulation(MasterObject cat)
	{
		Participant controller=new Participant(PhysicalEntity.fromObject(cat.getMembers("CONTROLLER").get(0).getMembers("PHYSICAL-ENTITY").get(0)),cat.getMembers("CONTROLLER").get(0));
//		BiochemicalReaction reaction=new BiochemicalReaction(cat.getMembers("CONTROLLED").get(0));	
		Interaction reaction=createInteraction(cat.getMembers("CONTROLLED").get(0));
		Modulation modulation=new Modulation(cat,controller,reaction);
		return modulation;
	}
	
	private Interaction createInteraction(MasterObject m)
	{
		if(m.getObjectType().equals("catalysis"))
		{
			return(processCatalysis(m));
		}
		if(m.getObjectType().equals("modulation"))
		{
			return(processModulation(m));
		}
		if(m.getObjectType().equals("biochemicalReaction"))
		{
			return(processBiochemicalReaction(m));
		}
		return null;
	}

	private Catalysis processCatalysis(MasterObject cat)
	{
		Participant controller=new Participant(PhysicalEntity.fromObject(cat.getMembers("CONTROLLER").get(0).getMembers("PHYSICAL-ENTITY").get(0)),cat.getMembers("CONTROLLER").get(0));
		BiochemicalReaction reaction=new BiochemicalReaction(cat.getMembers("CONTROLLED").get(0));
		Catalysis catalysis=new Catalysis(cat,controller,reaction);
		return catalysis;
	}

	private BiochemicalReaction processBiochemicalReaction(MasterObject rea)
	{
		return new BiochemicalReaction(rea);
	}

	public String getName()
	{
		return interactions.get(0).getName();
	}

	/**
	 * @return the interactions
	 */
	public List<Interaction> getInteractions() {
		return interactions;
	}

	public int compareTo(PathwayStep o) 
	{
		return 0;
	}

	@Override
	public String toString() 
	{
		return getName();
	}

}


