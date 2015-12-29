package mayday.graphviewer.plugins.misc;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.vis3.graph.model.GraphModelEvent;
import mayday.vis3.graph.model.GraphModelListener;

@SuppressWarnings("serial")
public class GraphOverviewPanel extends JPanel implements GraphModelListener {

	private GraphViewerPlot viewer;
	
	private JLabel nodesLabel;
	private JLabel edgesLabel;
	private JLabel groupsLabel;
	private JLabel probesLabel;
	
	public GraphOverviewPanel( GraphViewerPlot viewer) 
	{
		super(new GridLayout(4, 2));
		this.viewer=viewer;
		
		setBorder(BorderFactory.createTitledBorder("Graph Properties"));
		viewer.getModel().addGraphModelListener(this);
		nodesLabel=new JLabel("0");
		edgesLabel=new JLabel("0");
		groupsLabel=new JLabel("0");
		probesLabel=new JLabel("0");
		
		add(new JLabel("Nodes:"));
		add(nodesLabel);
		
		add(new JLabel("Edges:"));
		add(edgesLabel);
		
		add(new JLabel("Groups:"));
		add(groupsLabel);
		
		add(new JLabel("Probes:"));
		add(probesLabel);		
		
		init();
	}
	
	@Override
	public void graphModelChanged(GraphModelEvent event) 
	{
		init();
	}
	
	private void init()
	{
		SuperModel sm=(SuperModel)viewer.getModel();
		nodesLabel.setText(""+sm.getGraph().nodeCount());
		edgesLabel.setText(""+sm.getGraph().edgeCount());
		groupsLabel.setText(""+sm.getBags().size());
		probesLabel.setText(""+sm.getProbes().size());
	}
	
	@Override
	public void removeNotify() 
	{
		super.removeNotify();
		viewer.getModel().removeGraphModelListener(this);
	}

}
