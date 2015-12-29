package mayday.graphviewer.core.bag.tools;

import java.awt.BorderLayout;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import mayday.core.Probe;
import mayday.graphviewer.core.bag.BagComponent;
import mayday.graphviewer.core.bag.ComponentBag;

@SuppressWarnings("serial")
public class BagProbeOverview extends BagCentralComponent 
{
	private JTable table; 
	private ProbeTableModel model;
	
	
	public BagProbeOverview(ComponentBag bag, BagComponent comp) 
	{
		super(bag, comp);
		setLayout(new BorderLayout());
		model=new ProbeTableModel(bag.getProbes());
		
		table=new JTable(model);
		table.setAutoCreateRowSorter(true);
		table.getSelectionModel().addListSelectionListener(new ProbeTableListener());
		add(new JScrollPane(table),BorderLayout.CENTER);
	}
	
	private class ProbeTableListener implements ListSelectionListener
	{
		@Override
		public void valueChanged(ListSelectionEvent e) 
		{
			List<Probe> probes=new ArrayList<Probe>();
			for(int r:table.getSelectedRows())
			{
				if(r < 0) continue;
				int mr= table.convertRowIndexToModel(r);
				probes.add(model.probes.get(mr));
			}
			selectProbes(probes);
		}
	}
	
	private class ProbeTableModel extends DefaultTableModel
	{
		private List<Probe> probes;
			
		public ProbeTableModel(List<Probe> probes) 
		{
			this.probes = probes;
		}
		
		@Override
		public int getRowCount() 
		{
			if(probes==null)
				return 0;
			return probes.size();
		}
		
		@Override
		public int getColumnCount() 
		{
			return 5;
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex)
		{
			return String.class;
		}
		
		@Override
		public String getColumnName(int column) 
		{
			switch(column)
			{
				case 0: return "Display Name";
				case 1: return "Name";
				case 2: return "DataSet";
				case 3: return "mean";
				case 4: return "var";
				default: return null;
			}
		}
		@Override
		public Object getValueAt(int row, int column) 
		{
			Probe p=probes.get(row);
			switch(column)
			{
				case 0: return p.getDisplayName();
				case 1: return p.getName();
				case 2: return p.getMasterTable().getDataSet().getName();
				case 3: return NumberFormat.getNumberInstance().format(p.getMean());
				case 4: return NumberFormat.getNumberInstance().format(p.getVariance());
				default: return null;
			}
		}
		
		@Override
		public boolean isCellEditable(int row, int column) 
		{
			return false;
		}		
	}
}
