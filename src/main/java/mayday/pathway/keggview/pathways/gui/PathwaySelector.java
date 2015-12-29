package mayday.pathway.keggview.pathways.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mayday.core.gui.MaydayDialog;
import mayday.pathway.keggview.ModelFactory;
import mayday.pathway.keggview.pathways.PathwayLink;
import mayday.pathway.keggview.pathways.PathwayManager;

@SuppressWarnings("serial")
public class PathwaySelector extends MaydayDialog implements ListSelectionListener
{
	private PathwayManager manager;
	private PathwayLink selectedItem;
	
	private JList pathwayList;
	
	private ModelFactory model;
	
	public PathwaySelector(ModelFactory model)
	{
		this.model=model;
		this.manager=model.getPathwayManager();
		init();
	}
	
	public PathwaySelector(PathwayManager manager)
	{
		this.manager=manager;
		init();		
	}
	
	private void init()
	{
		setTitle("Pathway Selector");
		
		setLayout(new BorderLayout());
		
		Vector<PathwayLink> vec=new Vector<PathwayLink>();
		vec.addAll(manager.getPathways());
		pathwayList=new JList(vec);
		pathwayList.setCellRenderer(new PathwayRenderer());
		pathwayList.addListSelectionListener(this);
		pathwayList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		pathwayList.setVisibleRowCount(25);
		JScrollPane scrollpane=new JScrollPane(pathwayList);
		add(scrollpane,BorderLayout.CENTER);
		Box southBox=Box.createHorizontalBox();
		southBox.add(Box.createHorizontalGlue());
		JButton okButton=new JButton(new OkAction());
		JButton cancelButton=new JButton(new CancelAction());
		southBox.add(cancelButton);
		southBox.add(okButton);
		add(southBox,BorderLayout.SOUTH);	
		pathwayList.setToolTipText("<html>List of KEGG Pathways. Select a pathway and click on the &quot;Ok&quot; button to display the pathway. <br>" +
				"Pathway marked in gray are not available locally and must be downloaded from KEGG.</html>");
		pathwayList.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e)
			{
				if(e.getClickCount() >=2)
				{
					selectedItem=(PathwayLink)pathwayList.getSelectedValue();
					if(model!=null)
					{
						try {
							model.getPathway(selectedItem);
							selectedItem=null;
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}else
					{
						dispose();
					}
				}
			}
		});

		pack();
	}
	
	private class OkAction extends AbstractAction
	{
		public OkAction()
		{
			super("Ok");
		}
		
		public void actionPerformed(ActionEvent arg0) 
		{
			selectedItem=(PathwayLink)pathwayList.getSelectedValue();
			if(model!=null)
			{
				try {
					model.getPathway(selectedItem);
					selectedItem=null;
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			dispose();			
		}		
	}
	
	private class CancelAction extends AbstractAction
	{
		public CancelAction()
		{
			super("Cancel");
		}
		
		public void actionPerformed(ActionEvent arg0) 
		{
			selectedItem=null;
			dispose();			
		}		
	}

	public void valueChanged(ListSelectionEvent e) 
	{
		selectedItem=(PathwayLink)pathwayList.getSelectedValue();
		
	}
	

	private class PathwayRenderer extends DefaultListCellRenderer
	{
		 public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) 
		 {
			 if (isSelected) 
			 {
				 setBackground(list.getSelectionBackground());
				 setForeground(list.getSelectionForeground());
			 }
			 else 
			 {
				 setBackground(list.getBackground());
				 setForeground(list.getForeground());
			 }
			 setText((value == null) ? "" : value.toString());
			 setFont(list.getFont());
			 if(! ((PathwayLink)value).isAvailable())
			 {
				 setBackground(isSelected?Color.orange:Color.lightGray);
			 }
			 return this;			 
		 }
	}

	/**
	 * @return the manager
	 */
	public PathwayManager getManager() {
		return manager;
	}

	/**
	 * @param manager the manager to set
	 */
	public void setManager(PathwayManager manager) {
		this.manager = manager;
	}

	/**
	 * @return the selectedItem
	 */
	public PathwayLink getSelectedItem() {
		return selectedItem;
	}	
}
