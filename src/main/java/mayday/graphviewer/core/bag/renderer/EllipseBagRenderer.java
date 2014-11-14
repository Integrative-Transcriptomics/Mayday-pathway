package mayday.graphviewer.core.bag.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import mayday.graphviewer.core.bag.BagComponent;
import mayday.graphviewer.core.bag.ComponentBag;

public class EllipseBagRenderer implements BagRenderer {

	@Override
	public Shape getBoundingShape(BagComponent comp, ComponentBag bag) 
	{
		Rectangle rect= bag.getBoundingRect();
		rect.x-=5;
		rect.y-=5;
		rect.width+=10;
		rect.height+=10;
		
		double w= (Math.sqrt(2.0)*rect.width/2.0+rect.getCenterX())-rect.getCenterX();
		double h= (Math.sqrt(2.0)*rect.height/2.0+rect.getCenterY())-rect.getCenterY();
		Rectangle res=new Rectangle( 
				(int)(rect.getCenterX()-w),
				(int)(rect.getCenterY()-h),
				(int)(2*w),
				(int)(2*h));
		
		return res;		
	}

	@Override
	public boolean hideComponents() 
	{			return false;
	}

	@Override
	public boolean hideTitleBar() 
	{	
		return true;
	}

	@Override
	public void paint(Graphics2D g, BagComponent comp, ComponentBag bag, boolean isSelected) 
	{
		Rectangle rect=getBoundingShape(comp, bag).getBounds();		
		Shape s=new Ellipse2D.Double(5, 5, rect.width-10, rect.height-10);
		g.setColor(Color.white);
		g.fill(s);
		g.setColor(Color.black);
		g.setStroke(new BasicStroke(4.0f));
		g.draw(s);
		
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
		Rectangle2D sr=g.getFontMetrics().getStringBounds(bag.getName(), g);
		g.drawString(bag.getName(),(int) (s.getBounds2D().getCenterX()-sr.getWidth()/2), 30);
	}

}
