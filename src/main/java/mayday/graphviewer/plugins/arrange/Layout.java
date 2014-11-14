package mayday.graphviewer.plugins.arrange;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.PluginInstanceSetting;
import mayday.core.structures.graph.Node;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.layout.CanvasLayouter;
import mayday.vis3.graph.layout.CanvasLayouterPlugin;
import mayday.vis3.graph.model.GraphModel;

public class Layout extends AbstractGraphViewerPlugin 
{
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		PluginInstanceSetting<AbstractPlugin> layouterSetting=new PluginInstanceSetting<AbstractPlugin>("Layout",null,CanvasLayouterPlugin.MC);
		
		SettingDialog sd=new SettingDialog(canvas.getOutermostJWindow(), "Layout", layouterSetting);
		sd.showAsInputDialog();
		if(!sd.closedWithOK()) 
			return;
		
		if(components.isEmpty())
		{
			components=model.getComponents();		
		}
		
		Rectangle rect=getBoundingRect(components);
		
		CanvasLayouter layouter=(CanvasLayouter)layouterSetting.getInstance();
		
		List<Node> nodes=new ArrayList<Node>();
		for(CanvasComponent cc:components)
		{
			nodes.add(model.getNode(cc));
		}
		GraphModel subModel=((SuperModel)model).buildSubModel(nodes);
		layouter.layout(canvas, rect, subModel);
		
		
		
		for(CanvasComponent comp:subModel.getComponents())
		{
			try{
			model.getComponent(subModel.getNode(comp)).setLocation(comp.getLocation());
			}catch(Exception e){}
		}
		
		canvas.revalidateEdges();
		
	}
	
	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.LayoutPlugin",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Apply a layouting algorithm",
				"Layout"				
		);
		pli.addCategory(ARRANGE_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/layout.png");
		return pli;	
	}
}
