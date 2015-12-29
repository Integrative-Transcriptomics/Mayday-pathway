package mayday.graphviewer.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;

import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.plugins.io.Save;
import mayday.vis3.graph.components.CanvasComponent;

@SuppressWarnings("serial")
public class SaveAction extends AbstractAction
{
	private GraphViewerPlot viewer;

	public SaveAction(GraphViewerPlot viewer) 
	{
		super("Save");
		this.viewer = viewer;
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		new Save().run(viewer, viewer.getModel(), new ArrayList<CanvasComponent>());
	}
}
