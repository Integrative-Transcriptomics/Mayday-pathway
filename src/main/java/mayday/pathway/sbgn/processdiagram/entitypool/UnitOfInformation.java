package mayday.pathway.sbgn.processdiagram.entitypool;

import java.awt.geom.Rectangle2D;

public class UnitOfInformation 
{
	/** Parent EPN */
	private EntityPoolNode owningEPN;
	/** prefix */
	private String prefix;
	/** annotation to be displayed */
	private String annotation;
	
	protected static final Rectangle2D glyph=new Rectangle2D.Float(0,0,20,10);
	
	public UnitOfInformation(String prefix, String annotation) 
	{
		setPrefix(prefix);
		setAnnotation(annotation);
	}
	
	/**
	 * @return the owningEPN
	 */
	public EntityPoolNode getOwningEPN() {
		return owningEPN;
	}

	/**
	 * @param owningEPN the owningEPN to set
	 */
	public void setOwningEPN(EntityPoolNode owningEPN) {
		this.owningEPN = owningEPN;
	}

	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) 
	{
		this.prefix = prefix;		
	}

	/**
	 * @return the annotation
	 */
	public String getAnnotation()
	{
		return annotation;
	}

	/**
	 * @param annotation the annotation to set
	 */
	public void setAnnotation(String annotation) 
	{
		this.annotation = annotation;
		
	}
	
	public String toString()
	{
		return prefix+":"+annotation;
	}
	
}
