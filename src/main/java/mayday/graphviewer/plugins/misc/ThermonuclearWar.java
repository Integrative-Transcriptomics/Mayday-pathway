package mayday.graphviewer.plugins.misc;

import java.util.HashMap;
import java.util.List;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.PluginManager.IGNORE_PLUGIN;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

@IGNORE_PLUGIN
public class ThermonuclearWar extends AbstractGraphViewerPlugin 
{
	@Override
	public void run(final GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
//		int nuke=JOptionPane.showConfirmDialog(canvas, "Would you like to play a game?", "Shall we play a game?", JOptionPane.YES_NO_OPTION);
//		if(nuke==JOptionPane.OK_OPTION)
//		{
//			canvas.getRendererDispatcher().clearRoles();
//			AssignedRendererSetting setting=new AssignedRendererSetting("nuke", 
//					new RendererPluginSetting(canvas.getModelHub().getViewModel().getDataSet(), canvas.getModelHub().getColorProvider()));
//			setting.setPrimaryRenderer("PAS.GraphViewer.Renderer.Nuke");
//			canvas.getRendererDispatcher().setDefaultRenderer(setting);
//			return;
//		}
//		JOptionPane.showMessageDialog(canvas, "The only winning move is\nnot to play.","A strange game.",JOptionPane.INFORMATION_MESSAGE);			
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Wargame",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Shall we play a game?",
				"War Games"				
		);
		pli.addCategory(ANIMATE_CATEGORY);
		return pli;	

	}
}
