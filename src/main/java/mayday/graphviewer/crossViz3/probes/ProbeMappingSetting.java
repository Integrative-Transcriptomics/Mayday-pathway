package mayday.graphviewer.crossViz3.probes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import mayday.core.DataSet;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.MappingSourceSetting;

public class ProbeMappingSetting extends HierarchicalSetting 
{
	
	private Collection<DataSet> dataSets;
	private Map<DataSet,MappingSourceSetting> mappings=new HashMap<DataSet, MappingSourceSetting>();
	
	public ProbeMappingSetting(Collection<DataSet> dataSets) 
	{
		super("Probe Mapping");
		this.dataSets=dataSets;
		mappings=new HashMap<DataSet, MappingSourceSetting>();
		for(DataSet ds:dataSets)
		{
			MappingSourceSetting dsMapping= new MappingSourceSetting(ds);
			mappings.put(ds,dsMapping);
			HierarchicalSetting dsSetting=new HierarchicalSetting(ds.getName());
			dsSetting.addSetting(dsMapping);
			addSetting(dsSetting);
		}
		
	}
	
	public Map<DataSet, MappingSourceSetting> getMappings() {
		return mappings;
	}
	
	@Override
	public ProbeMappingSetting clone() 
	{
		ProbeMappingSetting clone=new ProbeMappingSetting(dataSets);
		clone.mappings=mappings;
		return clone;
	}
	
	public void addDataSet(DataSet ds)
	{
		dataSets.add(ds);
		MappingSourceSetting dsMapping= new MappingSourceSetting(ds);
		mappings.put(ds,dsMapping);
		HierarchicalSetting dsSetting=new HierarchicalSetting(ds.getName());
		dsSetting.addSetting(dsMapping);
		addSetting(dsSetting);
	}
	
	
}
