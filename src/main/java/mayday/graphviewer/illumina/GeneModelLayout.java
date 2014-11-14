package mayday.graphviewer.illumina;

import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.genetics.coordinatemodel.GBAtom;
import mayday.vis3.graph.layout.CanvasLayouter;
import mayday.vis3.graph.model.GraphModel;

public class GeneModelLayout implements CanvasLayouter, IGeneModelLayout
{
	private int layerStep=100;
	private ScalingStyle scaling=ScalingStyle.INTRONS_CROPPED;
	private int exonMinimumSize=10;
	
	private int exonHeight=40;
	
	public static Map<Node, Integer> layoutUnitLength(double factor, GraphModel model)
	{
		Graph g=model.getGraph();
		//calculate overlapping groups
		List<GeneticNode> sortedNodes=new ArrayList<GeneticNode>();
				
		for(Node n: g.getNodes())
		{
			if(n instanceof GeneticNode)
			{
				sortedNodes.add((GeneticNode)n);
			}
		}
		Collections.sort(sortedNodes, new NodeAtomStartComparator(g));
		int intronWidth=50; 
		// map overall length to bounds.width, but choose size factor so that every exon is at least 10 pixels
//		double factor=bounds.getWidth()/(1.0*overallLength);
		int currentX=50;
		Map<Node, Integer> placement=new HashMap<Node, Integer>();
		
		int i=0;		
		while(i < sortedNodes.size())
		{
			GeneticNode node=sortedNodes.get(i);
			GBAtom atom=node.getAtom();
			GBAtom last=atom;
			placement.put(node, currentX);
//			System.out.println(node+": "+currentX);
			long start=atom.from;
			List<GBAtom> overlappingGroup=new ArrayList<GBAtom>();
			overlappingGroup.add(atom);
			i=i+1;
			while(i < sortedNodes.size())
			{
				GeneticNode node2=sortedNodes.get(i);
				GBAtom atom2=node2.getAtom();
				if(atom2.from <= last.to)
				{
					overlappingGroup.add(atom2);
					last=atom2;
					int pos= (int)((atom2.from-start)*factor); 
					placement.put(node2, currentX+pos);
//					System.out.println(node+": "+currentX+pos);
					++i;					
				}else
				{
//					System.out.println(overlappingGroup);
					long e=last.to-start;
					currentX+= (int)(e*factor);
					currentX+=intronWidth;
					break;
					// end of overlapping group:
				}				
			}
		}
		return placement;
	}
	
	@Override
	public void layout(Container container, Rectangle bounds, GraphModel model) 
	{
		// identify node to start from:
		Graph g=model.getGraph();
		Map<Node, Integer> points =new HashMap<Node, Integer>();
		
		List<GeneticNode> startNodes=new ArrayList<GeneticNode>();
		for(Node n: g.getNodes())
		{
			if(g.getInDegree(n)==0 && n instanceof GeneticNode)
			{
				startNodes.add((GeneticNode)n);
			}			
		}
		if(startNodes.isEmpty())
			return;
		Collections.sort(startNodes, new NodeAtomStartComparator(g));
		
		int i=0; 
		for(Node n: startNodes)
		{
			place(n,g,i,points);
			++i;
		}
		// get longest layer:
		long min0=Long.MAX_VALUE;
		long max0=Long.MIN_VALUE;
		long min=Long.MIN_VALUE;
		long max=Long.MAX_VALUE;
		long sum0=0;
		long sum=0;
		int exons=0;
		for(Node n: points.keySet())
		{
			if(! (n instanceof GeneticNode))
				continue;
			
			GBAtom atom=((GeneticNode)n).getAtom();
			if(points.get(n)==0)
			{
				min0=Math.min(atom.from, min0);
				max0=Math.max(atom.to, max0);
				sum0+=(atom.to-atom.from);
				exons++;
			}
			min=Math.min(atom.from, min);
			max=Math.max(atom.to, max);
			sum+=(atom.to-atom.from);
		}
		
		long range0=max0-min0;
		double ppb=bounds.getWidth()/range0;
		if(sum0==0)
		{
			return;
		}
		double pps=(bounds.getWidth() - exons*50) /sum; // accomodate for 50px at left. therefore, use #exons insted of exons-1
		
		while(!testNodeWidth(points.keySet(), pps, exonMinimumSize))
			pps*=1.2;
		
		Map<Node, Integer> unitPlacement= layoutUnitLength(pps, model);
		
		
		System.out.println(ppb);
		
		for(Node n: points.keySet())
		{
			if(! (n instanceof GeneticNode))
				continue;
			
//			GBAtom atom=((GeneticNode)n).getAtom();
//			model.getComponent(n).setSize((int)((atom.to-atom.from)*pps), 40);
//		
//			//Point x=new Point( (int)(50+(atom.from-min0)*ppb), ( points.get(n)+1)*layerStep );
//			Point x=new Point(unitPlacement.get(n), ( points.get(n)+1)*layerStep );
//			model.getComponent(n).setLocation(x);
			GBAtom atom=((GeneticNode)n).getAtom();
			Point x=null;
			if(scaling==ScalingStyle.ALL_TO_SCALE)
			{				
				x=new Point( (int)(50+(atom.from-min0)*ppb), ( points.get(n)+1)*layerStep );
				model.getComponent(n).setSize((int)((atom.to-atom.from)*ppb), exonHeight);
			}else
			{
				x=new Point(unitPlacement.get(n), (points.get(n)+1)*layerStep );
				model.getComponent(n).setSize((int)((atom.to-atom.from)*pps), exonHeight);
			}			
			model.getComponent(n).setLocation(x);
			
		}
		
		
//		new SugiyamaLayout().layout(container, bounds, model);
		
	}
	
	public static boolean testNodeWidth(Collection<Node> nodes, double factor, int acceptable)
	{
		for(Node nn:nodes)
		{
			if(nn instanceof GeneticNode)
			{
				GeneticNode n=(GeneticNode)nn;
				long l=n.getAtom().to - n.getAtom().from;
				if( ((int)l*factor) < acceptable )
				{				
					return false;
				}
			}
		}
		return true;
	}
	
	private void place(Node n, Graph g, int startLayer, Map<Node,Integer> placement)
	{
		placement.put(n, startLayer);
		List<Node> nextNodes=new ArrayList<Node>();
		for(Node nnext: g.getOutNeighbors(n))
		{
			if(placement.containsKey(nnext))
				continue;
			nextNodes.add(nnext);
			
		}
		Collections.sort(nextNodes, new NodeOutWeightComparator(g));
		
		int i=0;
		for(Node nnext:nextNodes)
		{
			place(nnext, g, startLayer+i, placement);	
			++i;
		}
		
	}
	
	public static class NodeAtomStartComparator implements Comparator<GeneticNode>
	{
		private Graph graph;
		
		public NodeAtomStartComparator(Graph graph) {
			this.graph = graph;
		}
		
		@Override
		public int compare(GeneticNode o1, GeneticNode o2) 
		{
			int c= o1.getAtom().compareTo(o2.getAtom());
			GeneticNode n1= o1;
			GeneticNode n2= o2;
			Iterator<Node> i1=null; 
			Iterator<Node> i2=null; 
			while(c==0)
			{
				i1=graph.getOutNeighbors(n1).iterator();
				i2=graph.getOutNeighbors(n2).iterator();
				if(i1.hasNext() && ! i2.hasNext())
					return -1; // we want the longer one to be on bottom
				if(!i1.hasNext() && i2.hasNext())
					return 1; // we want the longer one to be on bottom
				if(!i1.hasNext() && !i2.hasNext())
					return 0; // finaly stop trying, should never happen. 
				
				n1=(GeneticNode)i1.next();
				n2=(GeneticNode)i2.next();
				c= n1.getAtom().compareTo(n2.getAtom());
			}
			return c; 
			
		}		
	}
	
	public static class NodeOutWeightComparator implements Comparator<Node>
	{
		private Graph g;
		
		public NodeOutWeightComparator(Graph g) {
			this.g = g;
		}

		@Override
		public int compare(Node o1, Node o2) 
		{
			double d1=0, d2=0;
			for(Edge e: g.getOutEdges(o1))
			{
				d1+=e.getWeight();
			}
			for(Edge e: g.getOutEdges(o2))
			{
				d2+=e.getWeight();
			}
//			return Double.compare(d2, d1);	
			return Double.compare(d1, d2);			
		}		
	}
	
	static enum ScalingStyle{
		INTRONS_CROPPED("Resize Introns to unit length"),
		ALL_TO_SCALE("Draw Exons and Introns to scale"),
		ALL_UNIT_SIZE("Draw Exons and Introns to scale");
		
		private String rep;
		
		private ScalingStyle(String rep) 
		{
			this.rep = rep;
		}

		@Override
		public String toString() 
		{
			return rep;
		}
	}
	
	@Override
	public void setExonMinimumSize(int m) 
	{
		exonMinimumSize=m;		
	}
	
	@Override
	public void setScalingStyle(ScalingStyle style) 
	{
		scaling=style;		
	}
	
	@Override
	public void setExonBaseHeight(int h) 
	{
		exonHeight=h;	
		layerStep=(int)(1.5*exonHeight);
	}
	
	@Override
	public void setExonScaling(boolean h)
	{
		// do npothing, not applicalble here. 
	}
	
}
