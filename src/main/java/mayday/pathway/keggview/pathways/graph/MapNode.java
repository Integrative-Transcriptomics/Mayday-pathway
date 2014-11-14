package mayday.pathway.keggview.pathways.graph;

import mayday.core.structures.graph.Graph;
import mayday.pathway.keggview.kegg.pathway.Entry;

public class MapNode extends PathwayNode
{
	private String pathwayId;
	
	public MapNode(PathwayGraph graph, Entry e)
	{
		super(graph);
		fromEntry(e);
		if(e.getGraphics().getName()!=null)	setName(e.getGraphics().getName());
		pathwayId=e.getName();
		pathwayId=pathwayId.split("\\D+")[1];
		role="Pathway";
	}

	/**
	 * @return the pathwayId
	 */
	public String getPathwayId() {
		return pathwayId;
	}

	/**
	 * @param pathwayId the pathwayId to set
	 */
	public void setPathwayId(String pathwayId) {
		this.pathwayId = pathwayId;
	}
	
	public int getImportance(Graph g)
	{
		return 0;
	}
	

}
