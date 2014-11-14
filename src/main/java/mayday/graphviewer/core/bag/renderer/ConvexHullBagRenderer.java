package mayday.graphviewer.core.bag.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import mayday.graphviewer.algorithm.QuickHull2D;
import mayday.graphviewer.core.bag.BagComponent;
import mayday.graphviewer.core.bag.ComponentBag;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.renderer.RendererTools;

public class ConvexHullBagRenderer implements BagRenderer
{
	@Override
	public void paint(Graphics2D g, BagComponent comp, ComponentBag bag,
			boolean isSelected) 
	{
		AffineTransform at=g.getTransform();
		QuickHull2D sc=new QuickHull2D();
		List<CanvasComponent> hull=sc.getConvexHull(new ArrayList<CanvasComponent>(bag.getComponents()),true);
		Polygon p=new Polygon();
//		int minx=Integer.MAX_VALUE;
//		int miny=Integer.MAX_VALUE;
//		for(CanvasComponent c:bag.getComponents())
//		{
//			if(c.getX() < minx)
//				minx=c.getX();
//			if(c.getY() < miny)
//				miny=c.getY();
//			
//		}
		for(CanvasComponent c:hull)
		{
//			p.addPoint((int)c.getBounds().getCenterX()-minx, (int) (c.getBounds().getCenterY()-miny+c.getHeight()/2));
			p.addPoint((int)c.getBounds().getCenterX(), (int) (c.getBounds().getCenterY()));
		}
//		return p;
//		
//		
//		Shape p=getBoundingShape(comp,bag);
		Color c=RendererTools.alphaColor(bag.getColor(), 128);
		Rectangle2D r=getBoundingShape(comp, bag).getBounds2D();
		Rectangle2D s=p.getBounds2D();
		
		
		double sx=(r.getWidth()-3)/s.getWidth();
		double sy=(r.getHeight()-3)/s.getHeight();
	
		g.scale(sx,sy);
				
		g.translate(-r.getX()-(s.getX()-r.getX()), -r.getY()-(s.getY()-r.getY()));
		g.setColor(c);
		
		g.fill(p);
		g.setColor(Color.black);
		g.setStroke(new BasicStroke(4.0f));
		g.draw(p);
		if(isSelected)
		{
			Color cs=new Color(
					bag.getColor().getRed(), 
					bag.getColor().getGreen(), 
					bag.getColor().getBlue());
			g.setColor(cs);
			g.draw(p);
		}
		g.setTransform(at);
	}
	
	@Override
	public boolean hideComponents() 
	{
		return false;
	}
	
	@Override
	public Shape getBoundingShape(BagComponent comp, ComponentBag bag) 
	{
		
		Rectangle rect= bag.getBoundingRect();
		rect.x-=15;
		rect.y-=15;
		rect.width+=30;
		rect.height+=30;
		return rect;
	}
	
	@Override
	public boolean hideTitleBar() 
	{
		return true;
	}
}
