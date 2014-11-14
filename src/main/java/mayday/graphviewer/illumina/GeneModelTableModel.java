package mayday.graphviewer.illumina;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import mayday.genetics.coordinatemodel.GBAtom;

@SuppressWarnings("serial")
public class GeneModelTableModel extends DefaultTableModel
{
	private GFFGeneModelParser parser;

	public GeneModelTableModel(GFFGeneModelParser parser) 
	{
		this.parser = parser;
	}

	public int getRowCount() 
	{ 
		if(parser==null) return 0;
		return parser.getGenes().size();
	}

	public int getColumnCount() 
	{
		return 6;
	}

	public Object getValueAt(int row, int col) 
	{
		GBAtom a=parser.getGenes().get(row);
		if(col==0)
		{
			if(parser.getGeneTomRNAs().containsKey( parser.getGeneToID().get(a)))
			{
				return parser.getExonToChromosome().get(parser.getGeneTomRNAs().get(parser.getGeneToID().get(a)).get(0));
			}
			else
			{
				return parser.getExonToChromosome().get(parser.getGeneToID().get(a));
			}
				
		}
		if(col==1)
			return a.strand;
		if(col==2)
			return a.from;
		if(col==3)
			return a.to;
		if(col==4)
			return parser.getGeneToID().get(a);
		if(col==5)
		{
			if(parser.getGeneTomRNAs().containsKey( parser.getGeneToID().get(a)))
				return parser.getGeneTomRNAs().get( parser.getGeneToID().get(a)).size();
			else
				return 1;
		}
		return null;
	}

    public String getColumnName(int col) 
    {
        switch (col) {
		case 0: return "Chr";
		case 1: return "Strand";		
		case 2: return "From";	
		case 3: return "To";
		case 4: return "Name";
		case 5: return "Gene Models";
		default:
			return null;
		}
    }

	public boolean isCellEditable(int row, int col)  
	{
		return false; 
	}

	public void setValueAt(Object value, int row, int col) 
	{	
		// do nothing; 
	}

	public static class GeneModelTablePanel extends JPanel
	{
		private JTable table;
		private GFFGeneModelParser parser;
		
		public GeneModelTablePanel(GFFGeneModelParser parser) {
			super(new BorderLayout());
			this.parser = parser;
			
			table=new JTable(new GeneModelTableModel(parser));
			add(new JScrollPane(table), BorderLayout.CENTER);			
		}
		
		public List<GBAtom> getSelectedModels()
		{
			List<GBAtom> res=new ArrayList<GBAtom>();
			for(int i:table.getSelectedRows())
			{
				res.add(parser.getGenes().get(i));
			}
			return res;
		}

	}


}
