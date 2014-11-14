package mayday.graphviewer.plugins.extend;

import java.util.HashMap;
import java.util.List;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.graphviewer.action.AddNodeAction;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class AddNodes extends AbstractGraphViewerPlugin  
{
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		new AddNodeAction(canvas).actionPerformed(null);
	}


	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.AddNodes",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Add new nodes to the graph",
				"Add Nodes..."				
		);
		pli.setIcon("mayday/pathway/gvicons/node.png");
		pli.addCategory(EXTEND_CATEGORY);
		return pli;	
	}
}
