package mayday.graphviewer.plugins.io;

import java.util.HashMap;
import java.util.List;

import javax.swing.JFileChooser;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.core.Utilities;
import mayday.graphviewer.datasources.maygraph.MayGraphIO;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class Load extends AbstractGraphViewerPlugin 
{
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components)
	{
		SuperModel sm=(SuperModel)model;
		
		String d= Utilities.prefs.get(Utilities.LAST_GRAPH_SAVE_DIR, System.getProperty("user.home"));
		JFileChooser fileChooser=new JFileChooser(d);
		int res=fileChooser.showOpenDialog(canvas.getOutermostJWindow());
		if(res==JFileChooser.APPROVE_OPTION)
		{
			try {
				MayGraphIO.loadGraph(fileChooser.getSelectedFile(), sm, canvas);
			} catch (Exception e) 
			{
				throw new RuntimeException("Error parsing the graph", e);
			}
			Utilities.prefs.put(Utilities.LAST_GRAPH_SAVE_DIR, fileChooser.getCurrentDirectory().getAbsolutePath());
			
			if(canvas.getModel().getGraph().getName()==null || canvas.getModel().getGraph().getName().isEmpty() )
				canvas.message("Opened new Graph");
			else
				canvas.message("Opened "+canvas.getModel().getGraph().getName());
		}
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Load",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Load a graph from mayday graph format.",
				"\0Load"				
		);
		pli.setIcon("mayday/pathway/gvicons/load_graph.png");
		return pli;	
	}
}
