package mayday.graphviewer.core.bag.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Node;
import mayday.graphviewer.core.Utilities;
import mayday.graphviewer.core.bag.BagComponent;
import mayday.graphviewer.core.bag.ComponentBag;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.NodeComponent;

public class SubmapBagRenderer implements BagRenderer
{

	private static final int PORT_POINT = 15;
	private static final int PORT_BODY = 40;

	@Override
	public boolean hideComponents() 
	{
		return false;
	}

	@Override
	public void paint(Graphics2D g, BagComponent comp, ComponentBag bag, boolean isSelected) 
	{	
		Rectangle bounds=comp.drawableRect();		
		g.setColor(Color.white);
		g.fillRect(0, 0, bounds.width, bounds.height);
		// draw border
		g.setColor(Color.black);
		g.setStroke(new BasicStroke(3.0f));
		g.drawRect(2, 2, bounds.width-4, bounds.height-4);

		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
		Rectangle2D r=g.getFontMetrics().getStringBounds(bag.getName(), g);
		g.drawString(bag.getName(), 
				(int) (bounds.width/2.0 -(r.getWidth()/2.0)),  
				(int) (bounds.height/2.0 +(r.getHeight()/2.0)));


		Rectangle cb=comp.getBounds();
		g.setStroke(new BasicStroke(1.0f));
		for(CanvasComponent cc: bag)
		{
			Node n=((NodeComponent)cc).getNode();
			boolean isConnected=false;
			
			if(cc.getWidth()==0 || cc.getHeight()==0)
			{
				Point p0=new Point(cc.getX()-cb.x,cc.getY()-cb.y);
				drawTerminal(g, cc.getLabel(), p0, cb);
				continue;
			}
			
			for(Edge e: bag.getModel().getGraph().getAllEdges(n))
			{
				if(isConnected)
					break;
				CanvasComponent cs=bag.getModel().getComponent(e.getSource());
				CanvasComponent ct=bag.getModel().getComponent(e.getTarget());
				
				Point2D p=null;
				if(!bag.contains(cs) || !bag.contains(ct))
				{
					isConnected=true;
					p=getIntersection(cs, ct, cb);
				}
				if(p==null)
				{
					continue;
				}
				
				Point p0=new Point((int)p.getX()-cb.x,(int)p.getY()-cb.y);
				
				if(cc.getWidth() >0 && cc.getHeight() > 0)
				{
					cc.setSize(0, 0);
					cc.setLocation((int)p.getX(),(int) p.getY());
				}				
				drawTerminal(g, cc.getLabel(), p0, cb);
			}
			
			if(!isConnected && cc.isVisible())
			{
				cc.setVisible(false);
			}
				
		}
	}
	
	private void drawTerminal(Graphics2D g, String l, Point p0, Rectangle cb)
	{
		g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
		AffineTransform tbak=g.getTransform();
		if(p0.getX()==2)
		{
			Polygon pol=new Polygon();
			pol.addPoint(p0.x,p0.y-PORT_POINT);
			pol.addPoint(p0.x+PORT_BODY,p0.y-PORT_POINT);
			pol.addPoint(p0.x+PORT_POINT+PORT_BODY,p0.y);
			pol.addPoint(p0.x+PORT_BODY,p0.y+PORT_POINT);
			pol.addPoint(p0.x,p0.y+PORT_POINT);
			pol.addPoint(p0.x,p0.y-PORT_POINT);
			g.drawPolygon(pol);
			g.drawString(l, p0.x, p0.y+5);
		}
		if(p0.getX()+2==cb.getWidth())
		{
			Polygon pol=new Polygon();
			pol.addPoint(p0.x,p0.y-PORT_POINT);
			pol.addPoint(p0.x-PORT_BODY,p0.y-PORT_POINT);
			pol.addPoint(p0.x-PORT_POINT-PORT_BODY,p0.y);
			pol.addPoint(p0.x-PORT_BODY,p0.y+PORT_POINT);
			pol.addPoint(p0.x,p0.y+PORT_POINT);
			pol.addPoint(p0.x,p0.y-PORT_POINT);
			g.drawPolygon(pol);
			g.drawString(l, p0.x-PORT_BODY-PORT_POINT/2, p0.y+5);
		}				
		if(p0.getY()==2)
		{
			Polygon pol=new Polygon();
			pol.addPoint(p0.x-PORT_POINT,p0.y);					
			pol.addPoint(p0.x-PORT_POINT,p0.y+PORT_BODY);
			pol.addPoint(p0.x,p0.y+PORT_BODY+PORT_POINT);
			pol.addPoint(p0.x+PORT_POINT,p0.y+PORT_BODY);
			pol.addPoint(p0.x+PORT_POINT,p0.y);		
			pol.addPoint(p0.x-PORT_POINT,p0.y);	
			g.drawPolygon(pol);
			g.rotate(Math.PI/2, p0.x, p0.y+PORT_POINT);
			g.drawString(l, p0.x-PORT_POINT, p0.y+PORT_POINT+3);
		}
		if(p0.getY()+2==cb.getHeight())
		{
			Polygon pol=new Polygon();
			pol.addPoint(p0.x-PORT_POINT,p0.y);					
			pol.addPoint(p0.x-PORT_POINT,p0.y-PORT_BODY);
			pol.addPoint(p0.x,p0.y-PORT_BODY-PORT_POINT);
			pol.addPoint(p0.x+PORT_POINT,p0.y-PORT_BODY);
			pol.addPoint(p0.x+PORT_POINT,p0.y);		
			pol.addPoint(p0.x-PORT_POINT,p0.y);	
			g.drawPolygon(pol);
			
			g.rotate(Math.PI/2, p0.x, p0.y);
			g.drawString(l, p0.x-PORT_BODY-PORT_POINT/2, p0.y+PORT_POINT/2-3);
			
		}
		g.setTransform(tbak);
	}
			
	private Point2D getIntersection(CanvasComponent here, CanvasComponent away, Rectangle bounds)
	{
		Line2D l1=new Line2D.Double(here.getBounds().getCenterX(),here.getBounds().getCenterY(), 
				away.getBounds().getCenterX(),away.getBounds().getCenterY() );

		Line2D l2=new Line2D.Double(bounds.getMinX(), bounds.getMinY(), bounds.getMinX(), bounds.getMaxY());
		if(l1.intersectsLine(l2))
			return Utilities.getIntersectionPoint(l1, l2);
			
		l2=new Line2D.Double(bounds.getMaxX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY());
		if(l1.intersectsLine(l2))
			return Utilities.getIntersectionPoint(l1, l2);
		
		l2=new Line2D.Double(bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMinY());
		if(l1.intersectsLine(l2))
			return Utilities.getIntersectionPoint(l1, l2);
		
		
		l2=new Line2D.Double(bounds.getMinX(), bounds.getMaxY(), bounds.getMaxX(), bounds.getMaxY());
		if(l1.intersectsLine(l2))
			return Utilities.getIntersectionPoint(l1, l2);
		
		return null;
	}

 

	@Override
	public Shape getBoundingShape(BagComponent comp, ComponentBag bag) 
	{
		Rectangle rect= bag.getBoundingRect();
		if(rect.width==0 || rect.height==0)
			return new Rectangle(rect.x, rect.y,150,150);
		rect.x+=20;
		rect.y+=10;
		rect.width-=30;
		rect.height-=20;
		
		rect.x-=2;
		rect.y-=2;
		rect.width+=4;
		rect.height+=4;
		
		return rect;
	}

	@Override
	public boolean hideTitleBar() 
	{
		return true;
	}
}
