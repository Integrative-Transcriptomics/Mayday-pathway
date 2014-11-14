package mayday.graphviewer.plugins.group;

import java.util.HashMap;
import java.util.List;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.core.bag.ComponentBag;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class RemoveAllBags  extends AbstractGraphViewerPlugin 
{
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		for(ComponentBag bag: ((SuperModel)model).getBags())
		{
			bag.setComponentsVisible(true);			
			((SuperModel)model).getComponent(bag).setVisible(false);
			canvas.remove(((SuperModel)model).getComponent(bag));
			
		}		
		canvas.updatePlot();		
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.RemoveBags",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Remove all Groups from the graph, retain the contents.",
				"Remove all Groups"				
		);
		pli.addCategory(GROUP_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/removegroups.png");	
		return pli;	
	}
}
