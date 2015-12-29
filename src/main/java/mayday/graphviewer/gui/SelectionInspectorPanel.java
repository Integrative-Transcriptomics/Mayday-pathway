package mayday.graphviewer.gui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import mayday.graphviewer.core.GraphViewerPlot;
import mayday.vis3.graph.dialog.ComponentTable;
import mayday.vis3.graph.model.SelectionModel;

@SuppressWarnings("serial")
public class SelectionInspectorPanel extends JPanel 
{
	private GraphViewerPluginList pluginList;
	private ComponentTable selectedComponents;
	private ProbePanel selectedProbePanel;
//	private SendToPanel sendToPanel; 
	
	
	private GraphViewerPlot viewer;
	private SelectionModel selectionModel;
	
	public SelectionInspectorPanel(GraphViewerPlot viewer) 
	{
		this.viewer=viewer;
		selectionModel=viewer.getSelectionModel();
		pluginList=new GraphViewerPluginList(viewer);
		pluginList.setBorder(BorderFactory.createTitledBorder("Graph Viewer Apps"));
		
		selectedComponents=new ComponentTable(this.viewer,selectionModel);		
		selectedComponents.setBorder(BorderFactory.createTitledBorder("Selected Components"));
		
		selectedProbePanel=new ProbePanel(selectionModel);
		selectedProbePanel.setBorder(BorderFactory.createTitledBorder("Selected Probes"));
		
		JSplitPane upDownSplit=new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		JSplitPane upperLeftRightSplit=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		
		upperLeftRightSplit.setResizeWeight(0.5);
		upDownSplit.setResizeWeight(0.66);
		
		upperLeftRightSplit.setLeftComponent(new JScrollPane(selectedComponents));
		upperLeftRightSplit.setRightComponent(new JScrollPane(pluginList));
		
		upDownSplit.setTopComponent(upperLeftRightSplit);
		upDownSplit.setBottomComponent(selectedProbePanel);
		
		setLayout(new BorderLayout());
		add(upDownSplit, BorderLayout.CENTER);	
	}
	
	@Override
	public void removeNotify() 
	{
		super.removeNotify();
		selectionModel.removeSelectionListener(selectedProbePanel);
	}
}
