package mayday.pathway.viewer.canvas;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import mayday.pathway.sbgn.processdiagram.entitypool.StateDescription;
import mayday.vis3.graph.components.CanvasComponent;

@SuppressWarnings("serial")
public class StateDescriptionComponent  extends SatelliteComponent
{
	private StateDescription unit;
	
	public StateDescriptionComponent(StateDescription unit, CanvasComponent parent, int index, int numUnits) 
	{
		super(parent,index,numUnits);
		this.unit=unit;
		
		FontMetrics m=getFontMetrics(getFont());
		Rectangle2D glyph= m.getStringBounds(unit.toString(),parent.getGraphics());
				
		setSize((int)(glyph.getWidth()*3/2), (int)(glyph.getHeight()*3/2) );
	}

	@Override
	public void paint(Graphics g) 
	{
		g.setColor(getBackground());
		g.fillOval(0, 0, getWidth()-1, getHeight()-1);
		g.setColor(getForeground());
		g.drawOval(0, 0, getWidth()-1, getHeight()-1);
		g.drawString(unit.toString(), getWidth()/6, getHeight()/6*5);
	}
}

