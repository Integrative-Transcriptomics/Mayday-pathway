package mayday.graphviewer.plugins.arrange;

import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import mayday.core.gui.MaydayDialog;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class RowsAndColumns extends AbstractGraphViewerPlugin 
{
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		if(components.isEmpty())
			return;
		// request rows and columns and distance 
		RowsAndColumnsDialog rcd=new RowsAndColumnsDialog(components.size());
		rcd.setModal(true);
		rcd.setVisible(true);
		Rectangle rect=getBoundingRect(components);
		int vspace=rect.y;
		int c=0;
		for(int i=0; i!=(Integer)rcd.rowsModel.getValue();++i)
		{
			int hspace=rect.x;
			int vLocaLMax=0;
			for(int j=0; j!=(Integer)rcd.colsModel.getValue(); ++j)
			{
				if(c >= components.size())
					return; // fertig. 
				components.get(c).setLocation(hspace, vspace);
				hspace+=components.get(c).getWidth();
				hspace+=(Integer)rcd.colDistModel.getValue();
				if(components.get(c).getHeight() > vLocaLMax)
					vLocaLMax= components.get(c).getHeight();
				++c;				
			}
			vspace+=vLocaLMax;
			vspace+=(Integer)rcd.rowDistModel.getValue();
		}		
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.RowsAndColumns",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Arrange the Nodes in rows and columns",
				"Rows and Columns"				
		);
		pli.addCategory(ARRANGE_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/rowscolumns.png");
		return pli;	
	}
	
	@SuppressWarnings("serial")
	private class RowsAndColumnsDialog extends MaydayDialog
	{
		SpinnerNumberModel rowsModel; 
		SpinnerNumberModel colsModel; 
		SpinnerNumberModel rowDistModel; 
		SpinnerNumberModel colDistModel;
		
		public RowsAndColumnsDialog(int numObject) 
		{
			int initRows=(int) Math.ceil(Math.sqrt(numObject));
			rowsModel=new SpinnerNumberModel(initRows, 1, numObject, 1);
			colsModel=new SpinnerNumberModel(initRows, 1, numObject, 1);
			
			rowDistModel=new SpinnerNumberModel(15, 0, 1000, 1);
			colDistModel=new SpinnerNumberModel(15, 0, 1000, 1);
			
			setTitle("Rows and Columns");
			
			setLayout(new GridLayout(5, 2,25,15));
			
			add(new JLabel("Rows:"));
			add(new JSpinner(rowsModel));
			
			add(new JLabel("Columns:"));
			add(new JSpinner(colsModel));
			
			add(new JLabel("Vertical Space:"));
			add(new JSpinner(rowDistModel));
			
			add(new JLabel("Horizontal Space:"));
			add(new JSpinner(colDistModel));
			
			add(new JLabel());
			add(new JButton(new OkAction()));
			pack();
		}
		
		public class OkAction extends AbstractAction
		{
			public OkAction() {
				super("Ok");
			}
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		}
	}
}
