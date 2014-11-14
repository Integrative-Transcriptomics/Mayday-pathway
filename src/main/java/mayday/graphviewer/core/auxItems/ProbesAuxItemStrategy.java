package mayday.graphviewer.core.auxItems;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.components.NodeComponent;

public class ProbesAuxItemStrategy implements AuxItemStrategy
{
	private AuxItemRenderer renderer=new CircleAuxItemRenderer();
	
	@Override
	public boolean isDrawAuxItems() 
	{
		return true;
	}
	
	@Override
	public void drawAuxItem(NodeComponent comp, Graphics2D g) 
	{
		if(comp instanceof MultiProbeComponent)
		{
			Rectangle r=new Rectangle(comp.getX()-12, comp.getY()-12,24,24);			
			int n=((MultiProbeComponent)comp).getProbes().size();
			renderer.paintAuxItem(g, r, Integer.toString(n), Color.white, Color.BLUE);
		}		
	}
	
	@Override
	public String toString() 
	{
		return "Only number of probes";
	}
	
}
