package mayday.graphviewer.plugins.group;

import java.util.HashMap;
import java.util.List;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.graphviewer.plugins.misc.UpdatableTreeDialog;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class InspectGroups  extends AbstractGraphViewerPlugin 
{
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		BagTree tree=new BagTree(canvas);
		UpdatableTreeDialog dialog=new UpdatableTreeDialog(tree, canvas.getOutermostJWindow(), "Graph Structure");
		dialog.setVisible(true);	
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.InspectBags",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Show a list of all groups",
				"\0Inspect Groups"				
		);
		pli.addCategory(GROUP_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/inspect_groups.png");
		return pli;	
	}
}
