package mayday.pathway.sbgn.processdiagram.container;

import java.awt.Shape;
import java.util.List;

import mayday.core.structures.graph.Graph;
import mayday.pathway.sbgn.graph.SBGNNode;
import mayday.pathway.sbgn.processdiagram.entitypool.UnitOfInformation;

/**
 * A Compartment. 
 * @author Stephan Symons
 *
 */
public class Compartment extends SBGNNode
{
	/** Units of information attached. Can be any number. */
	private List<UnitOfInformation> unitsOfInformation;

	public Compartment(Graph graph, String name) 
	{
		super(graph, name);
	}

	@Override
	public Shape getGlyph() {
		return null;
	}

	/**
	 * @return the unitsOfInformation
	 */
	public List<UnitOfInformation> getUnitsOfInformation() {
		return unitsOfInformation;
	}

	/**
	 * @param unitsOfInformation the unitsOfInformation to set
	 */
	public void setUnitsOfInformation(List<UnitOfInformation> unitsOfInformation) {
		this.unitsOfInformation = unitsOfInformation;
	}
	
	




}
