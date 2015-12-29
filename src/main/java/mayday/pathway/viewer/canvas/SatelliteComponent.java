package mayday.pathway.viewer.canvas;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JPanel;

import mayday.vis3.graph.components.CanvasComponent;

@SuppressWarnings("serial")
public abstract class SatelliteComponent extends JPanel
{
	private CanvasComponent parentComponent;
	private int index;
	private int numUnits;
	
	public SatelliteComponent(CanvasComponent parent, int index, int numUnits) 
	{
		super();
		this.parentComponent=parent;
		this.index=index;
		this.numUnits=numUnits;
		
		addMouseListener(parentComponent);
		addMouseWheelListener(parent);
	}
	
	public abstract void paint(Graphics g);
	
	@Override
	public Point getLocation() 
	{
		return new Point(getX(),getY());
	}
	
	public Rectangle getBounds()
	{
		return new Rectangle(getX(),getY(),getWidth(),getHeight());
	}
		
	public int getX()
	{
		if(numUnits > 4)
			return circularXPosition();
		if(index==0 && numUnits <=2)
			return parentComponent.getX()+parentComponent.getWidth()/2-getWidth()/2; // center

		if(index==0 && numUnits >2)
			return parentComponent.getX()-getWidth()/2; // left
		
		if(index==1 && numUnits==2)
			return parentComponent.getX()+parentComponent.getWidth()/2-getWidth()/2; // center
		
		if(index==1 && numUnits >2)
			return parentComponent.getX()+parentComponent.getWidth()-getWidth()/2; // right
		
		if(index==2 && numUnits==3)
			return parentComponent.getX()+parentComponent.getWidth()/2-getWidth()/2; // center
		
		if(index==2 && numUnits >3)
			return parentComponent.getX()-getWidth()/2; // left
		
		if(index==3)
			return parentComponent.getX()+parentComponent.getWidth()-getWidth()/2; // right
		
		return 0;		
	}
	
	public int getY()
	{
		if(numUnits > 4)
			return circularYPosition();
		if(index==0)
		{
			return parentComponent.getY()-getHeight()/2;
		}
		if(index==1)
		{
			if(numUnits==2)
			{
				return parentComponent.getY()+parentComponent.getHeight()-getHeight();
			}
			return parentComponent.getY()-getHeight()/2;
		}
		return parentComponent.getY()+parentComponent.getHeight()-getHeight();
		
		
	}
	
	private int circularXPosition()
	{
		double angle=index*(Math.PI*2)/numUnits;
//		int radius=Math.max(parentComponent.getWidth(),parentComponent.getHeight())/2;
		int radius=parentComponent.getWidth()/2;
		double xp= 0.0*Math.cos(angle) - (-1.0*radius)*Math.sin(angle);
		xp+=parentComponent.getBounds().getCenterX();
		xp-=getWidth()/2;
		return (int)xp;
	}
	
	private int circularYPosition()
	{
		double angle=index*(Math.PI*2)/numUnits;
//		int radius=Math.max(parentComponent.getWidth(),parentComponent.getHeight())/2;
		int radius=parentComponent.getHeight()/2;
		double yp= 0.0*Math.sin(angle) + (-1.0*radius)*Math.cos(angle);
		yp+=parentComponent.getBounds().getCenterY();
		yp-=getHeight()/2;
		return (int)yp;
	}

	/**
	 * @return the parentComponent
	 */
	public CanvasComponent getParentComponent() {
		return parentComponent;
	}

	/**
	 * @param parentComponent the parentComponent to set
	 */
	public void setParentComponent(CanvasComponent parentComponent) {
		this.parentComponent = parentComponent;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the numUnits
	 */
	public int getNumUnits() {
		return numUnits;
	}

	/**
	 * @param numUnits the numUnits to set
	 */
	public void setNumUnits(int numUnits) {
		this.numUnits = numUnits;
	}
	
		
}
