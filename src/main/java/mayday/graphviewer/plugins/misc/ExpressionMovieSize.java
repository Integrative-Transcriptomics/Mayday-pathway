package mayday.graphviewer.plugins.misc;

import java.util.HashMap;
import java.util.List;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.ValueProvider.ExperimentProvider;
import mayday.vis3.ValueProvider.Provider;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class ExpressionMovieSize extends AbstractGraphViewerPlugin 
{
	@SuppressWarnings("serial")
	@Override
	public void run(final GraphViewerPlot canvas, GraphModel model,
			List<CanvasComponent> components) 
	{
		ExpressionMovieDialog emd = new ExpressionMovieDialog(canvas){
			@Override
			protected void updateUIAndPlot() 
			{
				Provider p=canvas.getModelHub().getValueProvider().getProvider();
				if(p instanceof ExperimentProvider)
					((ExperimentProvider) p).setExperiment(listener.getCurrent());
				canvas.getModelHub().getValueProvider().setProvider(canvas.getModelHub().getValueProvider().getProvider());
				canvas.updatePlotNow();
				currentExp.setText(prepareLabel(listener.getCurrent()+1));
				currentExpName.setText(canvas.getModelHub().getViewModel().getDataSet().getMasterTable().getExperimentName(listener.getCurrent()));
			}
		};
		emd.setVisible(true);
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(this.getClass(),
				"PAS.GraphViewer.ExpressionMovieSize", new String[] {},
				GraphViewerPlugin.MC_GRAPH, new HashMap<String, Object>(),
				"Stephan Symons", "symons@informatik.uni-tuebingen.de",
				"TODO ", "ExpressionMovie (Size)");
		pli.addCategory(ANIMATE_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/expressionmovie2.png");
		return pli;
	}

}
