package mayday.graphviewer.core.edges;

public abstract class EdgeWeightTransformation 
{
	protected boolean invert;
	protected double max;
	protected double magnification;
	
	public abstract float transformEdgeWeight(double w);

	
	public boolean isInvert() {
		return invert;
	}

	public void setInvert(boolean invert) {
		this.invert = invert;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public double getMagnification() {
		return magnification;
	}

	public void setMagnification(double magnification) {
		this.magnification = magnification;
	}
	
	

}
