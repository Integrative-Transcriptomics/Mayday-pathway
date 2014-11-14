package mayday.graphviewer.plugins.misc;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Graphs;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.algorithm.StronglyConnectedComponents;
import mayday.vis3.graph.GraphCanvas;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

@SuppressWarnings("serial")
public class GraphStructureTree extends JTree implements Updatable
{
	private GraphModel graphModel;
	private GraphCanvas canvas;
	
	private DefaultMutableTreeNode rootNode; 
	
	public GraphStructureTree(GraphCanvas canvas) 
	{
		super();
		this.canvas=canvas;
		this.graphModel=canvas.getModel();
		buildModel();		
		setModel(new DefaultTreeModel(rootNode));
		
		setVisibleRowCount(20);
		setMinimumSize(new Dimension(600, 300));	
		
		setCellRenderer(new ComponentNodeRenderer());
		addTreeSelectionListener(new ComponentNodeListener());
	}
	
	public void update()
	{
		buildModel();		
		setModel(new DefaultTreeModel(rootNode));
	}
	
	
	private void buildModel()
	{
		rootNode=new DefaultMutableTreeNode();
		
		Graph g=canvas.getModel().getGraph();
		List<List<Node>> weaklyConnectedComponents = Graphs.calculateComponents(g);
		Collections.sort(weaklyConnectedComponents, new ListSorter());
		
		
		for(List<Node> comp:weaklyConnectedComponents)
		{
			if(comp.size() > 1)
			{
				ComponentObject co=new ComponentObject();
				co.component=comp;
				co.type=ComponentType.Connected;
				DefaultMutableTreeNode compNode=new DefaultMutableTreeNode(co);	
				rootNode.add(compNode);
				// dissect this one		
				Graph sg=Graphs.restrict(g, comp);
				List<List<Node>> scc= StronglyConnectedComponents.findComponents(sg);
				Collections.sort(scc, new ListSorter());
				for(List<Node> sc:scc)
				{
					if(sc.size() > 1)
					{
						ComponentObject ssco=new ComponentObject();
						ssco.component=sc;
						ssco.type=ComponentType.Cycle;
						DefaultMutableTreeNode sn=new DefaultMutableTreeNode(ssco);	
						compNode.add(sn);
						
						for(Node sscn: sc)
						{
							DefaultMutableTreeNode snn=new DefaultMutableTreeNode(sscn);
							sn.add(snn);
						}
						
					}else
					{
						DefaultMutableTreeNode sn=new DefaultMutableTreeNode(sc.get(0));
						compNode.add(sn);
					}
				}				
			}else
			{
				DefaultMutableTreeNode compNode=new DefaultMutableTreeNode(comp.get(0));
				rootNode.add(compNode);
			}			
		}		
	}
	
	
	
	private class ComponentNodeRenderer extends DefaultTreeCellRenderer
	{
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,	boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) 
		{
			JLabel lab=(DefaultTreeCellRenderer) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,row, hasFocus);
			DefaultMutableTreeNode tn=(DefaultMutableTreeNode)value;
			
			Object o=tn.getUserObject();
			if(o==null)
				return lab;
			if(o instanceof Node)
			{
				lab.setText(((Node) o).getName());
			}
			if(o instanceof ComponentObject)
			{
				lab.setText(((ComponentObject) o).toString());
			}		
			return lab;
		}		
	}

	private class ComponentNodeListener implements TreeSelectionListener
	{
		@Override
		public void valueChanged(TreeSelectionEvent e) 
		{
			if(getSelectionPaths()==null || getSelectionPaths().length==0)
				return;
			for(TreePath p: getSelectionPaths())
			{
				DefaultMutableTreeNode tn=(DefaultMutableTreeNode)p.getLastPathComponent();
				
				Object o=tn.getUserObject();
				if(o==null)
					return;
				if(o instanceof Node)
				{
					CanvasComponent cc=graphModel.getComponent((Node)o);
					canvas.center(cc.getBounds(), true);					
				}
				if(o instanceof ComponentObject)
				{
					
					Rectangle r=new Rectangle(-1,-1,-1,-1);
					for(Node n: ((ComponentObject) o).component)
					{
						r.add(graphModel.getComponent(n).getBounds());
					}
					canvas.center(r, true);			
				}
			}			
		}
	}
	
	private class ComponentObject
	{
		List<Node> component; 
		ComponentType type;
		
		
		@Override
		public String toString() 
		{
			return type + " of Size "+component.size();
		}
	}
	
	private enum ComponentType
	{
		Connected("Connected Component"),
		Cycle("Cycle");
		
		private final String rep;
		
		private ComponentType(String s) 
		{
			rep=s;
		}
		
		@Override
		public String toString() 
		{
			return rep;
		}
	}
	
	private class ListSorter implements Comparator<List<? extends Object>>
	{
		@Override
		public int compare(List<? extends Object> o1, List<? extends Object> o2) 
		{
			return o2.size()-o1.size();
		}
	}
}
