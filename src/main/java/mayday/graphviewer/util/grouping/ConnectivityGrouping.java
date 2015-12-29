package mayday.graphviewer.util.grouping;

import java.util.List;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.core.ModelHub;
import mayday.graphviewer.gui.InformationSetting;

public class ConnectivityGrouping extends HierarchicalSetting implements GroupStrategy
{
	public ConnectivityGrouping() 
	{
		super("Group by Connectivity");		
		addSetting(new InformationSetting("", "<html>Group nodes into four groups: <ul>" +
				"<li>nodes with only incoming edges,</li> " +
				"<li>nodes with only outgoing edges,</li> " +
				"<li>nodes with both incoming and outgoing edges,</li> " +
				"<li>unconnected nodes</li> "));
	}
	
	@Override
	public MultiHashMap<String, DefaultNode> groupNodes(List<DefaultNode> nodes) 
	{
		MultiHashMap<String, DefaultNode> grouping=new MultiHashMap<String,DefaultNode>();
		
		String sOut="outgoing";
		String sIO="inout";
		String sIn="incoming";
		String sOrphan="orphan";
		
		for(DefaultNode n: nodes)
		{
			if(n.getOutDegree() > 0 && n.getInDegree() > 0)
			{
				grouping.put(sIO,n);
			}
			if(n.getOutDegree() > 0 && n.getInDegree() ==0 )
			{
				grouping.put(sOut,n);
			}
			if(n.getOutDegree() ==0  && n.getInDegree() >0)
			{
				grouping.put(sIn,n);
			}
			if(n.getOutDegree() ==0  && n.getInDegree() ==0)
			{
				grouping.put(sOrphan,n);
			}			
		}		
		return grouping;
	}
	@Override
	public void setHub(ModelHub hub) {}
	
	@Override
	public ConnectivityGrouping clone() 
	{
		return new ConnectivityGrouping();
	}
}
