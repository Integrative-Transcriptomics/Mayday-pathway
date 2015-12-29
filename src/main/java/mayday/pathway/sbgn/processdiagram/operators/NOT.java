package mayday.pathway.sbgn.processdiagram.operators;

import mayday.core.structures.graph.Graph;

public class NOT  extends LogicGateNode
{
	public NOT(Graph graph) {
		super(graph, "NOT");
		role="Logic NOT";
		gateType=LogicGateNode.AND_GATE;
	}
}
