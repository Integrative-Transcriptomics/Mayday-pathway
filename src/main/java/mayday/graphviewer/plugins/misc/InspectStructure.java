package mayday.graphviewer.plugins.misc;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.List;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class InspectStructure  extends AbstractGraphViewerPlugin 
{
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		GraphStructureTree tree=new GraphStructureTree(canvas);
		UpdatableTreeDialog dialog=new UpdatableTreeDialog(tree, canvas.getOutermostJWindow(), "Graph Structure");
		dialog.add(new GraphOverviewPanel(canvas), BorderLayout.NORTH);
		dialog.setVisible(true);
	}
	
	

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.InspectStructure",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Show a list of graph components",
				"Inspect Structure"				
		);		
		pli.setIcon("mayday/pathway/gvicons/inspect_graph.png");
		return pli;	
	}
}
