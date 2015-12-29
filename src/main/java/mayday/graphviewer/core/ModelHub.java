package mayday.graphviewer.core;

import java.awt.Component;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.stream.XMLStreamWriter;

import mayday.core.DataSet;
import mayday.core.EventFirer;
import mayday.core.MaydayDefaults;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.probelistmanager.UnionProbeList;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.PluginInstanceSetting;
import mayday.core.settings.generic.SortedExtendableConfigurableObjectListSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.graphviewer.action.ScaleNodesToValues2;
import mayday.graphviewer.crossViz3.experiments.IExperimentMapping;
import mayday.graphviewer.crossViz3.probes.IProbeMapping;
import mayday.graphviewer.crossViz3.probes.IProbeUnit;
import mayday.graphviewer.crossViz3.probes.ProbeMappingSetting;
import mayday.graphviewer.datasources.maygraph.ViewerSettingsFileParser;
import mayday.vis3.ColorProviderSetting;
import mayday.vis3.PlotPlugin;
import mayday.vis3.ValueProvider;
import mayday.vis3.graph.vis3.SuperColorProvider;
import mayday.vis3.gui.PlotWindow;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3.model.Visualizer;

/**
 * Central object that keeps all the view models.
 * @author symons
 *
 */
public class ModelHub implements ViewModelListener
{
	private ViewModel masterViewModel;
	private SuperColorProvider masterColoring;
	private ValueProvider masterValueProvider;
	private ProbeList masterProbeList;

	private Map<DataSet, ViewModel> viewModels=new HashMap<DataSet, ViewModel>();	
	private Map<DataSet, SuperColorProvider> colorProviders=new HashMap<DataSet, SuperColorProvider>();
	private Map<DataSet, ValueProvider> valueProviders=new HashMap<DataSet, ValueProvider>();
	private Map<DataSet, ProbeList> addedProbeLists=new HashMap<DataSet, ProbeList>();

	private IProbeMapping probeMapping;
	private IExperimentMapping experimentMapping; 
	private ProbeMappingSetting probeMappingSetting;
	
	private HubEventFirer eventFirer=new HubEventFirer();

	private HierarchicalSetting masterSetting; 

	private HierarchicalSetting datasetSettings;
	private BooleanSetting communicateSelections=new BooleanSetting("Communicate Selections",null,true);
	private SortedExtendableConfigurableObjectListSetting<HierarchicalSetting> datasetSetting;
	
	public ModelHub(ViewModel master) 
	{
		masterViewModel=master;
		masterColoring=new SuperColorProvider(masterViewModel);
		
		masterColoring.setExperiment(0);
		masterColoring.setMode(ColorProviderSetting.COLOR_BY_EXPERIMENT_VALUE);
		masterColoring.addChangeListener(new ColorProviderListener());
		
		masterViewModel.addViewModelListener(this);
		
		masterValueProvider=new ValueProvider(masterViewModel, "Node Size");

		masterSetting=new HierarchicalSetting("Master ViewModel");
		masterSetting.addSetting(masterColoring.getSetting());
		masterSetting.addSetting(masterValueProvider.getSetting());	

		viewModels.put(master.getDataSet(), master);
		valueProviders.put(master.getDataSet(),masterValueProvider);
		colorProviders.put(master.getDataSet(),masterColoring);		
		
		datasetSettings=new HierarchicalSetting("Data Sets");
		
		HierarchicalSetting ms=new HierarchicalSetting(master.getDataSet().getName());
		ms.addSetting(masterColoring.getSetting()).addSetting(masterValueProvider.getSetting());
		
		PluginInstanceSetting<PlotPlugin> additionalPlugins=new PluginInstanceSetting<PlotPlugin>("Addtional Plot...", null, MaydayDefaults.Plugins.CATEGORY_PLOT);
		additionalPlugins.addChangeListener(new PlotPluginListener(master.getDataSet()));
		ms.addSetting(additionalPlugins);
		
		datasetSettings.addSetting(ms);
		datasetSetting=new SortedExtendableConfigurableObjectListSetting<HierarchicalSetting>("Additional DataSets",null, new DataSetSettingBridge());
		datasetSetting.addElement(ms);
		
	}
	
	

	private void addViewModel(ViewModel m)
	{
		viewModels.put(m.getDataSet(),m);
		SuperColorProvider coloring=new SuperColorProvider(m);
		colorProviders.put(m.getDataSet(), coloring);
		coloring.setExperiment(0);
		coloring.setMode(ColorProviderSetting.COLOR_BY_EXPERIMENT_VALUE);
		coloring.addChangeListener(new ColorProviderListener());
		
		valueProviders.put(m.getDataSet(), new ValueProvider(m, "Node Size"));		
		
		HierarchicalSetting ms=new HierarchicalSetting(m.getDataSet().getName());
		ms.addSetting(colorProviders.get(m.getDataSet()).getSetting())
		.addSetting(valueProviders.get(m.getDataSet()).getSetting());
		
		
		PluginInstanceSetting<PlotPlugin> additionalPlugins=new PluginInstanceSetting<PlotPlugin>("Addtional Plot...", null, MaydayDefaults.Plugins.CATEGORY_PLOT);
		additionalPlugins.addChangeListener(new PlotPluginListener(m.getDataSet()));
		ms.addSetting(additionalPlugins);
		
		datasetSetting.addElement(ms);
		
		datasetSettings.addSetting(datasetSetting);
		datasetSettings.addSetting(communicateSelections);
	}
	
	

	public void addProbeLists(DataSet ds, List<ProbeList> probeLists)
	{
		ViewModel vm=viewModels.get(ds);
		if(!getDataSets().contains(ds))
		{
			Visualizer v=new Visualizer(ds, probeLists);		
			vm= v.getViewModel();
			
			addViewModel(vm);			
		}
		
		for(ProbeList pl:probeLists)
			vm.addProbeListToSelection(pl);
	}
	
	public void addProbes(DataSet ds, List<Probe> probes)
	{
		ProbeList pl=getAddedProbes(ds);

		for(Probe p:probes)
		{
			if(!pl.contains(p))
				pl.addProbe(p);
		}
			
		List<ProbeList> pls=new ArrayList<ProbeList>();
		pls.add(pl);
		
		if(!getDataSets().contains(ds))
		{
			Visualizer v=new Visualizer(ds, pls);		
			ViewModel vm= v.getViewModel();
			
			addViewModel(vm);	
			
		}
		
		addProbeLists(ds, pls);
	}
	
	public void resetColoring()
	{
		for(SuperColorProvider c:colorProviders.values())
		{
			c.setMode(ColorProviderSetting.COLOR_BY_EXPERIMENT_VALUE);
			c.setExperiment(0);
		}
	}
	
	public void clear()
	{
		viewModels.clear();	
		colorProviders.clear();
		valueProviders.clear();
		addedProbeLists.clear();
	}
	
	public List<DataSet> getAllAvailableDataSets()
	{
		return masterViewModel.getDataSet().getDataSetManager().getDataSets();
	}

	public Set<DataSet> getDataSets()
	{
		return viewModels.keySet();
	}

	public HierarchicalSetting getMasterSetting() 
	{
		return masterSetting;
	}
	
	public HierarchicalSetting getDataSetSettings()
	{
		return datasetSettings;
	}
	
	public SortedExtendableConfigurableObjectListSetting<HierarchicalSetting> getDataSetSetting()
	{
		return datasetSetting;
	}

	public IProbeMapping getProbeMapping() 
	{
		return probeMapping;
	}

	public void setProbeMapping(IProbeMapping probeMapping) 
	{
		this.probeMapping = probeMapping;
	}

	public IExperimentMapping getExperimentMapping() 
	{
		return experimentMapping;
	}

	public void setExperimentMapping(IExperimentMapping experimentMapping) 
	{
		this.experimentMapping = experimentMapping;
	}

	public ViewModel getViewModel()
	{
		return masterViewModel;
	}

	public ViewModel getViewModel(DataSet ds)
	{
		return viewModels.get(ds);
	}

	public SuperColorProvider getColorProvider()
	{
		return masterColoring;
	}
	public SuperColorProvider getColorProvider(DataSet ds)
	{
		return colorProviders.get(ds);
	}

	public ValueProvider getValueProvider()
	{
		return masterValueProvider;
	}

	public ValueProvider getValueProvider(DataSet ds)
	{
		return valueProviders.get(ds);
	}	

	public ProbeList getAddedProbes()
	{
		if(masterProbeList==null)
		{
			masterProbeList=getAddedProbes(masterViewModel.getDataSet());
		}
		return masterProbeList;
	}

	public ProbeList getAddedProbes(DataSet ds)
	{
		ProbeList addedProbes=addedProbeLists.get(ds);

		if(addedProbes==null)
		{
			UnionProbeList pl=new UnionProbeList(ds, null);
			pl.setName("Graph Viewer ("+SimpleDateFormat.getDateTimeInstance().format(new Date())+")");
			ds.getProbeListManager().addObjectAtBottom(pl);
			addedProbes=new ProbeList(ds,true);
			addedProbes.setName("Graph Viewer Added Probes "+SimpleDateFormat.getDateTimeInstance().format(new Date())+")");
			addedProbes.setParent(pl);
			addedProbes.getDataSet().getProbeListManager().addObjectAtBottom(addedProbes);
			if(viewModels.containsKey(ds))
				viewModels.get(ds).addProbeListToSelection(addedProbes);
		}
		return addedProbes;
	}

	@Override
	public void viewModelChanged(ViewModelEvent vme) 
	{
		if(vme.getSource()==masterViewModel)
		{		
			if(vme.getChange()==ViewModelEvent.DATA_MANIPULATION_CHANGED)
			{
				masterColoring.setExperimentExtremes();	

				for(DataSet ds:getDataSets())
				{
					if(ds==masterViewModel.getDataSet())
						continue;
					viewModels.get(ds).setDataManipulator(masterViewModel.getDataManipulator());					
				}
				eventFirer.fireEvent(new HubEvent(this));

			}	
		}
		if(communicateSelections.getBooleanValue() && vme.getChange()==ViewModelEvent.PROBE_SELECTION_CHANGED)
		{
			ViewModel vm=(ViewModel)vme.getSource();
			for(Probe p: vm.getSelectedProbes())
			{
				dispatchCrossDSSelection(vm,p);
			}
		}
			
	}

	private class HubEventFirer extends EventFirer<HubEvent, HubListener>
	{
		protected void dispatchEvent(HubEvent event, HubListener listener) 
		{
			listener.hubStateChanged(event);
		}
	}
	
	public void addHubListener(HubListener l)
	{
		eventFirer.addListener(l);
	}
	
	public void removeHubListener(HubListener l)
	{
		eventFirer.removeListener(l);
	}

	private class ColorProviderListener implements ChangeListener
	{
		@Override
		public void stateChanged(ChangeEvent e) 
		{
			eventFirer.fireEvent(new HubEvent(ModelHub.this));
		}
	}
	
	private class PlotPluginListener implements SettingChangeListener
	{
		private DataSet ds;		
			
		public PlotPluginListener(DataSet ds) 
		{
			this.ds = ds;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void stateChanged(SettingChangeEvent e) 
		{
			Component c=((PluginInstanceSetting<PlotPlugin>)e.getSource()).getInstance().getComponent();			
			PlotWindow newPlot = new PlotWindow(c, viewModels.get(ds).getVisualizer());
			viewModels.get(ds).getVisualizer().addPlot(newPlot);
			newPlot.setVisible(true);
			
		}
	}

	public void removeNotify() 
	{
		for(DataSet ds: getDataSets())
		{
			viewModels.get(ds).removeViewModelListener(this);
			viewModels.get(ds).removeViewModelListener(colorProviders.get(ds));
			
//			viewModel.removeViewModelListener(this);
//			viewModel.removeViewModelListener(coloring);
//			viewModel.removeViewModelListener((SimpleSelectionModel)selectionModel);	
//			if(selectionInspector!=null)
//				selectionInspector.removeNotify();
		}		
	}

	private void dispatchCrossDSSelection(ViewModel source, Probe p)
	{
		if(probeMapping!=null)
		{
			for(IProbeUnit unit: probeMapping.getUnits(p))
			{
				//wake up view model
				for(Probe pr:unit.getProbes())
				{
					if(p.getMasterTable().getDataSet() == pr.getMasterTable().getDataSet())
					{
						continue;
					}
					ViewModel vm=getViewModel(pr.getMasterTable().getDataSet());
					if(vm!=source)
					{
						vm.toggleProbeSelected(pr);
						
					}
					
				}
			}
		}
	}
	

	public void setNodeSizing(ScaleNodesToValues2 scaleNodesToValues2) 
	{
		for(ValueProvider vp:valueProviders.values())
		{
			vp.addChangeListener(scaleNodesToValues2);
		}
		
	}	

	public ProbeMappingSetting getProbeMappingSetting() {
		return probeMappingSetting;
	}

	public void setProbeMappingSetting(ProbeMappingSetting probeMappingSetting) {
		this.probeMappingSetting = probeMappingSetting;
	}

	
	public void exportHubSettings(XMLStreamWriter writer) throws Exception
	{
		writer.writeStartElement(ViewerSettingsFileParser.VIS_3_SETTINGS);
		
		//write master color provider
		writer.writeStartElement(ViewerSettingsFileParser.DEFAULT_COLORING);
		StringWriter w=new StringWriter();
		masterColoring.getSetting().toPrefNode().saveTo(new BufferedWriter(w));
		writer.writeAttribute(ViewerSettingsFileParser.CONFIG, w.toString());		
		writer.writeEndElement();
			
		//write ds colorings;
		for(DataSet ds: getDataSets())
		{
			writer.writeStartElement(ViewerSettingsFileParser.DATASET_COLORINGS);
			writer.writeAttribute("target", ds.getName());
			w=new StringWriter();			
			colorProviders.get(ds).getSetting().toPrefNode().saveTo(new BufferedWriter(w));
			writer.writeAttribute(ViewerSettingsFileParser.CONFIG, w.toString());		
			writer.writeEndElement();				
		}	
		//write data manipulation method:
		writer.writeStartElement(ViewerSettingsFileParser.DATA_MANIPULATION);
		writer.writeAttribute(ViewerSettingsFileParser.CONFIG, masterViewModel.getDataManipulator().getManipulation().getPluginInfo().getIdentifier());
		writer.writeEndElement();		
		// done here		
		writer.writeEndElement();		

	}




}
