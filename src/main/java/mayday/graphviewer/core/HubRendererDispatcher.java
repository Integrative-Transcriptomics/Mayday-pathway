package mayday.graphviewer.core;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.structures.graph.Node;
import mayday.core.structures.maps.DefaultValueMap;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.renderer.ComponentRenderer;
import mayday.vis3.graph.renderer.RendererDecorator;
import mayday.vis3.graph.renderer.dispatcher.AbstractRendererPlugin;
import mayday.vis3.graph.renderer.dispatcher.AssignedRendererListSetting;
import mayday.vis3.graph.renderer.dispatcher.AssignedRendererSetting;
import mayday.vis3.graph.renderer.dispatcher.DecoratorListSetting;
import mayday.vis3.graph.renderer.dispatcher.RendererDispatcher;
import mayday.vis3.graph.renderer.dispatcher.RendererPluginSetting;
import mayday.vis3.graph.vis3.SuperColorProvider;

public class HubRendererDispatcher extends RendererDispatcher
{
	private ModelHub hub;
	
	public HubRendererDispatcher(ModelHub hub) 
	{
		super(hub.getViewModel().getDataSet(), hub.getColorProvider());
		this.hub=hub;
	}

	@SuppressWarnings("unchecked")
	protected void renderComponentWithRenderer(Graphics2D g, Node node, Object value, CanvasComponent comp,RendererPluginSetting rendererPluginSetting, boolean paintLabel)
	{
		ComponentRenderer renderer=rendererPluginSetting.getRenderer();
		SuperColorProvider coloring=null;
		if(value instanceof Probe)
		{
			 coloring=hub.getColorProvider(((Probe) value).getMasterTable().getDataSet());
			((AbstractRendererPlugin)renderer).setColorProvider(coloring);
		}
		if(value instanceof Iterable)
		{
			if( ((Iterable)value).iterator().hasNext() )
			{
				 coloring=hub.getColorProvider(((Probe)((Iterable)value).iterator().next()).getMasterTable().getDataSet());
				((AbstractRendererPlugin)renderer).setColorProvider(coloring);
			}
		}
		for(RendererDecorator dec: overallDecorators.getSelection())
		{
			dec.setRenderer(renderer);
			dec.setColorProvider(coloring);
			renderer=dec;
		}	
		renderer.draw(g, node, new Rectangle(comp.getSize()), value, paintLabel?comp.getLabel():"", comp.isSelected());	
	}
	
	
	public static HubRendererDispatcher onlyCircles(ModelHub hub)
	{
		HubRendererDispatcher rd=new HubRendererDispatcher(hub);
		DataSet ds=hub.getViewModel().getDataSet();
		SuperColorProvider coloring=hub.getColorProvider();
		rd.defaultRenderer=new AssignedRendererSetting("Default Renderer",ds, coloring);
		rd.defaultRenderer.setPrimaryRenderer("PAS.GraphViewer.Renderer.Circle");
		rd.overallDecorators=new DecoratorListSetting("Additional Information",ds);	
		
		Map<String, AssignedRendererSetting> baseMap=new HashMap<String, AssignedRendererSetting>();
		rd.roleRenderers=new DefaultValueMap<String, AssignedRendererSetting>(baseMap, rd.defaultRenderer);

		rd.roleRenderersSetting=new AssignedRendererListSetting(ds, coloring, baseMap.values());
		rd.roleRenderersSetting.addChangeListener(rd);
		
		return rd;
	}
	
	public static HubRendererDispatcher onlyProbes(ModelHub hub)
	{
		HubRendererDispatcher rd=new HubRendererDispatcher(hub);
		DataSet ds=hub.getViewModel().getDataSet();
		SuperColorProvider coloring=hub.getColorProvider();
		rd.defaultRenderer=new AssignedRendererSetting("Default Renderer",ds, coloring);
		rd.defaultRenderer.setPrimaryRenderer("PAS.GraphViewer.Renderer.Heatmap");
		rd.overallDecorators=new DecoratorListSetting("Additional Information",ds);	
		
		Map<String, AssignedRendererSetting> baseMap=new HashMap<String, AssignedRendererSetting>();
		rd.roleRenderers=new DefaultValueMap<String, AssignedRendererSetting>(baseMap, rd.defaultRenderer);
		
		rd.roleRenderersSetting=new AssignedRendererListSetting(ds, coloring, baseMap.values());
		rd.roleRenderersSetting.addChangeListener(rd);
		
		return rd;
	}	
	
	public static HubRendererDispatcher profilePlots(ModelHub hub)
	{
		HubRendererDispatcher rd=new HubRendererDispatcher(hub);
		DataSet ds=hub.getViewModel().getDataSet();
		SuperColorProvider coloring=hub.getColorProvider();
		rd.defaultRenderer=new AssignedRendererSetting("Default Renderer",ds, coloring);
		rd.defaultRenderer.setPrimaryRenderer("PAS.GraphViewer.Renderer.Profile");
		rd.overallDecorators=new DecoratorListSetting("Additional Information",ds);	
		
		Map<String, AssignedRendererSetting> baseMap=new HashMap<String, AssignedRendererSetting>();
		rd.roleRenderers=new DefaultValueMap<String, AssignedRendererSetting>(baseMap, rd.defaultRenderer);
		
		rd.roleRenderersSetting=new AssignedRendererListSetting(ds, coloring, baseMap.values());
		rd.roleRenderersSetting.addChangeListener(rd);
		
		return rd;
	}
	
	public static HubRendererDispatcher gradient(ModelHub hub)
	{
		HubRendererDispatcher rd=new HubRendererDispatcher(hub);
		DataSet ds=hub.getViewModel().getDataSet();
		SuperColorProvider coloring=hub.getColorProvider();
		rd.defaultRenderer=new AssignedRendererSetting("Default Renderer",ds, coloring);
		rd.defaultRenderer.setPrimaryRenderer("PAS.GraphViewer.Renderer.Gradient");
		rd.overallDecorators=new DecoratorListSetting("Additional Information",ds);	
		
		Map<String, AssignedRendererSetting> baseMap=new HashMap<String, AssignedRendererSetting>();
		rd.roleRenderers=new DefaultValueMap<String, AssignedRendererSetting>(baseMap, rd.defaultRenderer);
		
		rd.roleRenderersSetting=new AssignedRendererListSetting(ds, coloring, baseMap.values());
		rd.roleRenderersSetting.addChangeListener(rd);
		
		return rd;
	}

}
