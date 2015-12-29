package mayday.graphviewer.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.crossViz3.experiments.ExperimentMapping;
import mayday.graphviewer.crossViz3.experiments.ExperimentMappingDialog;
import mayday.graphviewer.crossViz3.experiments.IExperimentMapping;
import mayday.graphviewer.crossViz3.probes.IProbeMapping;
import mayday.graphviewer.crossViz3.probes.IProbeUnit;
import mayday.graphviewer.crossViz3.probes.ProbeMapping;
import mayday.graphviewer.crossViz3.unitTree.UnitCellRenderer;
import mayday.graphviewer.crossViz3.unitTree.UnitTree;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.GraphModelSelectionListener;
import mayday.vis3.graph.model.SelectionModel;

@SuppressWarnings("serial")
public class ProbesTreePanel extends JPanel implements GraphModelSelectionListener
{
	private IExperimentMapping experimentMapping;
	private IProbeMapping probeMapping;
	
	private JTree tree;
	private SelectionModel selectionModel;	
	
	public ProbesTreePanel(SelectionModel model) 
	{
		this.selectionModel=model;
		model.addSelectionListener(this);
		tree=new JTree();	
		setProbes();
		init();
		
		
	}
	
	public ProbesTreePanel(SelectionModel model, IExperimentMapping expMap, IProbeMapping probeMap) 
	{
		this.selectionModel=model;
		experimentMapping=expMap;
		probeMapping=probeMap;
		
		model.addSelectionListener(this);
		tree=new JTree();
		DefaultMutableTreeNode root= UnitTree.createTree(probeMapping, experimentMapping);
		tree.setModel(new DefaultTreeModel(root));		
		
		init();
	}
	
	private void init()
	{
		tree.setVisibleRowCount(20);
		double min=Double.MAX_VALUE;
		double max=Double.MIN_NORMAL;
		for(IProbeUnit u:probeMapping)
		{
			for(double[] dv: u.getValues(experimentMapping))
			{
				for(double d:dv)
				{
					min=Math.min(d, min);
					max=Math.max(d, max);
				}
			}
		}		
		tree.setCellRenderer(new UnitCellRenderer(min,max));
		
		setLayout(new BorderLayout());
		add(new JScrollPane(tree),BorderLayout.CENTER);
		
		JPanel buttonPanel=new JPanel();
		
		JButton expMappingButton=new JButton(new EditExperimentMappingAction());
		buttonPanel.add(expMappingButton);
		add(buttonPanel,BorderLayout.NORTH);
	}
	
	private void setProbes()
	{
		MultiHashMap<DataSet, Probe> probes=new MultiHashMap<DataSet, Probe>();
		
		for(CanvasComponent comp:selectionModel.getSelectedComponents())
		{
			if(comp instanceof MultiProbeComponent)
			{
				for( Probe p: ((MultiProbeComponent) comp).getProbes())
				{
					probes.put(p.getMasterTable().getDataSet(), p);
				}
			}
		}		
		probeMapping=ProbeMapping.createMappingByName(new ArrayList<DataSet>(probes.keySet()), probes, true);
		experimentMapping=ExperimentMapping.createMappingByName(new ArrayList<DataSet>(probes.keySet()));
		
		DefaultMutableTreeNode root= UnitTree.createTree(probeMapping, experimentMapping);
		tree.setModel(new DefaultTreeModel(root));
	}
	
	@Override
	public void selectionChanged() 
	{
		setProbes();		
	}
	
	public IExperimentMapping getExperimentMapping() 
	{
		return experimentMapping;
	}

	public void setExperimentMapping(IExperimentMapping experimentMapping) 
	{
		this.experimentMapping = experimentMapping;
		DefaultMutableTreeNode root= UnitTree.createTree(probeMapping, experimentMapping);
		tree.setModel(new DefaultTreeModel(root));
	}

	public IProbeMapping getProbeMapping() 
	{
		return probeMapping;
	}

	public void setProbeMapping(IProbeMapping probeMapping) 
	{
		this.probeMapping = probeMapping;
		DefaultMutableTreeNode root= UnitTree.createTree(probeMapping, experimentMapping);
		tree.setModel(new DefaultTreeModel(root));
	}
	
	private class EditExperimentMappingAction extends AbstractAction
	{
		public EditExperimentMappingAction() 
		{
			super("Configure Experiment Mapping");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			ExperimentMapping mapping=new ExperimentMapping((ExperimentMapping)experimentMapping);
			ExperimentMappingDialog d=new ExperimentMappingDialog(mapping);
			d.setModal(true);
			d.setVisible(true);
			
			if(d.isCancelled())
				return;
			
			setExperimentMapping(mapping);			
		}
	}
}
