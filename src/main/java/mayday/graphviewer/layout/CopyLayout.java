package mayday.graphviewer.layout;

import java.awt.Container;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import mayday.core.structures.graph.Node;
import mayday.pathway.viewer.canvas.AlignLayouter;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.NodeComponent;
import mayday.vis3.graph.layout.CanvasLayouter;
import mayday.vis3.graph.model.GraphModel;

public class CopyLayout implements CanvasLayouter 
{
	private Map<Node, Rectangle> savedPos = new HashMap<Node, Rectangle>(); 
	
	public CopyLayout() 
	{
		
	}
	
	public CopyLayout(Collection<CanvasComponent> comps) 
	{
		for(CanvasComponent cc:comps)
		{
			if(cc instanceof NodeComponent)
				savedPos.put( ((NodeComponent)cc).getNode(), cc.getBounds());
		}
	}
	
	
	@Override
	public void layout(Container container, Rectangle bounds, GraphModel model) 
	{
		for(CanvasComponent cc: model.getComponents())
		{
			if(cc instanceof NodeComponent)
				cc.setBounds(savedPos.get( ((NodeComponent)cc).getNode() ));
		}
		
		new AlignLayouter().layout(container, bounds, model);
	}
}
