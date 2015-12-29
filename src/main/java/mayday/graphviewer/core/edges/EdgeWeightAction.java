package mayday.graphviewer.core.edges;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.core.settings.SettingDialog;
import mayday.core.settings.typed.DoubleSetting;
import mayday.graphviewer.core.GraphViewerPlot;

@SuppressWarnings("serial")
public class EdgeWeightAction extends AbstractAction
{
	private GraphViewerPlot canvas;
	
	public EdgeWeightAction(GraphViewerPlot c) 
	{
		super("Set Weight");
		canvas=c;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		double w=canvas.getHighlightEdge().getWeight();
		
		DoubleSetting setting=new DoubleSetting("Weight", "The weight of the edge: any real number" , w);
		SettingDialog dialog=new SettingDialog(canvas.getOutermostJWindow(), "New Edge weight", setting);
		dialog.showAsInputDialog();
		if(dialog.closedWithOK())
		{
			canvas.getHighlightEdge().setWeight(setting.getDoubleValue());
		}
		
	}
}
