package mayday.graphviewer.core.bag.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

import mayday.graphviewer.core.bag.BagComponent;
import mayday.graphviewer.core.bag.ComponentBag;
import mayday.vis3.graph.renderer.RendererTools;

public class DefaultBagRenderer implements BagRenderer {

	@Override
	public void paint(Graphics2D g, BagComponent comp, ComponentBag bag,
			boolean isSelected) 
	{
		drawBackground(g, comp, bag, isSelected);
		drawBounds(g, comp, bag, isSelected);
		
	}
	
	@Override
	public boolean hideComponents() 
	{
		return false;
	}
	
	public static void drawBackground(Graphics2D g, BagComponent comp, ComponentBag bag, boolean isSelected)
	{
		Rectangle bounds=comp.drawableRect();
		Color c=RendererTools.alphaColor(bag.getColor(), 128);
		g.setColor(c);
		g.fillRoundRect(0, 0,bounds.width, bounds.height, 20, 20);
		
	}
	public static void drawBounds(Graphics2D g, BagComponent comp, ComponentBag bag, boolean isSelected)
	{
		Rectangle bounds=comp.drawableRect();
		g.setStroke(new BasicStroke(2f));
		g.setColor(Color.black);		
		if(isSelected)
		{
			g.setColor(bag.getColor());			
		}			
		g.drawRoundRect(1, 1, bounds.width-3, bounds.height-3, 20, 20);		
	}
	
	@Override
	public Shape getBoundingShape(BagComponent comp, ComponentBag bag) 
	{
		return bag.getBoundingRect();
	}
	
	@Override
	public boolean hideTitleBar() 
	{
		return false;
	}
	

}
