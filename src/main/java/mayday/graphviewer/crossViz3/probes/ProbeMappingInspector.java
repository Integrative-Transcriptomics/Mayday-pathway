package mayday.graphviewer.crossViz3.probes;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.gui.MaydayDialog;

@SuppressWarnings("serial")
public class ProbeMappingInspector extends MaydayDialog
{
	private JTable table;
	private IProbeMapping mapping;

	public ProbeMappingInspector(IProbeMapping mapping) 
	{
		this.mapping=mapping;
		setTitle("Probe Mapping");
		table=new JTable(new ProxyModel(mapping));
		
		setLayout(new BorderLayout());
		add(new JScrollPane(table),BorderLayout.CENTER);
		pack();
	}

	private class ProxyModel extends DefaultTableModel
	{
		private List<IProbeUnit> units;

		public ProxyModel(IProbeMapping mapping) 
		{
			units=new ArrayList<IProbeUnit>(mapping.getUnits());
			Collections.sort(units);
		}

		public int getColumnCount() 
		{
			return mapping.getNumberOfDataSets()+1;
		}

		public int getRowCount() 
		{
			return mapping.getNumberOfUnits();
		}

		public Object getValueAt(int row, int column) 
		{
			IProbeUnit unit=units.get(row);
			
			
			if(column==0)
			{
				return unit.getName();
			}
			DataSet rd=mapping.getDataSets().get(column-1);
			for(Probe p:unit.getProbes())
			{
				if(p.getMasterTable().getDataSet()==rd)
					return p;
			}
			return "";
		}

		public boolean isCellEditable(int row, int col) 
		{
			return false;
		}

		public String getColumnName(int column)
		{
			if(column==0)
				return "Name";
			return mapping.getDataSets().get(column-1).getName();
		}
	}
}
