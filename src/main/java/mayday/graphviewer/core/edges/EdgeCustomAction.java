package mayday.graphviewer.core.edges;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.core.settings.SettingDialog;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.vis3.graph.edges.EdgeSetting;

@SuppressWarnings("serial")
public class EdgeCustomAction extends AbstractAction
{
	private GraphViewerPlot canvas;
	
	public EdgeCustomAction(GraphViewerPlot c) 
	{
		super("Custom style");
		canvas=c;
	} 
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		EdgeSetting originalEdgeSetting= canvas.getEdgeDispatcher().getSetting(canvas.getHighlightEdge());
			
		EdgeSetting newSetting=originalEdgeSetting.clone();
		
		SettingDialog sd=new SettingDialog(null, "Customize Edge", newSetting);
		sd.showAsInputDialog();
		
		if(sd.closedWithOK())
		{
			 canvas.getEdgeDispatcher().putEdgeSetting(canvas.getHighlightEdge(), newSetting);
		}
		
	}
	
	
}
