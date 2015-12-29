package mayday.graphviewer.core.auxItems;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import mayday.vis3.graph.components.NodeComponent;

public class NoneAuxItemStrategy implements AuxItemStrategy {

	@Override
	public void drawAuxItem(NodeComponent comp, Graphics2D g) 
	{
		//do nothing; 
	}

	@Override
	public boolean isDrawAuxItems() 
	{		
		return false;
	}
	
	public int getX(Rectangle bounds, Dimension size, int index, int numUnits)
	{
		if(numUnits > 4)
			return circularXPosition(bounds,size,index,numUnits);
		if(index==0 && numUnits <=2)
			return bounds.x+bounds.width/2-size.width/2; // center

		if(index==0 && numUnits >2)
			return bounds.x-size.width/2; // left
		
		if(index==1 && numUnits==2)
			return bounds.x+bounds.width/2-size.width/2; // center
		
		if(index==1 && numUnits >2)
			return bounds.x+bounds.width-size.width/2; // right
		
		if(index==2 && numUnits==3)
			return bounds.x+bounds.width/2-size.width/2; // center
		
		if(index==2 && numUnits >3)
			return bounds.x-size.width/2; // left
		
		if(index==3)
			return bounds.x+bounds.width-size.width/2; // right
		
		return 0;		
	}
	
	public int getY(Rectangle bounds, Dimension size, int index, int numUnits)
	{
		if(numUnits > 4)
			return circularYPosition(bounds,size,index,numUnits);
		if(index==0)
		{
			return bounds.y-size.height/2;
		}
		if(index==1)
		{
			if(numUnits==2)
			{
				return bounds.y+bounds.height-size.height/2;
			}
			return bounds.y-size.height/2;
		}
		return bounds.y+bounds.height-size.height/2;
		
		
	}
	
	private int circularXPosition(Rectangle bounds, Dimension size, int index, int numUnits)
	{
		double angle=index*(Math.PI*2)/numUnits;
//		int radius=Math.max(bounds.width,bounds.height)/2;
		int radius=bounds.width/2;
		double xp= 0.0*Math.cos(angle) - (-1.0*radius)*Math.sin(angle);
		xp+=bounds.getCenterX();
		xp-=size.width/2;
		return (int)xp;
	}
	
	private int circularYPosition(Rectangle bounds, Dimension size, int index, int numUnits)
	{
		double angle=index*(Math.PI*2)/numUnits;
		int radius=bounds.height/2;
		double yp= 0.0*Math.sin(angle) + (-1.0*radius)*Math.cos(angle);
		yp+=bounds.getCenterY();
		yp-=size.height/2;
		return (int)yp;
	}
	
	@Override
	public String toString() 
	{
		return "Do not draw auxiliary items";
	}

}
