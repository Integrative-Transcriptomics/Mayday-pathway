package mayday.graphviewer.plugins.misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import mayday.core.gui.MaydayDialog;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.algorithm.FloydWarshall;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class DistanceMatrixPlugin extends AbstractGraphViewerPlugin 
{
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		//calculate distance map:
		FloydWarshall fw=new FloydWarshall(model.getGraph());
		FWTableModel tmodel=new FWTableModel(new ArrayList<Node>(model.getGraph().getNodes()), fw);
		JTable table=new JTable(tmodel);
		table.getSelectionModel().addListSelectionListener(new FWTableListener(canvas, table, fw));
		MaydayDialog frame=new MaydayDialog(canvas.getOutermostJWindow(),"Distance Matrix");
		frame.add(new JScrollPane(table));
		frame.pack();
		frame.setVisible(true);
	}
	
	private class FWTableListener implements ListSelectionListener
	{
		private GraphViewerPlot viewer;
		private JTable table;
		private FloydWarshall fw;
				
		public FWTableListener(GraphViewerPlot viewer, JTable table,FloydWarshall fw) {
			this.viewer = viewer;
			this.table = table;
			this.fw=fw;			
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
						
			SuperModel m=(SuperModel) viewer.getModel();
			viewer.getSelectionModel().clearSelection();
					
			int c=table.getSelectedColumn();
			int r=table.getSelectedRow();
			if(c==0)
				return;
			System.out.println(r +"\t"+c);
			
			Node s=(Node)table.getValueAt(r, 0);
			Node t=(Node)table.getValueAt(c-1, 0);
			System.out.println(s +"\t"+t);
			if(fw.getShortestDistance(s, t) == Double.MAX_VALUE)
				return;
			
			List<Node> path=fw.getShortestPath(s, t);
//			path.add(t);
			System.out.println(path);
			if(path.isEmpty()){
				path.add(s);
				path.add(t);
			}
			
			
			for(Node n:path)
			{
				viewer.getSelectionModel().select(m.getComponent(n));
			}
			

			
		}
	}
	
	@SuppressWarnings("serial")
	private class FWTableModel extends DefaultTableModel
	{
		private List<Node> nodes;
		private FloydWarshall fw;
		
		public FWTableModel(List<Node> nodes, FloydWarshall fw) {
			this.nodes = nodes;
			this.fw = fw;
		}
		
		@Override
		public int getRowCount() 
		{
			if(nodes==null)
				return 0;	
			return nodes.size();
		}
		
		@Override
		public int getColumnCount() 
		{	
			if(nodes==null)
				return 0;			
			return nodes.size()+1;
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) 
		{
			if(columnIndex==0)
				return String.class;
			return Double.class;
		}
		
		@Override
		public Object getValueAt(int row, int column) {
			
			if(column==0)
			{
				return nodes.get(row);
			}
			
			double d=fw.getShortestDistance(nodes.get(row),nodes.get(column-1) );		
			if(d==Double.MAX_VALUE)
				return null;
			return d;	
		}
		
		@Override
		public String getColumnName(int column) {
			if(nodes==null)
				return null;
			if(column==0)
				return "";
			return nodes.get(column-1).getName();
		}
		
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.DistanceMatrix",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Show a list of graph components",
				"Distance Matrix"				
		);		
//		pli.setIcon("mayday/pathway/gvicons/inspect_graph.png");
		return pli;	
	}
	
	
}
