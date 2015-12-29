package mayday.graphviewer.util.components;

import java.util.Comparator;

import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.NodeComponent;

public class DegreeComparator implements Comparator<CanvasComponent> 
{
	public static final int OVERALL_DEGREE=0;
	public static final int IN_DEGREE=1;
	public static final int OUT_DEGREE=2;

	int mode=0;

	public DegreeComparator() {
	}

	public DegreeComparator(int mode) {
		this.mode=mode;
	}

	@Override
	public int compare(CanvasComponent o1, CanvasComponent o2)
	{
		switch (mode) 
		{
			case OVERALL_DEGREE:return ((NodeComponent)o1).getNode().getDegree()-((NodeComponent)o2).getNode().getDegree();
			case IN_DEGREE:return ((NodeComponent)o1).getNode().getInDegree()-((NodeComponent)o2).getNode().getInDegree();
			case OUT_DEGREE:return ((NodeComponent)o1).getNode().getOutDegree()-((NodeComponent)o2).getNode().getOutDegree();
	
			default:
				throw new IllegalArgumentException("No such mode.");
		}
	}
	
	@Override
	public String toString() 
	{
		switch(mode)
		{
			case OVERALL_DEGREE: return "Overall Degree";
			case IN_DEGREE: return "InDegree";
			case OUT_DEGREE: return "OutDegree";
			default: return super.toString();
		}		
	}
}
