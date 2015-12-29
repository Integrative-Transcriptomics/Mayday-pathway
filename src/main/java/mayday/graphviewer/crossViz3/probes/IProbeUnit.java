package mayday.graphviewer.crossViz3.probes;

import java.util.List;
import java.util.Set;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.graphviewer.core.SummaryOption;
import mayday.graphviewer.crossViz3.experiments.IExperimentMapping;

public interface IProbeUnit extends Comparable<IProbeUnit>
{
	public List<Probe> getProbes();	
	public Set<DataSet> getDataSets();
	
	public int getNumberOfProbes();
	public int getNumberOfDataSets();
	
	public void addProbe(Probe p);
	
	public void setName(String name);
	public String getName();
	
	public boolean isCollapsable();
	public double[] getCollapsedValues(IExperimentMapping mapping);
	
	public List<double[]> getValues(IExperimentMapping mapping);
		
	public double[] summarize(SummaryOption summary, IExperimentMapping mapping);
	
	public Probe getProbeForDataset(DataSet ds);
}
