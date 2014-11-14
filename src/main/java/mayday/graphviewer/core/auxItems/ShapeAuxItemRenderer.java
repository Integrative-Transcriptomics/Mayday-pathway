package mayday.graphviewer.core.auxItems;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public abstract class ShapeAuxItemRenderer implements AuxItemRenderer 
{
	
	protected void paintShapeAuxItem(Graphics2D g, Shape shape, String text, Color lineColor, Color fillColor) 
	{
		g.setColor(fillColor);
		g.fill(shape);
		g.setColor(lineColor);
		g.setStroke(new BasicStroke(2.0f));
		g.draw(shape);
		Rectangle2D sb=g.getFontMetrics().getStringBounds(text, g);
		
		g.drawString(text, 
				(int)(shape.getBounds().getCenterX()-sb.getWidth()/2+1),
				(int)(shape.getBounds().getCenterY()+sb.getHeight()/2)-2);
		
	}
}
