package mayday.graphviewer.crossViz3.corheatmap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.graphviewer.crossViz3.experiments.IExperimentMapping;
import mayday.graphviewer.crossViz3.probes.IProbeMapping;
import mayday.graphviewer.crossViz3.probes.IProbeUnit;
import mayday.graphviewer.statistics.Correlations;

@SuppressWarnings("serial")
public class CorHeatMapModel extends DefaultTableModel
{
	private double[] cors;
	
	private String correlationMeasure;
	private ObjectSelectionSetting<DataSet> left;
	private ObjectSelectionSetting<DataSet> right;
	
	private List<IProbeUnit> units;
	private IExperimentMapping experimentMapping; 
	
	public CorHeatMapModel(String correlationMeasure,
			ObjectSelectionSetting<DataSet> left,
			ObjectSelectionSetting<DataSet> right, IProbeMapping probeMapping,
			IExperimentMapping experimentMapping) {
		
		this.left = left;
		this.right = right;		
		this.experimentMapping = experimentMapping;
		
		units=new ArrayList<IProbeUnit>(probeMapping.getNumberOfUnits());
		for(IProbeUnit u:probeMapping)
		{
			units.add(u);
		}		
		setCorrelationMeasure(correlationMeasure);
	}

	@Override
	public int getRowCount() 
	{
		if(units==null) return 0;
		return units.size();
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) 
	{
		switch (columnIndex) {
		case 0: return String.class;
		case 1: return Collection.class;
		case 2: return Double.class;
		case 3: return Collection.class;
	

		default:
			return super.getColumnClass(columnIndex);
		}
	}
	
	@Override
	public int getColumnCount() 
	{
		return 4;
	}
	
	@Override
	public String getColumnName(int column)
	{
		switch (column) {
		case 0: return "Gene";
		case 1: return left.getObjectValue().getName();
		case 2: return correlationMeasure;
		case 3: return right.getObjectValue().getName();
	

		default:
			return super.getColumnName(column);
		}
	}
	
	@Override
	public Object getValueAt(int row, int column) 
	{
		if(column==2)
				return cors[row];
		
		IProbeUnit unit=units.get(row);
		
		List<Probe> res=null;
		switch (column) 
		{
		case 0: return unit.getName();
		case 1: 
			res=new ArrayList<Probe>();
			Probe p=unit.getProbeForDataset(left.getObjectValue());
			if(p!=null)
				res.add(p);
			return res;
		case 3: 
			res=new ArrayList<Probe>();
			p=unit.getProbeForDataset(right.getObjectValue());
			if(p!=null)
				res.add(p);
		return res;	
			
			

		default:
			return super.getValueAt(row, column);
		}
		
		
		
	}
	
	public void setCorrelationMeasure(String correlationMeasure) 
	{
		this.correlationMeasure = correlationMeasure;
		
		cors=new double[units.size()];
		int i=0;
		for(IProbeUnit u:units)
		{
			Probe l=u.getProbeForDataset(left.getObjectValue());
			Probe r=u.getProbeForDataset(right.getObjectValue());
			if(l==null || r==null)
			{
				cors[i]=Double.NaN;
				++i;
				continue;
			}
			if(correlationMeasure.equals(CorHeatmap.CORRELATION_MEASURES[0]))
				cors[i]=Correlations.cor(getMappedValues(l, experimentMapping), getMappedValues(r, experimentMapping));			
			if(correlationMeasure.equals(CorHeatmap.CORRELATION_MEASURES[1]))
				cors[i]=Correlations.spearman(getMappedValues(l, experimentMapping), getMappedValues(r, experimentMapping));
			if(correlationMeasure.equals(CorHeatmap.CORRELATION_MEASURES[2]))
				cors[i]=Correlations.kendall(getMappedValues(l, experimentMapping), getMappedValues(r, experimentMapping));
			++i;
		}
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
	public boolean isCellEditable(int row, int column) 
	{
		return false;
	}
	

	
}
