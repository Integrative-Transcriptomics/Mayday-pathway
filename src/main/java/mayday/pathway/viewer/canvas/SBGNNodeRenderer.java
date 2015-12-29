package mayday.pathway.viewer.canvas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

import mayday.core.structures.graph.Node;
import mayday.pathway.sbgn.graph.SBGNNode;
import mayday.pathway.sbgn.processdiagram.container.Complex;
import mayday.pathway.sbgn.processdiagram.container.ComplexRenderer;
import mayday.pathway.sbgn.processdiagram.entitypool.EntityPoolNode;
import mayday.pathway.sbgn.processdiagram.entitypool.MultimerMacromolecule;
import mayday.pathway.sbgn.processdiagram.entitypool.MultimerNucleicAcidFeature;
import mayday.pathway.sbgn.processdiagram.entitypool.MultimerSimpleChemical;
import mayday.vis3.graph.components.LabelRenderer.Orientation;
import mayday.vis3.graph.renderer.ComponentRenderer;

public class SBGNNodeRenderer implements ComponentRenderer
{
	private static SBGNNodeRenderer defaultInstance=new SBGNNodeRenderer();

	public void draw(Graphics2D g, Node node, Rectangle bounds, Object value,
			String label, boolean selected) 
	{
		if(value instanceof SBGNNode)
		{
			SBGNNode n=(SBGNNode)value;
			boolean isMultimer=(n instanceof MultimerSimpleChemical || n instanceof MultimerMacromolecule || n instanceof MultimerNucleicAcidFeature); 
			int paddingTop=0;
			if(n instanceof EntityPoolNode)
			{
				if(!((EntityPoolNode) n).getUnitsOfInformation().isEmpty())
				{
					paddingTop=8;
				}
			}
			Path2D s=new Path2D.Double(n.getGlyph());
			s.transform(AffineTransform.getTranslateInstance(bounds.x, bounds.y));
			g.setStroke(new BasicStroke(1.5f));
			int offset=1+ (isMultimer?5:0);
			float sx= 1.0f*bounds.width/(s.getBounds().width+offset);
			float sy= (1.0f*bounds.height-paddingTop)/(s.getBounds().height+offset);
			
			g.scale(sx, sy);		

			if(value instanceof Complex)
			{
				ComplexRenderer.sharedInstance().draw(g, node, bounds, value, label, selected);
				return;
			}
			
			g.setColor(Color.white);
			
			if(isMultimer)
			{			
				Path2D s2=new Path2D.Double(n.getGlyph());
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
			
			if(n instanceof EntityPoolNode)
			{				
				if(((EntityPoolNode) n).isCloneMarker())
				{
					g.fill(((EntityPoolNode) n).getCloneMarker());
					if(isMultimer)
					{
						Path2D cm2=new Path2D.Double( ((EntityPoolNode) n).getCloneMarker());
						cm2.transform(AffineTransform.getTranslateInstance(5, 5));
						g.fill(cm2);
					}
				}
			}
		}
	}
		
	public Dimension getSuggestedSize(Node node, Object value) 
	{
		return null;
	}

	public static ComponentRenderer instance() 
	{
		return defaultInstance;
	}
	
	@Override
	public Orientation getLabelOrientation(Node node, Object value) 
	{
		return null;
	}

	@Override
	public boolean hasLabel(Node node, Object value) 
	{
		return true;
	}

}
