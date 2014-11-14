package mayday.graphviewer.util.components;

import java.util.Comparator;

import mayday.core.Probe;
import mayday.graphviewer.core.ModelHub;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;

public class ValueProviderComparator implements Comparator<CanvasComponent> 
{
	private ModelHub hub;
		
	public ModelHub getHub() 
	{
		return hub;
	}

	public void setHub(ModelHub hub) 
	{
		this.hub = hub;
	}

	@Override
	public int compare(CanvasComponent o1, CanvasComponent o2) 
	{
		if(! (o1 instanceof MultiProbeComponent) && (o2 instanceof MultiProbeComponent))
		{
			return -1;			
		}
		if( !(o1 instanceof MultiProbeComponent) && !(o2 instanceof MultiProbeComponent))
		{
			return 0;			
		}
		if((o1 instanceof MultiProbeComponent) && !(o2 instanceof MultiProbeComponent))
		{
			return 1;			
		}
		
		MultiProbeComponent m1=((MultiProbeComponent)o1);
		MultiProbeComponent m2=((MultiProbeComponent)o2);
		
		double v1=0;
		double v2=0;
		
		for(Probe p: m1.getProbes())
		{
			v1+=hub.getValueProvider(p.getMasterTable().getDataSet()).getValue(p);
		}
		v1 /= (1.0* m1.getProbes().size());
		for(Probe p: m2.getProbes())
		{
			v2+=hub.getValueProvider(p.getMasterTable().getDataSet()).getValue(p);
		}
		v2 /= (1.0* m2.getProbes().size());
		
		return Double.compare(v1, v2);
	}
	
	@Override
	public String toString() 
	{
		return "Probe Values";
	}

}
