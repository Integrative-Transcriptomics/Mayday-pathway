package mayday.graphviewer.core.auxItems;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Map.Entry;

import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.vis3.graph.components.NodeComponent;

public class SBGNAuxItemStrategy extends NoneAuxItemStrategy
{
	@Override
	public boolean isDrawAuxItems() 
	{
		return true;
	}

	@Override
	public void drawAuxItem(NodeComponent comp, Graphics2D g) 
	{
		int numComp=0;
		String[] units=new String[0];
		String[] states=new String[0];
		for(Entry<String,String> s: ((DefaultNode)comp.getNode()).getProperties().entrySet())
		{
			if(s.getKey().equals(AuxItems.Keys.UNIT_OF_INFORMATION) )
			{
				units=s.getValue().split(";");
				numComp+=units.length;
			}
			if(s.getKey().equals(AuxItems.Keys.STATE_VARIABLE) )
			{
				states=s.getValue().split(";");
				numComp+=states.length;
			}
		}		
		if(numComp==0)
			return;

		int i=0;
		Dimension size=new Dimension(45, 16);		
		for(String s: units)
		{
			Rectangle r=new Rectangle(getX(comp.getBounds(), size, i, numComp), getY(comp.getBounds(), size, i, numComp),size.width,size.height);
			AuxItems.UNIT_OF_INFORMATION.getRenderer().paintAuxItem(g, r, s, Color.black, Color.white);
			++i;
		}
		for(String s: states)
		{
			Rectangle r=new Rectangle(getX(comp.getBounds(), size, i, numComp), getY(comp.getBounds(), size, i, numComp),size.width,size.height);
			AuxItems.STATE_VARIABLE.getRenderer().paintAuxItem(g, r, s, Color.black, Color.white);
			++i;
		}
	}
	
	@Override
	public String toString() 
	{
		return "Only SBGN auxiliary items";
	}

}
