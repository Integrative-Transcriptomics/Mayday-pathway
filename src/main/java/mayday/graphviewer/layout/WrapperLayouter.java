package mayday.graphviewer.layout;

import java.awt.Container;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.structures.graph.Node;
import mayday.graphviewer.core.SuperModel;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.layout.CanvasLayouter;
import mayday.vis3.graph.layout.FruchtermanReingoldLayout;
import mayday.vis3.graph.layout.RandomLayout;
import mayday.vis3.graph.model.GraphModel;

public class WrapperLayouter extends GroupAndSortLayout {

	private CanvasLayouter[] layouters={new FruchtermanReingoldLayout(1000), new RandomLayout()};	
	private ObjectSelectionSetting<CanvasLayouter> layoutSetting=new ObjectSelectionSetting<CanvasLayouter>("Group Layouter", null, 0, layouters );
	
	private IntSetting componentWidth=new IntSetting("Group Width", "The horizontal space allocated for each group", 600);
	private IntSetting componentHeight=new IntSetting("Group Height", "The vertical space allocated for each group", 400);
	
	private static final int SPACER=20;
	
	public WrapperLayouter() 
	{
		initSetting();	
	}
	
	@Override
	protected void placeGroups(List<List<CanvasComponent>> components, Container container, Rectangle bounds, GraphModel model) 
	{
		System.out.println(bounds);
		int y=SPACER; 
		int x=SPACER; 
		
		for(List<CanvasComponent> cmp:components)
		{		
			CanvasLayouter layouter=(CanvasLayouter)layoutSetting.getObjectValue();
			
			List<Node> nodes=new ArrayList<Node>();
			for(CanvasComponent cc:cmp)
			{
				nodes.add(model.getNode(cc));
			}
			GraphModel subModel=((SuperModel)model).buildSubModel(nodes);
			Rectangle rect=new Rectangle(0,0, componentWidth.getIntValue(), componentHeight.getIntValue());
			layouter.layout(container, rect, subModel);
			
			int maxX=Integer.MIN_VALUE;
			int maxY=Integer.MIN_VALUE;
			
			for(CanvasComponent comp:subModel.getComponents())
			{
				model.getComponent(subModel.getNode(comp)).setLocation(comp.getX()+x, comp.getY()+y);
				maxX=Math.max(maxX, comp.getX());
				maxY=Math.max(maxY, comp.getY());
			}
			if(x+maxX > bounds.width)
			{
				x=SPACER;
				y+=SPACER+maxY; 
			}else
				x+=maxX;
			
			
		}
		
	}

	@Override
	protected void simpleLayout(Container container, Rectangle bounds,GraphModel model) {
		new FruchtermanReingoldLayout(1000).layout(container, bounds, model);

	}

	@Override
	protected void initSetting() {
		setting.addSetting(layoutSetting).addSetting(componentHeight).addSetting(componentWidth);
	}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Layout.LayoutGroup",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Group and sort nodes by varous criteria before laying them out using a second algorithm.",
				"Group and Sort: Wrappers"				
		);
		return pli;	
	}

}
