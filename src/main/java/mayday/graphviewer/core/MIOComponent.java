package mayday.graphviewer.core;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import mayday.core.structures.graph.nodes.MIONode;
import mayday.vis3.graph.renderer.dispatcher.RendererDispatcher;

@SuppressWarnings("serial")
public class MIOComponent extends DefaultNodeComponent 
{
	private RendererDispatcher rendererDispatcher;
	
	public MIOComponent(MIONode n) 
	{
		super(n);			
		setSize(100, 50);
	}
	
	@Override
	public void paint(Graphics g1) 
	{
		Graphics2D g=(Graphics2D)g1;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		rendererDispatcher.render(g, getNode(), getProbes(), false, this);
	}
	
	

}
