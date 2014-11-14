package mayday.graphviewer.plugins.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.typed.IntSetting;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.GraphModel;

public class FilterProbeCount extends FilterPlugin 
{

	@Override
	public void run(GraphViewerPlot canvas, GraphModel model,List<CanvasComponent> components) 
	{
		if(components.isEmpty())
			return;
		
		IntSetting cutoff=new IntSetting("Cutoff", null, 1);
		setting.addSetting(cutoff);
		
		SettingDialog sd=new SettingDialog(canvas.getOutermostJWindow(), "Filter", setting);
		sd.showAsInputDialog();
		
		if(!sd.closedWithOK())
			return;
		
		
		
		List<CanvasComponent> matching=new ArrayList<CanvasComponent>();
		
		for(CanvasComponent cc:components)
		{
			int pc=0; 
			if(cc instanceof MultiProbeComponent)
			{
				pc=((MultiProbeComponent)cc).getProbes().size();
			}
			if(pc > cutoff.getIntValue())
			{
				matching.add(cc);
			}
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
				"PAS.GraphViewer.FilterProbeCount",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Remove nodes from which carry the wrong number of probes",
				"Filter Nodes by Probe Count"				
		);
		pli.setIcon("mayday/pathway/gvicons/filterprobecount.png");
		pli.addCategory(FILTER_CATEGORY);
		return pli;	

	}

}
