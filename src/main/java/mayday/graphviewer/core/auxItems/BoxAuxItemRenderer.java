
package mayday.graphviewer.core.auxItems;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

public class BoxAuxItemRenderer extends ShapeAuxItemRenderer {
	
	@Override
	public void paintAuxItem(Graphics2D g, Rectangle bounds, String text,Color lineColor, Color fillColor)
	{
		Rectangle2D shape=new Rectangle2D.Float(bounds.x, bounds.y, bounds.width, bounds.height);		
		paintShapeAuxItem(g, shape, text, lineColor, fillColor);

	}

}
