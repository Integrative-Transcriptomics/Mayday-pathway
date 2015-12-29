package mayday.graphviewer.graphmodelprovider.gui;

import java.awt.Point;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.graphmodelprovider.GraphModelProvider;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class GraphModelProviderPlugin  extends AbstractGraphViewerPlugin
{
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		MultiHashMap<DataSet, ProbeList> probeLists=new MultiHashMap<DataSet, ProbeList>();
		for(ProbeList pl: canvas.getModelHub().getViewModel().getProbeLists(true))
		{
			probeLists.put(pl.getDataSet(), pl);
		}		
		GraphProviderWizard wizard=new GraphProviderWizard(canvas,  probeLists);
		wizard.setVisible(true);
		if(wizard.isCancelled() || wizard.getProvider()==null)
			return;
		GraphModelProvider provider=wizard.getProvider();
		SuperModel newModel=provider.getGraphModel();
		if(wizard.isAdd())
		{
			int yoff=canvas.getComponentMaxY();
			for(DataSet ds: provider.getProbeLists().keySet())
			{
				canvas.getModelHub().addProbeLists(ds, provider.getProbeLists().get(ds));		
			}
			provider.defaultLayouter().layout(canvas, canvas.getBounds(), newModel);
			Map<CanvasComponent, Point> compMap=new HashMap<CanvasComponent, Point>();
			for(CanvasComponent cc: newModel.getComponents())
			{
				compMap.put(cc, new Point(cc.getX(), cc.getY()+yoff));			
			}
			for(CanvasComponent cc: model.getComponents())
			{
				compMap.put(cc, cc.getLocation());			
			}
			((SuperModel)model).addModel(newModel);
			for(CanvasComponent cc: model.getComponents())
			{
				cc.setLocation(compMap.get(cc));			
			}
		}else
		{
			canvas.setModel(provider.getGraphModel());
			canvas.setLayouter(provider.defaultLayouter());
		}
		canvas.revalidateEdges();
		canvas.updateSize();
		canvas.center(getBoundingRect(newModel.getComponents()),false);
		

	}
	
	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.GraphProvider",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Create a new graph from mayday data or from external data",
				"Create new Graph"				
		);
		pli.setIcon("mayday/pathway/gvicons/new_graph.png");
		return pli;	
	}
}
