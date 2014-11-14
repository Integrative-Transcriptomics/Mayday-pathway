package mayday.pathway.sbgn.processdiagram.container;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

import mayday.core.structures.graph.Node;
import mayday.pathway.sbgn.graph.SBGNNode;
import mayday.pathway.viewer.canvas.SBGNNodeRenderer;
import mayday.vis3.graph.components.LabelRenderer.Orientation;
import mayday.vis3.graph.renderer.ComponentRenderer;
import mayday.vis3.graph.renderer.RendererTools;

public class ComplexRenderer implements ComponentRenderer
{
	private static ComplexRenderer sharedInstance=new ComplexRenderer();

	public void draw(Graphics2D g, Node node, Rectangle bounds, Object value,String label, boolean selected) 
	{
		AffineTransform t=g.getTransform();
		
		double sx=t.getScaleX();
		double sy=t.getScaleY();
		sx=1/sx;
		sy=1/sy;
		if(!(value instanceof Complex))
			return;
		Path2D outline=new Path2D.Float();
		int cw=10;


		outline.moveTo(sx*(bounds.x+cw), sy*(bounds.y+0));
		outline.lineTo(sx*(bounds.x+bounds.width-cw-1),  sy*(bounds.y+0));
		outline.lineTo(sx*(bounds.x+bounds.width-1),  sy*(bounds.y+cw));
		outline.lineTo(sx*(bounds.x+bounds.width-1),  sy*(bounds.y+bounds.height-cw-1));
		outline.lineTo(sx*(bounds.x+bounds.width-cw-1), sy*( bounds.y+bounds.height-1));
		outline.lineTo(sx*(bounds.x+cw),  sy*(bounds.y+bounds.height-1));
		outline.lineTo(sx*(bounds.x),  sy*(bounds.y+bounds.height-cw-1));
		outline.lineTo(sx*(bounds.x),  sy*(bounds.y+cw));
		
//		outline.moveTo((bounds.x+cw), (bounds.y+0));
//		outline.lineTo((bounds.x+bounds.width-cw-1),  (bounds.y+0));
//		outline.lineTo((bounds.x+bounds.width-1),  (bounds.y+cw));
//		outline.lineTo((bounds.x+bounds.width-1),  (bounds.y+bounds.height-cw-1));
//		outline.lineTo((bounds.x+bounds.width-cw-1), ( bounds.y+bounds.height-1));
//		outline.lineTo((bounds.x+cw),  (bounds.y+bounds.height-1));
//		outline.lineTo((bounds.x),  (bounds.y+bounds.height-cw-1));
//		outline.lineTo((bounds.x),  (bounds.y+cw));
		outline.closePath();
		
		Complex c=(Complex)value;
		
		
		
		g.setColor(Color.white);
		g.fill(outline);
		g.setColor(selected?Color.red:Color.black);
		RendererTools.drawBreakingString(g, c.getName(), bounds.width-2*cw, bounds.x+cw, bounds.y+cw/2);

		int d = (int) Math.ceil(Math.sqrt(c.componentCount()));
		int n=0;
		int h=bounds.y+20;
		for(int i=0; i!= d; ++i)
		{
			if(n >= c.componentCount()) break;
			int w=bounds.x+5; 
			int hMax=0;
			for(int j=0; j!=d; ++j)
			{
				if(n >= c.componentCount()) break;
				SBGNNode su=c.getSubunits().get(n);
				Path2D p=new Path2D.Double(su.getGlyph());
				p.transform(AffineTransform.getTranslateInstance(w, h));
				AffineTransform t2=g.getTransform();
				if(su instanceof Complex)
				{
					p=new Path2D.Double(su.getGlyph());
					p.transform(new AffineTransform());
					AffineTransform trans=new AffineTransform();
					trans.concatenate(AffineTransform.getScaleInstance(1/sx, 1/sy));
					trans.concatenate(AffineTransform.getTranslateInstance(w, h));
					p.transform(trans);
					
					ComplexRenderer.sharedInstance().draw(g, node,  p.getBounds(), su, su.getName(), selected);
				}
				else
				{
					SBGNNodeRenderer.instance().draw(g, node, p.getBounds(), su, su.getName(), selected);
					RendererTools.drawBreakingString(g, su.getName(), p.getBounds().width, p.getBounds().x+2, p.getBounds().y+20);
				}
				g.setTransform(t2);
			
				if(p.getBounds().height>hMax) hMax=p.getBounds().height;
				w+=p.getBounds().getWidth()+5;
				
				n++;
			}
			h+=hMax+5;
		}
		g.setColor(selected?Color.red:Color.black);
		g.draw(outline);
		
	}

	public Dimension getSuggestedSize(Node node, Object value) 
	{
		return null;
	}
	
	public static ComplexRenderer sharedInstance()
	{
		return sharedInstance;
	}
	
	@Override
	public Orientation getLabelOrientation(Node node, Object value) 
	{
		return Orientation.CENTER;
	}
	
	@Override
	public boolean hasLabel(Node node, Object value) 
	{
		return true;
	}

}
