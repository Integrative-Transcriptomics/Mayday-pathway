package mayday.graphviewer.plugins.shrink;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.nodes.Nodes;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.GraphModel;

public class JoinProbes  extends AbstractGraphViewerPlugin
{
	@Override
	public void run(final GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		MultiHashMap<DataSet, Probe> probes=new MultiHashMap<DataSet, Probe>();
		for(CanvasComponent comp:components)
		{
			if(! (comp instanceof MultiProbeComponent))
				continue;
			for(Probe p:((MultiProbeComponent)comp).getProbes())
			{
				probes.put(p.getMasterTable().getDataSet(), p);
			}
		}
		SuperModel sm=((SuperModel)model);
		List<CanvasComponent> results=new ArrayList<CanvasComponent>();
		for(DataSet ds: probes.keySet())
		{
			ProbeList pl=new ProbeList(ds,false);
			StringBuffer sb=new StringBuffer();
			if(probes.size() > 1)
				sb.append(ds.getName());
			int i=0;
			for(Probe p: probes.get(ds))
			{
				pl.addProbe(p);
				if(i < 3)
					sb.append(" "+p.getDisplayName());
				
			}
			if(probes.get(ds).size() > 4)
					sb.append("...");
			pl.setName(sb.toString());
			CanvasComponent comp=sm.addProbeListNode(pl);
			((MultiProbeComponent)comp).getNode().setRole(Nodes.Roles.PROBES_ROLE);
			results.add(comp);
		}
		
		placeComponents(results, canvas, 15, 20);
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.JoinProbes",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Join the Probes: Form a new node that conains all probes associated with the selected nodes.",
				"Join Probes"				
		);
		pli.setIcon("mayday/pathway/gvicons/joinprobes.png");
		pli.addCategory(SHRINK_CATEGORY);
		return pli;	

	}
	
	@SuppressWarnings("serial")
	public static class JoinAction extends AbstractAction{
		private GraphViewerPlot viewer;

		public JoinAction(GraphViewerPlot viewer) 
		{
			super("Join Probes");
			this.viewer = viewer;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			new JoinProbes().run(viewer, viewer.getModel(), viewer.getSelectionModel().getSelectedComponents());
			
		}
	}
	
}
