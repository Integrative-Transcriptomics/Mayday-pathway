package mayday.graphviewer.statistics;

import java.util.List;

import mayday.core.Probe;
import mayday.core.settings.Setting;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.crossViz3.experiments.IExperimentMapping;

public interface MultiDataSetMethod 
{
	public ResultSet calculate(MultiHashMap<String, Probe> probes, IExperimentMapping mapping);
	
	public Setting getSetting();
	
	public void setGroups(List<String> groups);
	
	public String getName();
	

}
