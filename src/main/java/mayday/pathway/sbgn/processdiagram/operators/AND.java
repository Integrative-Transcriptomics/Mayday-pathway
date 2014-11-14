package mayday.pathway.sbgn.processdiagram.operators;

import mayday.core.structures.graph.Graph;

public class AND extends LogicGateNode
{
	public AND(Graph graph) {
		super(graph, "AND");
		role="Logic AND";
		gateType=LogicGateNode.AND_GATE;
	}
}
