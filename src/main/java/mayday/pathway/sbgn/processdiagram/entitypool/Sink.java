package mayday.pathway.sbgn.processdiagram.entitypool;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import mayday.core.structures.graph.Graph;

public class Sink extends EntityPoolNode 
{
	public Sink(Graph graph, String name) 
	{
		super(graph, name);	
		role="Sink";
	}

	@Override
	public Shape getCloneMarker() 
	{
		return new Rectangle2D.Float(0,0,0,0);
	}

	@Override
	public Shape getGlyph() 
	{
		Path2D res=new Path2D.Float();
		res.append(new Ellipse2D.Float(0,0,50,50),false);
		res.append(new Line2D.Float(50,0,0,50), false);
		return res;		
	}
}
