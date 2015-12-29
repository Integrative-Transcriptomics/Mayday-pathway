package mayday.graphviewer.plugins.arrange;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.ValueProvider;
import mayday.vis3.ValueProviderSetting;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.GraphModel;

public class ArrangeByValues extends AbstractGraphViewerPlugin
{
	private static int xSpace=25;
	private static int ySpace=25;
	
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		ValueProviderSetting vpSetting=new ValueProviderSetting("Value to Sort", null, canvas.getModelHub().getValueProvider(), canvas.getModelHub().getViewModel());
		
		SettingDialog sd=new SettingDialog(canvas.getOutermostJWindow(), "Sort Components", vpSetting);
		sd.showAsInputDialog();
		
		if(!sd.closedWithOK())
			return;
		
		if(components.isEmpty())
		{			
			deployComponents(
					model.getComponents(),
					canvas.getModelHub().getValueProvider(),
					new Rectangle(25,25,canvas.getBounds().width-50, canvas.getBounds().height-50));
			
		}else
		{			
			Rectangle rect=getBoundingRect(components);
			deployComponents(components,canvas.getModelHub().getValueProvider(),rect);
		}
	}




	public List<MultiProbeComponent> deployComponents(List<CanvasComponent> comps, ValueProvider vp, Rectangle bounds)
	{
		List<MultiProbeComponent> oc=new LinkedList<MultiProbeComponent>();
		List<CanvasComponent> uc=new LinkedList<CanvasComponent>();
		for(CanvasComponent comp: comps)
		{
			if(comp instanceof MultiProbeComponent)
			{
				oc.add((MultiProbeComponent) comp);
			}else
			{
				uc.add(comp);
			}
		}
		Collections.sort(oc,new ValueProviderComparator(vp));
		
		int usedSpace=bounds.x+xSpace;
		int maxY=0;
		int yPos=bounds.y+ySpace;
		
		for(CanvasComponent comp:oc)
		{
				if(usedSpace+comp.getWidth() > bounds.x+bounds.getWidth() )
				{
					usedSpace=bounds.x+xSpace;
					maxY+=ySpace;
					yPos=maxY;
				}
				comp.setLocation(usedSpace,yPos );
				usedSpace+=xSpace+comp.getWidth();
				if(yPos+comp.getHeight() > maxY) maxY= yPos+comp.getHeight();	
		}
		
		for(CanvasComponent comp:uc)
		{
				if(usedSpace+comp.getWidth() > bounds.x+bounds.getWidth() )
				{
					usedSpace=bounds.x+xSpace;
					maxY+=ySpace;
					yPos=maxY;
				}
				comp.setLocation(usedSpace,yPos );
				usedSpace+=xSpace+comp.getWidth();
				if(yPos+comp.getHeight() > maxY) maxY= yPos+comp.getHeight();	
		}
		
		return oc;
	}
	
	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.ArrangeByValues",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Order the components by their Probe values ",
				"Arrange by Values"				
		);
		pli.addCategory(ARRANGE_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/probe.png");
		return pli;	
	}
	
	public static  class ValueProviderComparator implements Comparator<MultiProbeComponent>
	{
		private ValueProvider valueProvider;
		
		public ValueProviderComparator(ValueProvider valueProvider) 
		{
			this.valueProvider = valueProvider;
		}

		@Override
		public int compare(MultiProbeComponent o1, MultiProbeComponent o2) 
		{
			if(o1.getProbes().isEmpty() && o2.getProbes().isEmpty()) return 0;			
			if(o1.getProbes().isEmpty()) return -1;
			if(o2.getProbes().isEmpty()) return 1;
			
			double v1=valueProvider.getValue(o1.getProbes().get(0));
			double v2=valueProvider.getValue(o2.getProbes().get(0));
			
			return Double.compare(v1, v2);
		}
	}
}
