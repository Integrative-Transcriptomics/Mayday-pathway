package mayday.graphviewer.util;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.SelectableHierarchicalSetting;
import mayday.graphviewer.core.ModelHub;
import mayday.graphviewer.util.grouping.ConnectedComponentGrouping;
import mayday.graphviewer.util.grouping.ConnectivityGrouping;
import mayday.graphviewer.util.grouping.DataSetGrouping;
import mayday.graphviewer.util.grouping.GroupStrategy;
import mayday.graphviewer.util.grouping.MIOGrouping;
import mayday.graphviewer.util.grouping.ProbeListGrouping;
import mayday.graphviewer.util.grouping.PropertyGrouping;
import mayday.graphviewer.util.grouping.RoleGrouping;
import mayday.graphviewer.util.grouping.ValueGrouping;

public class GroupingSetting extends SelectableHierarchicalSetting
{
	private static HierarchicalSetting[] predef(){
		return new HierarchicalSetting[]{
			new ProbeListGrouping(),
			new DataSetGrouping(),
			new RoleGrouping(),
			new PropertyGrouping(),
			new MIOGrouping(),
			new ConnectivityGrouping(),
			new ConnectedComponentGrouping(),
			new ValueGrouping()};
	}
	
	public HierarchicalSetting[] values;
	
	private ModelHub hub;
	
	public GroupingSetting(ModelHub modelHub) 
	{
		super("Group Nodes",null,0,new HierarchicalSetting[]{});
		this.hub=modelHub;
		values=predef();
		setPredefined(values);
		for(HierarchicalSetting grp:values)
		{
			((GroupStrategy)grp).setHub(modelHub);
		}
	}
	
	public GroupStrategy getStrategy()
	{
		return (GroupStrategy)predef[getSelectedIndex()];
	}

	@Override
	public GroupingSetting clone() 
	{
		GroupingSetting res=new GroupingSetting(hub);
		res.fromPrefNode(toPrefNode());
		res.values=values;
		return res;
	}
	
}
