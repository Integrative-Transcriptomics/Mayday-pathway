package mayday.graphviewer.statistics;

import java.util.List;

import mayday.core.Probe;
import mayday.core.structures.linalg.matrix.DoubleMatrix;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.core.SummaryOption;

public class ResultSet 
{
	private DoubleMatrix probeResults;
	private DoubleMatrix groupResults;
	
	
	private MultiDataSetMethod method;
	private SummaryOption summary;
	private ResultScope preferredScope;
	private String title;
	
	private List<Probe> rowProbes;
	private MultiHashMap<String, Probe> rowGroupProbes;
	
	public ResultSet() 
	{		
	}


	public DoubleMatrix getProbeResults() {
		return probeResults;
	}


	public void setProbeResults(DoubleMatrix probeResults) {
		this.probeResults = probeResults;
	}


	public DoubleMatrix getGroupResults() {
		return groupResults;
	}


	public void setGroupResults(DoubleMatrix groupResults) {
		this.groupResults = groupResults;
	}


	public MultiDataSetMethod getMethod() {
		return method;
	}


	public void setMethod(MultiDataSetMethod method) {
		this.method = method;
	}


	public SummaryOption getSummary() {
		return summary;
	}


	public void setSummary(SummaryOption summary) {
		this.summary = summary;
	}


	public ResultScope getPreferredScope() {
		return preferredScope;
	}


	public void setPreferredScope(ResultScope preferredScope) {
		this.preferredScope = preferredScope;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}
	
	public void initGroups(List<String> names)
	{
		groupResults=new DoubleMatrix(names.size(), names.size());
		groupResults.setRowNames(names);
		groupResults.setColumnNames(names);
	}
	
	public void initGroups(List<String> rowNames, List<String> colNames)
	{
		groupResults=new DoubleMatrix(rowNames.size(), colNames.size());
		groupResults.setRowNames(rowNames);
		groupResults.setColumnNames(colNames);
	}
	
	public void initProbes(List<Probe> probes)
	{
		this.rowProbes=probes;
		probeResults=new DoubleMatrix(probes.size(), probes.size());
		int i=0;
		for(Probe p:probes)
		{
			probeResults.setRowName(i, p.getDisplayName());
			probeResults.setColumnName(i, p.getDisplayName());
			++i;
		}
	}
	
	public void initProbes(List<Probe> rowProbes,List<Probe> colProbes)
	{
		this.rowProbes=rowProbes;
		probeResults=new DoubleMatrix(rowProbes.size(), colProbes.size());
		int i=0;
		for(Probe p:rowProbes)
		{
			probeResults.setRowName(i, p.getDisplayName());
			++i;
		}
		i=0;
		for(Probe p:colProbes)
		{
			probeResults.setColumnName(i, p.getDisplayName());
			++i;
		}
	}
	
	public void setGroupResult(int row, int col, double v)
	{
		groupResults.setValue(row, col, v);
	}

	public void setProbeResult(int x, int y, double v)
	{
		probeResults.setValue(x, y, v);
	}
	
	@Override
	public String toString() 
	{
		return title;
	}
	
	public double groupMin()
	{
		return groupResults.getMinValue(false);
	}
	
	public double groupMax()
	{
		return groupResults.getMaxValue(false);
	}
	
	public double probeMin()
	{
		return probeResults.getMinValue(false);
	}
	
	public double probeMax()
	{
		return probeResults.getMaxValue(false);
	}
	
	public List<Probe> getRowProbes() 
	{
		return rowProbes;
	}
	
	public void setRowProbes(List<Probe> rowProbes) 
	{
		this.rowProbes = rowProbes;
	}
	
	public void setRowGroupProbes(MultiHashMap<String, Probe> rowGroupProbes) 
	{
		this.rowGroupProbes = rowGroupProbes;
	}
	
	public MultiHashMap<String, Probe> getRowGroupProbes() 
	{
		return rowGroupProbes;
	}
	
}
