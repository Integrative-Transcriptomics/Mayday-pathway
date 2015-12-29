package mayday.graphviewer.plugins.misc;

import java.util.HashMap;
import java.util.List;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class ExpressionMovie  extends AbstractGraphViewerPlugin 
{
	@Override
	public void run(final GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		ExpressionMovieDialog emd=new ExpressionMovieDialog(canvas);
		emd.setVisible(true);
	}
	

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.ExpressionMovie",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"TODO ",
				"ExpressionMovie (Color)"				
		);
		pli.setIcon("mayday/pathway/gvicons/expressionmovie.png");
		pli.addCategory(ANIMATE_CATEGORY);
		return pli;	

	}
	
	

}
