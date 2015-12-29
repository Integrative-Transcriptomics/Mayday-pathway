package mayday.graphviewer.util.grouping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.Probe;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.core.ModelHub;
import mayday.graphviewer.gui.InformationSetting;

public class DataSetGrouping extends HierarchicalSetting implements GroupStrategy {

	public DataSetGrouping() 
	{
		super("Group by DataSet");
		addSetting(new InformationSetting("Group by", "Group nodes by dataset"));
	}
	
	@Override
	public  MultiHashMap<String,DefaultNode> groupNodes(List<DefaultNode> nodes) 
	{
		MultiHashMap<String, DefaultNode> grouping=new MultiHashMap<String,DefaultNode>();
		
		for(DefaultNode n: nodes)
		{
			if(n instanceof MultiProbeNode)
			{
				List<String> dsNames=new ArrayList<String>();
				for(Probe p: ((MultiProbeNode) n).getProbes())
				{
					dsNames.add(p.getMasterTable().getDataSet().getName());
				}
				grouping.put(highestFrequencyItem(dsNames), n);				
			}else
			{
				grouping.put(EMPTY_GROUP,n);
			}
		}		
		return grouping;
	}
	public static<T> T highestFrequencyItem(List<T> items)
	{
		Map<T, Integer> r=new HashMap<T, Integer>();
		for(T s: items)
		{
			if(!r.containsKey(s))
				r.put(s, 1);
			else
				r.put(s, r.get(s)+1);
		}
		int i=0;
		T m=null;
		for(T s: r.keySet())
		{
			if(r.get(s) >i)
				m=s;
		}
		return m;
	}
	
	public void setHub(ModelHub hub){}
	
	@Override
	public DataSetGrouping clone() 
	{
		return new DataSetGrouping();
	}

}
