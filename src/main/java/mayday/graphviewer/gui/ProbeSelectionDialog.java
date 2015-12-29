package mayday.graphviewer.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.gui.MaydayDialog;
import mayday.core.gui.components.ExcellentBoxLayout;

@SuppressWarnings("serial")
public class ProbeSelectionDialog extends MaydayDialog
{
	private JList probeList;
	private JTextField query;
	
	private boolean cancelled;
	
	
	@SuppressWarnings("unchecked")
	public ProbeSelectionDialog(MasterTable masterTable) 
	{
		setLayout(new ExcellentBoxLayout(true, 10));
		
        JPanel qpnl = new JPanel(new BorderLayout());
        query=new JTextField(25);
        query.setText("");
        query.getDocument().addDocumentListener(new SearchListener());
        query.setMaximumSize(new Dimension(20000,query.getPreferredSize().height));
        qpnl.add(new JLabel("Probe search: "), BorderLayout.WEST);
        qpnl.add(query, BorderLayout.CENTER);
        qpnl.setMaximumSize(new Dimension(20000,qpnl.getPreferredSize().height));
		
        Vector<Probe> vprobes = new Vector<Probe>(masterTable.getProbes().values());
        Collections.sort(vprobes);        
        probeList=new JList(vprobes);        
        probeList.setCellRenderer(new ProbeCellRenderer());
        probeList.setVisibleRowCount(8);
        
        add(qpnl);
        add(new JScrollPane(probeList));
        
        JPanel bpnl=new JPanel(new ExcellentBoxLayout(false,10));
        JButton okButton=new JButton(new OkAction());
        JButton cancelButton=new JButton(new CancelAction());
        
        bpnl.add(cancelButton);
        bpnl.add(okButton);
        
        add(bpnl);
        setModal(true);
        pack();
	}
	
	public boolean isCancelled() {
		return cancelled;
	}

	private void search()
	{
        String s = query.getText();
        if (s.length() <= 0) {
            return;
        }
        for(int i=0; i!= probeList.getModel().getSize(); ++i)
        {
        	try {
        	if( ((Probe)probeList.getModel().getElementAt(i)).getDisplayName().matches(".*"+s+".*"))
        	{
        		probeList.ensureIndexIsVisible(i);
        		probeList.setSelectedIndex(i);
        		break;        		
        	}
        	} catch (Exception e) {}
        }
       
        

	}
	
	private class SearchListener implements DocumentListener
	{
	
		public void changedUpdate(DocumentEvent e) 
		{
		}
	
		public void insertUpdate(DocumentEvent e) 
		{
			search();		
		}
	
		public void removeUpdate(DocumentEvent e) 
		{
			search();				
		}		
	}
	
	public List<Probe> getProbes()
	{
		List<Probe> res=new ArrayList<Probe>();
		for(Object o:probeList.getSelectedValues())
			res.add((Probe)o);
		return res; 
	}
	
	private class OkAction extends AbstractAction
	{
		public OkAction() 
		{
			super("Ok");
		}
		
		public void actionPerformed(ActionEvent e) 
		{
			dispose();
			cancelled=false;
		}
	}
	
	private class CancelAction extends AbstractAction
	{
		public CancelAction() 
		{
			super("Cancel");
		}
		
		public void actionPerformed(ActionEvent e) 
		{
			dispose();
			cancelled=true;
		}
	}
	
	private class ProbeCellRenderer extends DefaultListCellRenderer
	{

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) 
		{
			if(value instanceof Probe)
			{
				return super.getListCellRendererComponent(list, ((Probe)value).getDisplayName(), index, isSelected,
						cellHasFocus);
			}
			return super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);
		}
		
	}
	
}
