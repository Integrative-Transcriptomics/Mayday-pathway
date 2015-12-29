package mayday.pathway.sbgn.processdiagram.entitypool;

import java.awt.Rectangle;
import java.awt.Shape;

import mayday.core.structures.graph.Graph;
import mayday.pathway.sbgn.processdiagram.ProcessDiagram;
import mayday.pathway.sbgn.processdiagram.container.SubMap;

public class Tag extends EntityPoolNode
{
	private SubMap submap;
	private String identifier;
	
	public Tag(Graph graph, String name) 
	{
		super(graph, name);
		role="Tag";		
	}
	
	@Override
	public Shape getCloneMarker() 
	{
		return new Rectangle(0,0,0,0);		
	}

	@Override
	public Shape getGlyph() 
	{
		return ProcessDiagram.getGlyph(ProcessDiagram.TAG_ROLE); 
	}

	/**
	 * @return the submap
	 */
	public SubMap getSubmap() {
		return submap;
	}

	/**
	 * @param submap the submap to set
	 */
	public void setSubmap(SubMap submap) {
		this.submap = submap;
	}

	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	
	
	
}
