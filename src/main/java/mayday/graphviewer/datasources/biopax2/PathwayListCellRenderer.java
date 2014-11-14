package mayday.graphviewer.datasources.biopax2;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import mayday.graphviewer.datasources.biopax2.PathwayListModel.PathwayItem;

@SuppressWarnings("serial")
public class PathwayListCellRenderer extends DefaultListCellRenderer
{
	@Override
	public Component getListCellRendererComponent(JList list, Object value,	int index, boolean isSelected, boolean cellHasFocus) 
	{
		JLabel lab= (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,	cellHasFocus);
		PathwayItem o=(PathwayItem)value;
		StringBuffer text=new StringBuffer("<html><body><h2>"+o.name+"</h2>");
		if(o.shortName!=null)
		{
			text.append("<b>"+o.shortName+"</b>");
		}
		text.append(o.reactions.size()+" Reactions ");
				                
		if(!o.synonyms.isEmpty())
		{
			text.append(" (");
			int i=0;
			for(String s:o.synonyms)
			{
				if(i==3)
				{
					text.append("...");
					break;
				}
				if(i==0)
					text.append(s);						
				else
					text.append(", "+s);
				
				++i;
			}
			text.append(" )");			
		}
		
		if(o.comment!=null)
		{
			String c=o.comment;			
			text.append(c.length()> 150?c.substring(0, 150):c);
		}
		lab.setText(text.toString());
		
		return lab;
	}
	
}
