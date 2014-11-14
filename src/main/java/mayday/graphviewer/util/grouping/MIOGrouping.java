package mayday.graphviewer.util.grouping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.MIGroupSetting;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.core.ModelHub;
import mayday.graphviewer.gui.InformationSetting;

public class MIOGrouping extends HierarchicalSetting implements GroupStrategy 
{	
	private ModelHub hub;
	private Map<DataSet, MIGroupSetting> miGroups=new HashMap<DataSet, MIGroupSetting>();
	
	public MIOGrouping()
	{
		super("Group by Meta Information");
		addSetting(new InformationSetting("", "<html>Group nodes by Meta Information value.<br>" +
				" For each DataSet, a seperate MIGroup can be selected. "));
	}
	
	@Override
	public MultiHashMap<String, DefaultNode> groupNodes(List<DefaultNode> nodes) 
	{
		MultiHashMap<String, DefaultNode> grouping=new MultiHashMap<String,DefaultNode>();		
		for(DefaultNode n: nodes)
		{
			if(n instanceof MultiProbeNode)
			{
				List<String> dsNames=new ArrayList<String>();
				for(Probe p: ((MultiProbeNode) n).getProbes())
				{
					
					if(miGroups.containsKey(p))
					{
						(miGroups.get(p.getMasterTable().getDataSet()).getMIGroup().getMIO(p)).toString();
						dsNames.add(p.getMasterTable().getDataSet().getName());
					}
				}
				if(dsNames.isEmpty())
				{
					grouping.put(EMPTY_GROUP,n);
				}else
				{
					grouping.put(DataSetGrouping.highestFrequencyItem(dsNames), n);		
				}						
			}else
			{
				grouping.put(EMPTY_GROUP,n);
			}
		}		
		return grouping;
	}

	@Override
	public void setHub(ModelHub hub) 
	{
		this.hub=hub;
		for(DataSet ds:hub.getDataSets())
		{
			MIGroupSetting setting=new MIGroupSetting(ds.getName(), null, null, null, false);
			addSetting(setting);
			miGroups.put(ds, setting);
		}
	}

	@Override
	public MIOGrouping clone() 
	{
		MIOGrouping gr=new MIOGrouping();
		gr.hub=hub;
		gr.miGroups=miGroups;
		return gr;
	}
	
}
