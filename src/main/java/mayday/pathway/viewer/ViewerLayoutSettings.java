package mayday.pathway.viewer;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.pathway.viewer.canvas.PathwayLayouter;
import mayday.pathway.viewer.canvas.PathwayLayouterSettings;
import mayday.vis3.graph.layout.CanvasLayouter;
import mayday.vis3.graph.layout.CanvasLayouterFactory;

public class ViewerLayoutSettings extends HierarchicalSetting 
{
	private BooleanSetting usePathwayLayouter; 
	private PathwayLayouterSettings pathwayLayouterSettings;
	private BooleanSetting sideComponentSetting;
	private RestrictedStringSetting layouterSetting;
	
	public ViewerLayoutSettings() 
	{
		super("Layouter Settings");
		
		usePathwayLayouter=new BooleanSetting("Use PathwayLayouter","Use a complex layouter recursively layouting pathway components",true);
		pathwayLayouterSettings=new PathwayLayouterSettings();
		sideComponentSetting=new BooleanSetting("Layout Side Components separately","Layout side components (enzymes, cofactors, etc)" +
				" around the main reaction node",true);
		layouterSetting=new RestrictedStringSetting("Layouter","Set the layout algorithm. If" +
				" the pathway layouter is used, this setting will be ignored",	3, CanvasLayouterFactory.LAYOUTERS);
		
		addSetting(usePathwayLayouter);
		addSetting(pathwayLayouterSettings);
		addSetting(sideComponentSetting);
		addSetting(layouterSetting);
	}
	
	public boolean isUsePathwayLayouter()
	{
		return usePathwayLayouter.getBooleanValue();
	}
	
	public boolean isLayoutSideComponents()
	{
		return sideComponentSetting.getBooleanValue();
	}
	
	public PathwayLayouter getPathwayLayouter()
	{
		return pathwayLayouterSettings.getPathwayLayouter();
	}
	
	public CanvasLayouter getLayouter()
	{
		if(isUsePathwayLayouter())
			return getPathwayLayouter();
		return CanvasLayouterFactory.createLayouter(layouterSetting.getStringValue());
	}
	
	public ViewerLayoutSettings clone() 
	{
		 return (ViewerLayoutSettings)reflectiveClone();
	}
	

}
