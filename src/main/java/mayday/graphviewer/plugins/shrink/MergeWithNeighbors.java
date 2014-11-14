package mayday.graphviewer.plugins.shrink;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.SelectableHierarchicalSetting;
import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.components.NodeComponent;
import mayday.vis3.graph.model.GraphModel;

public class MergeWithNeighbors extends AbstractGraphViewerPlugin
{
	public void run(final GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		if(components.isEmpty())
		{
			return;
		}

		if(components.size()!=1)
		{
			JOptionPane.showMessageDialog(canvas, "Please select only the central node", "Merge with Neighbors", JOptionPane.ERROR_MESSAGE);
			return;
		}



		String[] scopes=new String[]{"All Neighbors","Out Neighbors","In Neighbors"};		
		SelectableHierarchicalSetting scopeSetting=new SelectableHierarchicalSetting("Merge with", null, 0, scopes);

		SettingDialog sd=new SettingDialog(canvas.getOutermostJWindow(), "Filter", scopeSetting);
		sd.showAsInputDialog();

		if(!sd.closedWithOK())
			return;

		NodeComponent core=(MultiProbeComponent)components.get(0);		
		Graph g=model.getGraph();

		Set<DataSet> ds=new HashSet<DataSet>();

		Set<Node> targets=null;
		switch(scopeSetting.getSelectedIndex())
		{
		case 0:	
			targets=g.getNeighbors(core.getNode());
			break;
		case 1:	
			targets=g.getOutNeighbors(core.getNode());
			break;
		case 2:	
			targets=g.getInNeighbors(core.getNode());
			break;	
		}

		Set<Probe> coreP=new HashSet<Probe>(((MultiProbeComponent) core).getProbes());

		// check for multiple ds
		for(Node tn:targets)
		{
			CanvasComponent cc=model.getComponent(tn);
			if(cc instanceof MultiProbeComponent)
			{				
				ds.add( ((MultiProbeComponent) cc).getProbes().get(0).getMasterTable().getDataSet());
			}
		}
		List<CanvasComponent> comps=new ArrayList<CanvasComponent>();
		comps.addAll(components);
		for(Node n:targets)
		{
			comps.add(model.getComponent(n));
		}
		
		DatasetConflictResolver resolver=new DatasetConflictResolver(ds, 
				((SuperModel)model).getProbes().iterator().next().getMasterTable().getDataSet(), 
				canvas, comps);				
		if(ds.size() > 1)
		{
			// can not join probes of different origin (may crash viewer)
			boolean contin=resolver.resolveConflict();
			if(!contin)
				return;
		}
		
		for(Node tn: targets)
		{
			if(tn==core.getNode())
				continue;
			CanvasComponent cc=model.getComponent(tn);
			if(cc instanceof MultiProbeComponent)
			{
				if( ((MultiProbeComponent) cc).getProbes().get(0).getMasterTable().getDataSet() != resolver.getSelectedDataSet())
					continue;
			}
			
		
			for(Node nn: g.getOutNeighbors(tn))
			{
				Edge template=g.getEdge(tn, nn);						
				model.connect(core, model.getComponent(nn),template);

			}
			for(Node nn: g.getInNeighbors(tn))
			{
				Edge template=g.getEdge(nn, tn);	
				model.connect(model.getComponent(nn),core,template);
			}
			// steal additional probes
			if(cc instanceof MultiProbeComponent && !resolver.isOmitProbes())
			{
				coreP.addAll(((MultiProbeComponent) cc).getProbes());
			}
			// remove comp.
			model.remove(cc);
		}
		((MultiProbeComponent) core).setProbes(coreP);
		((MultiProbeComponent) core).updateDisplayMode();
		canvas.revalidateEdges();		

	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.MergeWithNeighbors",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Combine the selected component with all neighboring components.",
				"Merge with Neighbors"				
		);
		pli.addCategory(SHRINK_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/mergewithneighbors.png");
		return pli;	

	}

	@SuppressWarnings("serial")
	public static class MergeAction extends AbstractAction{
		private GraphViewerPlot viewer;

		public MergeAction(GraphViewerPlot viewer) 
		{
			super("MergeWithNeighbors");
			this.viewer = viewer;
		}

		@Override
		public void actionPerformed(ActionEvent e) 
		{
			new MergeWithNeighbors().run(viewer, viewer.getModel(), viewer.getSelectionModel().getSelectedComponents());

		}
	}
}

