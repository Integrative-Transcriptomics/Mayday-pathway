package mayday.pathway.sbgn.processdiagram.arcs;

import mayday.pathway.sbgn.graph.SBGNEdge;
import mayday.pathway.sbgn.processdiagram.ProcessDiagram;
import mayday.pathway.sbgn.processdiagram.entitypool.EntityPoolNode;
import mayday.pathway.sbgn.processdiagram.entitypool.Tag;
import mayday.vis3.graph.arrows.ArrowSettings;

public class Equivalence extends SBGNEdge
{
	private Tag tag;
	private EntityPoolNode node;
	
	public Equivalence(Tag n1, EntityPoolNode n2) {
		super(n1, n2);
		this.tag=n1;
		this.node=n2;
		role=ProcessDiagram.EQUIVALENCE_ROLE;
	}
	
	
	public ArrowSettings getArrowSettings()
	{
		ArrowSettings res=new ArrowSettings();	
		res.setRenderTarget(false);
		return res;
	}


	/**
	 * @return the tag
	 */
	public Tag getTag() {
		return tag;
	}


	/**
	 * @return the node
	 */
	public EntityPoolNode getNode() {
		return node;
	}
	
	
	
}
