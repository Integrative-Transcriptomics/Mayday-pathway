package mayday.graphviewer.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;

import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.core.gui.MaydayDialog;

@SuppressWarnings("serial")
public class ProbeListSelectorDialog extends MaydayDialog 
{
	private ProbeListSelector probeListSelector;
	
	private boolean closedWithOk;
	
	
	public ProbeListSelectorDialog(List<DataSet> dataSets) 
	{
		probeListSelector=new ProbeListSelector(dataSets);
		init();
	}
	
	public ProbeListSelectorDialog(List<DataSet> dataSets, Collection<ProbeList> probeLists) 
	{
		probeListSelector=new ProbeListSelector(dataSets, probeLists);
		init();
	}
	
	private void init()
	{
		setTitle("Select Probe Lists");
		setLayout(new BorderLayout());
		add(probeListSelector, BorderLayout.CENTER);
		
		Box buttonBox=Box.createHorizontalBox();
		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.add(new JButton(new OKAction("Cancel", false)));
		buttonBox.add(new JButton(new OKAction("Ok", true)));
		add(buttonBox,BorderLayout.SOUTH);
		pack();
		setModal(true);
	}
	
	private class OKAction extends AbstractAction
	{
		private boolean willCloseWithOk; 
		
		public OKAction(String name, boolean ok) 
		{
			super(name);
			willCloseWithOk=ok;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			closedWithOk=willCloseWithOk;
			dispose();
			
		}
	}
	
	public boolean closedWithOK()
	{
		return closedWithOk;
	}

	public List<ProbeList> getProbeLists() 
	{
		return probeListSelector.getSelectedProbeLists();
	}
	
	
	
}
