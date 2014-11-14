package mayday.graphviewer.util.grouping;

import java.util.List;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.core.ModelHub;
import mayday.graphviewer.gui.InformationSetting;

public class RoleGrouping  extends HierarchicalSetting implements GroupStrategy
{
	@Override
	public void setHub(ModelHub hub) {}
	
	public RoleGrouping() 
	{
		super("Group by Role");
		addSetting(new InformationSetting("Note", "Group the nodes by their roles."));
	}
	
	@Override
	public MultiHashMap<String, DefaultNode> groupNodes(List<DefaultNode> nodes) 
	{
		MultiHashMap<String, DefaultNode> grouping=new MultiHashMap<String,DefaultNode>();
		
		for(DefaultNode n: nodes)
		{
			grouping.put(n.getRole(), n);
		}		
		return grouping;
	}
	
	@Override
	public RoleGrouping clone() 
	{
		return new RoleGrouping(); 
	}
	
}
