package mayday.graphviewer.crossViz3.corheatmap;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.text.NumberFormat;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.graph.renderer.RendererTools;

@SuppressWarnings("serial")
public class CorRenderer extends DefaultTableCellRenderer
{
	private ColorGradient grad;
	
	private double d;

	public CorRenderer(ColorGradient grad) 
	{
		this.grad = grad;
	}
	
	private CorRenderer(ColorGradient grad, double d) 
	{
		this.grad = grad;
		this.d = d;
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) 
	{
		if(value instanceof Double)
		{
			return new CorRenderer(grad, (Double)value);
		}else
		{
			return new CorRenderer(grad, 0);
		}
		
	}
	
	@Override
	public void paint(Graphics g) 
	{
		if(Double.isNaN(d))
		{
			g.setColor(Color.lightGray);
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(Color.black);
		}else
		{
			g.setColor(grad.mapValueToColor(d));
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(RendererTools.invertColor(g.getColor()));
		}
		
		g.drawString(NumberFormat.getNumberInstance().format(d), 5, 12);
	}
}
