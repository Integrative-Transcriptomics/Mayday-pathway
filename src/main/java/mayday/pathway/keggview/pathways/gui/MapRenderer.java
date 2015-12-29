package mayday.pathway.keggview.pathways.gui;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Node;
import mayday.vis3.graph.renderer.primary.DefaultComponentRenderer;


public class MapRenderer extends DefaultComponentRenderer 
{
	private static MapRenderer sharedInstance;

	private int arcDimension=15;



	public void draw(Graphics2D g, Node n, Rectangle bounds, Object value,String label, boolean selected) 
	{	
		g.setColor(backgroundColor);
		if(selected) g.setColor(selectedColor);
		g.fillRoundRect(0, 0, bounds.width-1, bounds.height-1, arcDimension, arcDimension);
		g.setColor(foregroundColor);
		g.drawRoundRect(0, 0, bounds.width-1, bounds.height-1, arcDimension, arcDimension);		
		g.setFont(font);
		int h=g.getFontMetrics().getHeight();
		int w=g.getFontMetrics().stringWidth(label);		
		g.drawString(label, (bounds.width - w)/2,bounds.height-((bounds.height-h/2)/2));		
	}

	public static DefaultComponentRenderer getDefaultRenderer()
	{
		if(sharedInstance==null) sharedInstance=new MapRenderer();
		return sharedInstance;
	}

	public Dimension suggestedSize() 
	{
		return new Dimension(100,50);
	}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Renderer.Map",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Render KEGG Maplinks",
				"Map Renderer"				
		);
		pli.addCategory(GROUP_PRIMARY);
		return pli;	


	}
}
