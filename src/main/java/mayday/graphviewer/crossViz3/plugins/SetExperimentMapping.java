package mayday.graphviewer.crossViz3.plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.crossViz3.experiments.ExperimentMapping;
import mayday.graphviewer.crossViz3.experiments.ExperimentMappingDialog;
import mayday.graphviewer.crossViz3.experiments.IExperimentMapping;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class SetExperimentMapping  extends AbstractGraphViewerPlugin
{
	@Override
	public void run(final GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		SuperModel sm=(SuperModel)model;
		IExperimentMapping mapping=null; 
		if(canvas.getModelHub().getExperimentMapping()!=null)
		{
			mapping=canvas.getModelHub().getExperimentMapping();
		}else
		{
			Set<Probe> probes=sm.getProbes();
			Set<DataSet> ds=new TreeSet<DataSet>();
			
			for(Probe p:probes)
			{
				ds.add(p.getMasterTable().getDataSet());
			}
			
			String[] mappings={"By Name","By Time Point","By experiment number","Concatenate data dets", "By anchor data set"};
			RestrictedStringSetting mode=new RestrictedStringSetting("Initial Mapping", "How should Mayday guess the experiment mapping", 0, mappings);
			ObjectSelectionSetting<DataSet> anchorDS=new ObjectSelectionSetting<DataSet>("Anchor data set",null,0,(DataSet[]) ds.toArray(new DataSet[ds.size()]));
			HierarchicalSetting setting=new HierarchicalSetting("Experiment Mapping Initialization");
			setting.addSetting(mode).addSetting(anchorDS);
			
			SettingDialog dialog=new SettingDialog(null, "Experiment Mapping", setting);
			dialog.setModal(true);
			dialog.setVisible(true);
			
			if(mode.getSelectedIndex()==0)
				mapping=ExperimentMapping.createMappingByName(new ArrayList<DataSet>(ds));
			if(mode.getSelectedIndex()==1)
				mapping=ExperimentMapping.createMappingTime(new ArrayList<DataSet>(ds));
			if(mode.getSelectedIndex()==2)
				mapping=ExperimentMapping.createBySideMapping(new ArrayList<DataSet>(ds));
			if(mode.getSelectedIndex()==3)
				mapping=ExperimentMapping.createMapping(new ArrayList<DataSet>(ds));
			if(mode.getSelectedIndex()==4)
			{
				List<DataSet> secondaryDS= new ArrayList<DataSet>(ds);
				secondaryDS.remove(anchorDS.getObjectValue());
				mapping=ExperimentMapping.createMapping(anchorDS.getObjectValue(), secondaryDS);
			}
		}
		
		canvas.getModelHub().setExperimentMapping(mapping);
		
		ExperimentMappingDialog emd=new ExperimentMappingDialog(mapping);
		emd.setVisible(true);
		
		
		
		


	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.SetExperimentMapping",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH_CROSS_DATASET,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Set the mapping of experiments of different data sets to each other",
				"Experiment Mapping"				
		);
		return pli;	
	}

}
