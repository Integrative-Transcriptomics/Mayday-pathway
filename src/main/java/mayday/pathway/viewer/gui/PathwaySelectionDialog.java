package mayday.pathway.viewer.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import mayday.core.gui.MaydayDialog;
import mayday.pathway.biopax.parser.BioPaxParser;
import mayday.pathway.biopax.parser.MasterObject;
import mayday.pathway.biopax.parser.MasterObjectNameComparator;

@SuppressWarnings("serial")
public class PathwaySelectionDialog extends MaydayDialog
{
	private Vector<MasterObject> pathways;
	private JList pathwayList;
	
	private JTextField query;
	
	private boolean cancelled=true;
	
	public PathwaySelectionDialog(Map<String, MasterObject> fileContents)
	{
		super();
		setTitle("Select Pathway");
		pathways=new Vector<MasterObject>();
		initPathways(fileContents);
		
		pathwayList=new JList(pathways);
		pathwayList.setCellRenderer(new PathwayListCellRenderer());
		pathwayList.setVisibleRowCount(5);
		
		setLayout(new BorderLayout());
		add(new JScrollPane(pathwayList),BorderLayout.CENTER);
		
        query=new JTextField(25);
        query.setText("");
        query.getDocument().addDocumentListener(new SearchListener());
		
        add(query,BorderLayout.NORTH);
        
        JButton okButton=new JButton(new OkAction());
        JButton cancelButton=new JButton(new CancelAction());
        
        Box buttonBox=Box.createHorizontalBox();
        buttonBox.add(Box.createHorizontalGlue());
        buttonBox.add(cancelButton);
        buttonBox.add(Box.createHorizontalStrut(10));
        buttonBox.add(okButton);
        
        add(buttonBox,BorderLayout.SOUTH);
        
		pack();
		
	}
	
	private void initPathways(Map<String, MasterObject> fileContents)
	{
		for(MasterObject o:fileContents.values())
		{
			if(o.getObjectType().equals("pathway"))
			{
				pathways.add(o);
			}
		}
		Collections.sort(pathways, new MasterObjectNameComparator());		
	}
	
	public static void main(String[] args) throws Exception
	{
		BioPaxParser parser=new BioPaxParser();
//		Map<String, MasterObject> res= parser.parse("/home/symons/Desktop/pae-glycolysis.owl");
		Map<String, MasterObject> res= parser.parse("/home/symons/Desktop/pae.owl");
		
		new PathwaySelectionDialog(res).setVisible(true);
	}
	
	private class PathwayListCellRenderer extends DefaultListCellRenderer
	{
		@Override
		public Component getListCellRendererComponent(JList list, Object value,	int index, boolean isSelected, boolean cellHasFocus) 
		{
			JLabel lab= (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);
			MasterObject o=(MasterObject)value;
			StringBuffer text=new StringBuffer("<html><body><h2>"+o.getFirstValue("NAME")+"</h2>");
			if(o.hasValue("SHORT-NAME"))
			{
				text.append("<b>"+o.getFirstValue("SHORT-NAME")+"</b>");
			}
			text.append(o.getMembers("PATHWAY-COMPONENTS").size()+" Reactions ");
					                
			if(o.hasValue("SYNONYMS"))
			{
				text.append(" (");
				int i=0;
				for(String s:o.getValue("SYNONYMS"))
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
			if(o.hasValue("COMMENT"))
			{
				String c=o.getFirstValue("COMMENT");
				text.append(c.length()> 150?c.substring(0, 150):c);
			}
			lab.setText(text.toString());
			
			return lab;
		}
	}
	
	private void search()
	{
        String s = query.getText();
        if (s.length() <= 0) {
            return;
        }
        
        for(int i=0; i!=pathways.size(); ++i)
        {
        	try 
        	{
	        	if( pathways.get(i).toString().toLowerCase().matches(".*"+s.toLowerCase()+".*") )
	        	{
	        		pathwayList.ensureIndexIsVisible(i);
	        		pathwayList.setSelectedIndex(i);
	        		break;        		
	        	}
        	} catch (Exception e) {System.out.println("FAIL");}
        } 
	}
	
	private class SearchListener implements DocumentListener
	{

		public void changedUpdate(DocumentEvent e) 
		{
		}

		public void insertUpdate(DocumentEvent e) 
		{
			search();		
		}

		public void removeUpdate(DocumentEvent e) 
		{
			search();				
		}		
	}

	/**
	 * @return the cancelled
	 */
	public boolean isCancelled() {
		return cancelled;
	}


	public MasterObject getSelectedPathway()
	{
		return pathways.get(pathwayList.getSelectedIndex());
	}
	
	private class CancelAction extends AbstractAction
	{
		public CancelAction() 
		{
			super("Cancel");
		}
		
		public void actionPerformed(ActionEvent e) 
		{
			cancelled=true;	
			dispose();
		}
	}
	
	private class OkAction extends AbstractAction
	{
		public OkAction() 
		{
			super("Ok");
		}
		
		public void actionPerformed(ActionEvent e) 
		{
			cancelled=false;	
			dispose();
		}
	}
	
}
