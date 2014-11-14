package mayday.graphviewer.core.edges;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import mayday.core.gui.MaydayDialog;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.core.structures.graph.Edge;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;
import mayday.vis3.plotsWithoutModel.histogram.HistogramPlotComponent;


public class EdgeWeightSummary extends AbstractGraphViewerPlugin
{
	@Override
	public void run(final GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		EdgeWeightTransformation transformation=canvas.getEdgeTransformationSetting().getTransformation();
		List<Double> weights=new ArrayList<Double>();
		for(Edge e: model.getEdges())
		{
			weights.add((double) transformation.transformEdgeWeight(e.getWeight()));
		}

		HistogramPlotComponent hcp=new HistogramPlotComponent(10);
		hcp.getValueProvider().setValues(weights);
		hcp.setPreferredSize(new Dimension(400, 300));

		MaydayDialog histogramDialog=new MaydayDialog(canvas.getOutermostJWindow());
		histogramDialog.setLayout(new BorderLayout());
		histogramDialog.setTitle("Edge Weights");

		histogramDialog.add(hcp, BorderLayout.CENTER);
		histogramDialog.add(new JButton(new EdgeWeightsAction(canvas, hcp)), BorderLayout.SOUTH); 
		histogramDialog.pack();
		histogramDialog.setVisible(true);
	}

	@SuppressWarnings("serial")
	private class EdgeWeightsAction extends AbstractAction
	{
		private GraphViewerPlot gvp; 
		private HistogramPlotComponent hpc;

		public EdgeWeightsAction(GraphViewerPlot gvp, HistogramPlotComponent hpc) {
			super("Edge Weights");
			this.gvp = gvp;
			this.hpc=hpc;
		}

		@Override
		public void actionPerformed(ActionEvent event) 
		{
			SettingDialog sd=new SettingDialog(gvp.getOutermostJWindow(), "Edge Weights", gvp.getEdgeTransformationSetting());
			sd.showAsInputDialog();
			if(sd.closedWithOK())
			{
				EdgeWeightTransformation transformation=gvp.getEdgeTransformationSetting().getTransformation();
				List<Double> weights=new ArrayList<Double>();
				for(Edge e: gvp.getModel().getEdges())
				{
					weights.add((double) transformation.transformEdgeWeight(e.getWeight()));
				}				
				hpc.getValueProvider().setValues(weights);
				hpc.updatePlot();
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
				"PAS.GraphViewer.EdgeSummary",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Edge Weight",
				"Edge Weight"				
		);

		return pli;	
	}
}


