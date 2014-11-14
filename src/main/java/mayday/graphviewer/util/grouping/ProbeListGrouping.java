package mayday.graphviewer.util.grouping;

import java.util.ArrayList;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.core.ModelHub;
import mayday.graphviewer.gui.InformationSetting;

public class ProbeListGrouping  extends HierarchicalSetting implements GroupStrategy
{	
	private ModelHub hub;	
	private BooleanSetting ignoreDataSet=new BooleanSetting("Ignore DataSet", null, true);
	
	public ProbeListGrouping() 
	{
		super("Group by ProbeList");
		addSetting(new InformationSetting("", "Group by ProbeList. ProbeList "));
		addSetting(ignoreDataSet);
	}
	
	public void setHub(ModelHub hub) 
	{
		this.hub = hub;
	}


	@Override
	public  MultiHashMap<String,DefaultNode> groupNodes(List<DefaultNode> nodes) 
	{
		MultiHashMap<String, DefaultNode> grouping=new MultiHashMap<String,DefaultNode>();
		boolean useDS=! ignoreDataSet.getBooleanValue();
		for(DefaultNode n: nodes)
		{
			if(n instanceof MultiProbeNode)
			{
				List<String> dsNames=new ArrayList<String>();
				for(Probe p: ((MultiProbeNode) n).getProbes())
				{
					DataSet ds= p.getMasterTable().getDataSet();
					dsNames.add(useDS?ds.getName():"" + 
							hub.getViewModel(ds).getTopPriorityProbeList(p).getName());					
				}
				grouping.put(DataSetGrouping.highestFrequencyItem(dsNames), n);				
			}else
			{
				grouping.put(EMPTY_GROUP,n);
			}
		}		
		return grouping;
	}

	@Override
	public ProbeListGrouping clone() {
		ProbeListGrouping res=new ProbeListGrouping();
		res.fromPrefNode(toPrefNode());
		res.hub=hub;
		return res;
	}
}
