package mayday.graphviewer.plugins.io;

import java.util.HashMap;
import java.util.List;

import javax.swing.JFileChooser;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.PluginManager.IGNORE_PLUGIN;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.datasources.graphml.GraphMLIO;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

@IGNORE_PLUGIN
public class SaveGraphML  extends AbstractGraphViewerPlugin 
{
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		JFileChooser fileChooser=new JFileChooser();

		int res=fileChooser.showSaveDialog(canvas);
		if(res==JFileChooser.APPROVE_OPTION)
		{
			try 
			{
				GraphMLIO.export((SuperModel)model, fileChooser.getSelectedFile());
			} catch (Exception e1) 
			{
				e1.printStackTrace();
				throw new RuntimeException(e1.getMessage());
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
				"PAS.GraphViewer.SaveGraphML",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Save the current Graph to graphml ",
				"Save as GraphML"				
		);
		pli.addCategory(EXPORT_CATEGORY);
		return pli;	
	}
}
