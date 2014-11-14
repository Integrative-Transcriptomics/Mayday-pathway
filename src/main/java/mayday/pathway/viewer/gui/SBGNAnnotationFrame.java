package mayday.pathway.viewer.gui;

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import mayday.core.gui.MaydayDialog;
import mayday.pathway.sbgn.graph.SBGNNode;

@SuppressWarnings("serial")
public class SBGNAnnotationFrame extends MaydayDialog
{
	private JTable table;
	
	public SBGNAnnotationFrame(SBGNNode node) 
	{
		setName(node.getName());
		String[] names={"Name","Annotation"};
		DefaultTableModel model=new DefaultTableModel(names,0);
		model.addRow(new String[]{boldface("Compartment"),node.getCompartmentName()});
		for(String k:node.getAnnotations().keySet())
		{
			for(String v:node.getAnnotation(k))
			{
				String[] values={boldface(k),v};
				model.addRow(values);
			}
		}		
		for(String k:node.getReferences().keySet())
		{
			String[] values={boldface(k),node.getReference(k)};
			model.addRow(values);
		}	
		table=new JTable(model);
		setLayout(new BorderLayout());
			
		add(new JScrollPane(table), BorderLayout.CENTER);
		pack();
		
	}
	
	private String boldface(String text)
	{
		return "<html><b>"+text+"</b></html>";
	}
}
