package mayday.pathway.viewer.canvas;

import java.awt.Container;
import java.awt.Rectangle;

import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.layout.CanvasLayouter;
import mayday.vis3.graph.model.GraphModel;

/**
 * @author Stephan Symons
 *
 */
public class AlignLayouter implements CanvasLayouter
{
	private static AlignLayouter sharedInstance=new AlignLayouter();
	private int margin=100;
	
	public AlignLayouter() 
	{
		margin=100;
	}
	
	public AlignLayouter(int margin) 
	{
		this.margin=margin;
	}
	
	public void layout(Container container, Rectangle bounds, GraphModel model) 
	{
		int minX=Integer.MAX_VALUE;
		int minY=Integer.MAX_VALUE;
		for(CanvasComponent comp:model.getComponents())
		{
			if(comp.getX() < minX)
				minX=comp.getX();
			if(comp.getY() < minY)
				minY=comp.getY();
		}
		
		int adjX=margin-minX;
		int adjY=margin-minY;
		
		
		for(CanvasComponent comp:model.getComponents())
		{
			comp.setLocation(comp.getX()+adjX, comp.getY()+adjY);
		}	

	}

	/**
	 * @return the margin
	 */
	public int getMargin() {
		return margin;
	}

	/**
	 * @param margin the margin to set
	 */
	public void setMargin(int margin) {
		this.margin = margin;
	}
	
	public static AlignLayouter getInstance()
	{
		return sharedInstance;
	}
	
	
}
