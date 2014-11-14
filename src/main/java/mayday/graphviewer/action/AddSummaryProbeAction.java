package mayday.graphviewer.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.core.DataSet;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.SummaryProbe;
import mayday.vis3.graph.model.SummaryProbeSetting;

@SuppressWarnings("serial")
public class AddSummaryProbeAction extends AbstractAction
{
	private GraphViewerPlot viewer;
	private MultiProbeComponent comp;
	
	public AddSummaryProbeAction(GraphViewerPlot viewer, MultiProbeComponent comp) 
	{
		super("Add Summary Probe");
		this.viewer=viewer;
		this.comp=comp;
		
	}
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		
		SummaryProbeSetting sps=new SummaryProbeSetting();
		DataSet[] dss=(DataSet[]) viewer.getModelHub().getDataSets().toArray(new DataSet[viewer.getModelHub().getDataSets().size()]);
		ObjectSelectionSetting<DataSet> dataSetSetting= new ObjectSelectionSetting<DataSet>("Data Set",null, 0, dss);
		HierarchicalSetting setting=new HierarchicalSetting("Summary Settings");
		setting.addSetting(sps).addSetting(dataSetSetting);
		SettingDialog sd=new SettingDialog(null, "Summary Settings", setting);
		
		sd.setModal(true);
		sd.setVisible(true);
		if(sd.canceled())
			return;
		SummaryProbe sp=new SummaryProbe(
				dataSetSetting.getObjectValue().getMasterTable(),
				viewer.getModel().getGraph(),
				comp.getNode(),sps.getSummaryMode());
		sp.setName("Summary");
		sp.setWeightMode(sps.getWeightMode());
		sp.updateSummary();
		comp.addProbe(sp);
		((MultiProbeNode)comp.getNode()).addProbe(sp);
		comp.repaint();
	}
}
