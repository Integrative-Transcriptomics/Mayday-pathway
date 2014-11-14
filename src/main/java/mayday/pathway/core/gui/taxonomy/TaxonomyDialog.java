package mayday.pathway.core.gui.taxonomy;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import mayday.core.gui.MaydayDialog;
import mayday.pathway.keggview.kegg.taxonomy.TaxonomyComponent;
import mayday.pathway.keggview.kegg.taxonomy.TaxonomyItem;

@SuppressWarnings("serial")
public class TaxonomyDialog extends MaydayDialog implements TreeSelectionListener
{
	private JTree tree;
	private TaxonomyComponent taxon;
	private TaxonomyItem selectedItem;
	
	public TaxonomyDialog(DefaultMutableTreeNode root)
	{	
		setLayout(new BorderLayout());
		tree=new JTree(root);
	    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	    tree.addTreeSelectionListener(this);
	    
		JScrollPane scrollpane=new JScrollPane(tree);
		add(scrollpane,BorderLayout.CENTER);
				
		taxon=new TaxonomyComponent();
		//add(taxon,BorderLayout.SOUTH);
		
		Box southBox=Box.createVerticalBox();
		southBox.add(taxon);
		Box buttonBox=Box.createHorizontalBox();
		buttonBox.add(Box.createHorizontalGlue());
		JButton okButton=new JButton(new OkAction());
		JButton cancelButton=new JButton(new CancelAction());

		buttonBox.add(okButton);
		buttonBox.add(cancelButton);
		southBox.add(buttonBox);
		add(southBox,BorderLayout.SOUTH);
		setPreferredSize(new Dimension(300,400));
		pack();
	}
	
	public TaxonomyItem getSelectedItem()
	{
		return selectedItem;
	}

	public void valueChanged(TreeSelectionEvent e) 
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		if (node == null) return;

		Object nodeInfo = node.getUserObject();
		if (node.isLeaf()) 
		{
			TaxonomyItem item=(TaxonomyItem)nodeInfo;
			taxon.setItem(item);
			selectedItem=item;
		}

	}
	
	private class OkAction extends AbstractAction
	{
		public OkAction()
		{
			super("Ok");
		}
		
		public void actionPerformed(ActionEvent arg0) 
		{
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
}
