package mayday.graphviewer.core.bag.tools;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.plots.tagcloud.Tag;

@SuppressWarnings("serial")
public class TagListCellRenderer extends DefaultListCellRenderer
{
	private static final ColorGradient grad=ColorGradient.createDefaultGradient(0, 1);
	
	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) 
	{
		JLabel l=(JLabel)super.getListCellRendererComponent(list, value, index, isSelected,
				cellHasFocus);
		if(value instanceof Tag)
		{
			Tag t=(Tag)value;
			StringBuffer sb=new StringBuffer("<html><body><p>");
			double f=t.getFrequency();
			
			Color c=grad.mapValueToColor(f);

			int s=(int)(f*(7.0-1.0)+1.0);

			sb.append("<font color=\"#");
			if(c.getRed() < 15)
				sb.append("0");
			sb.append(Integer.toHexString(c.getRed()));
			if(c.getGreen() < 15)
				sb.append("0");
			sb.append(Integer.toHexString(c.getGreen()));
			if(c.getBlue() < 15)
				sb.append("0");
			sb.append(Integer.toHexString(c.getBlue()));
			sb.append("\"").append(" size=\"").append(s).append("\">");
			sb.append(t.getTag().toString()).append("</font> &nbsp; ");
			sb.append("</p></body></html>");
			l.setText(sb.toString());
		}
		return l;
	}
}
