package mayday.pathway.viewer.canvas;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Graphs;
import mayday.core.structures.graph.Node;
import mayday.pathway.sbgn.graph.AbstractSBGNPathwayGraph;
import mayday.pathway.sbgn.processdiagram.ProcessDiagram;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.NodeComponent;
import mayday.vis3.graph.layout.CanvasLayouter;
import mayday.vis3.graph.model.GraphModel;

public class SBGNLayouterWrapper implements CanvasLayouter
{

	private int spacer=200;

	private Map<Node,Boolean> isPlaced=new HashMap<Node, Boolean>();
	private CanvasLayouter layouter;

	private Dimension reactionSpace=new Dimension(400,350);

	private Map<Node,Angles> angleMap=new HashMap<Node, Angles>();

	public SBGNLayouterWrapper(CanvasLayouter layouter)
	{
		this.layouter=layouter;
	}

	public void layout(Container container, Rectangle bounds, GraphModel model) 
	{
		Graph graph=model.getGraph();
		if(graph instanceof AbstractSBGNPathwayGraph)
		{
			AbstractSBGNPathwayGraph g=(AbstractSBGNPathwayGraph)graph;
			Graph reactionSubgraph = ((AbstractSBGNPathwayGraph)graph).getReactionGraph();
			HelperModel reactionSubModel= new HelperModel(reactionSubgraph);
			layouter.layout(container, bounds, reactionSubModel);		

			for(Node n: reactionSubgraph.getNodes())
			{
				placeReaction(n, reactionSubModel.getComponent(n), model);				
			}
			for(Node n: reactionSubgraph.getNodes())
			{
				placeConnectingCompounds(g,n,model );			
			}
			for(Node n: reactionSubgraph.getNodes())
			{
				Angles angle=new Angles();
				double d=Double.MAX_VALUE;
				// calculate product angle as mean angle between this and following reactions. 	
				double a=0.0;
				if(reactionSubgraph.getOutDegree(n)!=0)
				{
					for(Node np:reactionSubgraph.getOutNeighbors(n))
					{
						double dist=model.getComponent(n).getLocation().distance(model.getComponent(np).getLocation());
						if(dist < d)
							d=dist;
						a=+normalAngle(model.getComponent(n).getLocation(), model.getComponent(np).getLocation(),dist);
					}
					angle.productAngle=a/(reactionSubgraph.getOutDegree(n)*1.0d);
					angle.dNext=d;
				}

				d=Double.MAX_VALUE;
				a=0.0;
				if(reactionSubgraph.getInDegree(n)!=0)
				{
					for(Node np:reactionSubgraph.getInNeighbors(n))
					{

						double dist=model.getComponent(n).getLocation().distance(model.getComponent(np).getLocation());
						if(dist < d)
							d=dist;
						a=+normalAngle(model.getComponent(n).getLocation(), model.getComponent(np).getLocation(),dist);	
					}
					angle.substrateAngle=a/(reactionSubgraph.getInDegree(n)*1.0d);
					angle.dPrev=d;
				}
				if(reactionSubgraph.getOutDegree(n)==0 && reactionSubgraph.getInDegree(n)!=0)
				{
					angle.productAngle=angle.substrateAngle+Math.PI;
					angle.dNext=angle.dPrev;
				}

				if(reactionSubgraph.getOutDegree(n)!=0 && reactionSubgraph.getInDegree(n)==0)
				{
					angle.substrateAngle=angle.productAngle-Math.PI;
					angle.dPrev=angle.dNext;
				}
				angleMap.put(n,angle);

				placeConsumables(g, n, model);
				placeProducts(g, n, model);
				placeEnzymes(g, n, model);
			}
		}
	}

	private void placeReaction(Node tCurr, Component c, GraphModel model)
	{		
		Rectangle bounds=c.getBounds();				
		Component comp=model.getComponent(tCurr);
		comp.setLocation( (int)(bounds.getCenterX()-comp.getWidth()/2), (int)(bounds.getCenterY()-comp.getHeight()/2) );
		isPlaced.put(tCurr, true);
	}

	private void placeProducts(Graph g, Node tCurr, GraphModel model)
	{		
		double productAngle=angleMap.get(tCurr).productAngle;		
		double angleRange=Math.toRadians(80);		
		double aStart=productAngle-angleRange/2.0;
		Graph reactionSubgraph = ((AbstractSBGNPathwayGraph)g).getReactionGraph();
		if(reactionSubgraph.getOutDegree(tCurr)==0)
		{
			aStart=productAngle-angleRange/2.0;
		}

		int n=0;	
		for(Edge e: tCurr.getOutEdges())
		{
			if(e.getRole().equals(ProcessDiagram.PRODUCTION_ROLE))
			{
				n++;
			}
		}
		
		List<Double> angles=null;
		if(reactionSubgraph.getOutDegree(tCurr)==0)
		{
			angles=place(aStart, aStart+angleRange, n);
		}else
		{
			angles=place(aStart, aStart+angleRange, productAngle, n);
		}
		
		if(n==1)
		{
			angles.set(0, angles.get(0)+Math.toRadians(20));
		}else
		{
			if( n %2 ==1)
				n++;
		}
		int i=0;
		for(Edge e: tCurr.getOutEdges())
		{
			if(e.getRole().equals(ProcessDiagram.PRODUCTION_ROLE) )
			{
				Node node=e.getTarget();
				if(isPlaced.containsKey(node)) 
				{
					continue;					
				}						
				// place this crap at n*  
				double rotAng=angles.get(i);
				int d= (int) angleMap.get(tCurr).dNext/5*2;
				d = d > spacer?spacer:d;
				Point2D pTarget=rotate(new Point(d,0),rotAng);
				pTarget.setLocation(
						model.getComponent(tCurr).getX()+model.getComponent(tCurr).getWidth()/2.0
							-model.getComponent(node).getWidth()/2.0 + pTarget.getX(), 
						model.getComponent(tCurr).getY()+model.getComponent(tCurr).getHeight()/2.0
							-model.getComponent(node).getHeight()/2.0 + pTarget.getY());
				model.getComponent(node).setLocation((int)pTarget.getX(), (int)pTarget.getY());
				i++;
				isPlaced.put(node,true);
			}
		}
	}

	private void placeConsumables(Graph g, Node tCurr, GraphModel model)
	{
		double substrateAngle=angleMap.get(tCurr).substrateAngle;		
		double angleRange=Math.toRadians(80);		
		//		double aStart=substrateAngle-angleRange/2;	
		double aStart=substrateAngle-angleRange/2.0;
		int n=0;	

		Graph reactionSubgraph = ((AbstractSBGNPathwayGraph)g).getReactionGraph();
		if(reactionSubgraph.getOutDegree(tCurr)==0)
		{
			aStart=substrateAngle-angleRange/2;
		}

		for(Edge e: tCurr.getEdges())
		{
			if(e.getRole().equals(ProcessDiagram.CONSUMPTION_ROLE))
			{
				n++;
			}
		}
		List<Double> angles=null;
		if(reactionSubgraph.getInDegree(tCurr)==0)
		{
			angles=place(aStart, aStart+angleRange, n);
		}else{
			angles=place(aStart, aStart+angleRange,  substrateAngle,n );
		}
		
		System.out.println(angles);
		if(n==1)
		{
			angles.set(0, angles.get(0)-Math.toRadians(20));
		}

		int i=0;

		for(Edge e: tCurr.getInEdges())
		{
			if(e.getRole().equals(ProcessDiagram.CONSUMPTION_ROLE))
			{
				Node node=e.getSource();
				if(isPlaced.containsKey(node))
				{					
					continue;					
				}
				// place this crap at n*  
				double rotAng=angles.get(i);
				int d= (int) (angleMap.get(tCurr).dPrev/5.0*2.0);
				d = d > spacer?spacer:d;
				Point2D pTarget=rotate(new Point(d,0),rotAng);
				pTarget.setLocation(
						model.getComponent(tCurr).getX()+model.getComponent(tCurr).getWidth()/2.0
							-model.getComponent(node).getWidth()/2.0 + pTarget.getX(), 
						model.getComponent(tCurr).getY()+model.getComponent(tCurr).getHeight()/2.0
							-model.getComponent(node).getHeight()/2.0 + pTarget.getY());
				model.getComponent(node).setLocation((int)pTarget.getX(), (int)pTarget.getY());
				i++;
				isPlaced.put(node,true);
			}
		}
	}

	private void placeEnzymes(Graph g, Node tCurr, GraphModel model)
	{
		double enzymeAngle=(angleMap.get(tCurr).productAngle + angleMap.get(tCurr).substrateAngle)/2;		
		if(Math.abs(angleMap.get(tCurr).productAngle - angleMap.get(tCurr).substrateAngle) <= Math.PI)
		{
			enzymeAngle+=Math.PI;
		}
		
		double angleRange=Math.toRadians(120);		
		double aStart=enzymeAngle-angleRange/2;		
		int n=1;	

		for(Edge e: tCurr.getInEdges())
		{
			if(e.getRole().equals(ProcessDiagram.MODULATION_ROLE) || e.getRole().equals(ProcessDiagram.CATALYSIS_ROLE))
			{
				n++;
			}
		}		
		int i=1;

		for(Edge e: tCurr.getInEdges())
		{
			if(e.getRole().equals(ProcessDiagram.MODULATION_ROLE) || e.getRole().equals(ProcessDiagram.CATALYSIS_ROLE))
			{
				Node node=(e).getSource();
				if(isPlaced.containsKey(node)) 
					continue;
				// place this crap at n*  
				double rotAng=aStart+i*(1.0/n)*angleRange;//+substrateAngle;
				Point2D pTarget=rotate(new Point(150,0),rotAng);
				
				pTarget.setLocation(
						model.getComponent(tCurr).getX()+model.getComponent(tCurr).getWidth()/2.0
							-model.getComponent(node).getWidth()/2.0 + pTarget.getX(), 
						model.getComponent(tCurr).getY()+model.getComponent(tCurr).getHeight()/2.0
							-model.getComponent(node).getHeight()/2.0 + pTarget.getY());
				
				model.getComponent(node).setLocation((int)pTarget.getX(), (int)pTarget.getY());
				i++;
				isPlaced.put(node,true);
			}
		}

	}


	@SuppressWarnings("deprecation")
	private void placeConnectingCompounds(Graph g, Node tCurr, GraphModel model)
	{
		for(Node n:g.getNeighbors(tCurr))
		{

			if(g.getDegree(n) > 1)
			{
				// find transition;
				int x=0;
				int y=0;
				int c=0;
				for(Node tp: g.getNeighbors(n))
				{
					if(tp.getRole()== ProcessDiagram.TRANSITION_ROLE || tp.getRole()==ProcessDiagram.PROCESS_ROLE)
					{

						x+=model.getComponent(tp).getBounds().getCenterX();
						y+=model.getComponent(tp).getBounds().getCenterY();
						c++;
					}
				}
				model.getComponent(n).setLocation(
						(x/c)-model.getComponent(n).getWidth()/2, 
						(y/c)-model.getComponent(n).getHeight()/2 
				);
				isPlaced.put(n, true);
			}
		}
	}

	private double normalAngle(Point p0, Point p1,double d)
	{
		Point pd=new Point(p1.x-p0.x,p1.y-p0.y);	
//		if(pd.x==0 && pd.y > 0)
//			return 0.5*Math.PI;
//		if(pd.x==0 && pd.y < 0) 
//			return 1.5*Math.PI;		
		return Math.atan2(pd.x, pd.y)+3*Math.PI/2.0d;// * 180 / Math.PI;
	}



	private Point2D rotate(Point p, double phi)
	{
		
		Point2D pp= new Point2D.Double(
				 (1.0*p.x*Math.cos(phi) - 1.0*p.y*Math.sin(phi)), 
				-(1.0*p.x*Math.sin(phi) + 1.0*p.y*Math.cos(phi))
		);

		return pp;
	}

	/**
	 * @return the spacer
	 */
	public int getSpacer() 
	{
		return spacer;
	}

	/**
	 * @param spacer the spacer to set
	 */
	public void setSpacer(int spacer) 
	{
		this.spacer = spacer;
	}

	/**
	 * @return the layouter
	 */
	public CanvasLayouter getLayouter() 
	{
		return layouter;
	}

	/**
	 * @param layouter the layouter to set
	 */
	public void setLayouter(CanvasLayouter layouter) 
	{
		this.layouter = layouter;
	}

	private class HelperModel extends GraphModel
	{
		public HelperModel(Graph graph) 
		{
			super(graph);
		}

		@Override
		protected void init() 
		{
			clear();
			for(Node n:getGraph().getNodes())
			{
				CanvasComponent comp=new NodeComponent(n);
				comp.setSize(reactionSpace);
				addComponent(comp);	
				getNodeMap().put(comp, n);
				getComponentMap().put(n, comp);			
			}
			Collections.sort(getComponents());			
		}	

		@Override
		public GraphModel buildSubModel(List<Node> selectedNodes) 
		{
			Graph g=Graphs.restrict(getGraph(), selectedNodes);
			return new HelperModel(g);
		}
	}

	private class Angles
	{
		public double substrateAngle=Math.toRadians(180d);
		public double productAngle=Math.toRadians(0d);
		public double dPrev=400;
		public double dNext=200;

		public String toString()
		{
			return "S:"+substrateAngle+"\tP:"+productAngle;
		}
	}

	private List<Double> place(double min, double max, int num)
	{

		List<Double> res=new ArrayList<Double>();
		if(num==1 || num==0)
		{
			res.add( (max+min)/2);
			return res;
		}
		res.add(min);
		double step=(max-min)/(num-1);
		for(int i=1; i!= num; ++i)
		{
			res.add(min+i*step);			
		}
		//		res.add(max);

		return res;	
	}

	private List<Double> place(double min, double max, double avoid, int num)
	{
		List<Double> lower=place(min, avoid, (int)(Math.floor(num/2.0)+1) );
		lower.remove(lower.size()-1);
		List<Double> upper=place(avoid, max, (int)(Math.ceil(num/2.0)+1) );
		upper.remove(0);
		lower.addAll( upper);
		return lower;
	}



}