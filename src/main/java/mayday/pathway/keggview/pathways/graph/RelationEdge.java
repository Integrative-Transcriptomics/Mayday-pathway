package mayday.pathway.keggview.pathways.graph;


public class RelationEdge extends PathwayEdge 
{

	private String type;	
	
	/**
	 * Constructor connecting two nodes
	 * @param source
	 * @param target
	 */
	public RelationEdge(PathwayNode source, PathwayNode target)
	{		
		super(source,target);
	}
	
	/**
	 * Constructor connecting two nodes. Additionally allows to set a label.
	 * @param source
	 * @param target
	 * @param label
	 */
	public RelationEdge(PathwayNode source, PathwayNode target, String label)
	{		
		super(source,target);
		setName(label);
	}
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	

}
