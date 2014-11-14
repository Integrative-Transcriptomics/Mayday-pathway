package mayday.pathway.keggview.pathways.gui.canvas;

import java.awt.Container;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.layout.CanvasLayouter;
import mayday.vis3.graph.model.GraphModel;

public class KEGGLayouter implements CanvasLayouter
{
	private static final double mag=2.0;

	public void layout(Container container, Rectangle bounds, GraphModel model)
	{
		List<CanvasComponent> nodesByX=new ArrayList<CanvasComponent>();
		nodesByX.addAll(model.getComponents());
		
		for(CanvasComponent comp:nodesByX)
		{
			comp.setBounds(new Rectangle(
					(int)(comp.getBounds().x*mag),
					(int)(comp.getBounds().y*mag),
					(int)(comp.getBounds().width*mag),
					(int)(comp.getBounds().height*mag)));
		}

	}
	
		

}
