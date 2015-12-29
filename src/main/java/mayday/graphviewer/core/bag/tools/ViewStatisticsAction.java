package mayday.graphviewer.core.bag.tools;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;

import mayday.graphviewer.statistics.MatrixPlotter;
import mayday.graphviewer.statistics.ResultSet;

@SuppressWarnings("serial")
public class ViewStatisticsAction extends AbstractAction
{
	private ResultSet res;
	
	public ViewStatisticsAction(ResultSet res) 
	{
		super(res.getTitle());
		this.res=res;		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		JDialog d=new JDialog();
		
		d.setTitle(res.getTitle());
		d.add(new MatrixPlotter(res, MatrixPlotter.createGradient(res)));
		d.pack();
		d.setVisible(true);		
	}
}
