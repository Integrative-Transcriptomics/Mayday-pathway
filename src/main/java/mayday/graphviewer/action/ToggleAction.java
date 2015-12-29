package mayday.graphviewer.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.HubRevolver;

@SuppressWarnings("serial")
public class ToggleAction extends AbstractAction {

	private GraphViewerPlot viewer;
	private HubRevolver revolver;
		
	public ToggleAction(GraphViewerPlot viewer, HubRevolver revolver) 
	{
		super();
		this.viewer = viewer;
		this.revolver=revolver;
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		revolver.next();
		viewer.updatePlot();
		viewer.message(revolver.getCurrentSet());
	}

}
