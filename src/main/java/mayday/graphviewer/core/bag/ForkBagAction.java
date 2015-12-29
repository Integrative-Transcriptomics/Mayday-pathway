package mayday.graphviewer.core.bag;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;

import mayday.core.structures.graph.Node;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.layout.CopyLayout;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;
import mayday.vis3.gui.PlotWindow;
import mayday.vis3.model.Visualizer;

@SuppressWarnings("serial")
public class ForkBagAction extends AbstractAction
{
	private ComponentBag bag;
	private GraphViewerPlot viewer;



	public ForkBagAction(ComponentBag bag, GraphViewerPlot viewer) 
	{
		super("Open in new Window");
		this.bag = bag;
		this.viewer = viewer;
	}



	@Override
	public void actionPerformed(ActionEvent e) 
	{
		// get new model
		List<Node> selectedNodes=new ArrayList<Node>();		
		for(CanvasComponent comp:bag.getComponents())
		{
			selectedNodes.add(viewer.getModel().getNode(comp));
		}
		GraphModel newModel=viewer.getModel().buildSubModel(selectedNodes);
		GraphViewerPlot viewer2=new GraphViewerPlot(newModel,true);
		Visualizer viz =viewer.getModelHub().getViewModel().getVisualizer();
		PlotWindow newPlot=new PlotWindow(viewer2, viz);
		viz.addPlot(newPlot);
		viewer2.setLayouter(new CopyLayout(bag.getComponents()));
		viewer2.setRendererRevolver(viewer.getRendererRevolver());
		newPlot.setVisible(true);
		//This kind of code shows that sometimes wired things can be coded in one line. 
		//furthermore, it is widely agreed on that less typecases are better than more. 
		((SuperModel)newModel).remove(((SuperModel)newModel).getComponent(((SuperModel)newModel).getBags().iterator().next()));
		
	}

}
