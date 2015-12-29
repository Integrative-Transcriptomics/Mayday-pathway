package mayday.graphviewer.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;

import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.plugins.io.Load;
import mayday.vis3.graph.components.CanvasComponent;

@SuppressWarnings("serial")
public class LoadAction extends AbstractAction
{
	private GraphViewerPlot viewer;

	public LoadAction(GraphViewerPlot viewer) 
	{
		super("Load");
		this.viewer = viewer;
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		new Load().run(viewer, viewer.getModel(), new ArrayList<CanvasComponent>());
	}
}
