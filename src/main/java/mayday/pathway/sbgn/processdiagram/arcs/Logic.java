package mayday.pathway.sbgn.processdiagram.arcs;

import mayday.pathway.sbgn.graph.SBGNEdge;
import mayday.pathway.sbgn.processdiagram.ProcessDiagram;
import mayday.pathway.sbgn.processdiagram.entitypool.EntityPoolNode;
import mayday.pathway.sbgn.processdiagram.operators.LogicGateNode;
import mayday.vis3.graph.arrows.ArrowSettings;

public class Logic extends SBGNEdge
{
	private EntityPoolNode modulatingNode;
	private LogicGateNode logicGate;
	
	public Logic(EntityPoolNode n1, LogicGateNode n2) 
	{
		super(n1, n2);
		modulatingNode=n1;
		logicGate=n2;
		role=ProcessDiagram.LOGIC_ROLE;
	}
	
	public ArrowSettings getArrowSettings()
	{
		ArrowSettings res=new ArrowSettings();	
		res.setRenderTarget(false);
		return res;
	}

	/**
	 * @return the modulatingNode
	 */
	public EntityPoolNode getModulatingNode() {
		return modulatingNode;
	}

	/**
	 * @return the logicGate
	 */
	public LogicGateNode getLogicGate() {
		return logicGate;
	}
	
	
	
	
}
