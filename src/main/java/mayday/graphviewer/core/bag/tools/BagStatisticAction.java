package mayday.graphviewer.core.bag.tools;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.core.ModelHub;
import mayday.graphviewer.core.bag.ComponentBag;
import mayday.graphviewer.crossViz3.experiments.ExperimentMapping;
import mayday.graphviewer.crossViz3.experiments.IExperimentMapping;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
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

@SuppressWarnings("serial")
public class BagStatisticAction extends AbstractAction 
{
	private ComponentBag bag;
	private ModelHub hub;
	

	
	
	public BagStatisticAction(ComponentBag bag, ModelHub hub) 
	{
		super("Calculate Statistic");
		this.bag = bag;
		this.hub = hub;
	}



	public ComponentBag getBag() {
		return bag;
	}



	public void setBag(ComponentBag bag) {
		this.bag = bag;
	}



	public ModelHub getHub() {
		return hub;
	}



	public void setHub(ModelHub hub) {
		this.hub = hub;
	}



	@Override
	public void actionPerformed(ActionEvent e) 
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
		
		List<CanvasComponent> components=new ArrayList<CanvasComponent>(bag.getComponents());
		
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
			if(hub.getExperimentMapping()!=null)
			{
				expMapping=hub.getExperimentMapping();
			}else
			{
				throw new RuntimeException("No Experiment Mapping available. Please set the Experiment Mapping.");
			}
		}	

		ResultSet res=method.calculate(probes, expMapping);		
		bag.addStatistic(res);
	}

}
