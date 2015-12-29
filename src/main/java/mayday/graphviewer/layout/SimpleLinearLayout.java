package mayday.graphviewer.layout;

import java.awt.Container;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;

import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.algorithm.DepthFirstIterator;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.layout.CanvasLayouter;
import mayday.vis3.graph.model.GraphModel;

public class SimpleLinearLayout implements CanvasLayouter {

	private boolean horizontal;
	
	private int xSpace=20;
	private int ySpace=30;
	
	
	public SimpleLinearLayout() {
		horizontal=true;
	}
	
	public SimpleLinearLayout(boolean h) 
	{
		horizontal=h;
	}
	
	@Override
	public void layout(Container container, Rectangle bounds, GraphModel model) 
	{
		
		Graph g=model.getGraph();
		
		CanvasComponent n0=model.getComponents().get(0);
		int inDeg=model.getNode(n0).getInDegree();
		for(CanvasComponent comp:model.getComponents())
		{
			if(model.getNode(comp).getInDegree() < inDeg)
			{
				n0=comp;
			}
		}		
		DepthFirstIterator dfiter=new DepthFirstIterator(g,model.getNode(n0));
		List<CanvasComponent> orderedComponents=new LinkedList<CanvasComponent>();
		while(dfiter.hasNext())
		{
			Node n=dfiter.next();
			orderedComponents.add(model.getComponent(n));
		}
		if(horizontal)
			placeHorizontal(bounds,orderedComponents);
		else
			placeVertical(bounds,orderedComponents);
	}
	
	private void placeHorizontal(Rectangle bounds, List<CanvasComponent> comps)
	{
		int usedSpace=bounds.x+xSpace;
		int yPos=bounds.y+ySpace;

		for(CanvasComponent comp: comps)
		{
			comp.setLocation(usedSpace,yPos );
			usedSpace+=xSpace+comp.getWidth();		
		}		
	}
	
	private void placeVertical(Rectangle bounds, List<CanvasComponent> comps)
	{
		int usedSpace=bounds.y+ySpace;
		int xPos=bounds.x+xSpace;
		for(CanvasComponent comp: comps)
		{		
			comp.setLocation(xPos, usedSpace );
			usedSpace+=ySpace+comp.getHeight();					
		}
	}

}
