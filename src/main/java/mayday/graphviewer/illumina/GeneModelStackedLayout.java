package mayday.graphviewer.illumina;

import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.genetics.coordinatemodel.GBAtom;
import mayday.graphviewer.illumina.GeneModelLayout.NodeAtomStartComparator;
import mayday.graphviewer.illumina.GeneModelLayout.ScalingStyle;
import mayday.vis3.graph.layout.CanvasLayouter;
import mayday.vis3.graph.model.GraphModel;

public class GeneModelStackedLayout implements CanvasLayouter, IGeneModelLayout
{
	private int intronWidth=50;
	private int layerStep=100;
	
	private ScalingStyle scaling=ScalingStyle.INTRONS_CROPPED;
	private int exonMinimumSize=10;
	
	private int exonHeight=40;
	private boolean scaleExons=true;
	
	@Override
	public void layout(Container container, Rectangle bounds, GraphModel model) 
	{
		// calculate ordered groups:
		List<List<GeneticNode>> orderedGroups=orderNodes(model);
		// calculate maximal length
		// to do so, we summarize the overall length of each ordered group.
		long length=0;
		
		long min=Long.MAX_VALUE;
		long max=Long.MIN_VALUE;
		if(orderedGroups.isEmpty())
			return;
		for(List<GeneticNode> grp:orderedGroups)
		{
			long f=Long.MAX_VALUE;
			long t=0;
			for(GeneticNode gn: grp)
			{
				f=Math.min(gn.getAtom().from, f);
				t=Math.max(gn.getAtom().to,t);
				min=Math.min(gn.getAtom().from, min);
				max=Math.max(gn.getAtom().to, max);
			}
			length+=(t-f);
		}
		double activeLength=(bounds.getWidth()-orderedGroups.size()*50);
		activeLength=activeLength<1000?1000:activeLength;
		double pps=activeLength/length;
		long range=max-min;
		double ppb=bounds.getWidth()/range;
		
		// find the optimal scale factor so that every exon is at least 20px wide. 
		while(!GeneModelLayout.testNodeWidth(model.getGraph().getNodes(), pps, exonMinimumSize))
			pps*=1.2;
		// calculate start positions
		Map<GeneticNode, Integer> placement=new HashMap<GeneticNode, Integer>();
		int currentX=50;
		for(List<GeneticNode> grp:orderedGroups)
		{
			List<GeneticNode> nodes=new ArrayList<GeneticNode>(grp);
			Collections.sort(nodes,new NodeAtomStartComparator(model.getGraph()));
			placement.put(nodes.get(0), currentX);
			long start=nodes.get(0).getAtom().from;
			for(int i=1; i< nodes.size(); ++i)
			{
				GeneticNode node2=nodes.get(i);
				GBAtom atom2=node2.getAtom();
				int pos= (int)((atom2.from-start)*pps); 
				placement.put(node2, currentX+pos);
			}
			long e=nodes.get(nodes.size()-1).getAtom().to-start;
			currentX+= (int)(e*pps);
			currentX+=intronWidth;
			int currentY=layerStep;
			Collections.reverse(grp);
			for(GeneticNode n: grp)
			{
				Point x=null;
				int lHeight=exonHeight;
				if(scaleExons)
				{
					int numMod=Integer.parseInt(n.getPropertyValue(GeneticNode.NUMBER_MODELS));
					lHeight=numMod*exonHeight;
				}
				if(scaling==ScalingStyle.ALL_TO_SCALE)
				{
					GBAtom atom=((GeneticNode)n).getAtom();
					model.getComponent(n).setSize((int)((n.getAtom().to-n.getAtom().from)*ppb), lHeight);
					x=new Point( (int)(50+(atom.from-min)*ppb), ( model.getComponent(n).getY()) );
				}else
				{
					model.getComponent(n).setSize((int)((n.getAtom().to-n.getAtom().from)*pps), lHeight);
					x=new Point(placement.get(n), currentY );
				}
				model.getComponent(n).setLocation(x);
				currentY+=(1.0*lHeight*1.5);
			}
		}
		
	}
	
	public static List<List<GeneticNode>> orderNodes(GraphModel model)
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
		Collections.sort(sortedNodes, new NodeAtomStartComparator(model.getGraph()));
		List<List<GeneticNode>> res=new ArrayList<List<GeneticNode>>();
		int i=0;		
		while(i < sortedNodes.size())
		{
			GeneticNode node=sortedNodes.get(i);
			GBAtom atom=node.getAtom();
			GBAtom last=atom;			
			List<GeneticNode> overlappingGroup=new ArrayList<GeneticNode>();
			overlappingGroup.add(node);
			i=i+1;
			while(i < sortedNodes.size())
			{
				GeneticNode node2=sortedNodes.get(i);
				GBAtom atom2=node2.getAtom();
				if(atom2.from <= last.to)
				{
					overlappingGroup.add(node2);
					last=atom2;
					++i;					
				}else
				{
//					System.out.println(overlappingGroup);
					
					break;
				}				
			}
			res.add(overlappingGroup);
		}
		NodeInWeightComparator cmp=new NodeInWeightComparator(g);
		for(List<GeneticNode> l: res)
		{
			Collections.sort(l, cmp);
		}
		return res;
	}
	
	public static class NodeInWeightComparator implements Comparator<Node>
	{
		private Graph g;
		
		public NodeInWeightComparator(Graph g) {
			this.g = g;
		}

		@Override
		public int compare(Node o1, Node o2) 
		{
			double d1=0, d2=0;
			for(Edge e: g.getInEdges(o1))
			{
				d1+=e.getWeight();
			}
			for(Edge e: g.getInEdges(o2))
			{
				d2+=e.getWeight();
			}
			return Double.compare(d1, d2);			
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
		scaleExons=h;		
	}
}
