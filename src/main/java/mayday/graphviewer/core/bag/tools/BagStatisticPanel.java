package mayday.graphviewer.core.bag.tools;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;

import mayday.core.Probe;
import mayday.graphviewer.core.bag.BagComponent;
import mayday.graphviewer.core.bag.ComponentBag;
import mayday.graphviewer.statistics.MatrixPlotter;
import mayday.graphviewer.statistics.MatrixPlotterListener;
import mayday.graphviewer.statistics.ResultScope;
import mayday.graphviewer.statistics.ResultSet;

@SuppressWarnings("serial")
public class BagStatisticPanel extends BagCentralComponent implements MatrixPlotterListener
{
	private ResultSet res; 

	public BagStatisticPanel(ResultSet res, ComponentBag bag, BagComponent comp) 
	{
		super(bag, comp);
		this.res=res;
		setLayout(new BorderLayout());
		MatrixPlotter pl=new MatrixPlotter(res, MatrixPlotter.createGradient(res));
		pl.addMatrixPlotterListener(this);
		add(new JScrollPane(pl));
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
