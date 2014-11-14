package mayday.graphviewer.util.components;

import java.util.Comparator;

import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.NodeComponent;

public class WeightComparator implements Comparator<CanvasComponent> 
{
	@Override
	public int compare(CanvasComponent o1, CanvasComponent o2) 
	{
		double w1=0;
		Graph g=((NodeComponent)o1).getNode().getGraph();
		for(Edge e: g.getAllEdges(((NodeComponent)o1).getNode()) )
		{
			w1+=e.getWeight();
		}
		g=((NodeComponent)o2).getNode().getGraph();
		double w2=0;
		for(Edge e: g.getAllEdges(((NodeComponent)o2).getNode()))
		{
			w2+=e.getWeight();
		}
		return -Double.compare(w1, w2);
	}
	
	@Override
	public String toString() 
	{
		return "Adjacent Edge Weight";
	}
}
