package mayday.pathway.viewer.canvas;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.vis3.graph.layout.CanvasLayouterFactory;

public class PathwayLayouterSettings  extends HierarchicalSetting
{
	private RestrictedStringSetting linearSetting;
	private RestrictedStringSetting circularSetting;
	private RestrictedStringSetting branchedSetting;
	private RestrictedStringSetting complexSetting;
	
	public PathwayLayouterSettings() 
	{
		super("Pathway Layouter Settings");
		linearSetting=new RestrictedStringSetting("Linear Layouter","Set the layout algorithm used for linear pathways",	6, CanvasLayouterFactory.LAYOUTERS);
		circularSetting=new RestrictedStringSetting("Circular Layouter","Set the layout algorithm used for circular pathways",	4, CanvasLayouterFactory.LAYOUTERS);
		branchedSetting=new RestrictedStringSetting("Branched Layouter","Set the layout algorithm used for branched pathways",	5, CanvasLayouterFactory.LAYOUTERS);
		complexSetting=new RestrictedStringSetting("Complex Layouter","Set the layout algorithm used for complex pathways",	3, CanvasLayouterFactory.LAYOUTERS);
		
		addSetting(linearSetting).
		addSetting(circularSetting).
		addSetting(branchedSetting).
		addSetting(complexSetting);		
	}
	
	public PathwayLayouter getPathwayLayouter()
	{
		PathwayLayouter layouter=new PathwayLayouter();
		
		layouter.setBranchedLayouter(CanvasLayouterFactory.createLayouter(branchedSetting.getStringValue()));
		layouter.setCircularLayouter(CanvasLayouterFactory.createLayouter(circularSetting.getStringValue()));
		layouter.setLinearLayouter(CanvasLayouterFactory.createLayouter(linearSetting.getStringValue()));
		layouter.setComplexLayouter(CanvasLayouterFactory.createLayouter(complexSetting.getStringValue()));
		
		return layouter;		
	}
	
	public PathwayLayouterSettings clone() 
	{
		 return (PathwayLayouterSettings)reflectiveClone();
	}
	
}
