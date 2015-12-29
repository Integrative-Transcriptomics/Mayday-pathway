package mayday.graphviewer.core.auxItems;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Map;
import java.util.Map.Entry;

import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.vis3.graph.components.NodeComponent;

public class DefaultAuxItemStrategy extends NoneAuxItemStrategy
{
	protected Map<String, AuxItemRenderer> rendererMap;
	
	public DefaultAuxItemStrategy() 
	{
		
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
				continue; // do not fall into general case. 
			}
			if(s.getKey().equals(AuxItems.Keys.STATE_VARIABLE) )
			{
				states=s.getValue().split(";");
				numComp+=states.length;
				continue; // do not fall into general case. 
			}			
			if(AuxItems.keyMap.containsKey(s.getKey()))
			{
				numComp++;				
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
		size=new Dimension(24, 24);
		for(Entry<String,String> s: ((DefaultNode)comp.getNode()).getProperties().entrySet())
		{
			
			Rectangle r=new Rectangle(
					getX(comp.getBounds(), size, i, numComp), 
					getY(comp.getBounds(), size, i, numComp),
					size.width,size.height
					);
						
			if(AuxItems.keyMap.containsKey(s.getKey()))
			{
				AuxItems item=AuxItems.keyMap.get(s.getKey());
				switch (item) {
				case STAR: // ft
				case BOX: // ft
				case CIRCLE: // ft	
				case TRIANGLE:
					Color c=parseColor(s.getValue());
					c= (c==null?item.getFillColor():c);
					item.getRenderer().paintAuxItem(g, r, "", item.getLineColor(), c);
					break;
				case QUESTION: //ft
				case WARNING: //ft
				case IMPORTANT:	
					item.getRenderer().paintAuxItem(g, r, item.getDefaultText(), item.getLineColor(), item.getFillColor());
					break;
				case NOTE:
					item.getRenderer().paintAuxItem(g, r, s.getValue(), item.getLineColor(), item.getFillColor());
					break;
				default: // sbgn uofi and state are treated directly
					break;
				}
				++i;
				
			}
		}
			
	}
	
	public static Color parseColor(String v)
	{
		try{
			Color c=Color.decode("0x"+v);
			return c;			
		} catch (NumberFormatException e) 
		{
			return null;
		}
	}
	
	@Override
	public boolean isDrawAuxItems() 
	{
		return true;
	}
	
	@Override
	public String toString() 
	{
		return "All auxiliary items";
	}
}
