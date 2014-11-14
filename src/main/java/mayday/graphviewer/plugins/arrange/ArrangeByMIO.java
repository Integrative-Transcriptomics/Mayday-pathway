package mayday.graphviewer.plugins.arrange;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mayday.core.Probe;
import mayday.core.meta.ComparableMIO;
import mayday.core.meta.MIGroup;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.typed.MIGroupSetting;
import mayday.core.structures.maps.MultiTreeMap;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.graphviewer.util.components.NameComparator;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.GraphModel;

public class ArrangeByMIO extends AbstractGraphViewerPlugin  {

	private static int xSpace=25;
	private static int ySpace=25;
	
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		MIGroupSetting miGroup=new MIGroupSetting("Meta Information", null, null, canvas.getModelHub().getViewModel().getDataSet().getMIManager(), false);
		miGroup.setAcceptableClass(ComparableMIO.class);
				
		SettingDialog sd=new SettingDialog(canvas.getOutermostJWindow(), "Select Meta Information", miGroup);
		sd.showAsInputDialog();
		
		if(!sd.closedWithOK())
			return;
		
		
		
		if(components.isEmpty())
		{
			deployComponents(
					model.getComponents(),
					new Rectangle(25,25,canvas.getBounds().width-50, canvas.getBounds().height-50),
					miGroup.getMIGroup());
			
			
		}else
		{
			Rectangle rect=getBoundingRect(components);
			deployComponents(
					components,
					rect,
					miGroup.getMIGroup());
		}
	}
	
	public void deployComponents(List<CanvasComponent> components, Rectangle bounds, MIGroup group )
	{
		MultiTreeMap<String, CanvasComponent> orderedComps=new MultiTreeMap<String, CanvasComponent>();		
				
		for(CanvasComponent comp: components)
		{
			if(comp instanceof MultiProbeComponent)
			{
				boolean f=false;
				for(Probe p: ((MultiProbeComponent) comp).getProbes())
				{
					if(group.getMIO(p)!=null)
					{
						orderedComps.put(group.getMIO(p).toString(),comp);
						f=true;
					}					
				}
				if(!f)
				{
					orderedComps.put("",comp);
				}
			}else
			{
				orderedComps.put("",comp);
			}
		}
		
		int usedSpace=bounds.x+xSpace;
		int maxY=0;
		int yPos=bounds.y+ySpace;	
		
		for(String s: orderedComps.keySet())
		{
			List<CanvasComponent> sc=new LinkedList<CanvasComponent>(orderedComps.get(s));
			Collections.sort(sc, new NameComparator());
				
			for(CanvasComponent comp: sc)
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
			usedSpace=bounds.x+xSpace;
			maxY+=ySpace;
			yPos=maxY;			
		}	
		
	}
	
	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.ArrangeByMIO",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Order the components by their Meta Information ",
				"Arrange by Meta Information"				
		);
		pli.addCategory(ARRANGE_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/migroup.png");
		return pli;	
	}
}
