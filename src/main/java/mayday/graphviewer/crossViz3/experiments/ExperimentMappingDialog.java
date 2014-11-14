package mayday.graphviewer.crossViz3.experiments;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;

import mayday.core.gui.MaydayDialog;

@SuppressWarnings("serial")
public class ExperimentMappingDialog extends MaydayDialog
{
	private IExperimentMapping mapping;
	private ExperimentMappingComponent mappingComponent;
	private JComboBox methodBox;
	private boolean cancelled=false;
	
	private String[] methods={"Order by Name","Group by Name","Group by Time"};
	
	public ExperimentMappingDialog(IExperimentMapping mapping) 
	{
		this.mapping=mapping;
		mappingComponent=new ExperimentMappingComponent(this.mapping);
		
		setLayout(new BorderLayout(5,10));
		add(mappingComponent,BorderLayout.CENTER);
		
		methodBox=new JComboBox(methods);		
		Box mBox=Box.createHorizontalBox();
		mBox.add(methodBox);
		mBox.add(new JButton(new CreateAction()));
		
		Box buttonBox=Box.createHorizontalBox();
		buttonBox.add(Box.createHorizontalGlue());
		buttonBox.add(new JButton(new OKAction(true)));
		buttonBox.add(new JButton(new OKAction(false)));
		add(buttonBox,BorderLayout.SOUTH);
		pack();
		setModal(true);
		
		
	}
	
	private class OKAction extends AbstractAction
	{
		boolean ok;
		
		public OKAction(boolean ok) 
		{
			super(ok?"Ok":"Cancel");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			dispose();
			cancelled=ok;
			
		}
	}
	
	private class CreateAction extends AbstractAction
	{
		public CreateAction() 
		{
			super("Create");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			int i=methodBox.getSelectedIndex();
			switch (i) {
			case 0:
				mappingComponent.setExperimentMapping(ExperimentMapping.createMapping(mapping.getDataSets()));
				break;
			case 1:
				mappingComponent.setExperimentMapping(ExperimentMapping.createMappingByName(mapping.getDataSets()));
				break;
			case 2:
				mappingComponent.setExperimentMapping(ExperimentMapping.createMappingTime(mapping.getDataSets()));
				break;					
			default:
				break;
			}
			
			
		}
	}

	public boolean isCancelled() 
	{
		return cancelled;
	}

	public void setCancelled(boolean cancelled) 
	{
		this.cancelled = cancelled;
	}
		
	
}
