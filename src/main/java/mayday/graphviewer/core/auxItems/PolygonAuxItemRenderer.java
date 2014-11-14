package mayday.graphviewer.core.auxItems;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;

import mayday.vis3.graph.renderer.RendererTools;

public class PolygonAuxItemRenderer extends ShapeAuxItemRenderer {

	private int sides=5;
	
	public PolygonAuxItemRenderer(int sides) 
	{
		this.sides = sides;
	}

	public int getSides() 
	{
		return sides;
	}

	public void setSides(int sides) 
	{
		this.sides = sides;
	}

	@Override
	public void paintAuxItem(Graphics2D g, Rectangle bounds, String text,Color lineColor, Color fillColor) 
	{
		Polygon shape=RendererTools.drawPolygon(bounds, sides);	
		for(int i=0; i!= shape.npoints;++i)
		{
			shape.xpoints[i]+=bounds.x;
			shape.ypoints[i]+=bounds.y;
		}
		paintShapeAuxItem(g, shape, text, lineColor, fillColor);
	}

}
