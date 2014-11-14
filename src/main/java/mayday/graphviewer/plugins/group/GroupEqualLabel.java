package mayday.graphviewer.plugins.group;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;

import mayday.core.gui.GUIUtilities;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.action.GraphBagModel;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.core.bag.ComponentBag;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class GroupEqualLabel  extends AbstractGraphViewerPlugin
{



	public void run(final GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		MultiHashMap<String, CanvasComponent> comps=new MultiHashMap<String, CanvasComponent>();
		for(CanvasComponent cc: components)
		{
			comps.put(cc.getLabel(),cc);
		}
		
		int numBags=0;
		
		for(String s: comps.keySet())
		{
			if(comps.get(s).size() > 1)
			{
				numBags++;
			}
		}
		Color[] bagColors = GUIUtilities.rainbow(numBags, 0.75 );
		int i=0;
		for(String s: comps.keySet())
		{
			if(comps.get(s).size() > 1)
			{
				ComponentBag bag=new ComponentBag((SuperModel)model);
				bag.setName(s);
				bag.setColor(bagColors[i]);
				++i;
				for(CanvasComponent c:comps.get(s))
				{
					bag.addComponent(c);				
				}
				((GraphBagModel)model).addBag(bag);			
			}
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
				"PAS.GraphViewer.GroupEqualLabel",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Group Nodes with the same label",
				"Group Nodes with identical label"				
		);
		pli.addCategory(GROUP_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/groupbylabel.png");	
		return pli;	
	}	
}


