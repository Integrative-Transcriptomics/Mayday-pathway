package mayday.graphviewer.action;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;

import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.core.bag.ComponentBag;
import mayday.vis3.graph.components.CanvasComponent;

@SuppressWarnings("serial")
public class GroupSelectedAction extends AbstractAction {

	private GraphViewerPlot viewer;
	
	
	
	public GroupSelectedAction(GraphViewerPlot viewer) 
	{
		this.viewer = viewer;
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		List<CanvasComponent> components=viewer.getSelectionModel().getSelectedComponents();
		if(components.isEmpty()) return; // nothing to do. 
		ComponentBag bag=new ComponentBag((SuperModel)viewer.getModel());
		bag.setName("Grouped Nodes");
		bag.setColor(Color.red);
		for(CanvasComponent c:components)
		{
			bag.addComponent(c);				
		}
		((GraphBagModel)viewer.getModel()).addBag(bag);
		viewer.updatePlot();
		
	}
}
