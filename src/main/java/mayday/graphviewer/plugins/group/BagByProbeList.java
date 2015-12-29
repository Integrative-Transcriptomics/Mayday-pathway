package mayday.graphviewer.plugins.group;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.graphviewer.action.GraphBagModel;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.bag.ComponentBag;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.layout.ProbeListLayout;
import mayday.vis3.graph.model.GraphModel;

public class BagByProbeList  extends AbstractGraphViewerPlugin 
{
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		Rectangle bounds=canvas.getBounds();
		int y=0;
		for(DataSet ds: canvas.getModelHub().getDataSets())
		{
			List<ComponentBag> bags=new ArrayList<ComponentBag>();
			for(ProbeList pl:canvas.getModelHub().getViewModel(ds).getProbeLists(false))		
			{
				ComponentBag bag=new ComponentBag((GraphBagModel)model);
				bag.setName(pl.getName());
				bag.setColor(pl.getColor());
				for(Probe p: pl.getAllProbes())
				{
					for(MultiProbeComponent c: ((GraphBagModel)model).getComponents(p))
					{
						bag.addComponent(c);
					}
		
				}
				((GraphBagModel)model).addBag(bag);
				bags.add(bag);
			}			
			new ProbeListLayout(canvas.getModelHub().getViewModel(ds)).layout(canvas, bounds, model);
			for(ComponentBag bag:bags)
			{
				if(bag.getBoundingRect().getMaxY() > y)
				{
					y=(int)bag.getBoundingRect().getMaxY();
				}
			}
			y+=25;
			bounds=new Rectangle(0,y,bounds.width, canvas.getHeight());
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
				"PAS.GraphViewer.BagByProbeList",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Arrange the Probes to be bagged together by probe lists.",
				"Group by Probe List"				
		);
		pli.addCategory(GROUP_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/groupbyprobelist.png");		
		return pli;	
	}
}
