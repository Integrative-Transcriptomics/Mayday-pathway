package mayday.graphviewer.datasources.biopax2;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mayday.graphviewer.datasources.biopax2.PathwayListModel.PathwayItem;

@SuppressWarnings("serial")
public class PathwaySelectionPanel extends JPanel
{
	private JList pathwayList;
	private JList reactionList;
	private PathwayListModel model;	
	private JTextField query;

	public PathwaySelectionPanel(BioPaxSqueezer2 squeezer) 
	{
		List<String> ids=null;

		try{
			ids= squeezer.getObjectsOfType(BioPaxSqueezer2.PATHWAY);
			model=new PathwayListModel(ids, squeezer);		
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		pathwayList=new JList(model);
		pathwayList.setVisibleRowCount(10);
		pathwayList.setCellRenderer(new PathwayListCellRenderer());
		pathwayList.getSelectionModel().addListSelectionListener(new PathwayListListener());

		reactionList=new JList();
		reactionList.setVisibleRowCount(5);

		setLayout(new BorderLayout());

		add(new JScrollPane(pathwayList), BorderLayout.CENTER);		
		add(new JScrollPane(reactionList),BorderLayout.SOUTH);


		query=new JTextField(25);
		query.setText("");
		query.getDocument().addDocumentListener(new SearchListener());		
		add(query,BorderLayout.NORTH);
	}



	/**
	 * @param i use constants in ListSelectionModel
	 * @see ListSelectionModel
	 */
	public void setSelectionMode(int i)
	{
		pathwayList.setSelectionMode(i);
	}

	private void search()
	{
		String s = query.getText();
		if (s.length() <= 0) {
			return;
		}

		for(int i=0; i!=model.size(); ++i)
		{
			try 
			{
				if( model.get(i).toString().toLowerCase().matches(".*"+s.toLowerCase()+".*") )
				{
					pathwayList.ensureIndexIsVisible(i);
					pathwayList.setSelectedIndex(i);
					break;        		
				}
			} catch (Exception e) {} // do nothing. 
		} 
	}

	public List<String> getSelectedObjects()
	{
		List<String> l=new ArrayList<String>();
		for(int i: pathwayList.getSelectedIndices())
		{
			PathwayItem item=(PathwayItem)pathwayList.getModel().getElementAt(i);
			l.add(item.id);
		}
		return l;
	}

	public String getSelectedObject()
	{
		PathwayItem item=(PathwayItem)pathwayList.getSelectedValue();
		return item.id;
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

	private class PathwayListListener implements ListSelectionListener
	{
		@Override
		public void valueChanged(ListSelectionEvent e) 
		{
			PathwayItem item=(PathwayItem)pathwayList.getSelectedValue();
			DefaultListModel m=new DefaultListModel();
			for(String rea: item.reactions)
				m.addElement(rea);
			reactionList.setModel(m);			
		}
	}

}
