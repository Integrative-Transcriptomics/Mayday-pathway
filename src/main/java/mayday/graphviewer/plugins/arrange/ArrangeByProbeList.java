package mayday.graphviewer.plugins.arrange;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;

import mayday.core.ProbeList;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.maps.MultiTreeMap;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.GraphModel;
import mayday.vis3.model.ViewModel;

public class ArrangeByProbeList extends AbstractGraphViewerPlugin {

	private static int xSpace=25;
	private static int ySpace=25;
	
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		if(components.isEmpty())
		{
			deployComponents(
					model.getComponents(),
					canvas.getModelHub().getViewModel(),
					new Rectangle(25,25,canvas.getBounds().width-50, canvas.getBounds().height-50));
			
		}else
		{
			Rectangle rect=getBoundingRect(components);
			deployComponents(components,canvas.getModelHub().getViewModel(),rect);
		}
	}




	private void deployComponents(List<CanvasComponent> comps, ViewModel viewModel, Rectangle bounds)
	{
		// partition probes
		MultiTreeMap<ProbeList, CanvasComponent> map=new MultiTreeMap<ProbeList, CanvasComponent>();
		for(CanvasComponent c:comps)
		{
			if(c instanceof MultiProbeComponent)
			{
				map.put(viewModel.getTopPriorityProbeList(((MultiProbeComponent) c).getFirstProbe()),c);
			}
		}
		
		int usedSpace=bounds.x+xSpace;
		int maxY=0;
		int yPos=bounds.y+ySpace;
		
		for(ProbeList pl:map.keySet())
		{
			for(CanvasComponent comp:map.get(pl))
			{
				if(usedSpace+comp.getWidth() > bounds.x+bounds.getWidth() )
				{
					usedSpace=bounds.x+xSpace;
					maxY+=ySpace;
					yPos=maxY;
				}
				comp.setLocation(usedSpace,yPos );
				usedSpace+=xSpace+comp.getWidth();
				if(yPos+comp.getHeight() > maxY) maxY= yPos+comp.getHeight();	
			}
			usedSpace=bounds.x+xSpace;
			maxY+=ySpace;
			yPos=maxY;
		}

	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.ArrangeBYProbeList",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Order the components by their Probe List ",
				"Arrange by ProbeList"				
		);
		pli.addCategory(ARRANGE_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/probelist.png");
		return pli;	
	}



}
