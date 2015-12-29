package mayday.graphviewer.crossViz3.unitTree;

import mayday.graphviewer.crossViz3.probes.IProbeUnit;


public class UnitTreeObject 
{
	public IProbeUnit unit;
	public double[] values;
	
	public UnitTreeObject(IProbeUnit unit, double[] values) 
	{
		super();
		this.unit = unit;
		this.values = values;
	}	
	
}
