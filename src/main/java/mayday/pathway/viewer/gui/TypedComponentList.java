package mayday.pathway.viewer.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import mayday.vis3.graph.GraphCanvas;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.dialog.ComponentTableCellRenderer;
import mayday.vis3.graph.model.GraphModel;



/**
 * @author Stephan Symons
 *
 */
@SuppressWarnings("serial")
public class TypedComponentList extends JPanel implements MouseListener
{
	private JTable componentTable;
	
	private GraphCanvas parent;
	private GraphModel model;
	
	@SuppressWarnings("unchecked")
	private Class targetClass=null; 
	
	private List<CanvasComponent> components;
	
	/**
	 * @param model
	 * @param parent
	 * @param targetClass The class of canvas components to be listed. Null lists all. 
	 */
	@SuppressWarnings("unchecked")
	public TypedComponentList(GraphModel model, GraphCanvas parent, Class targetClass) 
	{
		this.model=model;
		this.parent=parent;
		this.targetClass=targetClass;		
		init();		
	}
	
	private void init()
	{
		componentTable=new JTable();
		componentTable.setModel(new ProxyModel(model));
		componentTable.setDefaultRenderer(CanvasComponent.class, new ComponentTableCellRenderer());
		setLayout(new BorderLayout());
		JScrollPane scroller=new JScrollPane(componentTable);
		add(scroller,BorderLayout.CENTER);
		componentTable.addMouseListener(this);
		int i=0;
		for(CanvasComponent c:components)
		{
			componentTable.setRowHeight(i,c.getHeight()+4);
			++i;
		}
		
		
	}	
	
	public void mouseClicked(MouseEvent e) 
	{
			parent.center(components.get(componentTable.getSelectedRow()).getBounds(),true);
	}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent event) 
	{
		if(event.isPopupTrigger())
		{
			components.get(componentTable.getSelectedRow()).getMenu().show(this,event.getX(), event.getY());
			event.consume();
		}
	}

	public void mouseReleased(MouseEvent e) {}
	
	private class ProxyModel extends AbstractTableModel
	{
		
		
		@SuppressWarnings("unchecked")
		public ProxyModel(GraphModel model) 
		{
			components=new ArrayList<CanvasComponent>();
			if(targetClass==null)
			{
				components=model.getComponents();
			}else
			{				
				for(CanvasComponent comp:model.getComponents())
				{
					if(targetClass.isAssignableFrom(model.getNode(comp).getClass()))
					{
						components.add(comp);
					}
				}
			}
		}
		
		public int getColumnCount() 
		{
			return 4;
		}

		public int getRowCount() 
		{
			return components.size();
		}

		public Object getValueAt(int row, int column) 
		{
			switch (column) 
			{
			case 0: return components.get(row).getLabel();
			case 1: return components.get(row);
			case 2: return "("+components.get(row).getLocation().x+","+components.get(row).getLocation().y+")";
			case 3: return Boolean.valueOf(components.get(row).isVisible());
			default:
				return "";
			}
		}
		
        @SuppressWarnings("unchecked")
		public Class getColumnClass(int c) 
        {
        	if(c==1) return CanvasComponent.class;
        	if(c==3) return Boolean.class;
        	return String.class;
//            return getValueAt(0, c).getClass();
        }


		public boolean isCellEditable(int row, int col) 
		{
			if (col ==1) 
			{
				return false;
			} 
			return true;

		}
		
        public void setValueAt(Object value, int row, int col) 
        {
        	if(col==0)
        	{
        		model.getComponents().get(row).setLabel(value.toString());
        	}
        	if(col==3)
        	{
        		model.getComponents().get(row).setVisible((Boolean)value);
        	}        	
        }

		public String getColumnName(int column)
		{
			switch (column) 
			{
			case 0: return "Name";
			case 1: return "Component";
			case 2: return "Position";
			case 3: return "Visible";
			default:
				return "";
			}
		}
	}
}
