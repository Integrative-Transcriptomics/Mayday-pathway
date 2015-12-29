package mayday.pathway.viewer;

import java.awt.Component;
import java.util.HashMap;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.PlotPlugin;
import mayday.vis3.components.PlotScrollPane;

public class SBGNViewerPlugin extends PlotPlugin 
{
	
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"mayday.canvas.SBGNViewer",
				new String[0],
				MaydayDefaults.Plugins.CATEGORY_PLOT,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"View Pathways in SBGN",
				"SBGN Pathway Viewer"
		);
		pli.addCategory("Pathways");
		pli.setIcon("mayday/vis3/sbgn128.png");
		return pli;	
	}
	
	@Override
	public void init() {}
	
	public Component getComponent() 
	{
		Component myComponent;
		myComponent = new PlotScrollPane(new SBGNViewer());	
		return myComponent;
	}

}
