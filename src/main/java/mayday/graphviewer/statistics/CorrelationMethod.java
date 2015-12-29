package mayday.graphviewer.statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.Probe;
import mayday.core.math.Statistics;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.core.SummaryOption;
import mayday.graphviewer.core.SummaryOptionSetting;
import mayday.graphviewer.crossViz3.experiments.IExperimentMapping;

public class CorrelationMethod extends AbstractMultiDatasetMethod 
{
	public static final String[] CORRELATION_METHODS={"Pearson's r","Spearman's rho","Kendall's tau","Covariance"};
	protected RestrictedStringSetting method;
	protected BooleanHierarchicalSetting doSummary;
	protected SummaryOptionSetting summary;
	protected BooleanSetting absolute;

	public CorrelationMethod() 
	{
		super();
		setting=new HierarchicalSetting(getName());
		method=new RestrictedStringSetting("Method", null, 0, CORRELATION_METHODS);
		summary=new SummaryOptionSetting("Summarize");
		doSummary=new BooleanHierarchicalSetting("Summarize pairwise correlations", null, true);
		absolute=new BooleanSetting("Absolute value", null, false);
		setting.addSetting(method);
		doSummary.addSetting(summary);
		setting.addSetting(doSummary);	
		setting.addSetting(absolute);
	}
	
	@Override
	public ResultSet calculate(MultiHashMap<String, Probe> probes,IExperimentMapping mapping) 
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
			res.setTitle(summary.getObjectValue().toString()+" "+method.getStringValue());
			res.initGroups(groups);
			res.setRowGroupProbes(probes);
		}
		else
		{
			res.setPreferredScope(ResultScope.Probes);
			res.setTitle(method.getStringValue()+" Correlation ");
			res.initProbes(prbs);
			
			for(int i=0; i!= prbs.size(); ++i)
				pidx.put(prbs.get(i), i);
		}
		
		boolean abs=absolute.getBooleanValue();
		for(int i=0; i!= groups.size(); ++i)
		{
			for(int j=0; j!= groups.size(); ++j)
			{
				if(i==j)
				{
					if(doSummary.getBooleanValue())
					{
						res.setGroupResult(i,j,0);
						continue;
					}
				}
				List<Double> vals=new ArrayList<Double>(); 
				for(Probe p1:probes.get(groups.get(i)))
				{
					for(Probe p2:probes.get(groups.get(j)))
					{
						double v=cor(getMappedValues(p1, mapping), getMappedValues(p2, mapping));
						if(abs)
							v=Math.abs(v);
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
		}
		return res;
		
		

	}
	
	protected double cor(double[] x, double[] y)
	{
		switch (method.getSelectedIndex()) 
		{
		case 0: return Correlations.cor(x, y);
		case 1: return Correlations.spearman(x, y);
		case 2: return Correlations.kendall(x, y);	
		case 3: return Correlations.cov(x, y);
		default:
			return Double.NaN;
		}
		
	}

	@Override
	public String getName() 
	{
		return "Correlation";
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Statistics.Correlation",
				new String[]{},
				AbstractMultiDatasetMethod.MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Calculate Correlations between Groups",
				getName()				
		);
		
		return pli;	
	}

}
