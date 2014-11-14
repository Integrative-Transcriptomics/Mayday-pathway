package mayday.graphviewer.statistics;

import java.util.List;

import mayday.core.Probe;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.crossViz3.experiments.IExperimentMapping;

public abstract class AbstractMultiDatasetMethod extends AbstractPlugin implements MultiDataSetMethod 
{
	protected HierarchicalSetting setting;
	
	public static final String MC="GraphViewer/Statistical Method";
	
	public AbstractMultiDatasetMethod() 
	{
		
	}	
	
	@Override
	public abstract ResultSet calculate(MultiHashMap<String, Probe> probes, IExperimentMapping mapping);

	@Override
	public abstract String getName();

	@Override
	public Setting getSetting()
	{
		return setting;
	}

	protected double[] getMappedValues(Probe p, IExperimentMapping mapping)
	{
		List<Integer> mappedExps=mapping.getCommonExperiments(p.getMasterTable().getDataSet());
		double[] d=new double[mappedExps.size()];
		
		for(int i=0; i!=mappedExps.size(); ++i)
		{
			Double dv=p.getValue(mappedExps.get(i));
			if(dv==null)
				d[i]=Double.NaN;
			else
				d[i]=dv;
		}
		return d;
	}
	
	@Override
	public void setGroups(List<String> groups) 
	{		
	}
	
	@Override
	public String toString() 
	{
		return getName();
	}

	@Override
	public void init() {}
	
	
	

}
