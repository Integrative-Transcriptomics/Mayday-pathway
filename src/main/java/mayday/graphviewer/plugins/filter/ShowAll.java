package mayday.graphviewer.plugins.filter;

import java.util.HashMap;
import java.util.List;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class ShowAll extends AbstractGraphViewerPlugin  
{
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		for(CanvasComponent cc: model.getComponents())
		{
			if(!cc.isVisible())
			{
				cc.setVisible(true);
			}
			cc.restore();
		}
		canvas.updateSize();
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.ShowAll",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Show and restore all components. ",
				"Show all Nodes"				
		);
		pli.addCategory(FILTER_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/showall.png");
		return pli;	
	}


}

