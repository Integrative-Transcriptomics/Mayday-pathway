package mayday.pathway.sbgn.processdiagram.process;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.List;

import mayday.core.structures.graph.Graph;
import mayday.pathway.sbgn.graph.SBGNNode;
import mayday.pathway.sbgn.processdiagram.ProcessDiagram;
import mayday.pathway.sbgn.processdiagram.entitypool.EntityPoolNode;


/**
 * @author symons
 *
 */
public class Transition extends SBGNNode 
{

	private List<EntityPoolNode> produceableNodes;
	private List<EntityPoolNode> consumableNodes;
	private List<EntityPoolNode> modulatingNodes;

	
	public Transition(Graph graph, String name) 
	{
		super(graph, name);
		showLabel=false;
		produceableNodes=new ArrayList<EntityPoolNode>();
		consumableNodes=new ArrayList<EntityPoolNode>();
		modulatingNodes=new ArrayList<EntityPoolNode>();
		role=ProcessDiagram.PROCESS_ROLE;
	}
	
	@Override
	public Shape getGlyph() 
	{
		return ProcessDiagram.getGlyph(role);
	}
	
	public List<EntityPoolNode> getModulatingNodes() 
	{
		return modulatingNodes;
	}

	/**
	 * @return the produceableNodes
	 */
	public List<EntityPoolNode> getProduceableNodes() {
		return produceableNodes;
	}

	/**
	 * @param produceableNodes the produceableNodes to set
	 */
	public void setProduceableNodes(List<EntityPoolNode> produceableNodes) {
		this.produceableNodes = produceableNodes;
	}

	/**
	 * @return the consumableNodes
	 */
	public List<EntityPoolNode> getConsumableNodes() {
		return consumableNodes;
	}

	/**
	 * @param consumableNodes the consumableNodes to set
	 */
	public void setConsumableNodes(List<EntityPoolNode> consumableNodes) {
		this.consumableNodes = consumableNodes;
	}

	/**
	 * @param modulatingNodes the modulatingNodes to set
	 */
	public void setModulatingNodes(List<EntityPoolNode> modulatingNodes) {
		this.modulatingNodes = modulatingNodes;
	}
		
}
