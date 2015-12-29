package mayday.graphviewer.crossViz3.unitTree;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import mayday.graphviewer.core.SummaryOption;
import mayday.graphviewer.crossViz3.experiments.IExperimentMapping;
import mayday.graphviewer.crossViz3.probes.IProbeMapping;
import mayday.graphviewer.crossViz3.probes.IProbeUnit;


@SuppressWarnings("serial")
public class UnitTree extends JPanel
{
	private JTree unitTree;
	
	public UnitTree(IProbeMapping mapping, IExperimentMapping expMapping) 
	{
		super();
		DefaultMutableTreeNode root=createTree(mapping, expMapping);
		unitTree=new JTree(root);
		unitTree.setVisibleRowCount(20);
		unitTree.setMinimumSize(new Dimension(600, 300));	
		
		double min=Double.MAX_VALUE;
		double max=Double.MIN_NORMAL;
		for(IProbeUnit u:mapping)
		{
			for(double[] dv: u.getValues(expMapping))
			{
				for(double d:dv)
				{
					if(Double.isNaN(d)) continue;
					min=Math.min(d, min);
					max=Math.max(d, max);
				}
			}
		}		
		
		unitTree.setCellRenderer(new UnitCellRenderer(min,max));
		
		setLayout(new BorderLayout());
		add(new JScrollPane(unitTree),BorderLayout.CENTER);		
	}
	
	public static DefaultMutableTreeNode createTree(IProbeMapping mapping, IExperimentMapping expMapping)
	{
		DefaultMutableTreeNode root=new DefaultMutableTreeNode("");

		for(IProbeUnit u:mapping)
		{
			if(u==null)
				continue;
			if(u.getNumberOfProbes()==1)
			{
				ProbeTreeObject o=new ProbeTreeObject(u.getProbes().get(0),u.getValues(expMapping).get(0));
				DefaultMutableTreeNode node=new DefaultMutableTreeNode(o);
				root.add(node);
				continue;
			}			
			if(u.isCollapsable() && expMapping.isCollapsable())
			{
				UnitTreeObject o=new UnitTreeObject(u, u.getCollapsedValues(expMapping));				
				DefaultMutableTreeNode node=new DefaultMutableTreeNode(o);
				root.add(node);	
				continue;
			}
			
			UnitTreeObject o=new UnitTreeObject(u, u.summarize(SummaryOption.MEAN, expMapping));				
			DefaultMutableTreeNode head=new DefaultMutableTreeNode(o);
			List<double[]> values=u.getValues(expMapping);
			
			for(int i=0; i!= u.getNumberOfProbes(); ++i)
			{
				ProbeTreeObject po=new ProbeTreeObject(u.getProbes().get(i),values.get(i));
				DefaultMutableTreeNode node=new DefaultMutableTreeNode(po);
				head.add(node);
			}			
			root.add(head);
		}
		return root;
	}
}
