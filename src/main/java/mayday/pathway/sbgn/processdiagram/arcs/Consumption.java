package mayday.pathway.sbgn.processdiagram.arcs;

import mayday.pathway.sbgn.graph.SBGNEdge;
import mayday.pathway.sbgn.processdiagram.ProcessDiagram;
import mayday.pathway.sbgn.processdiagram.entitypool.EntityPoolNode;
import mayday.pathway.sbgn.processdiagram.entitypool.unitsofinformation.Cardinality;
import mayday.pathway.sbgn.processdiagram.process.Transition;
import mayday.vis3.graph.arrows.ArrowSettings;

public class Consumption extends SBGNEdge
{
	private EntityPoolNode consumable;
	private Transition process;
	
	private Cardinality cardinality;
	
	public Consumption(EntityPoolNode n1, Transition n2) 
	{
		super(n1, n2);
		consumable=n1;
		process=n2;
		role=ProcessDiagram.CONSUMPTION_ROLE;
	}
	
	public ArrowSettings getArrowSettings()
	{
		ArrowSettings res=new ArrowSettings();	
		res.setRenderTarget(false);
		return res;
	}

	/**
	 * @return the consumable
	 */
	public EntityPoolNode getConsumable() {
		return consumable;
	}

	/**
	 * @return the process
	 */
	public Transition getProcess() {
		return process;
	}

	/**
	 * @return the cardinality
	 */
	public Cardinality getCardinality() {
		return cardinality;
	}

	/**
	 * @param cardinality the cardinality to set
	 */
	public void setCardinality(Cardinality cardinality) {
		this.cardinality = cardinality;
	}
	
	
	
}
