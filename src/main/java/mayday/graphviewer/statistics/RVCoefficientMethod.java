package mayday.graphviewer.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mayday.core.Probe;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.structures.linalg.matrix.DoubleMatrix;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.crossViz3.experiments.IExperimentMapping;

public class RVCoefficientMethod extends AbstractMultiDatasetMethod
{
	private BooleanSetting useModifiedRV;
	
	public RVCoefficientMethod() 
	{
		super();
		setting=new HierarchicalSetting(getName());
		useModifiedRV=new BooleanSetting("Use modified RV coefficient", "For modified RV, cf. Smilde et al. Bioinf 25 No 3, pp 401-405", true);
		setting.addSetting(useModifiedRV);
	}

	@Override
	public ResultSet calculate(MultiHashMap<String, Probe> probes, IExperimentMapping mapping) 
	{
		ResultSet res=new ResultSet();
		res.setTitle((useModifiedRV.getBooleanValue()?"modified ":"")+"RV coefficient");
		res.setPreferredScope(ResultScope.Groups);
		
		List<String> groups=new ArrayList<String>(probes.keySet());
		res.initGroups(groups);
		res.setRowGroupProbes(probes);	
	
		//create matrices from the data
		
		List<DoubleMatrix> matrices=new ArrayList<DoubleMatrix>();
		for(int i=0; i!= groups.size(); ++i)
		{
			DoubleMatrix dm=new DoubleMatrix(
					probes.get(groups.get(i), true).size(), 
					mapping.getCommonExperiments(probes.get(groups.get(i),true).get(0).getMasterTable().getDataSet()).size());
			
			int r=0;
			for(Probe p: probes.get(groups.get(i)))
			{
				double[] v=getMappedValues(p, mapping);
				for(int j=0; j!= v.length; ++j)
				{
					dm.setValue(r,j, v[j]);
				}
				r++;
			}			
			dm.transpose();			
			matrices.add(dm);
		}
		
		boolean mod=useModifiedRV.getBooleanValue();
		for(int i=0; i!= groups.size(); ++i)
		{
			for(int j=0; j!= groups.size(); ++j)
			{
				double rv= mod?
						Correlations.rv2(matrices.get(i), matrices.get(j)):
						Correlations.rv(matrices.get(i), matrices.get(j));
				res.setGroupResult(i, j, rv);
			}
		}			
		return res;		
	}

	@Override
	public String getName() 
	{
		return "RV coefficient";
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Statistics.RVCoefficient",
				new String[]{},
				AbstractMultiDatasetMethod.MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Calculate RV Coefficient",
				getName()				
		);
		
		return pli;	
	}

}
