package mayday.graphviewer.core.edges;

public class EmptyEdgeWeightTransformation extends EdgeWeightTransformation {

	
	@Override
	public float transformEdgeWeight(double w) 
	{
		float wp=0;
		if(invert)
		{
			wp= (float)(1.0/w); 
		}else
		{
			wp=  (float)w;
		}
		return Math.min(wp, (float)max);
	}

	@Override
	public String toString() 
	{
		return ("Identity");
	}
}
