package mayday.graphviewer.util.components;

import java.util.Comparator;

import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;

public class ProbeCountComparator implements Comparator<CanvasComponent> {

	@Override
	public int compare(CanvasComponent o1, CanvasComponent o2) 
	{
		return ((MultiProbeComponent)o2).getProbes().size()-((MultiProbeComponent)o1).getProbes().size();
	}

	@Override
	public String toString() 
	{
		return "Number of Probes";
	}
}
