package mayday.graphviewer.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.core.probelistmanager.ProbeListManager;
import mayday.core.probelistmanager.ProbeListManagerTree;
import mayday.core.probelistmanager.UnionProbeList;
import mayday.core.probelistmanager.gui.ProbeListNode;
import mayday.tiala.pairwise.data.viewmodel.EnrichedViewModel;
import mayday.tiala.pairwise.data.viewmodel.NonClosingVisualizer;
import mayday.tiala.pairwise.gui.views.ProfileView;

@SuppressWarnings("serial")
public class ProbeListSelector extends JSplitPane
{
	private JTree dataSetsTree;
	private JList selectedItems;
	private DefaultListModel listModel;
	private ProfileView view;
	private NonClosingVisualizer visualizer;
	
	public ProbeListSelector(List<DataSet> dataSets, Collection<ProbeList> preselected)
	{
		super(HORIZONTAL_SPLIT);
		init(dataSets);
		
		listModel=new DefaultListModel();
		for(ProbeList pl:preselected)
		{
			listModel.addElement(pl);
		}
		selectedItems.setModel(listModel);
	}	
	
	public ProbeListSelector(List<DataSet> dataSets) 
	{
		super(HORIZONTAL_SPLIT);
		init(dataSets);
		
		listModel=new DefaultListModel();
		for(DataSet ds: dataSets)
		{
			Object[] selected= ds.getProbeListManager().getProbeListManagerView().getSelectedValues();			
			// it seems that we get a bunch of probelists, for great justice. 
			for(Object o: selected)
			{
				listModel.addElement(o);
			}
		}
		selectedItems.setModel(listModel);
	}
	
	private void init(List<DataSet> dataSets)
	{
		dataSetsTree=new JTree(scan(dataSets));
	
		selectedItems=new JList();
		
		selectedItems.setCellRenderer(new ProbeListDataSetCellRenderer());
		dataSetsTree.setCellRenderer(new ProbeListDataSetTreeCellRenderer());
		
		dataSetsTree.addMouseListener(new TreeMouseListener());
		dataSetsTree.addTreeSelectionListener(new DataSetTreeSelectionListener());
		
		JPanel treePanel=new JPanel();
		treePanel.setLayout(new BorderLayout());
		treePanel.add(new JScrollPane(dataSetsTree), BorderLayout.CENTER);
		treePanel.setMinimumSize(new Dimension(300, 400));
		JPanel treeButtonPanel=new JPanel();
		treeButtonPanel.setLayout(new FlowLayout());
		treeButtonPanel.add(new JButton(new AddProbeListsAction()));

		treePanel.add(treeButtonPanel, BorderLayout.SOUTH);
		
		visualizer=new NonClosingVisualizer();
		visualizer.setViewModel(new EnrichedViewModel(visualizer, dataSets.get(0)));
		view=new ProfileView(Color.red, visualizer);
		view.setMinimumSize(new Dimension(300,200));
		
		JSplitPane leftSplit=new JSplitPane(JSplitPane.VERTICAL_SPLIT,treePanel,view);
		leftSplit.setResizeWeight(0.75);
		
		JPanel selectionPanel=new JPanel();
		selectionPanel.setLayout(new BorderLayout());
		selectionPanel.add(new JScrollPane(selectedItems), BorderLayout.CENTER);
		
		JPanel selectionButtonBox=new JPanel(new FlowLayout());
		selectionButtonBox.add(new JButton(new RemoveSelectedAction()));
		selectionButtonBox.add(new JButton(new RemoveAllAction()));
		selectionPanel.add(selectionButtonBox, BorderLayout.SOUTH);
		
//		JSplitPane pane=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,leftSplit,selectionPanel);
		setLeftComponent(leftSplit);
		setRightComponent(selectionPanel);
		
		setResizeWeight(0.5);

	}
	
	
	public DefaultMutableTreeNode scan(List<DataSet> dataSets) 
	{
		DefaultMutableTreeNode root=new DefaultMutableTreeNode("Mayday");
		for(DataSet ds: dataSets)
		{
			DefaultMutableTreeNode dsRoot=new DefaultMutableTreeNode(ds);
			ProbeListManager plm=ds.getProbeListManager();
			
			if(plm instanceof ProbeListManagerTree)
			{
//				plm.getProbeListsBelow((((ProbeListManagerTree)plm).getSharedAncestor(probeLists));
				ProbeList topPL=((ProbeListNode) (((ProbeListManagerTree)plm).getTreeModel().getRoot())).getProbeList();
				ProbeListNode topPLNode=new ProbeListNode(topPL);
				buildTree(plm, topPL, topPLNode);
				dsRoot.add(topPLNode);
			}
			root.add(dsRoot);
		}
		return root;
		
	}
	
	private class AddProbeListsAction extends AbstractAction
	{
		
		public AddProbeListsAction() 
		{
			super("Add Selected");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			if(dataSetsTree.getSelectionCount() >0)
			{
				for(TreePath p:dataSetsTree.getSelectionPaths())
				{
					DefaultMutableTreeNode n=(DefaultMutableTreeNode)p.getLastPathComponent();
					Object o=n.getUserObject();
					if(o instanceof ProbeList && !listModel.contains(o))
					{
						listModel.addElement(o);
					}
				}
			}
			
		}
	}
	
	private class RemoveSelectedAction extends AbstractAction
	{
		public RemoveSelectedAction() 
		{
			super("Remove Selected");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			for(Object o:selectedItems.getSelectedValues())
				listModel.removeElement(o);
			
		}
	}
	
	private class RemoveAllAction extends AbstractAction
	{
		public RemoveAllAction() 
		{
			super("Remove all");			
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			listModel.removeAllElements();			
		}
	}
	
	private void buildTree(ProbeListManager plm, ProbeList plist, DefaultMutableTreeNode parent)
	{
		for(ProbeList pl: plm.getProbeListsBelow(plist))
		{
			ProbeListNode plNode=new ProbeListNode(pl);
			parent.add(plNode);
			buildTree(plm, pl, plNode);
		}
	}
	
	public List<ProbeList> getSelectedProbeLists()
	{
		List<ProbeList> res=new LinkedList<ProbeList>();
		for(int i=0; i!= selectedItems.getModel().getSize(); ++i)
		{
			res.add((ProbeList)selectedItems.getModel().getElementAt(i));
		}
		return res;
	}
	
	private class ProbeListDataSetTreeCellRenderer
	extends JLabel
	implements TreeCellRenderer
	{
		private Color color;
		
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) 
		{
		
			if (!(value instanceof DefaultMutableTreeNode))
				return this;
			
			
			if(! (((DefaultMutableTreeNode)value).getUserObject() instanceof ProbeList) )
			{
				return new DefaultTreeCellRenderer().getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
			}
			
			ProbeList pl = (ProbeList) ((DefaultMutableTreeNode)value).getUserObject();
			
			if (pl==null)
				return this;
			
			String  s = "<html><nobr>"; 
			s +=/*pl.getDataSet().getName()+"&nbsp;"+*/ 
			pl.getName();

			s += "<small><font color=#888888>";
		    if (pl instanceof UnionProbeList && ((UnionProbeList)pl).getNode()!=null) 
		    	s+="&nbsp;&nbsp;L="+((UnionProbeList)pl).getNode().getChildCount();	    	
			s += "&nbsp;&nbsp;S=" + pl.getNumberOfProbes();
			s += "&nbsp;&nbsp;M=" + pl.getDataSet().getMIManager().getGroupsForObject(pl).size();
			s += "</nobr></small>";
			s += "</html>"; 
			setText( s );

			color = pl.getColor();

			if ( selected )
			{
				setForeground( color );
				setBackground( UIManager.getColor("List.selectionBackground") ); 
			}
			else
			{
				setForeground( color );
				setBackground( Color.WHITE);
			}	

			setEnabled( tree.isEnabled() );
			setFont( tree.getFont() );
			setOpaque( true );

			setToolTipText( pl.getAnnotation().getQuickInfo() );

			return ( this );
		}		
	}

	private class ProbeListDataSetCellRenderer
	extends JLabel
	implements ListCellRenderer
	{
		// This is the only method defined by ListCellRenderer.
		// We just reconfigure the JLabel each time we're called.

		private Color color;

		public Component getListCellRendererComponent (
				JList list,
				Object value,            // value to display
				int index,               // cell index
				boolean isSelected,      // is the cell selected
				boolean cellHasFocus )    // the list and the cell have the focus
		{

			//if ( !value.getClass().equals( ProbeList.class ) )  //W-T-F ??
			if (!(value instanceof ProbeList)) {
				setText( value.toString() );

				return ( this ); 
			}

			String  s = "<html><nobr>"; 
			s +=((ProbeList)value).getDataSet().getName()+"&nbsp;"+((ProbeList)value).getName();

			s += "<small><font color=#888888>";
			s += "&nbsp;&nbsp;S=" + ((ProbeList)value).getNumberOfProbes();
			s += "&nbsp;&nbsp;M=" + ((ProbeList)value).getDataSet().getMIManager().getGroupsForObject(value).size();
			s += "</nobr></small>";
			s += "</html>"; 
			setText( s );

			color = ((ProbeList)value).getColor();

			if ( isSelected )
			{
				setForeground( color );
				setBackground( list.getSelectionBackground() );
			}
			else
			{
				setForeground( color );
				setBackground( list.getBackground() );
			}	

			setEnabled( list.isEnabled() );
			setFont( list.getFont() );
			setOpaque( true );

			setToolTipText( ((ProbeList)value).getAnnotation().getQuickInfo() );

			return ( this );
		}	
	}
	
	protected class TreeMouseListener extends MouseAdapter
	{
		public void mouseClicked(MouseEvent arg0) 
		{
			if(arg0.getButton()==MouseEvent.BUTTON1 && arg0.getClickCount() >1 )
			{
				try{
				TreePath path = dataSetsTree.getPathForLocation(arg0.getX(), arg0.getY());
				DefaultMutableTreeNode node=(DefaultMutableTreeNode) path.getLastPathComponent();
				Object o=node.getUserObject();
				if(o instanceof ProbeList && !listModel.contains(o))
				{
					listModel.addElement(o);
				}
				}catch(Exception e)
				{
					//well, no such luck.
				}
			}
		}
	}
    
    protected class DataSetTreeSelectionListener implements TreeSelectionListener	
    {
    	public void valueChanged(TreeSelectionEvent e)
    	{
    		DefaultMutableTreeNode node=(DefaultMutableTreeNode)e.getPath().getLastPathComponent();
    		Object o=node.getUserObject();
    		if(o instanceof ProbeList)
    		{
    			ProbeList pl=(ProbeList)o;
    			List<ProbeList> pls=new ArrayList<ProbeList>();
    			pls.add(pl);
    			view.setName(pl.getName());
//    			visualizer.setViewModel(new EnrichedViewModel(visualizer, pl.getDataSet()));
//    			visualizer.getViewModel().addProbeListToSelection(pl);  
    			if(!visualizer.getViewModel().getProbeLists(false).isEmpty())
    				visualizer.getViewModel().removeProbeListFromSelection(visualizer.getViewModel().getProbeLists(false).get(0));
    			visualizer.getViewModel().addProbeListToSelection(pl);   			
    		}

    	}
    }  

}
