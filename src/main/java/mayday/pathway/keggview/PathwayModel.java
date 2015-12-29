package mayday.pathway.keggview;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import mayday.core.Probe;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Graphs;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.maps.MultiTreeMap;
import mayday.pathway.keggview.pathways.graph.CompoundNode;
import mayday.pathway.keggview.pathways.graph.GeneNode;
import mayday.pathway.keggview.pathways.graph.MapNode;
import mayday.pathway.keggview.pathways.graph.PathwayGraph;
import mayday.pathway.keggview.pathways.graph.PathwayNode;
import mayday.pathway.keggview.pathways.gui.canvas.CompoundComponent;
import mayday.pathway.keggview.pathways.gui.canvas.GeneComponent;
import mayday.pathway.keggview.pathways.gui.canvas.MapComponent;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.GraphModel;
import mayday.vis3.graph.model.GraphWithProbeModel;


public class PathwayModel extends GraphModel implements GraphWithProbeModel
{
	private MultiTreeMap<Probe, MultiProbeComponent> probeToComponent;
	
	public PathwayModel(PathwayGraph graph)
	{
		super(graph);
		probeToComponent=new MultiTreeMap<Probe, MultiProbeComponent>();
		for(Node n:graph.getNodes())
		{
			CanvasComponent comp=null;
			if(n instanceof GeneNode)
			{
				comp=new GeneComponent((GeneNode)n,graph);

				//((GeneComponent)comp).setViewModel(viewModel);
	
			}
			if(n instanceof CompoundNode)
			{
				comp=new CompoundComponent((CompoundNode)n,graph);
				//((CompoundComponent)comp).setViewModel(viewModel);
			}
			if(n instanceof MapNode)
			{
				comp=new MapComponent((MapNode)n,graph);
//				((MapComponent)comp).setModel(this);
//				comp=new ImageDecorator(comp,pathwayIcon);
			}
			for(Probe p:((MultiProbeNode) n).getProbes())
			{
					probeToComponent.put(p, comp);
			}
			addComponent(comp);	
			getNodeMap().put(comp, n);
			getComponentMap().put(n, comp);
		}
		Collections.sort(getComponents());
	}
	
	public PathwayModel(PathwayGraph graph, Map<String,Probe> probeMapping)
	{
		super(graph);
		probeToComponent=new MultiTreeMap<Probe, MultiProbeComponent>();
		for(Node n:graph.getNodes())
		{
			CanvasComponent comp=null;
			if(n instanceof GeneNode)
			{
				comp=new GeneComponent((GeneNode)n,graph);

				//((GeneComponent)comp).setViewModel(viewModel);
	
			}
			if(n instanceof CompoundNode)
			{
				comp=new CompoundComponent((CompoundNode)n,graph);
				//((CompoundComponent)comp).setViewModel(viewModel);
			}
			if(n instanceof MapNode)
			{
				comp=new MapComponent((MapNode)n,graph);
//				((MapComponent)comp).setModel(this);
//				comp=new ImageDecorator(comp,pathwayIcon);
			}
			for(Probe p:((MultiProbeNode) n).getProbes())
			{
					probeToComponent.put(p, comp);
			}
			addComponent(comp);	
			getNodeMap().put(comp, n);
			getComponentMap().put(n, comp);
		}
		Collections.sort(getComponents());
	}
	
	@Override
	protected void init() 
	{
		clear();
		
	}
	
	public void applyImportance(int cutoff)
	{
		for(Node n:getGraph().getNodes())
		{
			int imp=((PathwayNode)n).getImportance(getGraph());
			if( imp < cutoff  )
			{
				getComponent(n).setVisible(false);
			}else
			{
				getComponent(n).setVisible(true);
			}
		}
	}
	
	public String getTitle()
	{
		return ((PathwayGraph)getGraph()).getTitle();
	}
	
	public String getName()
	{
		return ((PathwayGraph)getGraph()).getName();
	}
	
	public PathwayGraph getPathwayGraph()
	{
		return (PathwayGraph)getGraph();
	}
	
	public List<MultiProbeComponent> getComponents(Probe probe)
	{
		return probeToComponent.get(probe);
	}
	
	@Override
	public GraphModel buildSubModel(List<Node> selectedNodes) 
	{
		Graph g=Graphs.restrict(getGraph(), selectedNodes);
		PathwayModel m=new PathwayModel((PathwayGraph) g);
		return m;
	}

}
