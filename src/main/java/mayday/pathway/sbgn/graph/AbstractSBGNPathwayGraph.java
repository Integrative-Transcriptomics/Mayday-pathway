package mayday.pathway.sbgn.graph;

import mayday.core.structures.graph.Graph;
import mayday.pathway.biopax.parser.MasterObject;


/**
 * @author symons
 *
 */
public abstract class AbstractSBGNPathwayGraph extends Graph
{	
	protected String organism;
	protected String name;
	protected Graph reactionGraph;
	protected Graph stepGraph;

//	private static final int LEFT=0;
//	private static final int RIGHT=1;
	
	public AbstractSBGNPathwayGraph(MasterObject pathway) 
	{
		super();
		reactionGraph=new Graph();

		name=pathway.getFirstValue("NAME");
		if(pathway.hasMember("ORGANISM"))
			organism=pathway.getMembers("ORGANISM").get(0).getFirstValue("NAME");
		stepGraph=new Graph();
	}
	
	public AbstractSBGNPathwayGraph() 
	{
		super();
		reactionGraph=new Graph();
		stepGraph=new Graph();
	}


	/**
	 * @return the organism
	 */
	public String getOrganism() {
		return organism;
	}

	/**
	 * @param organism the organism to set
	 */
	public void setOrganism(String organism) {
		this.organism = organism;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the reactionGraph
	 */
	public Graph getReactionGraph() {
		return reactionGraph;
	}


	
	
	
	
}