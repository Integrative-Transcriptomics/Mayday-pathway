/**
 * 
 */
package mayday.graphviewer.action;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;

import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.graphviewer.core.DefaultNodeComponent;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.gui.AddMultipleNodesDialog;

@SuppressWarnings("serial")
public class AddNodeAction extends AbstractAction
{
	private GraphViewerPlot viewer;
	
	public AddNodeAction(GraphViewerPlot v) 
	{
		super("Add Node(s)");
		this.viewer=v;
	}

	public void actionPerformed(ActionEvent e) 
	{
		AddMultipleNodesDialog amnd=new AddMultipleNodesDialog();
		amnd.setModal(true);
		amnd.setVisible(true);

		List<MultiProbeNode> res=amnd.getNodes(viewer.getModel().getGraph(), viewer.getModelHub().getViewModel().getDataSet());

		int x=20;
		int y=viewer.getComponentMaxY()+20;
		
		
		
		for(MultiProbeNode n:res)
		{
			DefaultNodeComponent comp=((SuperModel)viewer.getModel()).addNode(n);
						
			comp.setLocation(x,y);
			x+=comp.getWidth()+20;
			if(x>viewer.getWidth())
			{
				x=20;
				y+=20+comp.getHeight();
			}
			viewer.addComponent(comp);				
		}

	}
}