package mayday.graphviewer.util.grouping;

import java.util.List;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.core.ModelHub;
import mayday.graphviewer.gui.InformationSetting;


public class PropertyGrouping extends HierarchicalSetting implements GroupStrategy
{
	private StringSetting propertyString=new StringSetting("Property", null, "");
	
	public PropertyGrouping() 
	{
		super("Group by Property");
		addSetting(new InformationSetting("Note:", "Group by property values of the nodes." ));
		addSetting(propertyString);
	}
	
	@Override
	public void setHub(ModelHub hub) {}
	
	@Override
	public MultiHashMap<String, DefaultNode> groupNodes(List<DefaultNode> nodes) 
	{
		MultiHashMap<String, DefaultNode> grouping=new MultiHashMap<String,DefaultNode>();
		String p=propertyString.getStringValue();
		
		for(DefaultNode n: nodes)
		{
			if(n.hasProperty(p))
				grouping.put(n.getPropertyValue(p), n);
			else
				grouping.put(EMPTY_GROUP, n);
		}		
		return grouping;		
	}
	
	@Override
	public PropertyGrouping clone() 
	{
		PropertyGrouping res=new PropertyGrouping();
		res.fromPrefNode(toPrefNode());
		
		return res;
	}
}
