package mayday.graphviewer.crossViz3.plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.gui.ProbeListSelectorDialog;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class AddCrossDataSetProbes extends AbstractGraphViewerPlugin
{
	@Override
	public void run(final GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		List<DataSet> dataSets=canvas.getModelHub().getAllAvailableDataSets();
		ProbeListSelectorDialog plsd=new ProbeListSelectorDialog(dataSets);
		plsd.setVisible(true);
		
		List<ProbeList> res=plsd.getProbeLists();
		
		MultiHashMap<DataSet, ProbeList> probeLists=new MultiHashMap<DataSet, ProbeList>();
		for(ProbeList pl:res)
		{
			probeLists.put(pl.getDataSet(), pl);
		}
		
		for(DataSet ds: probeLists.keySet())
		{
			canvas.getModelHub().addProbeLists(ds, probeLists.get(ds));			
			List<CanvasComponent> probesToBePlaced=new ArrayList<CanvasComponent>();
			for(ProbeList plist:probeLists.get(ds))				
			{
				List<CanvasComponent> comps=((SuperModel)model).addProbes(plist);
				probesToBePlaced.addAll(comps);
				
				placeComponents(probesToBePlaced, canvas, 25, 25);
			}
		}
		

	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.CrossProbeLists",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH_CROSS_DATASET,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Import probes from other datasets",
				"Cross ProbeList Import"				
		);
		return pli;	
	}

}