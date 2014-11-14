package mayday.pathway.sbgn.processdiagram.entitypool;



public class StateDescription 
{
	/** Parent stateful EPN */
	private EntityPoolNode owningStatefulEPN;
	/** Name (identification) of the state variable */
	private String variable;
	/** Value of the variable */
	private String value;
	
	public StateDescription() 
	{		
		super();
	}
	
	public StateDescription(String variable, String value) 
	{		
		this.value=value;
		this.variable=variable;
	}
	
	/**
	 * @return the owningStatefulEPN
	 */
	public EntityPoolNode getOwningStatefulEPN() {
		return owningStatefulEPN;
	}
	/**
	 * @param owningStatefulEPN the owningStatefulEPN to set
	 */
	public void setOwningStatefulEPN(EntityPoolNode owningStatefulEPN) {
		this.owningStatefulEPN = owningStatefulEPN;
	}
	/**
	 * @return the variable
	 */
	public String getVariable() {
		return variable;
	}
	/**
	 * @param variable the variable to set
	 */
	public void setVariable(String variable) {
		this.variable = variable;
	}
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return variable+":"+value;
	}
	
}
