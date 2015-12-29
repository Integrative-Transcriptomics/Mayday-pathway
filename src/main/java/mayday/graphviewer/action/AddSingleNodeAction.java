package mayday.graphviewer.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.vis3.graph.components.CanvasComponent;

@SuppressWarnings("serial")
public class AddSingleNodeAction extends AbstractAction
{
	protected String role;
	protected static int num=1; 
	protected GraphViewerPlot canvas;
	
	
	public AddSingleNodeAction(GraphViewerPlot canvas, String r) 
	{
		super("Add new "+r);
		this.role=r;
		this.canvas=canvas;
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(canvas.getModel() instanceof SuperModel)
		{
			SuperModel model=(SuperModel) canvas.getModel();
			MultiProbeNode n=new MultiProbeNode(model.getGraph());
			n.setRole(role);
			n.setName(role+" "+num);
			num++;
		
			CanvasComponent cc=model.addNode(n);
			cc.setLocation(canvas.getMousePosition());
		}		
		
	}
}
