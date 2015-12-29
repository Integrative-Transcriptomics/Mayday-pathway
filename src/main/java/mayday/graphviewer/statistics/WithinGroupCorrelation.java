package mayday.graphviewer.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.Probe;
import mayday.core.math.Statistics;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.core.SummaryOption;
import mayday.graphviewer.crossViz3.experiments.IExperimentMapping;

public class WithinGroupCorrelation extends CorrelationMethod 
{
	@Override
	public String getName() 
	{
		return "Within Groups Correlation";
	}
	
	@Override
	public ResultSet calculate(MultiHashMap<String, Probe> probes, IExperimentMapping mapping) 
	{
		ResultSet res=new ResultSet();

		res.setMethod(this);
		res.setSummary(summary.getObjectValue());

		List<String> groups=new ArrayList<String>(probes.keySet());
		List<Probe> prbs=new ArrayList<Probe>(probes.everything());
		Map<Probe, Integer> pidx=new HashMap<Probe, Integer>();
		if(doSummary.getBooleanValue())
		{
			res.setPreferredScope(ResultScope.Groups);
			res.setTitle(summary.getObjectValue().toString()+" "+method.getStringValue()+ "Correlation within groups");
			List<String> xName=new ArrayList<String>();
			xName.add("Distance");
			res.initGroups(xName,groups);
			res.setRowGroupProbes(probes);
		}
		else
		{
			res.setPreferredScope(ResultScope.Probes);
			res.setTitle(method.getStringValue()+" Correlation within groups ");
			res.initProbes(prbs);

			for(int i=0; i!= prbs.size(); ++i)
				pidx.put(prbs.get(i), i);
		}

		int i=0;
		for(int j=0; j!= groups.size(); ++j)
		{
			List<Double> vals=new ArrayList<Double>(); 
			for(Probe p1:probes.get(groups.get(i)))
			{
				for(Probe p2:probes.get(groups.get(j)))
				{
					double v=cor(getMappedValues(p1, mapping), getMappedValues(p2, mapping));
					if(!doSummary.getBooleanValue())
					{
						res.setProbeResult(pidx.get(p1),pidx.get(p2),v);
					}
					vals.add(v);
				}
			}
			if(doSummary.getBooleanValue())
			{
				if(summary.getObjectValue().equals(SummaryOption.MEAN))
					res.setGroupResult(i, j, Statistics.mean(vals));
				if(summary.getObjectValue().equals(SummaryOption.MEDIAN))
					res.setGroupResult(i, j, Statistics.median(vals));
			}			
		}
		return res;
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Statistics.CorrelationWithinGroups",
				new String[]{},
				AbstractMultiDatasetMethod.MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Calculate Correlations within Groups",
				getName()				
		);
		
		return pli;	
	}
}
