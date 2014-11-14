package mayday.pathway.sbgn.processdiagram.entitypool;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import mayday.core.structures.graph.Graph;

/**
 * @author symons
 *
 */
public class UnspecifiedEntity extends EntityPoolNode
{
	public static final String ROLE="unspecified";
	protected static Area cloneMarkerGlyph;
	private static final Shape glyph=new Ellipse2D.Float(0,0,80,50);
	
	public UnspecifiedEntity(Graph graph, String name) 
	{
		super(graph, name);
		setRole(ROLE);
	}

	@Override
	public Shape getGlyph() 
	{
		return glyph;
	}
	
	@Override
	public Shape getCloneMarker() 
	{
//		return new Rectangle2D.Float(0,40.0f,80,10);
		if(cloneMarkerGlyph==null)
		{
			cloneMarkerGlyph=new Area(glyph);
			Area b=new Area(new Rectangle2D.Float(0,40,80,15));
			cloneMarkerGlyph.intersect(b);
		}
		return cloneMarkerGlyph;
	}

}
