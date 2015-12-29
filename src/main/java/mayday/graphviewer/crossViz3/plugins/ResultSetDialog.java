package mayday.graphviewer.crossViz3.plugins;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JScrollPane;

import mayday.core.Probe;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.statistics.MatrixPlotter;
import mayday.graphviewer.statistics.MatrixPlotterListener;
import mayday.graphviewer.statistics.ResultScope;
import mayday.graphviewer.statistics.ResultSet;
import mayday.vis3.graph.components.MultiProbeComponent;

@SuppressWarnings("serial")
public class ResultSetDialog extends JDialog implements MatrixPlotterListener
{
	private MatrixPlotter plotter;
	private GraphViewerPlot viewer;
	private ResultSet res;

	

	public ResultSetDialog(GraphViewerPlot viewer, ResultSet res) 
	{
		super(viewer.getOutermostJWindow());
		setTitle(res.getTitle());
		this.viewer = viewer;
		this.res = res;		
		
		setLayout(new BorderLayout());
		plotter=new MatrixPlotter(res, MatrixPlotter.createGradient(res));
		plotter.addMatrixPlotterListener(this);
		add(new JScrollPane(plotter));
		
		pack();
	}

	protected void selectProbes(Collection<Probe> probes)
	{
		SuperModel model=(SuperModel)viewer.getModel();
		viewer.getSelectionModel().clearSelection();
		for(Probe p:probes)
		{
			for(MultiProbeComponent comp: model.getComponents(p))
			{
				viewer.getSelectionModel().select(comp);
			}								
		}
	}
	
	@Override
	public void selectedRows(int[] rows) 
	{
		List<Probe> selectedProbes=new ArrayList<Probe>();
		if(res.getPreferredScope()==ResultScope.Probes)
		{		
			for(int i:rows)
			{
				selectedProbes.add(res.getRowProbes().get(i));
			}
		}
		if(res.getPreferredScope()==ResultScope.Groups)
		{
			for(int i:rows)
			{
				selectedProbes.addAll(res.getRowGroupProbes().get(res.getGroupResults().getRowName(i)));
			}
		}
		selectProbes(selectedProbes);
	}
}
