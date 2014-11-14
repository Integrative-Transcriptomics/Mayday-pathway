package mayday.pathway.sbgn.processdiagram.operators;

import mayday.core.structures.graph.Graph;

public class OR  extends LogicGateNode
{
	public OR(Graph graph) {
		super(graph, "OR");
		role="Logic OR";
		gateType=LogicGateNode.AND_GATE;
	}
}
