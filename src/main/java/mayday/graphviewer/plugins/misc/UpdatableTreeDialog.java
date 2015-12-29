package mayday.graphviewer.plugins.misc;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import mayday.core.gui.MaydayDialog;

@SuppressWarnings("serial")
public class UpdatableTreeDialog extends MaydayDialog 
{
	private Updatable centerPanel;

	public UpdatableTreeDialog(Updatable c, Window owner, String title) 
	{
		super(owner, title);
		this.centerPanel=c;
		
		setLayout(new BorderLayout());
		add(new JScrollPane((Component)centerPanel), BorderLayout.CENTER);
		
		JPanel buttonPanel=new JPanel();
		buttonPanel.add(new JButton(new UpdateAction()));
		buttonPanel.add(new JButton(new OkAction()));
		
		add(buttonPanel, BorderLayout.SOUTH);
		setMinimumSize(new Dimension(300, 200));
		pack();
	}
	
	
	private class UpdateAction extends AbstractAction
	{
		public UpdateAction() 
		{
			super("Update");
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			centerPanel.update();			
		}
	}
	
	private class OkAction extends AbstractAction
	{
		public OkAction() 
		{
			super("Ok");
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
			dispose();			
		}
	}
	
	
	
}
