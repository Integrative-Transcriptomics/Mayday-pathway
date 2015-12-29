package mayday.graphviewer.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.gui.ProbeSelectionDialog;
import mayday.vis3.graph.components.CanvasComponent;

@SuppressWarnings("serial")
public class AddProbesAction extends AbstractAction 
{
	private GraphViewerPlot canvas;
	
	public AddProbesAction(GraphViewerPlot viewer) 
	{
		
		this.canvas=viewer;
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		ProbeSelectionDialog psd=new ProbeSelectionDialog(canvas.getModelHub().getViewModel().getDataSet().getMasterTable());
		psd.setVisible(true);
		if(psd.isCancelled()) return;


		int x=20;
		int y=canvas.getComponentMaxY()+20;
		ProbeList pl=canvas.getModelHub().getAddedProbes();
		for(Probe p:psd.getProbes())
		{
			CanvasComponent comp=((SuperModel)canvas.getModel()).addProbe(p);
			pl.addProbe(p);
			comp.setLocation(x,y);
			x+=comp.getWidth()+20;
			if(x>canvas.getWidth())
			{
				x=20;
				y+=20+comp.getHeight();
			}		
			
			canvas.addComponent(comp);	
			canvas.center(comp.getBounds(), true);			
		}
	}

}
