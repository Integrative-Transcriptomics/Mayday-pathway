package mayday.graphviewer.action;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;

import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.algorithm.DepthFirstIterator;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.layout.ParentCenteredRadialLayout;
import mayday.vis3.graph.GraphCanvas;
import mayday.vis3.graph.components.CanvasComponent;

@SuppressWarnings("serial")
public class CenterAction extends AbstractAction 
{
	private GraphCanvas viewer;
	private CanvasComponent center;
	
	public CenterAction(GraphCanvas viewer) 
	{
		super("Center Graph at this node");
		this.viewer=viewer;		
	}
	
	public CenterAction(GraphCanvas viewer, CanvasComponent center) 
	{
		super("Center Graph at this node");
		this.viewer=viewer;	
		this.center=center;
	}
		
	public CanvasComponent getCenter() {
		return center;
	}

	public void setCenter(CanvasComponent center) {
		this.center = center;
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if(center==null) return;
		Node centerNode= viewer.getModel().getNode(center);
		DepthFirstIterator dfi=new DepthFirstIterator(viewer.getModel().getGraph(), centerNode);
		dfi.setSingleComponent(true);
		List<Node> nodes=new ArrayList<Node>();
		while(dfi.hasNext())
		{
			nodes.add(dfi.next());			
		}
		
		SuperModel model=((SuperModel)viewer.getModel()).buildSubModel(nodes);
		
		Rectangle r=null;
		boolean first=true;
		for(Node n:nodes)
		{
			CanvasComponent c=viewer.getModel().getComponent(n);
			if(first)
			{
				first=false;
				r=new Rectangle(c.getLocation(), new Dimension(c.getWidth(), c.getHeight()));
				continue;
			}
			r.add(c.getBounds());			
		}
		new ParentCenteredRadialLayout(centerNode).layout(viewer, r, model);
		
		for(CanvasComponent comp:model.getComponents())
		{
			viewer.getModel().getComponent(model.getNode(comp)).setLocation(comp.getLocation());
			viewer.revalidateEdge(viewer.getModel().getComponent(model.getNode(comp)));			
			viewer.center(center.getBounds(),true);
		}
		viewer.message("Centered at "+center.getLabel());
		viewer.updateSize();
		
	}
}
