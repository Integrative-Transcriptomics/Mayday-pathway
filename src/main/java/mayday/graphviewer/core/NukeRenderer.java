package mayday.graphviewer.core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Random;

import mayday.core.Probe;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Node;
import mayday.vis3.graph.renderer.primary.DefaultComponentRenderer;

public class NukeRenderer  extends DefaultComponentRenderer
{
	private Font font=new Font(Font.SANS_SERIF,Font.PLAIN,60);
	private Random r=new Random();
	private String[] signs={"☠","☣","☢","☭","☕","☃","⚛","⚔"};
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Renderer.Nuke",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Nuke all your nodes",
				"zzzzz(Nuke Renderer)"				
		);
		pli.addCategory(GROUP_PRIMARY);
		return pli;		
	}
	
	public Dimension getSuggestedSize() 
	{
		return new Dimension(100,60);
	}
	
	private void itsTheEnd(Graphics2D g, Rectangle bounds)
	{
		g.setColor(Color.yellow);
		g.fillRect(0, 0, bounds.width, bounds.height);
		g.setColor(Color.black);
		g.setFont(font);
		String s="";
		int i=r.nextInt(signs.length);
		s=signs[i];
		g.drawString(s, 20, bounds.height);		
	}

	@Override
	public void drawDouble(Graphics2D g, Rectangle bounds, String label,boolean selected, double... value) {
		itsTheEnd(g, bounds);
	}

	@Override
	public void drawNode(Graphics2D g, Rectangle bounds, String label,boolean selected, Node node) {
		itsTheEnd(g, bounds);
	}

	@Override
	public void drawObject(Graphics2D g, Rectangle bounds, String label,boolean selected, Object value) {
		itsTheEnd(g, bounds);		
	}

	@Override
	public void drawProbe(Graphics2D g, Rectangle bounds, String label,	boolean selected, Probe value) {
		itsTheEnd(g, bounds);		
	}

	@Override
	public void drawProbes(Graphics2D g, Rectangle bounds, String label,boolean selected, Iterable<Probe> value){
		itsTheEnd(g, bounds);
	}

	@Override
	public void drawString(Graphics2D g, Rectangle bounds, boolean selected,String value) {
		itsTheEnd(g, bounds);
	}
	
	
}
