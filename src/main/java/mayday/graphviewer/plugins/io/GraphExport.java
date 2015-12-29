package mayday.graphviewer.plugins.io;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Settings;
import mayday.core.settings.SettingsDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.io.GraphMLExport;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.Utilities;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class GraphExport extends AbstractGraphViewerPlugin 
{
	public static String[] GRAPH_EXPORT_FORMATS={"GraphML", "GraphML with yFiles extensions", "GML", "Dot", "plain text"};
	
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		String lastExportDir= Utilities.prefs.get(Utilities.LAST_GRAPH_EXPORT_DIR, System.getProperty("user.home"));
		
		HierarchicalSetting setting=new HierarchicalSetting("Export Graph");
		RestrictedStringSetting formatSetting=new RestrictedStringSetting("Target Format", null, 0, GRAPH_EXPORT_FORMATS);
		PathSetting fileSetting=new PathSetting("File", null, lastExportDir, false, false, false);
		
		setting.addSetting(formatSetting).addSetting(fileSetting);
		
		SettingsDialog dialog=new SettingsDialog(canvas.getOutermostJWindow(), "Graph Export", new Settings(setting, getPluginInfo().getPreferences()));
		dialog.showAsInputDialog();
		
		if(!dialog.closedWithOK())
			return; 
		
		File file=new File(fileSetting.getStringValue());
		Graph g=model.getGraph();
		
		try {
			switch (formatSetting.getSelectedIndex()) {
			case 0:
				mayday.core.structures.graph.io.GraphExport.exportGraphML(g, file);			
				break;
			case 1:
				GraphMLExport.exportYed(model, file);	
				break;	
			case 2: 
				mayday.core.structures.graph.io.GraphExport.exportGML(g, file);			
				break;
			case 3: 
				mayday.core.structures.graph.io.GraphExport.exportDot(g, file);			
				break;	
			case 4: 
				mayday.core.structures.graph.io.GraphExport.exportText(g, file);			
				break;		
			default:
				break;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}	

		Utilities.prefs.put(Utilities.LAST_GRAPH_EXPORT_DIR, file.getAbsolutePath());
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.ExportGraph",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Export the graph to various formats",
				"Export..."				
		);
		pli.addCategory(EXPORT_CATEGORY);
		return pli;	
	}
}
