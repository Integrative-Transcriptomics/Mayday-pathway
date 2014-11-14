package mayday.graphviewer.plugins.extend;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.graph.nodes.Nodes;
import mayday.graphviewer.core.DefaultNodeComponent;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.NodeComponent;
import mayday.vis3.graph.model.GraphModel;
import mayday.vis3.graph.model.SummaryProbe;
import mayday.vis3.graph.model.SummaryProbeSetting;

public class AddSummaryNode extends AbstractGraphViewerPlugin
{
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		if(components.isEmpty()) return;
		
		SummaryProbeSetting sps=new SummaryProbeSetting();
		SettingDialog sd=new SettingDialog(null, "Summary Settings", sps);
		sd.setModal(true);
		sd.setVisible(true);
				
		MultiProbeNode n=new MultiProbeNode(model.getGraph());
		model.getGraph().addNode(n);
		for(CanvasComponent c:components)
		{
			Edge e=new Edge(((NodeComponent)c).getNode(), n);
			e.setName("Summary");
			model.getGraph().connect(e);			
		}
				
		SummaryProbe sp=new SummaryProbe(canvas.getModelHub().getViewModel().getDataSet().getMasterTable(),model.getGraph(),n,sps.getSummaryMode());
		sp.setName("Summary");
		sp.setWeightMode(sps.getWeightMode());
		sp.updateSummary();
		n.addProbe(sp);
		n.setRole(Nodes.Roles.PROBE_ROLE);
		n.setName("Summary");
		
		DefaultNodeComponent comp=((SuperModel)canvas.getModel()).addNode(n);
		Rectangle r=getBoundingRect(components);
		comp.setLocation(r.x+r.width/2, r.y+r.height/2);
		canvas.addComponent(comp);	
						
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.AddSummary",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Add a node showing a summary profile of the selected node. ",
				"Add Summary"				
		);
		pli.addCategory(EXTEND_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/summary.png");
		return pli;	
	}
}
