package mayday.graphviewer.util.components;

import java.util.Comparator;

import mayday.core.Probe;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;

public class ComponentProbesPropertyComparator implements Comparator<CanvasComponent> 
{
	private int mode; 
	
	public static final int MEAN=0;
	public static final int VAR=1;
	public static final int SD=2;
	public static final int MIN=3;
	public static final int MAX=4;
	
	
	public ComponentProbesPropertyComparator() 
	{
		mode=MEAN;
	}
	
	public ComponentProbesPropertyComparator(int m) 
	{
		mode=m;
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
			v1+=getValue(p);
		}
		v1 /= (1.0* m1.getProbes().size());
		for(Probe p: m2.getProbes())
		{
			v2+=getValue(p);
		}
		v2 /= (1.0* m2.getProbes().size());
		
		return Double.compare(v1, v2);
	}
	
	private double getValue(Probe p)
	{
		switch (mode)
		{
			case MEAN:
				return p.getMean();
			case VAR:
				return p.getVariance();
			case SD:
				return p.getStandardDeviation();
			case MIN:
				return p.getMinValue();
			case MAX:
				return p.getMaxValue();	

		default:
			throw new IllegalStateException("Illegal mode");
		}
	}
	
	@Override
	public String toString() 
	{
		switch (mode)
		{
		case MEAN:
			return "Probe Mean";
		case VAR:
			return "Probe Variance";
		case SD:
			return "Probe Standard Deviation";
		case MIN:
			return "Probe Minimum";
		case MAX:
			return "Probe Maximum";	

	default:
		throw new IllegalStateException("Illegal mode");
		}
	}

}
