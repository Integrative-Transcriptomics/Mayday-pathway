package mayday.graphviewer.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.graphviewer.core.GraphViewerPlot;
import mayday.pathway.sbgn.processdiagram.ProcessDiagram;
import mayday.vis3.graph.dialog.GraphComponentEditor;

@SuppressWarnings("serial")
public class EdgeSettingsAction extends AbstractAction
{
	private GraphViewerPlot viewer;

	public EdgeSettingsAction(GraphViewerPlot viewer) {
		super("Settings");
		this.viewer = viewer;
	}
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		GraphComponentEditor d=new GraphComponentEditor(viewer.getHighlightEdge());
		d.addAdditionalRoles(ProcessDiagram.EDGE_ROLES);
		d.setModal(true);
		d.setVisible(true);

		viewer.updatePlot(
				viewer.getModel().getComponent(viewer.getHighlightEdge().getSource()).getX(),viewer.getModel().getComponent(viewer.getHighlightEdge().getSource()).getY(),
				viewer.getModel().getComponent(viewer.getHighlightEdge().getTarget()).getX(),viewer.getModel().getComponent(viewer.getHighlightEdge().getTarget()).getY()		
		);

	}

}


