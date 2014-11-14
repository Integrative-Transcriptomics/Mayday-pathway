package mayday.pathway.viewer.canvas;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import mayday.pathway.sbgn.processdiagram.entitypool.UnitOfInformation;
import mayday.vis3.graph.components.CanvasComponent;

@SuppressWarnings("serial")
public class UnitOfInformationComponent extends SatelliteComponent
{
	private UnitOfInformation unit;

	public UnitOfInformationComponent(UnitOfInformation unit, CanvasComponent parent, int index, int numUnits) 
	{
		super(parent,index,numUnits);
		this.unit=unit;

		
		FontMetrics m=getFontMetrics(getFont());
		Rectangle2D glyph= m.getStringBounds(unit.toString(),parent.getGraphics());
				
		setSize((int)(glyph.getWidth()+2), (int)(glyph.getHeight()+2) );
			
	}
	
	@Override
	public void paint(Graphics g) 
	{
		g.clearRect(0, 0, getWidth(), getHeight());
		g.drawRect(0, 0, getWidth()-1, getHeight()-1);
		g.drawString(unit.toString(), 1, getHeight()-4);
	}
}
