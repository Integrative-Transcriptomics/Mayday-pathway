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

public class Save  extends AbstractGraphViewerPlugin 
{
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components)
	{
		SuperModel sm=(SuperModel)model;
		String d= Utilities.prefs.get(Utilities.LAST_GRAPH_SAVE_DIR, System.getProperty("user.home"));
		JFileChooser fileChooser=new JFileChooser(d);
		int res=fileChooser.showSaveDialog(canvas.getOutermostJWindow());
		if(res==JFileChooser.APPROVE_OPTION)
		{
			try 
			{
				MayGraphIO.exportCompleteGraph(canvas, sm, fileChooser.getSelectedFile());
			} catch (Exception e1) 
			{
				e1.printStackTrace();
				throw new RuntimeException(e1.getMessage());
			}
			Utilities.prefs.put(Utilities.LAST_GRAPH_SAVE_DIR, fileChooser.getCurrentDirectory().getAbsolutePath());
			canvas.message("Saving complete");

		}
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Save",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Save the current Graph to mayday graph format ",
				"\0Save"				
		);
		pli.setIcon("mayday/pathway/gvicons/save_graph.png");
		return pli;	
	}
}


