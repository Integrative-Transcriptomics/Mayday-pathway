package mayday.graphviewer.core.edges;

import java.awt.Container;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Edge;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.edges.router.AbstractEdgeRouter;
import mayday.vis3.graph.edges.router.EdgePoints;
import mayday.vis3.graph.model.GraphModel;

public class RightTransitive  extends AbstractEdgeRouter
{
	public Path2D routeEdge(Edge e, Container container, GraphModel model) 
	{
		if(e.getSource()==e.getTarget())
		{
			return returnEdge(model.getComponent(e.getSource()), model.getComponent(e.getTarget()));
		}
		EdgePoints points=getAdjustedPoints(model.getComponent(e.getSource()), model.getComponent(e.getTarget()));
		Path2D path=new Path2D.Double();
		
		path.moveTo(points.source.getX(),points.source.getY());
		// calculate support point
		double px=Math.min(points.source.getX(), points.target.getX());
		px+= Math.min(model.getComponent(e.getSource()).getWidth(), model.getComponent(e.getTarget()).getWidth());
//		px+= Math.abs(points.source.getY()-points.target.getY()) ;
//		px+=Math.log( Math.abs(points.source.getY()-points.target.getY()) )/Math.log(2.0d)*10;
		px+=Math.abs(points.source.getY()-points.target.getY())/5;
//		double py=Math.min(points.source.getY(), points.target.getY()) + Math.abs(points.source.getY()-points.target.getY())/2.0d;
		
		
//		path.quadTo(px,points.source.getY(), px,py);
//		path.quadTo(px,points.target.getY(),  points.target.getX(), points.target.getY());
//		path.quadTo(px, py, points.target.getX(), points.target.getY());
		path.curveTo(px,points.source.getY(),px, points.target.getY(),    points.target.getX(), points.target.getY());
		
		path.moveTo(points.target.getX(), points.target.getY());
		path.closePath();
		return path;
	}
	
	public Point2D getSupportPoint(CanvasComponent source, CanvasComponent target) 
	{
		EdgePoints points=getAdjustedPoints(source, target);
		double px=Math.min(points.source.getX(), points.target.getX());
		px+= Math.min(source.getWidth(), target.getWidth());
		px+=Math.abs(points.source.getY()-points.target.getY())/5;
		double py=Math.min(points.source.getY(), points.target.getY()) + Math.abs(points.source.getY()-points.target.getY())/2.0d;
		return new Point2D.Double(px, py);
	}
	
	@Override
	public EdgePoints getAdjustedPoints(CanvasComponent source,	CanvasComponent target) 
	{
		Point2D s=new Point2D.Double(source.getBounds().getMaxX(), source.getBounds().getCenterY());
		Point2D t=new Point2D.Double(target.getBounds().getMaxX(), target.getBounds().getCenterY());
		EdgePoints points=new EdgePoints(s, t);
		return points;
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.EdgeRouter.RightTransitive",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Transtive edges (right)",
				"Transitive (right)"				
		);
		return pli;
	}
	
}


