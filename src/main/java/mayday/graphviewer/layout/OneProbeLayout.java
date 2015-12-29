package mayday.graphviewer.layout;

import java.awt.Container;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.core.bag.ComponentBag;
import mayday.pathway.viewer.canvas.AlignLayouter;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.NodeComponent;
import mayday.vis3.graph.layout.CanvasLayouterPlugin;
import mayday.vis3.graph.layout.FruchtermanReingoldLayout;
import mayday.vis3.graph.model.DefaultGraphModel;
import mayday.vis3.graph.model.GraphModel;

public class OneProbeLayout  extends CanvasLayouterPlugin
{
	private int ySpace=50; // top spacer
	
	public OneProbeLayout() 
	{	
	}
	
	@Override
	protected void initSetting() {}
	
	@Override
	public void layout(Container container, Rectangle bounds, GraphModel model) 
	{
		// assume: we have a SuperModel 
		if(!(model instanceof SuperModel))
		{
			//if not: fall back to FR. 
			new FruchtermanReingoldLayout().layout(container, bounds, model);
			return;			
		}
		SuperModel sm=(SuperModel)model;
		Graph g=sm.getGraph();
		//identify zero indeg nodes.
		List<Node> centers=new ArrayList<Node>();
		for(Node n: g.getNodes() )
		{
			if(g.getInDegree(n)==0)
			{
				centers.add(n);
			}
		}
		
		
		//for each centerNode
		int y=ySpace;
		for(Node center: centers)
		{
			Set<ComponentBag> bags=new HashSet<ComponentBag>();
			// get bags associated with the nodes connected to center:
			// look at each bag; inspect first node and see if it is connected w/ center
			
			//create graph of bags and center
			Graph subGraph=new Graph();
			subGraph.addNode(center);
			Map<Node, ComponentBag> nodeBagMap=new HashMap<Node, ComponentBag>();
			
			for(ComponentBag bag:sm.getBags())
			{
				Node bn=((NodeComponent)(bag.getComponents().iterator().next())).getNode();
				if(g.getNeighbors(bn).contains(center) || g.isConnected(center, bn) || g.isConnected( bn, center))
				{
					bags.add(bag);
					//make bag compact for better usage of screen. 
					bag.restoreAll();
					//add bag to subgraph
					Node bagRep=new Node(subGraph);
					subGraph.addNode(bagRep);
					nodeBagMap.put(bagRep, bag);
					subGraph.connect(center, bagRep);					
				}
			}
			GraphModel subModel=new DefaultGraphModel(subGraph);
			//place at centerX of canvas
			subModel.getComponent(center).setLocation( bounds.width/2, y+(bounds.width/2) );
			//center at centernode
			new ParentCenteredRadialLayout(center).layout(container, new Rectangle(0,y,bounds.width,bounds.width), subModel);
			// place components
			int yn=0;
			for(Node n: subGraph.getNodes())
			{
				CanvasComponent cc=subModel.getComponent(n);
				if(model.getComponent(n)!=null)
				{
					model.getComponent(n).setLocation(cc.getX(), y+cc.getY());
				}else
				{
					ComponentBag bag=nodeBagMap.get(n);
					for(CanvasComponent bc:bag.getComponents())
					{
						bc.setLocation(cc.getX(), y+cc.getY());	
					}
					bag.compress();
									
				}
				if(cc.getY() > yn)
					yn=cc.getY();
			}
			y+=yn+ySpace;			
		}
		
		new AlignLayouter(50).layout(container, bounds, model);

		
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Layout.OneProbe",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Arrange nodes around a centeral node.",
				"One Probe Layout"				
		);
		return pli;	
	}
}
