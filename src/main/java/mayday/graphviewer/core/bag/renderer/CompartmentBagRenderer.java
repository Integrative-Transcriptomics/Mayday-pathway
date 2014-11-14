package mayday.graphviewer.core.bag.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

import mayday.graphviewer.core.bag.BagComponent;
import mayday.graphviewer.core.bag.ComponentBag;

public class CompartmentBagRenderer implements BagRenderer
{
	@Override
	public boolean hideComponents() 
	{
		return false;
	}
	
	@Override
	public void paint(Graphics2D g, BagComponent comp, ComponentBag bag, boolean isSelected) 
	{	
		Font f=g.getFont();
		Rectangle bounds=comp.drawableRect();		
		g.setColor(Color.white);
		g.fillRect(0, 0, bounds.width, bounds.height);
		g.setColor(Color.black);
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
		g.drawString(bag.getName(), 10, 25);
		// draw thick border
		if(isSelected)
			g.setColor(Color.red);
		g.setStroke(new BasicStroke(4.0f));
		g.drawRoundRect(5, 5, bounds.width-10, bounds.height-10, 20, 20);
		g.setFont(f);
	}
	
	@Override
	public Shape getBoundingShape(BagComponent comp, ComponentBag bag) 
	{
		Rectangle rect= bag.getBoundingRect();
		rect.x-=10;
		rect.y-=35;
		rect.width+=20;
		rect.height+=45;
		return rect;
	}
	
	@Override
	public boolean hideTitleBar() 
	{
		return true;
	}
	
}
