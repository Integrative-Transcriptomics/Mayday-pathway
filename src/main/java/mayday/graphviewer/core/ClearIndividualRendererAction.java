package mayday.graphviewer.core;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.renderer.dispatcher.RendererDispatcher;

@SuppressWarnings("serial")
public class ClearIndividualRendererAction extends AbstractAction {

	private CanvasComponent comp;
	private RendererDispatcher dispatcher;
		
	ClearIndividualRendererAction(CanvasComponent cc, RendererDispatcher d)
	{
		super("Reset Renderer");
		comp=cc;
		dispatcher=d;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		dispatcher.clearIndividualRenderer(comp);
		comp.repaint();
	}

}
