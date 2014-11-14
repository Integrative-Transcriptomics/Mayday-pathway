package mayday.graphviewer.core.edges;

import java.text.NumberFormat;

public class LogEdgeWeightTransformation extends EdgeWeightTransformation 
{
	private float base;
	
	public LogEdgeWeightTransformation(float base) {
		this.base = base;
	}

	@Override
	public float transformEdgeWeight(double w) 
	{
		float wp=(float)(Math.log(w)/Math.log(base));
		if(invert)
		{
			wp*=-1;
		}
		return Math.min(wp, (float)max);
	}
	
	@Override
	public String toString() 
	{
		return ("log "+NumberFormat.getNumberInstance().format(base));
	}

	
}
