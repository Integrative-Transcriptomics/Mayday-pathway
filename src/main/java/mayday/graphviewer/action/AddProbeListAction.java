/**
 * 
 */
package mayday.graphviewer.action;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.probelist.ProbeListSelectionDialog;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.gui.PlotComponent;
import mayday.vis3.plots.boxplot.BoxPlotComponent;
import mayday.vis3.plots.heatmap2.HeatMap;
import mayday.vis3.plots.profile.ProfilePlotComponent;


@SuppressWarnings("serial")
public class AddProbeListAction extends AbstractAction
{
	private GraphViewerPlot viewer;
	
	public AddProbeListAction(GraphViewerPlot viewer) 
	{
		super("Add ProbeList as Node");
		this.viewer=viewer;
	}

	public void actionPerformed(ActionEvent e) 
	{
		ProbeListSelectionDialog psd=new ProbeListSelectionDialog(viewer.getModelHub().getViewModel().getDataSet().getProbeListManager());
		psd.setVisible(true);
		if(psd.isCanceled())
			return;
		
		String[] options={"single node","node for each probe","Profile Plot","Heat Map","Box Plot"};
		RestrictedStringSetting method=new RestrictedStringSetting("Display as...",null,0,options);
		
		SettingDialog dialog=new SettingDialog(null, "Adding "+psd.getSelection().size()+" ProbeLists", method);
		dialog.setModal(true);
		dialog.setVisible(true);
		if(!dialog.closedWithOK())
			return;
		
		addProbeLists(viewer, psd.getSelection(), method.getSelectedIndex());
	}
	
	public static void addProbeLists(GraphViewerPlot viewer, List<ProbeList> probeLists, int method)
	{
		int x=20;
		int y=viewer.getComponentMaxY()+20;
		
		MultiHashMap<DataSet, ProbeList> dsMap=new MultiHashMap<DataSet, ProbeList>();		
		for(ProbeList pl: probeLists)
		{
			dsMap.put(pl.getDataSet(), pl);
		}
		for(DataSet ds: dsMap.keySet())
		{
			viewer.getModelHub().addProbeLists(ds, dsMap.get(ds));
		}
		
		if(method==0)
		{
			for(ProbeList pl:probeLists)
			{
				CanvasComponent comp=((SuperModel)viewer.getModel()).addProbeListNode(pl);
				viewer.getModelHub().getViewModel().addProbeListToSelection(pl);
				comp.setLocation(x,y);
				x+=comp.getWidth()+20;
				if(x>viewer.getWidth())
				{
					x=20;
					y+=20+comp.getHeight();
				}	
				
			}
			return;
		}
		if(method==1)
		{
			for(ProbeList pl:probeLists){
				for(Probe p:pl){
					CanvasComponent comp=((SuperModel)viewer.getModel()).addProbe(p);					
					comp.setLocation(x,y);
					x+=comp.getWidth()+20;
					if(x>viewer.getWidth()){
						x=20;
						y+=20+comp.getHeight();
					}				
				}	
				
			}
			return;
		}
		
		PlotComponent vcomp=null;
		switch (method) {
		case 2:vcomp=new ProfilePlotComponent();			
			break;
		case 3:vcomp=(PlotComponent) new HeatMap().getComponent();			
			break;
		case 4:vcomp=new BoxPlotComponent();			
			break;			
		default:
			break;
		}
		
		CanvasComponent comp=((SuperModel)viewer.getModel()).addProbeLists(probeLists, vcomp);
		comp.setLocation(x,y);
		x+=comp.getWidth()+20;	
		viewer.addComponent(comp);	
			
	}
}