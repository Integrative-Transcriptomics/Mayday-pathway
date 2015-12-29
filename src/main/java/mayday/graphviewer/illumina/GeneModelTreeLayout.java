package mayday.graphviewer.illumina;

import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Graphs;
import mayday.core.structures.graph.Node;
import mayday.genetics.coordinatemodel.GBAtom;
import mayday.graphviewer.illumina.GeneModelLayout.ScalingStyle;
import mayday.graphviewer.util.Trees;
import mayday.vis3.graph.layout.CanvasLayouter;
import mayday.vis3.graph.model.GraphModel;

public class GeneModelTreeLayout implements CanvasLayouter, IGeneModelLayout
{
	private int yOffset=50;
	private int yStep=45; 

	private ScalingStyle scaling=ScalingStyle.INTRONS_CROPPED;
	private int exonMinimumSize=10;

	private int exonHeight=40;
	private boolean scaleExons=true;

	@Override
	public void layout(Container container, Rectangle bounds, GraphModel model) 
	{
		Graph g=model.getGraph();
		// get connected components
		List<List<Node> > comps=Graphs.calculateComponents(g);

		int exons=GeneModelStackedLayout.orderNodes(model).size();

		long min=Long.MAX_VALUE;
		long max=Long.MIN_VALUE;
		long sum=0;
		for(Node n: g.getNodes() )
		{
			if(! (n instanceof GeneticNode))
				continue;

			GBAtom atom=((GeneticNode)n).getAtom();
			min=Math.min(atom.from, min);
			max=Math.max(atom.to, max);
			sum+=(atom.to-atom.from);
		}
		double ppb=bounds.getWidth()/(1.0*max-1.0*min);

		double activeLength=(bounds.getWidth()-exons*50);
		activeLength=activeLength<1000?1000:activeLength;
		double pps=activeLength/sum;

		//		double pps=(bounds.getWidth()-exons*50)/sum;

		while(!GeneModelLayout.testNodeWidth(g.getNodes(), pps, exonMinimumSize))
			pps*=1.2;

		Map<Node, Integer> unitPlacement= GeneModelLayout.layoutUnitLength(pps, model);

		for(Node n: g.getNodes())
		{
			if(! (n instanceof GeneticNode))
				continue;

			GBAtom atom=((GeneticNode)n).getAtom();

			int lHeight=exonHeight;
			if(scaleExons)
			{
				int numMod=((GeneticNode)n).getProbes().size();
				lHeight=numMod*exonHeight;
			}			
			if(scaling==ScalingStyle.ALL_TO_SCALE)
			{
				model.getComponent(n).setSize((int)((atom.to-atom.from)*ppb), lHeight);
			}else
			{
				model.getComponent(n).setSize((int)((atom.to-atom.from)*pps), lHeight);
			}
		}

		int y=yOffset;


		Collections.sort(comps, new ComponentComparator(g));
		for(List<Node> comp:comps)
		{			
			Node root=null;
			int leaves=0;
			int h=0;
			for(Node n:comp)
			{
				// find root of each component
				if(g.getInDegree(n)==0)
				{
					root=n;					
				}
				// find number of leaves,
				if(g.getOutDegree(n)==0)
				{
					leaves++;	
					h+=model.getComponent(n).getHeight();
					h+=yStep;
				}
			}		
			// place recursively
			place(model,g,root,h,y);
			y+=h;
			//			System.out.println(y);
		}

		for(Node n: g.getNodes())
		{
			if(! (n instanceof GeneticNode))
				continue;

			Point x=null;
			if(scaling==ScalingStyle.ALL_TO_SCALE)
			{
				GBAtom atom=((GeneticNode)n).getAtom();
				x=new Point( (int)(50+(atom.from-min)*ppb), ( model.getComponent(n).getY()) );
			}else
			{
				x=new Point(unitPlacement.get(n), ( model.getComponent(n).getY()) );
			}			
			model.getComponent(n).setLocation(x);
		}		
	}

	private void place(GraphModel model, Graph g, Node node, int height, int y)
	{
		// place node @ y+1/2 height; 
		model.getComponent(node).setLocation(0, y+(height/2));

		// find degree and place: 
		int deg=g.getOutDegree(node);
		if(deg==0)
			return;

		int i=0;

		int cTotal=Trees.numberOfLeaves(g, node);


		int hnext=y; 
		List<Node> nodes=new ArrayList<Node>(g.getOutNeighbors(node));
		Collections.sort(nodes,new GeneticNodeComparator());
		for(Node n: nodes)
		{
			int cl=Trees.numberOfLeaves(g, n);
			cl= cl==0?1:cl;

			double cp= cl/(1.0*cTotal);
			int h0=(int)(cp*height);
			//			System.out.println(cTotal + "\t" + cl+"\t"+height);
			place(model, g, n, h0, hnext);
			hnext+=(int)((cp*height));
			++i;
		}

		//		System.out.println("--");
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
	}

	@Override
	public void setExonScaling(boolean h)
	{
		scaleExons=h;

	}

	private class GeneticNodeComparator implements Comparator<Node>
	{
		@Override
		public int compare(Node o1, Node o2) 
		{
			GeneticNode gn1=(GeneticNode)o1;
			GeneticNode gn2=(GeneticNode)o2;
			return gn1.getAtom().compareTo(gn2.getAtom());
		}	
	}

	private class ComponentComparator implements Comparator<List<Node>>
	{
		private Graph graph; 

		public ComponentComparator(Graph graph) {
			this.graph = graph;
		}

		@Override
		public int compare(List<Node> o1, List<Node> o2) 
		{
			// find zero indeg node
			Node r1=null;
			for(Node n:o1)
			{
				// find root of each component
				if(graph.getInDegree(n)==0)
				{
					r1=n;					
				}
			}
			Node r2=null;
			for(Node n:o2)
			{
				// find root of each component
				if(graph.getInDegree(n)==0)
				{
					r2=n;					
				}
			}
			GeneticNode gn1=(GeneticNode)r1;
			GeneticNode gn2=(GeneticNode)r2;
			return gn1.getAtom().compareTo(gn2.getAtom());
		}
	}

}
