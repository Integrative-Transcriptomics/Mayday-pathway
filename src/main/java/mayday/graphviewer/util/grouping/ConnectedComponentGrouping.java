package mayday.graphviewer.util.grouping;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Graphs;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.algorithm.StronglyConnectedComponents;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.core.ModelHub;
import mayday.graphviewer.gui.InformationSetting;

public class ConnectedComponentGrouping extends HierarchicalSetting implements GroupStrategy {

	private RestrictedStringSetting method=new RestrictedStringSetting("Component", null, 0, 
			new String[]{"Connected Components","Strongly Connected Components"});
	
	private BooleanSetting combineUnconnectedNodes=new BooleanSetting("Group unconnected nodes together", null, true);
	
	public ConnectedComponentGrouping() 
	{
		super("Group by Connected Components");
		addSetting(new InformationSetting("Group by", "<html>Group nodes by the connected component they are in"));
		addSetting(method);
		addSetting(combineUnconnectedNodes);
	}
	
	@Override
	public MultiHashMap<String, DefaultNode> groupNodes(List<DefaultNode> nodes) 
	{
		Graph g=nodes.get(0).getGraph();
		MultiHashMap<String, DefaultNode> res=new MultiHashMap<String, DefaultNode>();
		List<List<Node>> comps=null;
		if(method.getSelectedIndex()==0)
		{	
			comps= Graphs.calculateComponents(g);			
		}else
		{
			comps= StronglyConnectedComponents.findComponents(g);
		}
		Collections.sort(comps, new ListSizeComparator());
		int i=1; 
		for(List<Node> cc: comps)
		{
			if(cc.size()==1 && combineUnconnectedNodes.getBooleanValue())
			{
				res.put(EMPTY_GROUP, cc.get(0) );
				continue;
			}
			String grp= "Component "+(i++);
			for(Node n: cc)
			{
				System.out.println(n);
				res.put(grp, (DefaultNode)n);				
			}
		}
		return res;
		
		
	}

	@Override
	public void setHub(ModelHub hub) {}
	
	@Override
	public ConnectedComponentGrouping  clone() 
	{
		ConnectedComponentGrouping res=new ConnectedComponentGrouping();
		res.fromPrefNode(toPrefNode());
		
		return res;
	}
	
	@SuppressWarnings("unchecked")
	private class ListSizeComparator implements Comparator<List>
	{
		@Override
		public int compare(List o1, List o2) 
		{
			return o1.size() - o2.size();
		}
	}

}
