package mayday.graphviewer.layout;

import java.awt.Container;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.layout.GridLayouter;
import mayday.vis3.graph.model.GraphModel;

public class GridGroupLayouter  extends GroupAndSortLayout
{
	public static final int FILL=0;
	public static final int HORIZONTAL=1;
	public static final int VERTICAL=2;

	private RestrictedStringSetting modeSetting=new RestrictedStringSetting("Mode", "How the components should be placed:\n" +
			"fill: fill the up the space\n, " +
			"horizontal: place all components on a horizontal line\n" +
			"vertical: place all components on a vertical line", 0, new String[]{"fill","horizontal","vertical"});
	
	private IntSetting spacer=new IntSetting("Between Groups Space", null, 80);
	private IntSetting xSpace=new IntSetting("Horizontal Spacer",null,20);
	private IntSetting ySpace=new IntSetting("Vertical Spacer",null,30);
	
	public GridGroupLayouter() 
	{
		initSetting();		
	}
	
	@Override
	protected void placeGroups(List<List<CanvasComponent>> components,	Container container, Rectangle bounds, GraphModel model) 
	{
		if(modeSetting.getSelectedIndex() == 0)
		{
			doFillLayout(components,container,bounds,model);
		}
		if(modeSetting.getSelectedIndex() == 1)
		{
			layoutHorizontal(components,container,bounds,model);
		}
		if(modeSetting.getSelectedIndex() == 2)
		{
			layoutVertical(components,container,bounds,model);
		}	
	}
	
	public void layoutVertical(List<List<CanvasComponent>> components,Container container, Rectangle bounds, GraphModel model) 
	{
		int xPos=bounds.x+xSpace.getIntValue();
		for(List<CanvasComponent> grp: components)
		{
			int usedSpace=bounds.y+spacer.getIntValue();
			int maxX=0;			
			for(CanvasComponent comp: grp)
			{
				comp.setLocation(xPos, usedSpace );
				usedSpace+=ySpace.getIntValue()+comp.getHeight();
				if(xPos+comp.getWidth() > maxX) maxX= xPos+comp.getWidth();		
			}
			xPos+=spacer.getIntValue();
		}
	}

	public void layoutHorizontal(List<List<CanvasComponent>> components, Container container, Rectangle bounds, GraphModel model) 
	{		
		int yPos=bounds.y+ySpace.getIntValue();
		for(List<CanvasComponent> grp: components)
		{
			int usedSpace=bounds.x+spacer.getIntValue();
			int maxY=0;			
			for(CanvasComponent comp: grp)
			{
				comp.setLocation(usedSpace,yPos );
				usedSpace+=xSpace.getIntValue()+comp.getWidth();
				if(yPos+comp.getHeight() > maxY) maxY= yPos+comp.getHeight();		
			}
			yPos+=spacer.getIntValue();
		}
	}

	public void doFillLayout(List<List<CanvasComponent>> components, Container container, Rectangle bounds, GraphModel model) 
	{
		int yPos=bounds.y+spacer.getIntValue();
		
		for(List<CanvasComponent> grp: components)
		{
			int usedSpace=bounds.x+spacer.getIntValue();
			int maxY=0;
			yPos+=spacer.getIntValue();
			
			for(CanvasComponent comp: grp)
			{
				if(usedSpace+comp.getWidth() > bounds.x+(bounds.getWidth()-2*spacer.getIntValue()) )
				{
					usedSpace=bounds.x+xSpace.getIntValue();
					maxY+=ySpace.getIntValue();
					yPos=maxY;
				}
				comp.setLocation(usedSpace,yPos );
				usedSpace+=xSpace.getIntValue()+comp.getWidth();
				if(yPos+comp.getHeight() > maxY) maxY= yPos+comp.getHeight();		
			}			
		}
	}
	

	@Override
	protected void simpleLayout(Container container, Rectangle bounds, GraphModel model) 
	{
		new GridLayouter(GridLayouter.FILL).layout(container, bounds, model);		
	}

	@Override
	protected void initSetting() 
	{
		setting.addSetting(modeSetting).addSetting(spacer).addSetting(xSpace).addSetting(ySpace);
		
	}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Layout.GridGroup",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Group and sort nodes by varous criteria before placing them in on a grid.",
				"Group and Sort: Grid"				
		);
		return pli;	
	}
	
}
