package mayday.graphviewer.layout;

import java.awt.Container;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.typed.IntSetting;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.graphviewer.util.components.DegreeComparator;
import mayday.pathway.viewer.canvas.AlignLayouter;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class LinNetLayout extends GroupAndSortLayout
{
	private IntSetting axOffset=new IntSetting("Axis offset", "the distance of the first node\n of an axis to the center of the graph", 50);
	private IntSetting axLength=new IntSetting("Axis length", "maximal length of the axis", 500);
	
	public LinNetLayout() 
	{
		initSetting();
	}
	
	protected void simpleLayout(Container container, Rectangle bounds, GraphModel model)
	{
		Graph g=model.getGraph();
		List<List<CanvasComponent>> comps=buildAxis(model, g);		
		
		placeGroups(comps, container, bounds, model);
	}
	
	public List<List<CanvasComponent>> buildAxis(GraphModel model, Graph g)
	{
		List<CanvasComponent> outNodes=new ArrayList<CanvasComponent>();
		List<CanvasComponent> inNodes=new ArrayList<CanvasComponent>();
		List<CanvasComponent> inoutNodes=new ArrayList<CanvasComponent>();
		for(Node n:g.getNodes())
		{
			if(g.getInDegree(n) > 0 && g.getOutDegree(n)==0)
			{
				inNodes.add(model.getComponent(n) );
			}
			if(g.getInDegree(n) == 0  && g.getOutDegree(n) > 0)
			{
				outNodes.add(model.getComponent(n));
			}
			if(g.getInDegree(n) > 0  && g.getOutDegree(n) > 0)
			{
				inoutNodes.add(model.getComponent(n));
			}
		}
		
		List<List<CanvasComponent>> axes=new ArrayList<List<CanvasComponent>>();
		axes.add(outNodes);
		axes.add(inoutNodes);
		axes.add(inNodes);
		
		Collections.sort(outNodes,new DegreeComparator());
		
		return axes;
	
	}

	@Override
	protected void initSetting() 
	{
		setting.addSetting(axOffset).addSetting(axLength);	
	}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Layout.LinNet",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Group and Sort: Axes",
				"Group and Sort: Axes"				
		);
		return pli;	
	}

	@Override
	protected void placeGroups(List<List<CanvasComponent>> components,
			Container container, Rectangle bounds, GraphModel model) 
	{
		double angle=2.0*Math.PI / components.size();		
		int axisLength=axLength.getIntValue();
		int axisOffset=axOffset.getIntValue();
		double ac=0;
		for(int i=0; i!= components.size(); ++i)
		{
			if(components.get(i).isEmpty())
				continue;
			
			
			double xStep=axisLength/components.get(i).size();
			
			double x=axisOffset;
			for(CanvasComponent cc: components.get(i))
			{
				double xp= x*Math.cos(ac);
				double yp= x*Math.sin(ac);				
				cc.setLocation((int)xp, (int)yp);				
				x+=xStep;
			}			
			ac+=angle;
		}		
		int n=10;
		if( (2* axisLength + 2* axisOffset) < bounds.width)
			n = (bounds.width-(2* axisLength + 2* axisOffset)) / 2;
		new AlignLayouter(n).layout(container, bounds, model);
		
	}
}
