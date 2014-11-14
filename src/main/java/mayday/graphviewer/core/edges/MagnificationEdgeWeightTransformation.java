package mayday.graphviewer.core.edges;

public class MagnificationEdgeWeightTransformation extends EdgeWeightTransformation
{
	@Override
	public float transformEdgeWeight(double w) 
	{
		float wp=(float) (invert?w/magnification : w*magnification);
		return Math.min(wp, (float)max);
	}
	
	@Override
	public String toString() 
	{
		return "Scale";
	}
}
