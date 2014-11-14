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

public class AlignComponentsVertical extends AbstractGraphViewerPlugin 
{
	
	private RestrictedStringSetting alignment=new RestrictedStringSetting("Alignment", null, 1, new String[]{"left","center","right"});
		
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
		
		MultiTreeMap<Integer,CanvasComponent> componentsByY=new MultiTreeMap<Integer,CanvasComponent>();
		
		int avgH=0;
		for(CanvasComponent comp:components)
			avgH+=comp.getHeight();
		
		avgH/=components.size();
		
		for(CanvasComponent comp:components)
			componentsByY.put(comp.getY()/avgH, comp);
		
		Rectangle rect=getBoundingRect(components);
		
		int baseX=0;
		
		switch(mode)
		{
		
			case 0: baseX=rect.x; break; 
			case 1: baseX=rect.x+rect.width/2; break; 
			case 2: baseX=rect.x+rect.width; break; 
			default: break;
		}
		int l=0;
		int y=rect.y;
		ComponentXComparator comparator=new ComponentXComparator();
		for(Integer i:componentsByY.keySet())
		{
			
			List<CanvasComponent> comps=new ArrayList<CanvasComponent>(componentsByY.get(i));
			
			Collections.sort(comps,comparator);
			switch(mode)
			{
			case 0: y+=left(comps,baseX,y);				
				break;
			case 1: y+=center(comps,baseX,y);				
				break;	
			case 2: y+=right(comps,baseX,y);				
			break;		
			}
			y+=step;
			++l;
		}
		canvas.updateSize();
	}
	
	private int left(List<CanvasComponent> comps, int baseX, int y)
	{
		int max=0;
		for(CanvasComponent c:comps)
		{
			c.setLocation(baseX, y);
			baseX+=step+c.getWidth();
			max=Math.max(max, c.getHeight());
		}
		return max;
	}
	
	private int center(List<CanvasComponent> comps, int baseX, int y)
	{
		int width=0;
		for(CanvasComponent c:comps)
		{
			width+=c.getWidth()+step;
		}
		return left(comps,baseX-width/2,y);
	}
	
	private int right(List<CanvasComponent> comps, int baseX, int y)
	{
		int width=0;
		for(CanvasComponent c:comps)
		{
			width+=c.getWidth()+step;
		}
		return left(comps,baseX-width,y);
	}
	
	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.VAlign",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Align columns to be either left-aligned, centered or right-aligned",
				"Align Columns"				
		);
		pli.addCategory(ARRANGE_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/columns.png");
		return pli;	
	}
	
	private class ComponentXComparator implements Comparator<CanvasComponent>
	{
		@Override
		public int compare(CanvasComponent o1, CanvasComponent o2) 
		{
			return o1.getX()-o2.getX();
		}
	}
}
