package mayday.graphviewer.plugins.shrink;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;

import mayday.core.Probe;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.graphviewer.core.DefaultNodeComponent;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.GraphModel;

public class MergeNodes extends AbstractGraphViewerPlugin
{
	private CanvasComponent lastResult;
	
	public void run(final GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		MultiProbeNode node=new MultiProbeNode(model.getGraph());
		SuperModel sm=(SuperModel)model;
		DefaultNodeComponent comp= sm.addNode(node);
		List<String> roles=new ArrayList<String>();
		Set<Probe> probes=new HashSet<Probe>();
		Graph g=sm.getGraph();
		StringBuffer sb=new StringBuffer();
		boolean first=true;
		int w=0;
		int h=0;
		for(CanvasComponent cc:components)
		{
			Node n=sm.getNode(cc);
			roles.add(n.getRole());
			w+=cc.getWidth();
			h+=cc.getHeight();
			for(Edge e: g.getInEdges(n))
			{
				e.setTarget(node);
				sm.connect(sm.getComponent(e.getSource()), comp, e);
			}
			for(Edge e: g.getOutEdges(n))
			{
				e.setSource(node);
				sm.connect(comp,sm.getComponent(e.getTarget()),e);
			}
			if(n instanceof DefaultNode)
			{
				node.getProperties().putAll(((DefaultNode) n).getProperties());
			}
			if(n instanceof MultiProbeNode)
			{
				for(Probe p:((MultiProbeNode) n).getProbes())
					probes.add(p);
			}
			if(first)
			{
				first=false;
				sb.append(cc.getLabel());
			}else
			{
				sb.append(",").append(cc.getLabel());
			}
			model.remove(cc);
//			cc.setVisible(false);			
		}
		for(Probe p:probes)
			node.addProbe(p);
		
		Map<String,Integer> roleCount=new HashMap<String,Integer>();
		for(String s: roles)
		{
			if(roleCount.containsKey(s))
				roleCount.put(s, roleCount.get(s)+1);
			else
				roleCount.put(s,1);
		}
		
		String maxRole=null;
		int max=0;
		for(String s: roleCount.keySet())
		{
			if(roleCount.get(s) > max)
			{
				max=roleCount.get(s);
				maxRole=s;
			}
		}
		if(sb.length() < 60)
			node.setName(sb.toString());
		else
			node.setName(components.size()+" joined Nodes");
		node.setRole(maxRole);
		Rectangle r=getBoundingRect(components);
		comp.setLocation(r.x+r.width/2, r.y+r.height/2);
		comp.setSize((int) ((1.0*w) /(1.0*components.size())), (int) ((1.0*h) /(1.0*components.size())));
		lastResult=comp;
		canvas.revalidateEdge(comp);
		((MultiProbeComponent)comp).updateDisplayMode();
	}
	
	public CanvasComponent getLastResult() {
		return lastResult;
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.MergeSelected",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Combine the selected nodes.",
				"Merge selected Nodes"				
		);
		pli.addCategory(SHRINK_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/merge.png");
		return pli;	

	}
	
	@SuppressWarnings("serial")
	public static class MergeNodesAction extends AbstractAction{
		private GraphViewerPlot viewer;

		public MergeNodesAction(GraphViewerPlot viewer) 
		{
			super("Merge Nodes");
			this.viewer = viewer;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			new MergeNodes().run(viewer, viewer.getModel(), viewer.getSelectionModel().getSelectedComponents());
			
		}
	}
}
