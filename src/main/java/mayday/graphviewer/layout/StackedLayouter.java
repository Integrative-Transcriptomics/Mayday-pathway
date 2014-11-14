package mayday.graphviewer.layout;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Graphs;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.algorithm.CycleDetection;
import mayday.core.structures.graph.algorithm.StronglyConnectedComponents;
import mayday.vis3.graph.layout.CanvasLayouter;
import mayday.vis3.graph.layout.CanvasLayouterPlugin;
import mayday.vis3.graph.layout.FruchtermanReingoldLayout;
import mayday.vis3.graph.layout.SimpleCircularLayout;
import mayday.vis3.graph.layout.SnakeLayout;
import mayday.vis3.graph.layout.SugiyamaLayout;
import mayday.vis3.graph.model.DefaultGraphModel;
import mayday.vis3.graph.model.GraphModel;

public class StackedLayouter extends CanvasLayouterPlugin
{
	private Dimension nodeSize=new Dimension(400,300);
	
	private CanvasLayouter[] linearLayouters={new SimpleLinearLayout(true), new SimpleLinearLayout(false), new SnakeLayout(), new SimpleCircularLayout()};	
	private ObjectSelectionSetting<CanvasLayouter> linearLayout=new ObjectSelectionSetting<CanvasLayouter>("Linear Layouter", null, 0, linearLayouters );

	private CanvasLayouter[] circularLayouters={new SimpleCircularLayout()};
	private ObjectSelectionSetting<CanvasLayouter> circularLayout=new ObjectSelectionSetting<CanvasLayouter>("Circular Layouter", null, 0, circularLayouters);
	
	private CanvasLayouter[] treeLayouters={new SugiyamaLayout()};
	private ObjectSelectionSetting<CanvasLayouter> treeLayout=new ObjectSelectionSetting<CanvasLayouter>("Tree Layouter", null, 0, treeLayouters);
	
	private CanvasLayouter[] complexLayouters={new FruchtermanReingoldLayout(3000)};
	private ObjectSelectionSetting<CanvasLayouter> complexLayout=new ObjectSelectionSetting<CanvasLayouter>("Complex Layouter", null, 0, complexLayouters);

	public StackedLayouter() {
		initSetting();
	}
	
	
	@Override
	public void layout(Container container, Rectangle bounds, GraphModel model) 
	{
		Graph g=model.getGraph();
		// for each connected component
		List<List<Node> > cc= Graphs.calculateComponents(g);
		for(List<Node> comp:cc)
		{
			//restrict graph to current component; 
			Graph gcc=Graphs.restrict(g, comp);
			//identify the backbone: backbone nodes are all nodes w/ deg >=2
			List<Node> backbone=new ArrayList<Node>();
			for(Node n: comp)
			{
				if(gcc.getDegree(n)>=2)
					backbone.add(n);
			}
			System.out.println(backbone.size());
			// we have the backbone. Restrict gcc to backbone
			GraphModel backboneModel=model.buildSubModel(backbone);
			//layout backbone			
			layoutBackbone(container, bounds, backboneModel);
			
			for(Node n:backbone)
			{
				model.getComponent(n).setLocation(backboneModel.getComponent(n).getLocation());
			}
		}



	}

	private void layoutBackbone(Container container, Rectangle bounds, GraphModel model)
	{

		Graph graph=model.getGraph();
		//		System.out.println("Los gehts: "+graph.nodeCount()+"  "+graph.edgeCount()+Graphs.hasCycle(graph));
		// determine the topology of the input pathway as LINEAR, CYCLIC or BRANCHED

		// CYCLIC
		// use cycle detection
		Set<Node> cycle= new CycleDetection().detectCycle(graph,graph.getNodes().iterator().next());
		if(cycle.size()==graph.nodeCount())
		{
						System.out.println("()");
			circularLayout.getObjectValue().layout(container, bounds, model);
			return;
		}
		// BRANCHED:
		// we have more than one scc --> is the graph branched? 
		if(Graphs.isBranched(graph))
		{
						System.out.println("<");
			treeLayout.getObjectValue().layout(container, bounds, model);
			return;
		}		
		// LINEAR:
		// the graph is not branched --> is it linear: 
		if(Graphs.isLinear(graph))
		{
						System.out.println("|");
			linearLayout.getObjectValue().layout(container, bounds, model);
			return;
		}
		System.out.println("X_X");
		// none of the above apply: therefore: 
		// the graph is complex!! 
		// detect SSCs
		List<List<Node>> scc=StronglyConnectedComponents.findComponents(graph);
		System.out.println("Found "+ scc.size()+ " SCCs");
		// only one SCC? --> use forceLayout
		if(scc.size()==1)
		{
			complexLayout.getObjectValue().layout(container, new Rectangle(nodeSize), model);
			return;
		}
		
		Map<Node, Point> nodePosMap=new HashMap<Node, Point>();	
		Map<List<Node>, Node> compNodeMap=new HashMap<List<Node>, Node>(); 
		// several SCCs: prepare for building the graph 
		// create Graph of connected components: 
		Graph cGraph=componentConnectionGraph(scc, graph, compNodeMap);
		System.out.println("ComponentGraph size: "+cGraph.nodeCount());
		GraphModel cGraphModel=new DefaultGraphModel(cGraph);
		// layout of component graph
		
		// layout the components - they are complex or circular in any case 
		
		for(List<Node> comp:scc)
		{
			Node compNode=compNodeMap.get(comp);
			if(comp.size()==1)
			{
				cGraphModel.getComponent(compNode).setSize(nodeSize);
				nodePosMap.put(comp.get(0), new Point(0,0));
				continue;
			}
			GraphModel compModel=model.buildSubModel(comp);
			layoutBackbone(container, new Rectangle(nodeSize), compModel);
			Rectangle rect=new Rectangle(compModel.getComponent(comp.get(0)).getBounds());
			for(Node nc:comp)
			{
				rect.add(compModel.getComponent(nc).getBounds());
			}
			cGraphModel.getComponent(compNode).setSize( rect.getSize());
			for(Node nc:comp)
			{
				nodePosMap.put(nc, new Point(
						compModel.getComponent(nc).getX() - rect.x,
						compModel.getComponent(nc).getY() - rect.y)
						);
			}
			
		}
		
		// lay out backbone; hopefully use the sizes of the components.
		layoutBackbone(container, bounds, cGraphModel);
		
		
		for(Node n: cGraph.getNodes())
		{
			System.out.println(n+"\t"+cGraphModel.getComponent(n).getLocation());			
		}		
		for(List<Node> l: compNodeMap.keySet())
		{
			Node c=(compNodeMap.get(l));
			int x=cGraphModel.getComponent(c).getX();
			int y=cGraphModel.getComponent(c).getY();
			for(Node lc: l)
			{
				Point p=new Point(x
						+nodePosMap.get(lc).x
						,
						y
						+nodePosMap.get(lc).y
						);
				model.getComponent(lc).setLocation(p);

			}
		}
		
		
//		// iterate over sccs and layout each component, if necessary
//		// i.e. a component consists of more than one 
//		for(List<Node> comp:scc) 
//		{
//			if(comp.size() > 1)
//			{				
//				System.out.println(scc);
//				HelperModel sccModel=new HelperModel(Graphs.restrict(graph, comp), nodeSizeMap);
//				//				complexLayouter.layout(container, new Rectangle(0,0,600,600), sccModel);
//				layout(container, new Rectangle(0,0,800,800), sccModel);
//				new AlignLayouter().layout(container,new Rectangle(0,0,800,800) , sccModel);
//				int minX=Integer.MAX_VALUE;
//				int maxX=Integer.MIN_VALUE;
//				int minY=Integer.MAX_VALUE;
//				int maxY=Integer.MIN_VALUE;
//
//				for(Node n:comp)
//				{
//					Rectangle b= sccModel.getComponent(n).getBounds();
//					if(b.getMinX() < minX)
//						minX=(int)b.getMinX();
//					if(b.getMinY() < minY)
//						minY=(int)b.getMinY();
//					if(b.getMaxX() > maxX)
//						maxX=(int)b.getMaxX();
//					if(b.getMaxY() > maxY)
//						maxY=(int)b.getMaxY();					
//					model.getComponent(n).setLocation(sccModel.getComponent(n).getLocation());						
//				}
//				nodeOffsetMap.put(compNodeMap.get(comp), comp);
//				nodeSizeMap.put(compNodeMap.get(comp),new Dimension(maxX-minX,maxY-minY));
//			}else
//			{
//				nodeSizeMap.put(compNodeMap.get(comp), nodeSize);
//				nodeOffsetMap.put(compNodeMap.get(comp), comp);
//			}
//		}
//
//		HelperModel masterModel=new HelperModel(cGraph, nodeSizeMap);
//		//		System.out.println(cGraph.nodeCount()+"\t"+cGraph.edgeCount());
//		//		branchedLayouter.layout(container, bounds, masterModel);
//		layout(container, bounds, masterModel);
//
//		new AlignLayouter().layout(container, bounds, masterModel);
//
//		for(Node n: masterModel.getGraph().getNodes())
//		{
//			//			System.out.println(n+"   "+masterModel.getComponent(n).getBounds());
//			if(nodeOffsetMap.containsKey(n))
//			{
//				if(nodeOffsetMap.get(n).size()==1)
//				{
//					Node nm=nodeOffsetMap.get(n).iterator().next();
//					Point p=model.getComponent(nm).getLocation();
//					p.x=masterModel.getComponent(n).getX();
//					p.y=masterModel.getComponent(n).getY();
//					model.getComponent(nm).setLocation(p);
//					continue;
//				}
//				for(Node nm:nodeOffsetMap.get(n))
//				{
//					Point p=model.getComponent(nm).getLocation();
//					p.x+=masterModel.getComponent(n).getX();
//					p.y+=masterModel.getComponent(n).getY();
//					model.getComponent(nm).setLocation(p);
//				}
//			}
//		}
//		new AlignLayouter().layout(container, bounds, model);
	}

	private Graph componentConnectionGraph(List<List<Node>> components, Graph g, Map<List<Node>, Node> compNodeMap)
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

//	private class HelperModel extends GraphModel
//	{
//		private Map<Node, Dimension> nodeSizeMap=new HashMap<Node, Dimension>();
//
//		public HelperModel(Graph graph, Map<Node, Dimension> nodeSizeMap) {
//			super(graph);
//			this.nodeSizeMap = nodeSizeMap;
//			init();
//		}
//
//		@Override
//		protected void init() 
//		{
//			if(nodeSizeMap==null)
//				return;
//			clear();
//			for(Node n:getGraph().getNodes())
//			{
//				CanvasComponent comp=new NodeComponent(n);
//				comp.setSize(nodeSizeMap.get(n));
//				addComponent(comp);	
//				getNodeMap().put(comp, n);
//				getComponentMap().put(n, comp);			
//			}
//			Collections.sort(getComponents());			
//		}	
//
//		@Override
//		public GraphModel buildSubModel(List<Node> selectedNodes) 
//		{
//			Graph g=Graphs.restrict(getGraph(), selectedNodes);
//			return new HelperModel(g, nodeSizeMap);
//		}
//	}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Layout.Stacked",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"",
				"Stacked"				
		);
		return pli;	
	}

	@Override
	protected void initSetting() 
	{
		setting.addSetting(linearLayout).addSetting(circularLayout).addSetting(treeLayout).addSetting(complexLayout);
	}
	

	
	

}
