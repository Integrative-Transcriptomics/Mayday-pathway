package mayday.graphviewer.TimeSeriesBitmap;

import java.awt.geom.Point2D;

public abstract class ChaosGame 
{
	protected Point2D p; 
	
	public ChaosGame() {
		p=new Point2D.Double(.5,.5);
	}
	
	public Point2D getP() {
		return p;
	}



	public void setP(Point2D p) {
		this.p = p;
	}



	public abstract Point2D iterate(char c);
}
