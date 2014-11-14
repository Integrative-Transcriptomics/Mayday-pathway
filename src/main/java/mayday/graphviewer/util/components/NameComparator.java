package mayday.graphviewer.util.components;

import java.util.Comparator;

import mayday.vis3.graph.components.CanvasComponent;

public class NameComparator implements Comparator<CanvasComponent> 
{
	public int compare(CanvasComponent o1, CanvasComponent o2) 
	{
		return o1.getLabel().compareTo(o2.getLabel());
	}
	
	@Override
	public String toString() 
	{
		return "Component Name";
	}
}
