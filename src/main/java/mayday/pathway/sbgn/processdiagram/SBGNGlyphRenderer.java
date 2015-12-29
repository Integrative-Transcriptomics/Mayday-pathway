package mayday.pathway.sbgn.processdiagram;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.vis3.graph.components.LabelRenderer.Orientation;
import mayday.vis3.graph.renderer.primary.DefaultComponentRenderer;

public class SBGNGlyphRenderer extends DefaultComponentRenderer
{

	@Override
	public void draw(Graphics2D g, Node n, Rectangle bounds, Object value,	String label, boolean selected) 
	{
		DefaultNode node=(DefaultNode)n;
		boolean isMultimer=(node.getRole().equals(ProcessDiagram.MULTIMER_MACROMOLECULE_ROLE) ||
				node.getRole().equals(ProcessDiagram.MULTIMER_NUCLEIC_ACID_FEATURE_ROLE) ||
				node.getRole().equals(ProcessDiagram.MULTIMER_SIMPLE_CHEMICAL_ROLE) ); 
		int paddingTop=0;
		
		Path2D s=new Path2D.Double(ProcessDiagram.getGlyph(node.getRole()));
		s.transform(AffineTransform.getTranslateInstance(bounds.x, bounds.y));

		g.setStroke(new BasicStroke(1.5f));
		
		int offset=2+ (isMultimer?5:0);
		float sx= 1.0f*bounds.width/(s.getBounds().width+offset);
		float sy= (1.0f*bounds.height-paddingTop)/(s.getBounds().height+offset);
		
		g.scale(sx, sy);
		g.setColor(Color.white);
		
		if(isMultimer)
		{			
			Path2D s2=new Path2D.Double(ProcessDiagram.getGlyph(node.getRole()));
			s2.transform(AffineTransform.getTranslateInstance(bounds.x+5, bounds.y+5));
			g.setColor(Color.white);	
			g.fill(s2);
			g.setColor(selected?Color.red:Color.black);	
			g.draw(s2);
			g.setColor(Color.white);
			g.fill(s);	
			g.setColor(selected?Color.red:Color.black);	
			g.draw(s);				
		}else
		{						
			g.fill(s);
			g.setColor(Color.black);
			g.draw(s);
			if(selected)
			{
				g.setColor(Color.red);					
				g.draw(s);
			}
		}
		
		if(node.hasProperty(ProcessDiagram.CLONE_MARKER_KEY))
		{				
			if(node.getPropertyValue(ProcessDiagram.CLONE_MARKER_KEY).equals(ProcessDiagram.CLONE_MARKER_PRESENT_VALUE))
			{
				g.fill(ProcessDiagram.defaultCloneMarker(s));
				if(isMultimer)
				{
					Path2D cm2=new Path2D.Double( ProcessDiagram.defaultCloneMarker(s));
					cm2.transform(AffineTransform.getTranslateInstance(5, 5));
					g.fill(cm2);
				}
			}
		}		
	}

	@Override
	public Dimension getSuggestedSize(Node node, Object value)
	{
		
		String role=node.getRole();
		if(node==null || role==null)
		{
			return getSuggestedSize();
		}
		if(role.equals(ProcessDiagram.SIMPLE_CHEMICAL_ROLE))
		{
			return new Dimension(60,60);
		}
		if(role.equals(ProcessDiagram.SINK_ROLE) || role.equals(ProcessDiagram.SOURCE_ROLE))
		{
			return new Dimension(30,30);
		}
		if(ProcessDiagram.isEntityPoolNode(role))
		{
			return getSuggestedSize();
		}
		
		if(ProcessDiagram.isLogic(role))
		{
			return new Dimension(40,40);
		}		
		
		if(ProcessDiagram.isTransition(role))
		{
			return new Dimension(20, 20);
		}		
		return getSuggestedSize();
	}
	
	
	@Override
	public Dimension getSuggestedSize() 
	{
		return new Dimension(80,60);
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Renderer.SBGN",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Renders components as SBGN Process Diagram Glyphs",
				"SBGN Renderer"				
		);
		pli.addCategory(GROUP_PRIMARY);
		return pli;	
	}
	
	@Override
	public boolean hasLabel(Node node, Object value) 
	{
		if(ProcessDiagram.isTransition(node.getRole()))
		{
			return false;
		}	
		return true;
	}
	
	@Override
	public Orientation getLabelOrientation(Node node, Object value)
	{
		return Orientation.CENTER;
	}

}
