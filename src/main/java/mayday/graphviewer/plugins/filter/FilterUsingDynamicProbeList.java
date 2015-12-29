package mayday.graphviewer.plugins.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.dynamicpl.NewDynamicProbelist;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class FilterUsingDynamicProbeList  extends FilterPlugin 
{
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model,List<CanvasComponent> components) 
	{
		if(components.isEmpty())
			return;
				
		SettingDialog sd=new SettingDialog(canvas.getOutermostJWindow(), "Filter", setting);
		sd.showAsInputDialog();
		
		if(!sd.closedWithOK())
			return;
		
		List<ProbeList> probeLists=canvas.getModelHub().getViewModel().getProbeLists(false);
		List<ProbeList> dpl=new NewDynamicProbelist().run(probeLists, probeLists.get(0).getDataSet().getMasterTable());
		
		if(dpl.size()==0)
			return;
		
		ProbeList pl=dpl.get(0);
		
		List<CanvasComponent> matching=new ArrayList<CanvasComponent>();
		for(Probe p:pl)
		{
			matching.addAll(((SuperModel)model).getComponents(p));
		}
				
		Set<CanvasComponent> compsToHide=new HashSet<CanvasComponent>(components);
		if(invert.getBooleanValue())
			compsToHide.retainAll(matching);
		else
			compsToHide.removeAll(matching);
		
		applyFilter(canvas, model, compsToHide);
	}
	


	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.FilterDPL",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Use the dynamic probelist filtering tool to filter probe nodes.",
				"\0Filter Probes..."				
		);
		pli.addCategory(FILTER_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/filterProbes.png");
		return pli;	

	}
}
