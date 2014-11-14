package mayday.graphviewer.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;

import mayday.core.DataSet;
import mayday.core.structures.graph.Node;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.layout.CopyLayout;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;
import mayday.vis3.graph.renderer.RendererDecorator;
import mayday.vis3.graph.renderer.dispatcher.AssignedRendererSetting;
import mayday.vis3.graph.renderer.dispatcher.RendererDispatcher;
import mayday.vis3.graph.vis3.SuperColorProvider;
import mayday.vis3.gui.PlotWindow;
import mayday.vis3.model.Visualizer;

@SuppressWarnings("serial")
public class ForkSubgraphAction extends AbstractAction 
{

	private GraphViewerPlot viewer;
	
	public ForkSubgraphAction(GraphViewerPlot viewer) 
	{
		super("New Window");
		this.viewer = viewer;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{	
		// no selection, no action:
		if(viewer.getSelectionModel().getSelectedComponents().isEmpty())
			return;
		// get new model
		List<Node> selectedNodes=new ArrayList<Node>();
		
		
		for(CanvasComponent comp: viewer.getSelectionModel().getSelectedComponents())
		{
			selectedNodes.add(viewer.getModel().getNode(comp));
		}
		GraphModel newModel=viewer.getModel().buildSubModel(selectedNodes);
				
		
		
		GraphViewerPlot viewer2=new GraphViewerPlot(newModel,true);
		
		Visualizer viz =viewer.getModelHub().getViewModel().getVisualizer();
		PlotWindow newPlot=new PlotWindow(viewer2, viz);
		viz.addPlot(newPlot);
		// copy node rendering 
		viewer2.setLayouter(new CopyLayout(viewer.getSelectionModel().getSelectedComponents()));
		viewer2.getRendererDispatcher().updateDefaultRenderer(viewer.getRendererDispatcher().getDefaultRenderer());
		
		viewer2.getRendererDispatcher().clearRoles();
		
		DataSet ds= viewer2.getModelHub().getViewModel().getDataSet();
		SuperColorProvider coloring=viewer2.getModelHub().getColorProvider();
		
		List<AssignedRendererSetting> ret=new ArrayList<AssignedRendererSetting>();
		for(AssignedRendererSetting s: viewer.getRendererDispatcher().getRoleRenderersSetting().getSelection())
		{
			AssignedRendererSetting set=new AssignedRendererSetting(s.getTarget().getStringValue(), ds, coloring);
			set.setPrimaryRenderer(s.getPrimaryRenderer().getStringValue());
			ret.add(set);
		}
		
		viewer2.getRendererDispatcher().getOverallDecorators().clear();
		for(RendererDecorator dec:  viewer.getRendererDispatcher().getOverallDecorators().getSelection())
		{
			viewer2.getRendererDispatcher().getOverallDecorators().add(dec);
		}
		
		viewer2.getRendererDispatcher().getRoleRenderersSetting().update(ret);
	//		viewer2.
	//		viewer2.setRendererRevolver(viewer.getRendererRevolver());
		RendererDispatcher.copyIndividualRenderers(
				viewer.getRendererDispatcher(), viewer2.getRendererDispatcher(), 
				(SuperModel)viewer.getModel(), (SuperModel)viewer2.getModel());
		// end copy node rendering 
		
		newPlot.setVisible(true);
		
	
	}
	
	

}
