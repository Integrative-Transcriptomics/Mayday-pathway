package mayday.pathway.keggview.pathways;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import mayday.core.Probe;

@SuppressWarnings("serial")
public class PathwayDetailModel extends AbstractTableModel
{
	private List<String> enzymeNames;
	private List<Probe> probes;
	
	public PathwayDetailModel(List<String> enzymes, List<Probe> probes)
	{
		this.enzymeNames=enzymes;
		this.probes=probes;
	}
	
	public int getColumnCount() 
	{
		return 2;
	}

	public int getRowCount() 
	{
		return probes.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) 
	{
		switch (columnIndex) 
		{
			case 0: return enzymeNames.get(rowIndex);
			case 1: return probes.get(rowIndex).getDisplayName();	
			default: return "";
		}
	}

	public String getColumnName(int column)
	{
		switch (column) 
		{
		case 0: return "Enzyme";
		case 1: return "Probe";
		default:
			return "";
		}
	}
	
}
