package mayday.pathway.keggview.vis3;

import java.awt.Component;
import java.util.HashMap;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.pathway.core.PathwayDefaults;
import mayday.pathway.keggview.pathways.AnnotationManager;
import mayday.pathway.keggview.pathways.PathwayManager;
import mayday.vis3.PlotPlugin;
import mayday.vis3.components.PlotScrollPane;

public class PathwayPlotPlugin  extends PlotPlugin 
{
	private AnnotationManager annotationManager;
	private PathwayManager pathwayManager;
	
	public void init() 
	{
	}
	
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"mayday.datenkrake.PathwayPlotter",
				new String[0],
				MaydayDefaults.Plugins.CATEGORY_PLOT,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Basic KEGG-Based Pathway Visualizer",
				"KEGG Pathway Viewer"
		);
		pli.addCategory(PathwayDefaults.CATEGORY_PATHWAYS);
		pli.setIcon("mayday/vis3/pathway128.png");
		return pli;	
	}
	
	public Component getComponent() 
	{
		annotationManager=new AnnotationManager();
		pathwayManager=new PathwayManager();
		Component myComponent;
		myComponent = new PlotScrollPane(new PathwayCanvas(annotationManager, pathwayManager));	
		return myComponent;
	}
	

    
    
}
