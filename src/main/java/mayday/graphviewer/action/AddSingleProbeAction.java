package mayday.graphviewer.action;

import java.awt.event.ActionEvent;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.graph.nodes.Nodes;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.gui.ProbeSelectionDialog;
import mayday.vis3.graph.components.CanvasComponent;

@SuppressWarnings("serial")
public class AddSingleProbeAction extends AddSingleNodeAction 
{
	public AddSingleProbeAction(GraphViewerPlot canvas) 
	{
		super(canvas,Nodes.Roles.PROBE_ROLE);		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		ProbeSelectionDialog psd=new ProbeSelectionDialog(canvas.getModelHub().getViewModel().getDataSet().getMasterTable());
		psd.setVisible(true);
		if(psd.isCancelled()) return;


		
		ProbeList pl=canvas.getModelHub().getAddedProbes();
		StringBuffer name=new StringBuffer();
		for(Probe p: psd.getProbes())
		{
			if(!pl.contains(p))
				pl.addProbe(p);
			
			name.append(p.getDisplayName());
			name.append(",");
		}
		MultiProbeNode n=new MultiProbeNode(canvas.getModel().getGraph(),psd.getProbes());
		
		SuperModel model=(SuperModel) canvas.getModel();
		n.setName(name.substring(0,name.length()-1));
		num++;
	
		CanvasComponent cc=model.addNode(n);
		cc.setLocation(canvas.getMousePosition());
		
		

	}
}
