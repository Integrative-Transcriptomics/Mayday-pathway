package mayday.graphviewer.algorithm;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.vis3.graph.components.CanvasComponent;

public class QuickHull2D 
{
	private Map<Point2D,CanvasComponent> comps=new HashMap<Point2D, CanvasComponent>();
	
	public List<CanvasComponent> getConvexHull(List<CanvasComponent> components,boolean ensure)
	{
		int n=components.size();
		List<CanvasComponent> res=new ArrayList<CanvasComponent>();
		if(n < 3)
		{
			res.addAll(components);
			return res;
		}
		List<Point2D> points=new ArrayList<Point2D>();
		for(CanvasComponent comp:components)
		{
			points.add(comp.getLocation());
			comps.put(comp.getLocation(), comp);
		}
		List<Point2D> resP=getConvexHull(points);
		
		for(Point2D p:resP)
		{
			res.add(comps.get(p));
		}
		return res;
	}
	
	public List<Point2D> getConvexHull(List<Point2D> points)
	{
		if(points.size() <=3)
			return points;
		
		List<Point2D> hull=new ArrayList<Point2D>();
		//find left/rightmost point
		Point2D left=points.get(0);
		Point2D right=points.get(0);
		for(Point2D p:points)
		{
			if(p.getX() > right.getX())
				right=p;
			if(p.getX() < left.getX())
				left=p;
		}
		
		
		Line2D lr=new Line2D.Double(left,right);
		Line2D rl=new Line2D.Double(right,left);
		List<Point2D> leftPoints=new ArrayList<Point2D>();
		List<Point2D> rightPoints=new ArrayList<Point2D>();
		
		
		for(Point2D p: points)
		{
			if(p==left || p==right) continue;
			int i=lr.relativeCCW(p);
			if(i <= 0)
			{
				leftPoints.add(p);
				continue;
			}
			
			i=rl.relativeCCW(p);
			if(i <= 0)
			{
				rightPoints.add(p);
				
			}
		}
		hull.add(left);
		hull.addAll(findHull(leftPoints, lr));
		hull.add(right);
		hull.addAll(findHull(rightPoints, rl));
		
		return hull;
	}
	
	private List<Point2D> findHull(List<Point2D> points, Line2D line)
	{
		if(points.isEmpty())
			return new ArrayList<Point2D>();
		// find farthest point
		Point2D c=points.get(0);
		double d=line.ptSegDist(c);
		for(Point2D p:points)
		{
			double dn=line.ptSegDist(p);
			if(dn > d )
			{
				d=dn;
				c=p;
			}
		}
		// re-partition
		Point2D p=line.getP1();
		Point2D q=line.getP2();
		Line2D pc=new Line2D.Double(p,c);
		Line2D cq=new Line2D.Double(c,q);
		
		List<Point2D> s1=new ArrayList<Point2D>();
		List<Point2D> s2=new ArrayList<Point2D>();
		
		for(Point2D pp:points)
		{
			if(pp==c) continue;
			int i=pc.relativeCCW(pp);
			if(i <= 0)
			{
				s1.add(pp);
				continue;
			}
			
			i=cq.relativeCCW(pp);
			if(i <= 0)
			{
				s2.add(pp);				
				continue;
			}
		}
		List<Point2D> res=new ArrayList<Point2D>();
		res.addAll(findHull(s1, pc));
		res.add(c);
		res.addAll(findHull(s2, cq));
		
		return res;
				
	}
}
