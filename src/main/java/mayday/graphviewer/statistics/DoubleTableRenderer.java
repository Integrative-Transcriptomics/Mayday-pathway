package mayday.graphviewer.statistics;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.text.NumberFormat;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.gradient.ColorGradientSetting;
import mayday.vis3.graph.renderer.RendererTools;

@SuppressWarnings("serial")
public class DoubleTableRenderer extends DefaultTableCellRenderer implements SettingChangeListener
{
	private ColorGradient gradient;
	private boolean renderText=false;
	
	protected HierarchicalSetting setting;
	protected ColorGradientSetting gradientSetting;
	protected BooleanSetting renderTextSetting;
	
		
	public DoubleTableRenderer(ColorGradient gradient) 
	{
		this.gradient = gradient;
		setting=new HierarchicalSetting("Rendering Options");
		gradientSetting=new ColorGradientSetting("Color Gradient", null, this.gradient);
		renderTextSetting=new BooleanSetting("Show Values", null, renderText);
		setting.addSetting(gradientSetting).addSetting(renderTextSetting);
		setting.addChangeListener(this);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) 
	{
		
		if(! (value instanceof Double) )
		{
			setBackground(Color.WHITE);
			setForeground(Color.BLACK);
			if(table.getRowHeight() > 8 )
				return super.getTableCellRendererComponent(table, value, isSelected, hasFocus,row, column);
			else
				return super.getTableCellRendererComponent(table, null, isSelected, hasFocus,row, column);
		}
		if(renderText)
		{
			setText(NumberFormat.getNumberInstance().format(((Double)value)));
		}else
		{
			setText(null);
		}
		setToolTipText(NumberFormat.getNumberInstance().format((Double)value));
		setBackground(gradient.mapValueToColor((Double)value));
		setForeground(RendererTools.getInverseBlackOrWhite(getBackground()));
		setOpaque(true);
		
		return this;
		
	}
	
	@Override
	public void paint(Graphics g) 
	{
		super.paint(g);
//		if(selected){
//			g.setColor(RendererTools.getInverseBlackOrWhite(getBackground()));
//			g.drawLine(0, 0, getWidth(), getHeight());
//		}
	}
	
	public ColorGradient getGradient() {
		return gradient;
	}
	
	public void setGradient(ColorGradient gradient) {
		this.gradient = gradient;
	}
	
	public void setRenderText(boolean renderText) {
		this.renderText = renderText;
	}
	
	public boolean isRenderText() {
		return renderText;
	}
	
	@Override
	public void stateChanged(SettingChangeEvent e) 
	{
		renderText=renderTextSetting.getBooleanValue();		
		gradient=gradientSetting.getColorGradient();
	}
	
	public HierarchicalSetting getSetting() {
		return setting;
	}
}
