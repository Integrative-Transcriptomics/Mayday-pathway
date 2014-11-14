package mayday.pathway.sbgn.processdiagram.entitypool;

import java.awt.Shape;
import java.awt.geom.Area;

import mayday.core.structures.graph.Graph;
import mayday.pathway.sbgn.processdiagram.ProcessDiagram;

public class Macromolecule extends StatefulEntityPoolNode
{
	/** The macromolecule type */
	private String macromoleculeType;
	
	protected static Area cloneMarkerGlyph;
	
	public Macromolecule(Graph graph, String name)
	{
		super(graph,name);
		role=ProcessDiagram.MACROMOLECULE_ROLE;
	}
	
	@Override
	public Shape getGlyph()
	{
		return ProcessDiagram.getGlyph(role);
	}

	/**
	 * @return the macromoleculeType
	 */
	public String getMacromoleculeType() {
		return macromoleculeType;
	}

	/**
	 * @param macromoleculeType the macromoleculeType to set
	 */
	public void setMacromoleculeType(String macromoleculeType) 
	{
		this.macromoleculeType = macromoleculeType;
	}
	
}
