package mayday.graphviewer.crossViz3.plugins;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.List;

import mayday.core.gui.MaydayDialog;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.crossViz3.experiments.IExperimentMapping;
import mayday.graphviewer.crossViz3.probes.IProbeMapping;
import mayday.graphviewer.crossViz3.unitTree.UnitTree;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class CrossProbeTreePlugin  extends AbstractGraphViewerPlugin
{
	@Override
	public void run(final GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		IExperimentMapping expMapping=null; 
		IProbeMapping probeMapping=null;
		if(canvas.getModelHub().getExperimentMapping()!=null)
		{
			expMapping=canvas.getModelHub().getExperimentMapping();
		}else
		{
			throw new RuntimeException("No Experiment Mapping available. Please set the Experiment Mapping.");
		}		
		if(canvas.getModelHub().getProbeMapping()!=null)
		{
			probeMapping=canvas.getModelHub().getProbeMapping();
		}
		else
		{
			throw new RuntimeException("No Probe Mapping available. Please set the Probe Mapping.");
		}
		
		MaydayDialog d=new MaydayDialog();
		d.setLayout(new BorderLayout());
		UnitTree tree= new UnitTree(probeMapping, expMapping);
		d.add(tree);
		d.pack();
		d.setVisible(true);
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.CrossProbeTree",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH_CROSS_DATASET,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Import probes from other datasets",
				"Probe Mapping Tree"				
		);
		return pli;	
	}

}
