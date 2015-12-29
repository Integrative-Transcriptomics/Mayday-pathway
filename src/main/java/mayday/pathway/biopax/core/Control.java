package mayday.pathway.biopax.core;

import java.util.ArrayList;
import java.util.List;

import mayday.pathway.biopax.parser.MasterObject;

public class Control extends Interaction
{
	protected List<Participant> controller;
	protected Interaction controlled;
	protected String controlType;
	
	public Control(MasterObject object, Participant controller, Interaction controlled) 
	{
		super(object);	
		init(object);
		this.controller.add(controller);
		this.controlled=controlled;
	}

	@Override
	public void init(MasterObject object) 
	{
		controller=new ArrayList<Participant>();
		if(object.hasValue("CONTROL-TYPE"))
			controlType=object.getFirstValue("CONTROL-TYPE");
		
	}

	/**
	 * @return the controller
	 */
	public List<Participant> getController() {
		return controller;
	}

	/**
	 * @return the controlled
	 */
	public Interaction getControlled() {
		return controlled;
	}

	/**
	 * @return the controlType
	 */
	public String getControlType() {
		return controlType;
	}
	
	

}
