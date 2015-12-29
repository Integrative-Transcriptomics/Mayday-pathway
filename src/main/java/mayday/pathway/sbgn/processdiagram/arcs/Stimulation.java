package mayday.pathway.sbgn.processdiagram.arcs;

import mayday.pathway.sbgn.processdiagram.ProcessDiagram;
import mayday.pathway.sbgn.processdiagram.entitypool.EntityPoolNode;
import mayday.pathway.sbgn.processdiagram.process.Transition;
import mayday.vis3.graph.arrows.ArrowSettings;
import mayday.vis3.graph.arrows.ArrowStyle;

public class Stimulation extends Modulation
{
	public Stimulation(EntityPoolNode cont, Transition modded) 
	{
		super(cont, modded);
		role=ProcessDiagram.STIMULATION_ROLE;
	}
	
	@Override
	public ArrowSettings getArrowSettings() 
	{
		ArrowSettings s=super.getArrowSettings();
		s.setRenderTarget(true);
		s.setFillTarget(false);
		s.setTargetAngle(Math.toRadians(45));
		s.setTargetStyle(ArrowStyle.ARROW_TRIANGLE);
		return s;
	}
	
}
