package mayday.graphviewer.plugins.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.GraphModel;

public class FilterDataSet extends FilterPlugin 
{

	@Override
	public void run(GraphViewerPlot canvas, GraphModel model,List<CanvasComponent> components) 
	{
		if(components.isEmpty())
			return;
		
		Set<DataSet> ds=canvas.getModelHub().getDataSets();
		DataSet[] dataSets=(DataSet[]) ds.toArray(new DataSet[ds.size()]);
		
		ObjectSelectionSetting<DataSet> dataSet=new ObjectSelectionSetting<DataSet>("DataSet","The dataset to be kept",0,dataSets);
		setting.addSetting(dataSet);
		
		SettingDialog sd=new SettingDialog(canvas.getOutermostJWindow(), "Filter", setting);
		sd.showAsInputDialog();
		
		if(!sd.closedWithOK())
			return;
		
		
		
		List<CanvasComponent> matching=new ArrayList<CanvasComponent>();
		
		for(CanvasComponent cc:components)
		{			
			if(cc instanceof MultiProbeComponent)
			{
				for(Probe p: ((MultiProbeComponent) cc).getProbes())
				{
					if(p.getMasterTable().getDataSet()==dataSet.getObjectValue())
					{
						matching.add(cc);
						break;
					}
				}
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
				"PAS.GraphViewer.FilterDataSet",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Remove nodes from which carry probes from certain datasets",
				"Filter Probes by DataSet"				
		);
		pli.setIcon("mayday/pathway/gvicons/filterdataset.png");
		pli.addCategory(FILTER_CATEGORY);
		return pli;	

	}

} 
