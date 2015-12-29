package mayday.graphviewer.datasources.biopax2;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.ListSelectionModel;

@SuppressWarnings("serial")
public class PathwaySelectionDialog extends JDialog
{
	private PathwaySelectionPanel panel;
	private boolean cancelled=true;
	
	public PathwaySelectionDialog(Window owner, BioPaxSqueezer2 squeezer) 
	{
		super(owner, "Select Pathways");
		panel=new PathwaySelectionPanel(squeezer);
				
		setLayout(new BorderLayout());
		
		add(panel, BorderLayout.CENTER);		
		
	    JButton okButton=new JButton(new OkAction());
	    JButton cancelButton=new JButton(new CancelAction());
	    
	    Box buttonBox=Box.createHorizontalBox();
	    buttonBox.add(Box.createHorizontalGlue());
	    buttonBox.add(cancelButton);
	    buttonBox.add(Box.createHorizontalStrut(10));
	    buttonBox.add(okButton);
	    	    
	    add(buttonBox,BorderLayout.SOUTH);
	    

	    
		pack();
		
	}
	

	
	public List<String> getSelectedObjects()
	{
		return panel.getSelectedObjects();
	}
	
	public String getSelectedObject()
	{
		return panel.getSelectedObject();
	}
	
	/**
	 * @param i use constants in ListSelectionModel
	 * @see ListSelectionModel
	 */
	public void setSelectionMode(int i)
	{
		panel.setSelectionMode(i);
	}
	
	
	private class CancelAction extends AbstractAction
	{
		public CancelAction() 
		{
			super("Cancel");
		}
		
		public void actionPerformed(ActionEvent e) 
		{
			cancelled=true;	
			dispose();
		}
	}
	
	private class OkAction extends AbstractAction
	{
		public OkAction() 
		{
			super("Ok");
		}
		
		public void actionPerformed(ActionEvent e) 
		{
			cancelled=false;	
			dispose();
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		BioPaxSqueezer2 sq=new BioPaxSqueezer2("/home/symons/Anaconda/eco/Escherichia coli.owl");
		PathwaySelectionDialog d=new PathwaySelectionDialog(null, sq);
		d.setVisible(true);
	}

	public boolean isCancelled() 
	{
		return cancelled;
	}

	
	
}
