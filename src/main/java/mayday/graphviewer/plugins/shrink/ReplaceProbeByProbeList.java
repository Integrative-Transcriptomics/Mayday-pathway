package mayday.graphviewer.plugins.shrink;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.core.DefaultNodeComponent;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.components.NodeComponent;
import mayday.vis3.graph.model.GraphModel;

public class ReplaceProbeByProbeList extends AbstractGraphViewerPlugin
{

	public void run(final GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		if(components.isEmpty())
		{
			return;
		}
		MultiHashMap<ProbeList,CanvasComponent> probeListMap=new MultiHashMap<ProbeList, CanvasComponent>();
		for(CanvasComponent cc: components)
		{
			if(cc instanceof MultiProbeComponent)
			{
				for(Probe p: ((MultiProbeComponent) cc).getProbes())
				{
					probeListMap.put(canvas.getModelHub().getViewModel(p.getMasterTable().getDataSet()).getTopPriorityProbeList(p), cc);
				}
			}
		}	
		

		
		Graph g=model.getGraph();
		for(ProbeList pl: probeListMap.keySet())
		{
			Set<CanvasComponent> comps=new HashSet<CanvasComponent>(probeListMap.get(pl));
			if(comps.size() > 1)
			{
				Iterator<CanvasComponent> iter=comps.iterator();
				CanvasComponent coreTemplate=iter.next();

				DefaultNodeComponent core=((SuperModel)model).addProbeListNode(pl);
				core.setLocation(coreTemplate.getLocation());
				core.setSize(coreTemplate.getSize());
				
				iter=comps.iterator();
				while(iter.hasNext())
				{
					CanvasComponent cc=iter.next();
					Node n=((NodeComponent) cc).getNode();
					
					for(Node nn: g.getOutNeighbors(n))
					{
						Edge template=g.getEdge(n, nn);						
						model.connect(core, model.getComponent(nn),template);
						
					}
					for(Node nn: g.getInNeighbors(n))
					{
						Edge template=g.getEdge(nn, n);	
						model.connect(model.getComponent(nn),core,template);
					}
					// remove comp.
					model.remove(cc);					
				}				
			}
		}		
		canvas.revalidateEdges();		
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.ReplaceProbeByProbeList",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Replace probes by thier top priority probe list",
				"Replace Probes with their ProbeLists"				
		);
		pli.addCategory(SHRINK_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/replacebyprobelist.png");
		return pli;	

	}
}
