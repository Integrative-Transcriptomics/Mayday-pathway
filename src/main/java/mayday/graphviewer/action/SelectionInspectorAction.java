package mayday.graphviewer.action;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.core.gui.MaydayDialog;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.gui.SelectionInspectorPanel;

@SuppressWarnings("serial")
public class SelectionInspectorAction extends AbstractAction 
{
	private GraphViewerPlot viewer;
	
	
	public SelectionInspectorAction(GraphViewerPlot viewer) 
	{
		super("Show Selection Inspector");
		this.viewer=viewer;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		MaydayDialog dialog=new MaydayDialog();
		dialog.setTitle("Selection Inspector");
		
		dialog.setLayout(new BorderLayout());
		dialog.add(new SelectionInspectorPanel(viewer));
		dialog.pack();
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(MaydayDialog.DISPOSE_ON_CLOSE);
	}

}
