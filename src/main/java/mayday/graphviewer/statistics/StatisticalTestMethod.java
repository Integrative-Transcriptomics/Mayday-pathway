package mayday.graphviewer.statistics;

import java.util.HashMap;

import mayday.core.Probe;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.PluginManager.IGNORE_PLUGIN;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.crossViz3.experiments.IExperimentMapping;
@IGNORE_PLUGIN
public class StatisticalTestMethod extends AbstractMultiDatasetMethod {

//	private PluginInstanceListSetting<StatisticalTestMethod> method;
//	private DoubleSetting pValueCutoff;
//	private ClassSelectionSetting classSelection;
	
	@Override
	public ResultSet calculate(MultiHashMap<String, Probe> probes,	IExperimentMapping mapping) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() 
	{
		return "Statistical Test";
	}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Statistics.StatTest",
				new String[]{},
				AbstractMultiDatasetMethod.MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Calculate a statistical test between Groups",
				getName()				
		);
		
		return pli;	
	}
	
}
