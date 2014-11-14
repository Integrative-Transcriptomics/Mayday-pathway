package mayday.graphviewer.crossViz3.experiments;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;



@SuppressWarnings("serial")
public class ExperimentMappingComponent extends JComponent
{
	private IExperimentMapping mapping;
	private ExperimentMappingModel model;
	private JTable table;

	private static final String removeKey="(remove experiment)";

	public ExperimentMappingComponent(IExperimentMapping mapping) 
	{
		super();
		this.mapping=mapping;
		setLayout(new BorderLayout());
		model=new ExperimentMappingModel();
		table=new JTable(model);		
		table.setPreferredScrollableViewportSize(new Dimension(600,400));
		
		for(int i=1; i!=table.getColumnCount(); ++i)
		{
			TableColumn col=table.getColumnModel().getColumn(i);
			JComboBox comboBox = new JComboBox();
			for(String s: mapping.getDataSets().get(i-1).getMasterTable().getExperimentNames())
			{
				comboBox.addItem(s);
			}
			comboBox.addItem(removeKey);
			col.setCellEditor(new DefaultCellEditor(comboBox));
		}		
		setPreferredSize(new Dimension(400,600));
		add(new JScrollPane(table),BorderLayout.CENTER);
		
		Box buttonBox=Box.createHorizontalBox();
		buttonBox.add(new JButton(new RemoveAction()));
		buttonBox.add(new JButton(new AddAction()));
		
		add(buttonBox,BorderLayout.SOUTH);
	}
	
	public void setExperimentMapping(IExperimentMapping mapping)
	{
		this.mapping=mapping;
		model=new ExperimentMappingModel();
		table.setModel(model);
	}

	private class RemoveAction extends AbstractAction
	{
		public RemoveAction() 
		{
			super("Remove Experiment");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			model.removeRow(table.getSelectedRow());			
		}
	}
	
	private class AddAction extends AbstractAction
	{
		public AddAction() 
		{
			super("Add Experiment");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			String s=JOptionPane.showInputDialog("Insert experiment name", ""+model.getRowCount());
			model.addRow(s);	
		}
	}
		
	private class ExperimentMappingModel extends DefaultTableModel
	{
		@Override
		public Class<?> getColumnClass(int columnIndex) 
		{
			return String.class;
		}

		@Override
		public int getColumnCount() 
		{
			return mapping.getDataSets().size()+1;
		}

		@Override
		public String getColumnName(int columnIndex) 
		{
			if(columnIndex==0)
				return "global";
			else
			{
				return mapping.getDataSets().get(columnIndex-1).getName();
			}
		}

		@Override
		public int getRowCount() 
		{
			return mapping.getNumberOfExperiments();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) 
		{
			if(columnIndex==0)
			{
				return mapping.getGlobalName(rowIndex);
			}
			else
			{
				if(mapping.getLocalExperiment(mapping.getDataSets().get(columnIndex-1), rowIndex)<0)
				{
					return "-";
				}
				return mapping.getLocalExperiment(mapping.getDataSets().get(columnIndex-1), rowIndex)+" "+
				mapping.getLocalName(mapping.getDataSets().get(columnIndex-1), rowIndex);
			}			
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
////			return false;
//			if(columnIndex!=0)
//				return true;
//			return false;
			return true;
		}


		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) 
		{
			
			if(rowIndex==0)
			{
				mapping.setGlobalName(rowIndex, aValue.toString());
				fireTableCellUpdated(rowIndex, columnIndex);
				return;
			}
			if(aValue.equals(removeKey))
			{
				mapping.removeMapping(rowIndex, mapping.getDataSets().get(columnIndex-1));				
			}
			mapping.map(
					mapping.getDataSets().get(columnIndex-1), 
					mapping.getDataSets().get(columnIndex-1).getMasterTable().getExperimentNames().indexOf(aValue), 
					rowIndex);
		

			fireTableCellUpdated(rowIndex, columnIndex);
			
		}
		
		public void removeRow(int rowIndex)
		{
			mapping.removeGlobalExperiment(rowIndex);
			fireTableRowsDeleted(rowIndex, rowIndex);			
		}

		public void addRow(String name)
		{
			int i=mapping.addGlobalExperiment();
			mapping.setGlobalName(i, name);
			fireTableRowsInserted(i, i);		
		}
	}
}
