package mayday.graphviewer.plugins.group;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.core.bag.BagComponent;
import mayday.graphviewer.core.bag.ComponentBag;
import mayday.graphviewer.plugins.misc.Updatable;
import mayday.vis3.graph.components.CanvasComponent;

@SuppressWarnings("serial")
public class BagTree extends JTree implements Updatable
{
	private DefaultMutableTreeNode root;

	private GraphViewerPlot viewer;

	public BagTree(GraphViewerPlot viewer) 
	{
		super();
		this.viewer=viewer;		
		buildModel((SuperModel) viewer.getModel());
		setModel(new DefaultTreeModel(root));
		setVisibleRowCount(20);
		setMinimumSize(new Dimension(600, 300));	
		setCellRenderer(new ComponentNodeRenderer());
		addTreeSelectionListener(new ComponentNodeListener());
		addMouseListener(new ComponentMouseListener());
	}

	private void buildModel(SuperModel sm)
	{
		root=new DefaultMutableTreeNode();
		Set<CanvasComponent> used=new HashSet<CanvasComponent>();
		for(ComponentBag bag: sm.getBags())
		{
			DefaultMutableTreeNode tn=new DefaultMutableTreeNode(sm.getComponent(bag));
			root.add(tn);
			used.addAll(buildBag(tn, bag));
			used.add(sm.getComponent(bag));
		}
		Set<CanvasComponent> all=new HashSet<CanvasComponent>(sm.getComponents());
		all.removeAll(used);
		for(CanvasComponent cc: all)
		{
			DefaultMutableTreeNode tn=new DefaultMutableTreeNode(cc);
			root.add(tn);
		}		
	}

	private Set<CanvasComponent> buildBag(DefaultMutableTreeNode root, ComponentBag bag)
	{
		Set<CanvasComponent> used=new HashSet<CanvasComponent>();
		for(CanvasComponent cc: bag.getComponents())
		{
			DefaultMutableTreeNode tn=new DefaultMutableTreeNode(cc);
			tn.setAllowsChildren(false);
			root.add(tn);
			used.add(cc);
			if(cc instanceof BagComponent)
			{
				tn.setAllowsChildren(true);
				used.addAll(buildBag(tn, ((BagComponent)cc).getBag())); // recurse!
			}
		}
		return used;
	}

	private class ComponentNodeRenderer extends DefaultTreeCellRenderer
	{
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,	boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) 
		{
			JLabel lab=(DefaultTreeCellRenderer) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,row, hasFocus);
			DefaultMutableTreeNode tn=(DefaultMutableTreeNode)value;
			CanvasComponent cc=(CanvasComponent)tn.getUserObject();
			if(cc==null)
				return lab;
			lab.setText(cc.getLabel());
			return lab;
		}		
	}

	private class ComponentNodeListener implements TreeSelectionListener
	{
		@Override
		public void valueChanged(TreeSelectionEvent e) 
		{
			for(TreePath p: getSelectionPaths())
			{
				DefaultMutableTreeNode tn=(DefaultMutableTreeNode)p.getLastPathComponent();
				CanvasComponent cc=(CanvasComponent)tn.getUserObject();
				if(cc==null)
					return;
				viewer.center(cc.getBounds(), true);
			}			
		}
	}

	private class ComponentMouseListener extends MouseAdapter
	{
		@Override
		public void mousePressed(MouseEvent e) 
		{
			if(e.isPopupTrigger())
			{

				int selRow = getRowForLocation(e.getX(), e.getY());
				TreePath p = getPathForLocation(e.getX(), e.getY());
				if(selRow >=0) 
				{
					DefaultMutableTreeNode tn=(DefaultMutableTreeNode)p.getLastPathComponent();
					CanvasComponent comp=(CanvasComponent)tn.getUserObject();
					comp.getMenu().show(BagTree.this,e.getX(), e.getY());
					e.consume();
				}
			}			
		}
	}
	
	@Override
	public void update() 
	{
		buildModel((SuperModel) viewer.getModel());
		setModel(new DefaultTreeModel(root));
		setSelectionModel(new DefaultTreeSelectionModel());
	}

}
