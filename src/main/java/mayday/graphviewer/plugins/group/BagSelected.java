package mayday.graphviewer.plugins.group;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.graphviewer.action.GraphBagModel;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.core.bag.ComponentBag;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class BagSelected  extends AbstractGraphViewerPlugin 
{
	private ComponentBag lastBag;
	
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		if(components.isEmpty()) return; // nothing to do. 
		ComponentBag bag=new ComponentBag((SuperModel)model);
		bag.setName("Grouped Nodes");
		bag.setColor(Color.red);
		for(CanvasComponent c:components)
		{
			bag.addComponent(c);				
		}
		((GraphBagModel)model).addBag(bag);
		canvas.updatePlot();
		lastBag=bag;
	}
	
	public ComponentBag getLastBag() {
		return lastBag;
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.BagSelected",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Arrange the selected nodes to be grouped together.",
				"\0Group selected Nodes"				
		);
		pli.addCategory(GROUP_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/group.png");
		return pli;	
	}
}

