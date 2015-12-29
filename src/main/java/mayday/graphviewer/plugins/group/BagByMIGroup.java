package mayday.graphviewer.plugins.group;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.Probe;
import mayday.core.meta.GenericMIO;
import mayday.core.meta.MIType;
import mayday.core.meta.types.StringMIO;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.typed.MIGroupSetting;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.core.bag.ComponentBag;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.graphviewer.plugins.arrange.ArrangeByMIO;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.GraphModel;
import mayday.vis3.graph.renderer.MIOColoring;

public class BagByMIGroup extends AbstractGraphViewerPlugin 
{
	@SuppressWarnings("unchecked")
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		if(components.isEmpty())
		{
			return;
		}
		
		MIGroupSetting miGroup=new MIGroupSetting("Meta Information", null, null, canvas.getModelHub().getViewModel().getDataSet().getMIManager(), false);
		miGroup.setAcceptableClass(StringMIO.class);
				
		SettingDialog sd=new SettingDialog(canvas.getOutermostJWindow(), "Select Meta Information", miGroup);
		sd.showAsInputDialog();
		
		if(!sd.closedWithOK())
			return;
		
		MIOColoring coloring=new MIOColoring();
		coloring.setMIGroup(miGroup.getMIGroup());
		
		Map<String, ComponentBag> termBagMap=new HashMap<String, ComponentBag>();
		ComponentBag uncat=new ComponentBag((SuperModel)model);
		uncat.setColor(Color.LIGHT_GRAY);
		uncat.setName("Uncategorized");
		
		new ArrangeByMIO().deployComponents(components,getBoundingRect(components) , miGroup.getMIGroup());
		
		((SuperModel)model).addBag(uncat);
		for(CanvasComponent comp: components)
		{
			if(comp instanceof MultiProbeComponent)
			{
				boolean cat=false;
				for(Probe p: ((MultiProbeComponent) comp).getProbes())
				{
					MIType m=miGroup.getMIGroup().getMIO(p);
					if(m!=null)
					{
						if(termBagMap.containsKey(m.toString()))
						{
							termBagMap.get(m.toString()).addComponent(comp);
							cat=true;
						}else
						{
							ComponentBag bag=new ComponentBag(((SuperModel)model));
							bag.setName(m.toString());
							bag.setColor(coloring.getColor((GenericMIO)m));
						
							bag.addComponent(comp);
							termBagMap.put(m.toString(), bag);
							
							cat=true;
						}						
					}
				}
				if(!cat)
				{
					uncat.addComponent(comp);
				}
			}else
			{
				uncat.addComponent(comp);
			}			
		}
		
		for(ComponentBag bag: termBagMap.values())
			((SuperModel)model).addBag(bag);
		
		((SuperModel)model).addBag(uncat);
		
		
		canvas.updatePlot();		
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.BagByMIO",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Arrange the Components to be bagged together by some meta information.",
				"Group by Meta Information"				
		);
		pli.addCategory(GROUP_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/groupbymio.png");
		return pli;	
	}
}
