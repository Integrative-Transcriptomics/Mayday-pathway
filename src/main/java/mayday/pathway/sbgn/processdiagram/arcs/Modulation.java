package mayday.pathway.sbgn.processdiagram.arcs;

import mayday.core.structures.graph.Node;
import mayday.pathway.sbgn.graph.SBGNEdge;
import mayday.pathway.sbgn.processdiagram.ProcessDiagram;
import mayday.pathway.sbgn.processdiagram.entitypool.EntityPoolNode;
import mayday.pathway.sbgn.processdiagram.process.Transition;
import mayday.vis3.graph.arrows.ArrowSettings;
import mayday.vis3.graph.arrows.ArrowStyle;

public class Modulation extends SBGNEdge
{
	private EntityPoolNode controller;
	private Transition modulated;
	
	public Modulation(EntityPoolNode cont, Transition modded)
	{
		super(cont,modded);
		controller=cont;
		modulated=modded;
		role=ProcessDiagram.MODULATION_ROLE;
	}
	
	@Override
	public ArrowSettings getArrowSettings() 
	{
		ArrowSettings s=super.getArrowSettings();
		s.setRenderTarget(true);
		s.setFillTarget(false);
		s.setTargetAngle(Math.toRadians(45));
		s.setTargetStyle(ArrowStyle.ARROW_DIAMOND);
		return s;
	}

	/**
	 * @return the controller
	 */
	public Node getController() {
		return controller;
	}

	/**
	 * @return the modulated
	 */
	public Node getModulated() {
		return modulated;
	}
	
	public static Modulation createModulation(mayday.pathway.biopax.core.Modulation mod, EntityPoolNode source, Transition target)
	{
		if(mod.getControlType().startsWith("INHIBITION"))
		{
			return new Inhibition(source,target);
		}
		if(mod.getControlType().startsWith("ACTIVATION"))
		{
			return new Stimulation(source,target);
		}
		return new Modulation(source,target);
	}
	
}
