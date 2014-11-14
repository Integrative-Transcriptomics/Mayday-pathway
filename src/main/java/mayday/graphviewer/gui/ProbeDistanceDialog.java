package mayday.graphviewer.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.Probe;
import mayday.core.gui.MaydayDialog;
import mayday.core.gui.components.ExcellentBoxLayout;
import mayday.core.math.distance.DistanceMeasureManager;
import mayday.core.math.distance.DistanceMeasurePlugin;

@SuppressWarnings("serial")
public class ProbeDistanceDialog extends MaydayDialog
{
	private DistanceMeasurePlugin distanceMeasure;
	private double tolerance=1.0;
	
	private JComboBox distanceMeasureBox= new JComboBox(DistanceMeasureManager.values().toArray());
	private JSpinner toleranceSpinner;
	private JSpinner numberSpinner;
	private JCheckBox maxNeighbors;
	
	private int numberOfNeighbors;
	private boolean cancelled;
	
	public ProbeDistanceDialog(List<Probe> selectedProbes) 
	{
		setLayout(new ExcellentBoxLayout(true,10));
		
        SpinnerNumberModel toleranceModel=new SpinnerNumberModel(0.1,0.0,10000.0,0.1);
        toleranceSpinner=new JSpinner(toleranceModel);
        tolerance=toleranceModel.getNumber().doubleValue();
        toleranceModel.addChangeListener(new ChangeListener()
        {
			public void stateChanged(ChangeEvent e) 
			{
				tolerance=((SpinnerNumberModel)e.getSource()).getNumber().doubleValue();
			}
        	
        });
        
        SpinnerNumberModel numberModel=new SpinnerNumberModel(100,0,1000,1);
        numberSpinner=new JSpinner(numberModel);
        numberOfNeighbors=numberModel.getNumber().intValue();
        numberModel.addChangeListener(new ChangeListener()
        {
			public void stateChanged(ChangeEvent e) 
			{
				numberOfNeighbors=((SpinnerNumberModel)e.getSource()).getNumber().intValue();
			}
        	
        });
        
        distanceMeasureBox= new JComboBox(DistanceMeasureManager.values().toArray());
        distanceMeasureBox.setSelectedItem(DistanceMeasureManager.get("Euclidean"));
        distanceMeasureBox.setEditable(false);
        distanceMeasureBox.setMaximumRowCount(4);
		
        maxNeighbors=new JCheckBox("Limit neighbors to");
        maxNeighbors.setSelected(true);
        
        JLabel probesLabel=new JLabel();
        if(selectedProbes.size() > 5)
        {
        	StringBuffer sb=new StringBuffer(selectedProbes.get(0).getDisplayName());
        	for(int i=1; i!=selectedProbes.size(); ++i)
        	{
        		sb.append(","+selectedProbes.get(i));
        	}
        	probesLabel.setText(sb.toString());
        }else
        {
        	probesLabel.setText(selectedProbes.size()+" Probes");
        }
        
        JPanel dpanel=new JPanel(new BorderLayout());
        dpanel.add(distanceMeasureBox, BorderLayout.CENTER);
        dpanel.setBorder(BorderFactory.createTitledBorder("Distance Measure"));
        Box tBox=Box.createHorizontalBox();
        tBox.add(new JLabel("Tolerance"));
        tBox.add(Box.createHorizontalGlue());
        tBox.add(toleranceSpinner);
        dpanel.add(tBox, BorderLayout.SOUTH);
        
        JPanel nPanel=new JPanel(new BorderLayout() );
        Box nBox=Box.createHorizontalBox();
        nBox.add(maxNeighbors);
        nBox.add(numberSpinner);
        nPanel.add(nBox,BorderLayout.CENTER);
        nPanel.setBorder(BorderFactory.createTitledBorder("Number of Neighbors"));
        
        add(dpanel);
        add(nPanel);
        
        JButton okButton=new JButton(new OkAction());
        JButton cancelButton=new JButton(new CancelAction());
        
        Box buttonBox=Box.createHorizontalBox();
        buttonBox.add(Box.createHorizontalGlue());
        buttonBox.add(cancelButton);
        buttonBox.add(okButton);
        add(buttonBox);
        pack();
        setModal(true);
	}
	
	public DistanceMeasurePlugin getDistanceMeasure()
	{
		return distanceMeasure;
	}

	public double getTolerance() 
	{
		return tolerance;
	}
	
	public int getNumberOfNeighbors() 
	{
		return numberOfNeighbors;
	}

	public boolean isLimitNeighbors()
	{
		return maxNeighbors.isSelected();
	}
	
	public boolean isCancelled() 
	{
		return cancelled;
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
			cancelled=false;
			distanceMeasure=(DistanceMeasurePlugin)distanceMeasureBox.getSelectedItem();
			dispose();			
		}
	}
	
	private class CancelAction extends AbstractAction
	{
		public CancelAction() 
		{
			super("Cancel");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			cancelled=true;
			dispose();			
		}
	}


	
	
	
	
}
