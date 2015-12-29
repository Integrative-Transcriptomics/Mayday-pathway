package mayday.graphviewer.core.edges;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.graphviewer.core.GraphViewerPlot;

@SuppressWarnings("serial")
public class EdgeResetAction extends AbstractAction 
{
	private GraphViewerPlot canvas;
	
	public EdgeResetAction(GraphViewerPlot c) 
	{
		super("Reset edge");
		canvas=c;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		canvas.getEdgeDispatcher().resetEdgeSetting(canvas.getHighlightEdge());
	}

}
