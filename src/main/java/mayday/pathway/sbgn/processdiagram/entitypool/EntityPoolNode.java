package mayday.pathway.sbgn.processdiagram.entitypool;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import mayday.core.structures.graph.Graph;
import mayday.pathway.sbgn.graph.SBGNNode;
import mayday.pathway.sbgn.processdiagram.container.Compartment;

public abstract class EntityPoolNode extends SBGNNode
{
	/** Compartment the entity is located in, if null, the default compartment is used */ 
	private Compartment compartment;
	/** Units of information attached. can be any number. */
	private List<UnitOfInformation> unitsOfInformation;
	/** Denotes whether this Node is a clone */
	private boolean cloneMarker;
	private String cloneMarkerText;
	
	public EntityPoolNode(Graph graph, String name) 
	{
		super(graph, name);
		unitsOfInformation=new ArrayList<UnitOfInformation>();
		cloneMarker=false;		
	}
	
	public Shape getCloneMarker()
	{
		return defaultCloneMarker();
	}

	/**
	 * @return the compartment
	 */
	public Compartment getCompartment() {
		return compartment;
	}

	/**
	 * @param compartment the compartment to set
	 */
	public void setCompartment(Compartment compartment) {
		this.compartment = compartment;
	}

	/**
	 * @return the cloneMarker
	 */
	public boolean isCloneMarker() {
		return cloneMarker;
	}

	/**
	 * @param cloneMarker the cloneMarker to set
	 */
	public void setCloneMarker(boolean cloneMarker) 
	{
		this.cloneMarker = cloneMarker;
	}

	/**
	 * @return the unitsOfInformation
	 */
	public List<UnitOfInformation> getUnitsOfInformation() {
		return unitsOfInformation;
	}
	
	public void addUnitOfInformation(UnitOfInformation unit)
	{
		unitsOfInformation.add(unit);
	}
	
	public void addUnitOfInformation(String prefix, String annotation)
	{
		unitsOfInformation.add(new UnitOfInformation(prefix,annotation));
	}

	/**
	 * @return the cloneMarkerText
	 */
	public String getCloneMarkerText() {
		return cloneMarkerText;
	}

	/**
	 * @param cloneMarkerText the cloneMarkerText to set
	 */
	public void setCloneMarkerText(String cloneMarkerText) {
		this.cloneMarkerText = cloneMarkerText;
	}

	/**
	 * @param unitsOfInformation the unitsOfInformation to set
	 */
	public void setUnitsOfInformation(List<UnitOfInformation> unitsOfInformation) {
		this.unitsOfInformation = unitsOfInformation;
	}
	
	protected Area defaultCloneMarker()
	{
		Area cloneMarkerGlyph=new Area(getGlyph());
		Rectangle r=getGlyph().getBounds();
		Area b=new Area(new Rectangle2D.Float(r.x,(int)(0.80*r.height),r.width,(int)(0.20*r.height)));
		cloneMarkerGlyph.intersect(b);
		
		return cloneMarkerGlyph;
	}
	
	
	
}
