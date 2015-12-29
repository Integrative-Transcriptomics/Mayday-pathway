package mayday.pathway.keggview.pathways;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.pathway.keggview.pathways.graph.GeneNode;
import mayday.pathway.keggview.pathways.graph.PathwayGraph;

@SuppressWarnings("serial")
public class PathwayOverviewTableModel extends AbstractTableModel
{
	private List<Integer> foundProbes;
	private List<PathwayGraph> pathways;
	private ProbeList probeList;
	
	public PathwayOverviewTableModel(List<Integer> foundProbes, List<PathwayGraph> pathways, ProbeList probeList)
	{
		this.foundProbes=foundProbes;
		this.pathways=pathways;
		this.probeList=probeList;
	}

	
	public PathwayGraph getPathway(int i)
	{
		return pathways.get(i);
	}



	public int getColumnCount() 
	{
		return 4;
	}



	public int getRowCount() 
	{
		return pathways.size();
	}

	public String getColumnName(int column)
	{
		switch (column) 
		{
		case 0: return "Pathway Name";
		case 1: return "Pathway Id";
		case 2: return "Probes found";
		case 3: return "% found";
		default:
			return "";
		}
	}

	public Object getValueAt(int rowIndex, int columnIndex) 
	{
		switch (columnIndex) 
		{
		case 0: return pathways.get(rowIndex).getTitle();
		case 1: return pathways.get(rowIndex).getName();
		case 2: return foundProbes.get(rowIndex);
		case 3: return NumberFormat.getPercentInstance().format((1.0*foundProbes.get(rowIndex))/(probeList.getNumberOfProbes()));
		default:
			return "";
		}
	}
	
	public PathwayDetailModel getDetailModel(int index)
	{
		List<String> enzymeNames=new ArrayList<String>();
		List<Probe> probes=new ArrayList<Probe>();
		
		for(GeneNode g:pathways.get(index).getGeneNodes())
		{
			for(Probe p:g.getProbes())
			{
				enzymeNames.add(g.getName());
				probes.add(p);
			}
		}
		
		return new PathwayDetailModel(enzymeNames,probes);
	}

	/**
	 * @return the foundProbes
	 */
	public List<Integer> getFoundProbes() {
		return foundProbes;
	}

	/**
	 * @param foundProbes the foundProbes to set
	 */
	public void setFoundProbes(List<Integer> foundProbes) {
		this.foundProbes = foundProbes;
	}

	/**
	 * @return the pathways
	 */
	public List<PathwayGraph> getPathways() {
		return pathways;
	}

	/**
	 * @param pathways the pathways to set
	 */
	public void setPathways(List<PathwayGraph> pathways) {
		this.pathways = pathways;
	}

	/**
	 * @return the probeList
	 */
	public ProbeList getProbeList() {
		return probeList;
	}

	/**
	 * @param probeList the probeList to set
	 */
	public void setProbeList(ProbeList probeList) {
		this.probeList = probeList;
	}
	
	
	
	
}
