package mayday.graphviewer.crossViz3.plugins;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.crossViz3.probes.ProbeMapping;
import mayday.graphviewer.crossViz3.probes.ProbeMappingInspector;
import mayday.graphviewer.crossViz3.probes.ProbeMappingSetting;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class InspectProbeMapping  extends AbstractGraphViewerPlugin 
{
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		//collect probeS;
		SuperModel sm=(SuperModel)model;
		if(canvas.getModelHub().getProbeMapping()!=null)
		{
			new ProbeMappingInspector(canvas.getModelHub().getProbeMapping()).setVisible(true);
		}else
		{
			Set<Probe> probes=sm.getProbes();
			MultiHashMap<DataSet, Probe> probeMap=new MultiHashMap<DataSet, Probe>();
			
			for(Probe p:probes)
			{
				probeMap.put(p.getMasterTable().getDataSet(), p);
			}		

			ProbeMappingSetting pms=new ProbeMappingSetting(probeMap.keySet());

			SettingDialog sd=new SettingDialog(null, "Set Probe Mapping", pms);
			sd.setModal(true);
			sd.setVisible(true);

			ProbeMapping probeMapping=ProbeMapping.createMappingBySetting(pms, probeMap);

			canvas.getModelHub().setProbeMapping(probeMapping);
			canvas.getModelHub().setProbeMappingSetting(pms);
			
			new ProbeMappingInspector(probeMapping).setVisible(true);
		}
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Cross.InspectMapping",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH_CROSS_DATASET,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Show a table of mapped probes",
				"Probe Mapping..."				
		);
		return pli;	
	}


}

