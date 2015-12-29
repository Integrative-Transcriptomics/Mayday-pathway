package mayday.graphviewer.util.components;

import java.util.Comparator;

import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.NodeComponent;

public class PropertyComparator implements Comparator<CanvasComponent> {

	private String property;
	
	public PropertyComparator() {}
		
	public PropertyComparator(String property) {
		this.property = property;
	}

	@Override
	public int compare(CanvasComponent o1, CanvasComponent o2) 
	{
		Node n1= ((NodeComponent)o1).getNode();
		Node n2= ((NodeComponent)o2).getNode();
		
		String s1=((DefaultNode)n1).getPropertyValue(property);
		String s2=((DefaultNode)n2).getPropertyValue(property);
		
		if(s1 ==null && s2 != null)
			return -1;
		
		if(s1 !=null && s2 == null)
			return 1;
		
		if(s1 ==null && s2 == null)
			return 0;
		
		return s1.compareTo(s2);		
	}
	
	@Override
	public String toString() 
	{
		return "Node Property";
	}
	
	public void setProperty(String property) {
		this.property = property;
	}

}
