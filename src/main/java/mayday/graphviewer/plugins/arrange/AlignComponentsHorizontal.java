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
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.maps.MultiTreeMap;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class AlignComponentsHorizontal  extends AbstractGraphViewerPlugin 
{
	private RestrictedStringSetting alignment=new RestrictedStringSetting("Alignment", null, 1, new String[]{"top","center","bottom"});
	
	private static final int step=25;
	
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		if(components.isEmpty())
		{
			return;			
		}
		
	
		
		SettingDialog sd=new SettingDialog(null, "Alignment Settings", alignment);
		sd.setModal(true);
		sd.setVisible(true);
		
		int mode=alignment.getSelectedIndex();
		
		MultiTreeMap<Integer,CanvasComponent> componentsByX=new MultiTreeMap<Integer,CanvasComponent>();
		
		int avgW=0;
		for(CanvasComponent comp:components)
			avgW+=comp.getWidth();
		
		avgW/=components.size();
		
		for(CanvasComponent comp:components)
			componentsByX.put(comp.getX()/avgW, comp);
		
		Rectangle rect=getBoundingRect(components);
		
		int baseY=0;
		
		switch(mode)
		{
		
			case 0: baseY=rect.y; break; 
			case 1: baseY=rect.y+rect.height/2; break; 
			case 2: baseY=rect.y+rect.height; break; 
			default: break;
		}
		int l=0;
		int x=rect.x;
		ComponentYComparator comparator=new ComponentYComparator();
		for(Integer i:componentsByX.keySet())
		{
			
			List<CanvasComponent> comps=new ArrayList<CanvasComponent>(componentsByX.get(i));
			
			Collections.sort(comps,comparator);
			switch(mode)
			{
			case 0: x+=top(comps,baseY,x);				
				break;
			case 1: x+=center(comps,baseY,x);				
				break;	
			case 2: x+=bottom(comps,baseY,x);				
				break;		
			}
			x+=step;
			++l;
		}
		canvas.updateSize();
	}
	
	private int top(List<CanvasComponent> comps, int baseY, int x)
	{
		int max=0;
		for(CanvasComponent c:comps)
		{
			c.setLocation(x, baseY);
			baseY+=step+c.getHeight();
			max=Math.max(max, c.getWidth());
		}
		return max;
	}
	
	private int center(List<CanvasComponent> comps, int baseY, int x)
	{
		int height=0;
		for(CanvasComponent c:comps)
		{
			height+=c.getHeight()+step;
		}
//		height-=step;
		return top(comps,baseY-height/2,x);
	}
	
	private int bottom(List<CanvasComponent> comps, int baseY, int x)
	{
		int height=0;
		for(CanvasComponent c:comps)
		{
			height+=c.getHeight()+step;
		}
//		height-=step;
		return top(comps,baseY-height,x);
	}
	
	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.HAlign",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Align rows to be either top-aligned, centered or bottom-aligned",
				"Align Rows"				
		);
		pli.addCategory(ARRANGE_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/rows.png");
		return pli;	
	}
	
	private class ComponentYComparator implements Comparator<CanvasComponent>
	{
		@Override
		public int compare(CanvasComponent o1, CanvasComponent o2) 
		{
			return o1.getY()-o2.getY();
		}
	}
}
