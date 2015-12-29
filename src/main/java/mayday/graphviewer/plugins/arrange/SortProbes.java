package mayday.graphviewer.plugins.arrange;

import java.awt.Rectangle;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import mayday.core.Probe;
import mayday.core.meta.MIGroup;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.GraphModel;
import mayday.vis3.plots.chromogram.probelistsort.ProbeComparatorFactory;
import mayday.vis3.plots.chromogram.probelistsort.ProbeComparisonMode;
import mayday.vis3.plots.chromogram.probelistsort.ProbeSortSetting;

public class SortProbes extends AbstractGraphViewerPlugin 
{
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		// if components are selected, only sort the selected
		ProbeSortSetting setting=new ProbeSortSetting(canvas.getModelHub().getViewModel().getDataSet());
		SettingDialog sd=new SettingDialog(null, "Probe Sort Settings", setting);
		sd.setModal(true);
		sd.setVisible(true);
		if(!components.isEmpty())
		{
			Rectangle rect=getBoundingRect(components);
			deployComponents(sortProbes(components, setting), rect);
		}
		else
			deployComponents(sortProbes(model.getComponents(), setting), new Rectangle(25,25,canvas.getBounds().width-50, canvas.getBounds().height-50));
	}
	
	private TreeMap<Probe, CanvasComponent> sortProbes(List<CanvasComponent> comps, ProbeSortSetting sortSetting)
	{
		ProbeComparisonMode mode=ProbeComparisonMode.forString(sortSetting.getModeSetting().getStringValue());
		MIGroup gr=sortSetting.getMiGroup().getMIGroup();
		int exp=sortSetting.getExperimentSetting().getSelectedIndex();
		Comparator<Probe> comparator=ProbeComparatorFactory.createProbeComparator(mode, exp, gr);
		if(sortSetting.getReverseSetting().getBooleanValue())
		{
			comparator=new ReverseComparator(comparator);
		}
		TreeMap<Probe, CanvasComponent> map=new TreeMap<Probe, CanvasComponent>(comparator);
		for(CanvasComponent c:comps)
		{
			for(Probe p:((MultiProbeComponent) c).getProbes() )
			{
				map.put(p, c);
			}
		}		
		return map;
	}
	
	private class ReverseComparator implements Comparator<Probe>
	{
		private Comparator<Probe> comparator;
		
		public ReverseComparator(Comparator<Probe> comp) 
		{
			this.comparator=comp;
		}
		@Override
		public int compare(Probe o1, Probe o2) 
		{
			return comparator.compare(o2, o1);
		}
		
		@Override
		public boolean equals(Object obj) 
		{			
			return super.equals(obj);
		}
	}
	
	private void deployComponents(TreeMap<Probe, CanvasComponent> probeMap, Rectangle bounds)
	{
		Set<CanvasComponent> placed=new HashSet<CanvasComponent>();
		int x=bounds.x;
		int y=bounds.y;
		int ymax=0;
		int space=15;
		for(CanvasComponent c:probeMap.values())
		{
			if(placed.contains(c))
				continue;

			placed.add(c);
			
			if(x+c.getWidth() >bounds.width)
			{
				x=bounds.x;
				y+=ymax+space;
				ymax=0;
			}
			c.setLocation(x, y);
			x+=c.getWidth()+space;
			if(c.getHeight() > ymax)
				ymax=c.getHeight();			
		}		
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.SortProbes",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Order the components by their probes in different ways ",
				"Sort Probes..."				
		);
		pli.addCategory(ARRANGE_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/sortprobes.png");
		return pli;	
	}
}
