package mayday.pathway.viewer.canvas;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Graphs;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.algorithm.CycleDetection;
import mayday.core.structures.graph.algorithm.StronglyConnectedComponents;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.NodeComponent;
import mayday.vis3.graph.layout.CanvasLayouter;
import mayday.vis3.graph.model.GraphModel;

public class PathwayLayouter implements CanvasLayouter
{
	private CanvasLayouter linearLayouter;
	private CanvasLayouter circularLayouter;
	private CanvasLayouter branchedLayouter;
	private CanvasLayouter complexLayouter;

	private Dimension nodeSize=new Dimension(400,300);
	
	private Map<Node, Dimension> nodeSizeMap=new HashMap<Node, Dimension>();
	private Map<Node, Collection<Node>> nodeOffsetMap=new HashMap<Node, Collection<Node>>();
	
	Map<List<Node>, Node> compNodeMap=new HashMap<List<Node>, Node>(); 
	
	public void layout(Container container, Rectangle bounds, GraphModel model) 
	{
		
		Graph graph=model.getGraph();
//		System.out.println("Los gehts: "+graph.nodeCount()+"  "+graph.edgeCount()+Graphs.hasCycle(graph));
		// determine the topology of the input pathway as LINEAR, CYCLIC or BRANCHED
		
		// CYCLIC
		// use cycle detection
		Set<Node> cycle= new CycleDetection().detectCycle(graph,graph.getNodes().iterator().next());
		if(cycle.size()==graph.nodeCount())
		{
//			System.out.println("()");
			circularLayouter.layout(container, bounds, model);
			return;
		}
		// BRANCHED:
		// we have more than one scc --> is the graph branched? 
		if(Graphs.isBranched(graph))
		{
//			System.out.println("<");
			branchedLayouter.layout(container, bounds, model);
			return;
		}		
		// LINEAR:
		// the graph is not branched --> is it linear: 
		if(Graphs.isLinear(graph))
		{
//			System.out.println("|");
			linearLayouter.layout(container, bounds, model);
			return;
		}
		// none of the above apply: therefore: 
		// the graph is complex!! 
		// detect SSCs
		List<List<Node>> scc=StronglyConnectedComponents.findComponents(graph);
		// only one SCC? --> use forceLayout
		if(scc.size()==1)
		{
			complexLayouter.layout(container, new Rectangle(1000,1000), model);
			return;
		}
		// several SCCs: prepare for building the graph 
		for(Node n:graph.getNodes())
		{
			nodeSizeMap.put(n, nodeSize);
		}
		// calculate connection graph of sccs
		Graph cGraph=componentConnectionGraph(scc, graph);
		// iterate over sccs and layout each component, if necessary
		// i.e. a component consists of more than one 
		for(List<Node> comp:scc) 
		{
			if(comp.size() > 1)
			{				
				HelperModel sccModel=new HelperModel(Graphs.restrict(graph, comp));
//				complexLayouter.layout(container, new Rectangle(0,0,600,600), sccModel);
				layout(container, new Rectangle(0,0,800,800), sccModel);
				new AlignLayouter().layout(container,new Rectangle(0,0,800,800) , sccModel);
				int minX=Integer.MAX_VALUE;
				int maxX=Integer.MIN_VALUE;
				int minY=Integer.MAX_VALUE;
				int maxY=Integer.MIN_VALUE;

				for(Node n:comp)
				{
					Rectangle b= sccModel.getComponent(n).getBounds();
					if(b.getMinX() < minX)
						minX=(int)b.getMinX();
					if(b.getMinY() < minY)
						minY=(int)b.getMinY();
					if(b.getMaxX() > maxX)
						maxX=(int)b.getMaxX();
					if(b.getMaxY() > maxY)
						maxY=(int)b.getMaxY();					
					model.getComponent(n).setLocation(sccModel.getComponent(n).getLocation());						
				}
				nodeOffsetMap.put(compNodeMap.get(comp), comp);
				nodeSizeMap.put(compNodeMap.get(comp),new Dimension(maxX-minX,maxY-minY));
			}else
			{
				nodeSizeMap.put(compNodeMap.get(comp), nodeSize);
				nodeOffsetMap.put(compNodeMap.get(comp), comp);
			}
		}
		
		HelperModel masterModel=new HelperModel(cGraph);
//		System.out.println(cGraph.nodeCount()+"\t"+cGraph.edgeCount());
//		branchedLayouter.layout(container, bounds, masterModel);
		layout(container, bounds, masterModel);
		
		new AlignLayouter().layout(container, bounds, masterModel);
			
		for(Node n: masterModel.getGraph().getNodes())
		{
//			System.out.println(n+"   "+masterModel.getComponent(n).getBounds());
			if(nodeOffsetMap.containsKey(n))
			{
				if(nodeOffsetMap.get(n).size()==1)
				{
					Node nm=nodeOffsetMap.get(n).iterator().next();
					Point p=model.getComponent(nm).getLocation();
					p.x=masterModel.getComponent(n).getX();
					p.y=masterModel.getComponent(n).getY();
					model.getComponent(nm).setLocation(p);
					continue;
				}
				for(Node nm:nodeOffsetMap.get(n))
				{
					Point p=model.getComponent(nm).getLocation();
					p.x+=masterModel.getComponent(n).getX();
					p.y+=masterModel.getComponent(n).getY();
					model.getComponent(nm).setLocation(p);
				}
			}
		}
		
	new AlignLayouter().layout(container, bounds, model);
		
		
	}
	
	
	private Graph componentConnectionGraph(List<List<Node>> components, Graph g)
	{
		Graph res=new Graph();
		
		for(List<Node> l:components)
		{
			Node n=new Node(res,l.toString());
			compNodeMap.put(l,n);
			res.addNode(n);
		}
		
		for(int i=0; i< components.size(); ++i)
		{
			for(int j=i+1; j< components.size(); ++j)
			{
				boolean foundConnection=false;
				for(Node n:components.get(i))
				{
					if(foundConnection) break;
					for(Node m:components.get(j))
					{
						if(g.isConnected(n, m) )
						{
							res.connect(compNodeMap.get(components.get(i)),compNodeMap.get(components.get(j)));
							foundConnection=true;
							break;
						}
						if( g.isConnected(m, n))
						{
							res.connect(compNodeMap.get(components.get(j)),compNodeMap.get(components.get(i)));
							foundConnection=true;
							break;
						}	
					}
				}				
			}
		}
		return res;
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
				comp.setSize(nodeSizeMap.get(n));
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

	/**
	 * @return the linearLayouter
	 */
	public CanvasLayouter getLinearLayouter() {
		return linearLayouter;
	}


	/**
	 * @param linearLayouter the linearLayouter to set
	 */
	public void setLinearLayouter(CanvasLayouter linearLayouter) {
		this.linearLayouter = linearLayouter;
	}


	/**
	 * @return the circularLayouter
	 */
	public CanvasLayouter getCircularLayouter() {
		return circularLayouter;
	}


	/**
	 * @param circularLayouter the circularLayouter to set
	 */
	public void setCircularLayouter(CanvasLayouter circularLayouter) {
		this.circularLayouter = circularLayouter;
	}


	/**
	 * @return the branchedLayouter
	 */
	public CanvasLayouter getBranchedLayouter() {
		return branchedLayouter;
	}


	/**
	 * @param branchedLayouter the branchedLayouter to set
	 */
	public void setBranchedLayouter(CanvasLayouter branchedLayouter) {
		this.branchedLayouter = branchedLayouter;
	}


	/**
	 * @return the complexLayouter
	 */
	public CanvasLayouter getComplexLayouter() {
		return complexLayouter;
	}


	/**
	 * @param complexLayouter the complexLayouter to set
	 */
	public void setComplexLayouter(CanvasLayouter complexLayouter) {
		this.complexLayouter = complexLayouter;
	}


	/**
	 * @return the nodeSize
	 */
	public Dimension getNodeSize() {
		return nodeSize;
	}


	/**
	 * @param nodeSize the nodeSize to set
	 */
	public void setNodeSize(Dimension nodeSize) {
		this.nodeSize = nodeSize;
	}
	
	

}
