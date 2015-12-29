package mayday.graphviewer.plugins.group;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.graphviewer.action.GraphBagModel;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.core.bag.ComponentBag;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.graphviewer.plugins.arrange.ArrangeByValues;
import mayday.vis3.ValueProviderSetting;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.GraphModel;

public class GroupByValue extends AbstractGraphViewerPlugin 
{
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		if(components.isEmpty())
		{
			return;
		}

		ValueProviderSetting vpSetting=new ValueProviderSetting("Value to Sort", null, canvas.getModelHub().getValueProvider(), canvas.getModelHub().getViewModel());
		IntSetting numBins=new IntSetting("Number of Bins",null,3,2,20,true,true);		
		HierarchicalSetting setting=new HierarchicalSetting("Grouping by Values");
		setting.addSetting(numBins).addSetting(vpSetting);		
		SettingDialog sd=new SettingDialog(canvas.getOutermostJWindow(),"Grouping by Values" , setting);
		sd.showAsInputDialog();		
		if(!sd.closedWithOK())
			return;
		
		List<MultiProbeComponent> oc=new ArrangeByValues().deployComponents(components, canvas.getModelHub().getValueProvider(), getBoundingRect(components));
		
		double binWidth=Math.ceil((1.0*oc.size())/(1.0*numBins.getIntValue()) );
		ColorGradient grad=ColorGradient.createRainbowGradient(1, numBins.getIntValue()+1);
		
		
		int i=0;
		int bc=1;
		
		List<ComponentBag> bags=new LinkedList<ComponentBag>();
		
		ComponentBag bag=new ComponentBag((GraphBagModel) model);	
		bag.setColor(grad.mapValueToColor(bc));
		bag.setName("Group "+bc);
		bags.add(bag);
			
		
		for(CanvasComponent cc: oc)
		{
			bag.addComponent(cc);
			++i;
			if(i == binWidth)
			{
				bc++;
				bag=new ComponentBag((GraphBagModel) model);				
				bag.setColor(grad.mapValueToColor(bc));
				bag.setName("Group "+bc);
				bags.add(bag);
				i=0;
			}
		}
		for(ComponentBag b:bags)
			((SuperModel)model).addBag(b);
		canvas.updatePlot();		
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.BagByValue",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Arrange the Probes to be bagged together by their value",
				"Group by Value"				
		);
		pli.addCategory(GROUP_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/groupbyvalue.png");		
		return pli;	
	}
}
