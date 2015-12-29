package mayday.graphviewer.crossViz3.plugins;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.crossViz3.experiments.ExperimentMapping;
import mayday.graphviewer.crossViz3.experiments.IExperimentMapping;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.graphviewer.statistics.BetweenGroupsDistance;
import mayday.graphviewer.statistics.CorrelationMethod;
import mayday.graphviewer.statistics.MultiDataSetMethod;
import mayday.graphviewer.statistics.RVCoefficientMethod;
import mayday.graphviewer.statistics.ResultSet;
import mayday.graphviewer.statistics.StarCorrelation;
import mayday.graphviewer.statistics.StarGroupDistance;
import mayday.graphviewer.statistics.WithinGroupCorrelation;
import mayday.graphviewer.statistics.WithinGroupsDistance;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class CrossProbeSummaryPlugin  extends AbstractGraphViewerPlugin
{
	@Override
	public void run(final GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		ObjectSelectionSetting<MultiDataSetMethod> methodsSetting=new ObjectSelectionSetting<MultiDataSetMethod>("Method",null,0,
				new MultiDataSetMethod[]{
				new BetweenGroupsDistance(), new WithinGroupsDistance(), new StarGroupDistance(),
				new CorrelationMethod(), new WithinGroupCorrelation(), new StarCorrelation(),
				new RVCoefficientMethod()
		});
		
		RestrictedStringSetting groupSetting=new RestrictedStringSetting("Group Probes by", null, 0, new String[]{"Data Set","Node Label","Name","Display Name","Node Role"});
		
		HierarchicalSetting methodSetting=new HierarchicalSetting("Select Method");
		methodSetting.addSetting(methodsSetting);
		methodSetting.addSetting(groupSetting);
		
		SettingDialog sd=new SettingDialog(null, "Select Method", methodSetting);
		sd.setModal(true);
		sd.setVisible(true);
		
		MultiDataSetMethod method=methodsSetting.getObjectValue();
		
		MultiHashMap<String, Probe> probes=null;
		
		switch (groupSetting.getSelectedIndex()) {
		case 0: probes=AbstractGraphViewerPlugin.groupProbesByDataSet(components);			
			break;
		case 1: probes=AbstractGraphViewerPlugin.groupProbesByNode(components);			
			break;
		case 2: probes=AbstractGraphViewerPlugin.groupProbesByName(components);			
			break;
		case 3: probes=AbstractGraphViewerPlugin.groupProbesByDisplayName(components);			
			break;	
		case 4: probes=AbstractGraphViewerPlugin.groupProbesByNodeRole(components);			
			break;
		default:
			break;
		}
		
		List<String> groups=new ArrayList<String>(probes.keySet());
		Collections.sort(groups);
		method.setGroups(groups);
		
		sd=new SettingDialog(null, method.getName(), method.getSetting());
		sd.setModal(true);
		sd.setVisible(true);

		if(!sd.closedWithOK())
			return;
		
		IExperimentMapping expMapping=null;
		Set<DataSet> ds=new HashSet<DataSet>();
		for(Probe p:probes.everything())
		{
			ds.add(p.getMasterTable().getDataSet());
		}
		if(ds.size()==1)
		{
			expMapping=ExperimentMapping.createSingleDataSetMapping(ds.iterator().next());
		}else
		{
			if(canvas.getModelHub().getExperimentMapping()!=null)
			{
				expMapping=canvas.getModelHub().getExperimentMapping();
			}else
			{
				throw new RuntimeException("No Experiment Mapping available. Please set the Experiment Mapping.");
			}
		}	

		ResultSet res=method.calculate(probes, expMapping);	
		
		ResultSetDialog resDialog=new ResultSetDialog(canvas, res);
		resDialog.setVisible(true);

				
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.CrossProbeSummary",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH_CROSS_DATASET,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Import probes from other datasets",
				"Probe Summary"				
		);
		return pli;	
	}
}
