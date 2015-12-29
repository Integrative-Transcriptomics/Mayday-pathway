package mayday.graphviewer.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;

import mayday.graphviewer.core.DefaultNodeComponent;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.vis3.graph.components.CanvasComponent;

@SuppressWarnings("serial")
public class DeleteAction extends AbstractAction {

	private GraphViewerPlot viewer;
	
	public DeleteAction(GraphViewerPlot viewer) {
		this.viewer = viewer;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		SuperModel sm=(SuperModel)viewer.getModel();
		List<CanvasComponent> affectedComponents=new ArrayList<CanvasComponent>(viewer.getSelectionModel().getSelectedComponents());
		for(CanvasComponent cc: affectedComponents)
		{
			sm.remove(cc);
			if(cc instanceof DefaultNodeComponent)
				((DefaultNodeComponent)cc).invalidateCache();
		}
		viewer.getSelectionModel().clearSelection();
	}

}
