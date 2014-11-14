package mayday.graphviewer.plugins.arrange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Node;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.layout.FruchtermanReingoldLayout;
import mayday.vis3.graph.model.GraphModel;

public class Bounce extends AbstractGraphViewerPlugin 
{
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		FruchtermanReingoldLayout layouter=new FruchtermanReingoldLayout(1);
		layouter.setRandomInit(false);
			
		List<Node> nodes=new ArrayList<Node>();
		for(CanvasComponent cc:components)
		{
			nodes.add(model.getNode(cc));
			
		}
		layouter.layout(canvas, canvas.getBounds(), model);

		
		canvas.revalidateEdges();
		
	}
	
	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.BouncePlugin",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Resolve overlapping nodes",
				"Perturbate Layout"				
		);
		pli.addCategory(ARRANGE_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/perturbatelayout.png");
		return pli;	
	}
	
	
}
