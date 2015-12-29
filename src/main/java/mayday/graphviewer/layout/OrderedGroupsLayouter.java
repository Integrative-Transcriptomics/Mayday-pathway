package mayday.graphviewer.layout;

import java.awt.Container;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.core.structures.maps.MultiHashMap;
import mayday.vis3.graph.layout.CanvasLayouterPlugin;
import mayday.vis3.graph.model.GraphModel;

public class OrderedGroupsLayouter  extends CanvasLayouterPlugin 
{	
	public static final String GROUP="group";
	public static final String ORDER="order";
	
	private String groupKey=GROUP;
	private String orderKey=ORDER;
	
	private int leftOffset=50;
	private int topOffset=50; 
	
	private int xStep=500;
	private int yStep=25; 
	
	@Override
	public void layout(Container container, Rectangle bounds, GraphModel model) 
	{
		Graph g=model.getGraph();

		Set<String> groupKeys=new TreeSet<String>();
		MultiHashMap<String, DefaultNode> nodes=new MultiHashMap<String, DefaultNode>();
	
		
		for(Node n: g.getNodes())
		{
			if(n instanceof DefaultNode)
			{
				if(!((DefaultNode)n).hasProperty(groupKey))
					continue;
				String k=((DefaultNode) n).getPropertyValue(groupKey);
				groupKeys.add(k);
				nodes.put_unambigous(k, (DefaultNode)n);
			}
		}
		
		NodeOrderComparator cmp=new NodeOrderComparator();
		
		int x=leftOffset;
		int y=topOffset;
		
		for(String k:groupKeys)
		{
			List<DefaultNode> kNodes=new ArrayList<DefaultNode>(nodes.get(k));
			Collections.sort(kNodes, cmp );
			
			for(DefaultNode n:kNodes)
			{
				model.getComponent(n).setLocation(x, y);
				y+=model.getComponent(n).getHeight()+yStep;
			}
			x+=xStep;
			y=topOffset;
		}
	}
	
	protected class NodeOrderComparator implements Comparator<DefaultNode>
	{
		@Override
		public int compare(DefaultNode o1, DefaultNode o2) 
		{
			int d1=Integer.parseInt( o1.getPropertyValue(orderKey));
			int d2=Integer.parseInt( o2.getPropertyValue(orderKey));
			
			return d1-d2;
		}
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Layout.OrderedGroups",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Place nodes column-wise, ordered by a key for column and within-order.",
				"Ordered Groups"				
		);
		return pli;	
	}
	
	@Override
	protected void initSetting() {}
}
