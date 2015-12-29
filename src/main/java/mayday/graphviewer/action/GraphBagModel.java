package mayday.graphviewer.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mayday.core.Probe;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Graphs;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.graphviewer.core.bag.BagComponent;
import mayday.graphviewer.core.bag.ComponentBag;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModelEvent;
import mayday.vis3.graph.model.ProbeGraphModel;
import mayday.vis3.graph.model.GraphModelEvent.GraphModelChange;

public class GraphBagModel extends ProbeGraphModel
{
	protected Set<ComponentBag> bags=new HashSet<ComponentBag>();
	protected Map<ComponentBag, BagComponent> bagToComponent=new HashMap<ComponentBag, BagComponent>();
	protected Map<BagComponent, ComponentBag> componentToBag=new HashMap<BagComponent, ComponentBag>();
	
	public GraphBagModel()
	{
		super();
		setGraph(new Graph());		
	}
	
	public GraphBagModel(Graph g)
	{
		super(g);
	}
	
	public GraphBagModel(Set<Probe> probes)
	{
		Graph graph=new Graph();
		for(Probe p:probes)
		{
			MultiProbeNode n=new MultiProbeNode(graph,p);
			graph.addNode(n);
		}
		setGraph(graph);		
		init();
	}
	
	public void clearAll()
	{
		bags.clear();
		List<CanvasComponent> bcs=new ArrayList<CanvasComponent>(componentToBag.keySet());
		fireEvent(new GraphModelEvent(GraphModelChange.ComponentsRemoved,bcs));	
		bagToComponent.clear();
		componentToBag.clear();
		
		super.clearAll();
	}
	
	public void addBag(ComponentBag bag)
	{
		bags.add(bag);
//		BagComponent comp=new BagComponent(bag);
		BagComponent comp=new BagComponent(bag);
		componentToBag.put(comp, bag);
		bagToComponent.put(bag, comp);
		addComponent(comp);
//		fireEvent(new GraphModelEvent(GraphModelChange.ComponentsAdded,comp));		
	}
	
	public void remove(ComponentBag bag)
	{
		BagComponent comp=bagToComponent.get(bag);
		remove(comp);
		bagToComponent.remove(bag);
		componentToBag.remove(comp);	
		bags.remove(bag);
		fireEvent(new GraphModelEvent(GraphModelChange.ComponentsRemoved,comp));	
	}
	
	public Set<ComponentBag> getBags()
	{
		return bags;
	}
	
	public BagComponent getComponent(ComponentBag bag)
	{
		return bagToComponent.get(bag);
	}
	
	@Override
	public GraphBagModel buildSubModel(List<Node> selectedNodes) 
	{
		Graph g=Graphs.restrict(getGraph(), selectedNodes);
		GraphBagModel m=new GraphBagModel(g);
		for(ComponentBag bag:bags)
		{
			ComponentBag bag2=new ComponentBag(this);
			bag2.setName(bag.getName());
			bag2.setColor(bag.getColor());
			for(CanvasComponent comp:bag)
			{
				if(g.contains(getNode(comp)))
				{
					bag2.addComponent(m.getComponent(getNode(comp)));
				}				
			}
			if(!bag2.isEmpty())
			{
				m.addBag(bag2);
			}
		}
		return m;
	}
	
	
	
	
}
