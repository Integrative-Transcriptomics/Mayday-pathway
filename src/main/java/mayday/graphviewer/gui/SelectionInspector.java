package mayday.graphviewer.gui;

import java.awt.BorderLayout;

import mayday.core.gui.MaydayDialog;
import mayday.graphviewer.core.GraphViewerPlot;

@SuppressWarnings("serial")
public class SelectionInspector extends MaydayDialog 
{
	public SelectionInspector(GraphViewerPlot viewer) 
	{
		setTitle("Selection Inspector");

		setLayout(new BorderLayout());
		add(new SelectionInspectorPanel(viewer));
		pack();
		setVisible(true);
		setDefaultCloseOperation(MaydayDialog.DISPOSE_ON_CLOSE);		
	}
	
}
