package mayday.pathway.keggview.pathways.graph;

import mayday.pathway.keggview.kegg.pathway.ReactionEntry;


public class ReactionEdge extends PathwayEdge 
{
	private ReactionEntry reaction;	
	private boolean reversible;

	/**
	 * Constructor connecting two nodes
	 * @param source
	 * @param target
	 */
	public ReactionEdge(PathwayNode source, PathwayNode target)
	{	
		super(source,target);		
	}
	
	/**
	 * Constructor connecting two nodes. Additionally allows to set a label.
	 * @param source
	 * @param target
	 * @param label
	 */
	public ReactionEdge(PathwayNode source, PathwayNode target, String label)
	{		
		super(source,target);
		setName(label);
	}
	
	/**
	 * @return the reversible
	 */
	public boolean isReversible() 
	{
		return reversible;
	}

	/**
	 * @param reversible the reversible to set
	 */
	public void setReversible(boolean reversible) 
	{
		this.reversible = reversible;
	}

	/**
	 * @return the reaction
	 */
	public ReactionEntry getReaction() {
		return reaction;
	}

	/**
	 * @param reaction the reaction to set
	 */
	public void setReaction(ReactionEntry reaction) {
		this.reaction = reaction;
	}
	
	
}
