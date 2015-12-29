package mayday.graphviewer.graphprovider;

import javax.swing.JOptionPane;

import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;

public class GraphProviderApplicator implements Runnable
{
	private GraphViewerPlot viewer;

	public GraphProviderApplicator(GraphViewerPlot viewer) 
	{		
		this.viewer = viewer;
	}

	@Override
	public void run() 
	{
		GraphProvider gp=(GraphProvider)(viewer.getGraphProviderSetting().getInstance());		

		int i=JOptionPane.showConfirmDialog(viewer.getOutermostJWindow(), "Really discard the current graph and create a new one?","Graph Provider Changed",JOptionPane.OK_CANCEL_OPTION);
		if(i!= JOptionPane.OK_OPTION)
			return;

		MultiHashMap<DataSet, ProbeList> probeLists=new MultiHashMap<DataSet, ProbeList>();
		for(ProbeList pl: viewer.getModelHub().getViewModel().getProbeLists(false))
		{
			probeLists.put(pl.getDataSet(), pl);
		}
		
		SuperModel model=new SuperModel(gp.createGraph(probeLists));
		viewer.setModel(model);
		viewer.setLayouter(gp.defaultLayouter());
	}
}
