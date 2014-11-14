package mayday.graphviewer.plugins.arrange;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.graphviewer.util.components.DegreeComparator;
import mayday.graphviewer.util.components.NameComparator;
import mayday.graphviewer.util.components.WeightComparator;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class SortComponents extends AbstractGraphViewerPlugin 
{
	@SuppressWarnings("unchecked")
	private ObjectSelectionSetting<Comparator<CanvasComponent>> sortingOption=new ObjectSelectionSetting<Comparator<CanvasComponent>>
	("Sort probes by", null,0, new Comparator[]{new WeightComparator(),
			new NameComparator(),
			new DegreeComparator(DegreeComparator.OVERALL_DEGREE),
			new DegreeComparator(DegreeComparator.IN_DEGREE),
			new DegreeComparator(DegreeComparator.OUT_DEGREE)});
	
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		// if components are selected, only sort the selected
		SettingDialog sd=new SettingDialog(null, "Probe Sort Settings", sortingOption);
		sd.setModal(true);
		sd.setVisible(true);
		if(!sd.closedWithOK())
			return;
		
		if(!components.isEmpty())
		{
			Rectangle rect=getBoundingRect(components);
			Collections.sort(components,sortingOption.getObjectValue());
			deployComponents(components, rect);
		}
		else
		{
			List<CanvasComponent> comps=new ArrayList<CanvasComponent>(model.getComponents());
			Collections.sort(comps,sortingOption.getObjectValue());
			deployComponents(comps, new Rectangle(25,25,canvas.getBounds().width-50, canvas.getBounds().height-50));
		}
	}
	
	private void deployComponents(List<CanvasComponent> comps, Rectangle bounds)
	{		
		int x=bounds.x;
		int y=bounds.y;
		int ymax=0;
		int space=25;
		for(CanvasComponent c:comps)
		{
			if(x+c.getWidth() >bounds.width)
			{
				x=bounds.x;
				y+=ymax+space;
				ymax=0;
			}
			c.setLocation(x, y);
			x+=c.getWidth()+space;
			if(c.getHeight() > ymax)
				ymax=c.getHeight();			
		}		
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.SortComponents",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Order the components by different criteria ",
				"Sort Nodes..."				
		);
		pli.addCategory(ARRANGE_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/sortnodes.png");
		return pli;	
	}
	
	@Override
	public void init() {}
}

