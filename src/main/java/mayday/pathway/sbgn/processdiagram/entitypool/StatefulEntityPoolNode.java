package mayday.pathway.sbgn.processdiagram.entitypool;

import java.util.ArrayList;
import java.util.List;

import mayday.core.structures.graph.Graph;

public abstract class StatefulEntityPoolNode extends EntityPoolNode
{
	/** Any number of state descriptions */
	protected List<StateDescription> stateDescriptions;
	
	public StatefulEntityPoolNode(Graph graph, String name) 
	{
		super(graph, name);
		stateDescriptions=new ArrayList<StateDescription>();
	}
	
	public void addStateDescription(StateDescription state)
	{
		stateDescriptions.add(state);
	}
	
	public void addStateDescription(String variable, String value)
	{
		stateDescriptions.add(new StateDescription(variable,value));
	}
	
	public StateDescription getStateDescription(String variable)
	{
		for(StateDescription d:stateDescriptions)
		{
			if(d.getVariable().equals(variable))
				return d;
		}
		return null;
	}

	/**
	 * @return the stateDescriptions
	 */
	public List<StateDescription> getStateDescriptions() 
	{
		return stateDescriptions;
	}
	
	
	
}
