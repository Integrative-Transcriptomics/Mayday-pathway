/**
 * 
 */
package mayday.graphviewer.crossViz3.unitTree;

import mayday.core.Probe;

public class ProbeTreeObject
{
	public Probe probe;
	public double[] values;
	
	public ProbeTreeObject(Probe probe, double[] values) 
	{
		super();
		this.probe = probe;
		this.values = values;
	}	
}