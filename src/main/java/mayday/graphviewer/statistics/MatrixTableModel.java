package mayday.graphviewer.statistics;

import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class MatrixTableModel extends DefaultTableModel 
{
	private ResultSet res;
	
	public MatrixTableModel(ResultSet res)
	{
		this.res=res;
	}

	@Override
	public int getColumnCount() 
	{
		if(res==null) return 0;
		if(res.getPreferredScope()==ResultScope.Probes)
			return res.getProbeResults().ncol()+1;
		return res.getGroupResults().ncol()+1;
	}
	
	@Override
	public int getRowCount() 
	{
		if(res==null) return 0;
		if(res.getPreferredScope()==ResultScope.Probes)
			return res.getProbeResults().nrow();
		return res.getGroupResults().nrow();
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) 
	{
		if(columnIndex==0)
			return String.class;
		return Double.class;
	}
	
	@Override
	public String getColumnName(int column) 
	{
		if(column==0)
			return "";
		if(res.getPreferredScope()==ResultScope.Probes)
			return res.getProbeResults().getColumnName(column-1);
		return res.getGroupResults().getColumnName(column-1);
	}
	
	@Override
	public boolean isCellEditable(int row, int column) 
	{
		return false;
	}
	
	@Override
	public Object getValueAt(int row, int column) 
	{
		if(column==0)
		{
			if(res.getPreferredScope()==ResultScope.Probes)
				return res.getProbeResults().getRowName(row);
			return res.getGroupResults().getRowName(row);
		}
		column=column-1;
		if(res.getPreferredScope()==ResultScope.Probes)
			return res.getProbeResults().getValue(row, column);			
		return res.getGroupResults().getValue(row, column);		
	}
	
	
}
