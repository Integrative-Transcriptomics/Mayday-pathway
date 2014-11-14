package mayday.graphviewer.plugins;

import java.util.List;

import mayday.graphviewer.core.GraphViewerPlot;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public interface GraphViewerPlugin 
{
	public static final String MC_GRAPH="GraphViewer/Extension";
	public static final String MC_GRAPH_GRAPH="GraphViewer/Extension/Graph";
	public static final String MC_GRAPH_CROSS_DATASET="GraphViewer/Extension/CrossDataSet";
	
	public static final String IMPORT_CATEGORY="\0Import";
	public static final String EXPORT_CATEGORY="\0Export";
	public static final String GROUP_CATEGORY="\0Group";
	public static final String EXTEND_CATEGORY="\0Extend";
	public static final String MI_CATEGORY="\0Meta Information";
	public static final String CONNECT_CATEGORY="\0Connect";
	public static final String SHRINK_CATEGORY="\0Shrink";
	public static final String FILTER_CATEGORY="\0Filter";
	public static final String ARRANGE_CATEGORY="\0Arrange";
	public static final String CROSS_DS_CATEGORY="\0Cross Dataset";
	public static final String ANIMATE_CATEGORY="Animate";
	
	public abstract void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components);
	


}
