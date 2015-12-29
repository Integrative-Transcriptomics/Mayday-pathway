package mayday.graphviewer.core.ccp;

import java.util.List;

import mayday.core.structures.graph.Edge;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class GraphWrap 
{
	public GraphModel model;
	public GraphViewerPlot viewer;
	public List<CanvasComponent> components;
	public List<Edge> edges;	
}
