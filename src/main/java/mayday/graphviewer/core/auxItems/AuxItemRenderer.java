package mayday.graphviewer.core.auxItems;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public interface AuxItemRenderer 
{
	public void paintAuxItem(Graphics2D g, Rectangle bounds, String text, Color lineColor, Color fillColor);
}
