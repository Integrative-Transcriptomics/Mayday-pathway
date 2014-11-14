package mayday.pathway.keggview.pathways.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import mayday.core.gui.MaydayDialog;
import mayday.core.gui.MaydayFrame;
import mayday.pathway.keggview.pathways.PathwayOverviewTableModel;

@SuppressWarnings("serial")
public class PathwayOverviewFrame extends MaydayFrame
{
	private List<PathwayOverviewTableModel> models;
	
	public PathwayOverviewFrame(List<PathwayOverviewTableModel> models)
	{
		this.models=models;
		init();
		pack();
		setTitle("Pathway Overview");
	}
	
	private void init()
	{
		JPanel panel=new JPanel();
		panel.setLayout(new GridLayout(models.size(),1));
		for(PathwayOverviewTableModel m:models)
		{
			PathwayOverview ov=new PathwayOverview(m);
			panel.add(ov);
		}
		JScrollPane scroller=new JScrollPane(panel);
		add(scroller);
		setMaximumSize(new Dimension(400,800));
	}
	
	
	private class PathwayOverview extends JPanel
	{
		private JTable table;
		private JButton button;
		private PathwayOverviewTableModel model;
		
		public PathwayOverview(PathwayOverviewTableModel model)
		{
			super();
			setBorder(BorderFactory.createTitledBorder(model.getProbeList().getName()));
			this.model=model;
			setLayout(new BorderLayout());
			table=new JTable(model);
			JScrollPane scroller=new JScrollPane(table);
			add(scroller,BorderLayout.CENTER);
				
			button=new JButton(new ShowAction());
			add(button,BorderLayout.EAST);	
			JLabel label=new JLabel(model.getProbeList().getName()+ "    ("+model.getProbeList().getNumberOfProbes()+")" );
			add(label,BorderLayout.NORTH);
			setMaximumSize(new Dimension(400,200));
		}
		
		private class ShowAction extends AbstractAction
		{
			public ShowAction()
			{
				super("Details");
			}

			public void actionPerformed(ActionEvent e) 
			{
				int i=table.getSelectedRow();
				if(i < 0 || i >= model.getRowCount()) return;
				MaydayDialog dialog=new MaydayDialog();
				dialog.setLayout(new BorderLayout());
				
				JLabel label=new JLabel((String)table.getValueAt(i, 0));
				dialog.add(label,BorderLayout.NORTH);
				dialog.setTitle(label.getText());
				JTable table=new JTable(model.getDetailModel(i));
				JScrollPane scroller=new JScrollPane(table);
				dialog.add(scroller,BorderLayout.CENTER);
				dialog.pack();
				dialog.setVisible(true);
			}
			
		}
	}
}
