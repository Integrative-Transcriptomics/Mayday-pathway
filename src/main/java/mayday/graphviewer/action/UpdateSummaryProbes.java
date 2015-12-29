package mayday.graphviewer.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.core.Probe;
import mayday.graphviewer.core.DefaultNodeComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.SummaryProbe;

@SuppressWarnings("serial")
public class UpdateSummaryProbes extends AbstractAction 
{
	private MultiProbeComponent comp;
	
	
	
	public UpdateSummaryProbes(MultiProbeComponent comp) 
	{
		super("Update Summary");
		this.comp = comp;
	}



	@Override
	public void actionPerformed(ActionEvent e)
	{
		for(Probe p: comp.getProbes())
		{
			if(p instanceof SummaryProbe)
			{
				((SummaryProbe) p).updateSummary();
			}
		}
		if(comp instanceof DefaultNodeComponent)
			((DefaultNodeComponent)comp).invalidateCache();
		
//		for(Probe p: ((MultiProbeNode)comp.getNode()).getProbes())
//		{
//			if(p instanceof SummaryProbe)
//			{
//				((SummaryProbe) p).updateSummary();
//			}
//		}
		comp.updateDisplayMode();
	}
}
