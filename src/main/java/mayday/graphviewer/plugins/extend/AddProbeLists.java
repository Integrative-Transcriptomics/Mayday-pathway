package mayday.graphviewer.plugins.extend;

import java.util.HashMap;
import java.util.List;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.graphviewer.action.AddProbeListAction;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class AddProbeLists extends AbstractGraphViewerPlugin  
{
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		new AddProbeListAction(canvas).actionPerformed(null);
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.AddProbeLists",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Add probe lists to the graph",
				"Add ProbeLists..."				
		);
		pli.setIcon("mayday/pathway/gvicons/probelist.png");
		pli.addCategory(EXTEND_CATEGORY);
		return pli;	
	}
}

