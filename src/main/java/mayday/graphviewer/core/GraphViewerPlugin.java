package mayday.graphviewer.core;

import java.awt.Component;
import java.util.HashMap;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.graphviewer.graphprovider.GraphProvider;
import mayday.graphviewer.graphprovider.ProbeGraphProvider;
import mayday.graphviewer.graphprovider.ProbeListHierarchyGraphProvider;
import mayday.graphviewer.graphprovider.ProbeListsFlatGraphProvider;
import mayday.vis3.PlotPlugin;
import mayday.vis3.components.PlotScrollPane;

public class GraphViewerPlugin extends PlotPlugin 
{
	private static ObjectSelectionSetting<GraphProvider> graphProviderSetting;
		
	public void init() 
	{
	}
	
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"mayday.pathway.GraphViewer",
				new String[0],
				MaydayDefaults.Plugins.CATEGORY_PLOT,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Graph Viewer plugin",
				"MGV - Mayday Graph Viewer"
		);
		pli.setIcon("mayday/vis3/graph128.png");
		setIsMajorPlot(pli);
		return pli;	
	}
	
	@SuppressWarnings("unchecked")
	public Component getComponent() 
	{
		PluginInfo.loadDefaultSettings(getSetting(), "mayday.pathway.GraphViewer");
		
		Component myComponent;
		myComponent = new PlotScrollPane(new GraphViewerPlot(
				((ObjectSelectionSetting<GraphProvider>)getSetting()).getObjectValue()));	
		return myComponent;
	}
	
	@Override
	public Setting getSetting() 
	{
		if(graphProviderSetting==null)
		{
			graphProviderSetting=new ObjectSelectionSetting<GraphProvider>
			(	"Graph Provider", 
					"A graph provider creates graphs from Mayday data structures.\n" +
					" There are several ways of doing this:\n" +
					"- Use the probes or probe lists without structure" +
					"- The probe lists with their hierarchical order" , 0,new GraphProvider[]{
					new ProbeGraphProvider(), new ProbeListHierarchyGraphProvider(), new ProbeListsFlatGraphProvider()});
		}
		return graphProviderSetting;
	}
	
	
}
