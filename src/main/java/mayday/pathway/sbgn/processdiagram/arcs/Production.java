package mayday.pathway.sbgn.processdiagram.arcs;

import java.awt.Color;

import mayday.pathway.sbgn.graph.SBGNEdge;
import mayday.pathway.sbgn.processdiagram.ProcessDiagram;
import mayday.pathway.sbgn.processdiagram.entitypool.EntityPoolNode;
import mayday.pathway.sbgn.processdiagram.entitypool.unitsofinformation.Cardinality;
import mayday.pathway.sbgn.processdiagram.process.Transition;
import mayday.vis3.graph.arrows.ArrowSettings;
import mayday.vis3.graph.arrows.ArrowStyle;

public class Production extends SBGNEdge
{
	private Transition process;
	private EntityPoolNode product;
	
	private Cardinality cardinality;
	
	public Production(Transition transition, EntityPoolNode product)
	{
		super(transition,product);
		this.product=product;
		role=ProcessDiagram.PRODUCTION_ROLE;
	}
	
	@Override
	public ArrowSettings getArrowSettings() 
	{
		ArrowSettings s=super.getArrowSettings();
		s.setRenderTarget(true);
		s.setFillTarget(true);
		s.setTargetAngle(Math.toRadians(45));
		s.setTargetStyle(ArrowStyle.ARROW_TRIANGLE);
		s.setFillColor(Color.black);
		return s;
	}

	/**
	 * @return the process
	 */
	public Transition getProcess() {
		return process;
	}

	/**
	 * @return the product
	 */
	public EntityPoolNode getProduct() {
		return product;
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
