package mayday.graphviewer.core;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;
import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Graphs;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.edges.Edges;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.core.structures.graph.nodes.MIONode;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.action.GraphBagModel;
import mayday.graphviewer.core.bag.ComponentBag;
import mayday.pathway.sbgn.processdiagram.ProcessDiagram;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.GraphModelEvent;
import mayday.vis3.graph.model.GraphModelEvent.GraphModelChange;
import mayday.vis3.graph.vis3.Vis3Component;
import mayday.vis3.gui.PlotComponent;
import mayday.vis3.plots.profile.ProfilePlotComponent;

public class SuperModel extends GraphBagModel
{
	public SuperModel()
	{
		super();
	}

	public SuperModel(Graph graph)
	{
		setGraph(graph);
		init();
	}

	protected void init()
	{
		clear();
		for(Node n:getGraph().getNodes())
		{
			DefaultNodeComponent comp=new DefaultNodeComponent((MultiProbeNode)n);
			addComponent(comp);	
			getNodeMap().put(comp, n);
			getComponentMap().put(n, comp);
			if(n instanceof MultiProbeNode )
			{
				for(Probe p: ((MultiProbeNode)n).getProbes())
				{
					probeToComponent.put(p, comp);
				}
			}
		}
		Collections.sort(getComponents());	
	}
	
	public Vis3Component addProbeList(ProbeList pl)
	{
		return addProbeList(pl, new ProfilePlotComponent());
	}
	
	public Vis3Component addProbeList(ProbeList pl, PlotComponent vcp)
	{
		MultiProbeNode n=new MultiProbeNode(getGraph(),pl);
		getGraph().addNode(n);
		
		Vis3Component comp=new Vis3Component(n,vcp);
		
		addComponent(comp);	
		getNodeMap().put(comp, n);
		getComponentMap().put(n, comp);	

		for(Probe p:pl)
		{
			probeToComponent.put(p, comp);
		}
		return comp;
	}
	
	public Vis3Component addProbeLists(List<ProbeList> pls, PlotComponent vcp)
	{
		ProbeList pl=ProbeList.createUniqueProbeList(pls);
		MultiProbeNode n=new MultiProbeNode(getGraph(),pl);
		StringBuffer sb=new StringBuffer();
		for(ProbeList p: pls)
		{
			sb.append(p.getName());
			sb.append(", ");
		}
		sb.delete(sb.length()-2, sb.length()-1);
		n.setName(sb.toString());
		getGraph().addNode(n);

		Vis3Component comp=new Vis3Component(n,pls,vcp);
		
		addComponent(comp);	
		getNodeMap().put(comp, n);
		getComponentMap().put(n, comp);	

		for(Probe p:pl)
		{
			probeToComponent.put(p, comp);
		}
		return comp;
	}

	public DefaultNodeComponent addProbeListNode(ProbeList pl)
	{
		MultiProbeNode n=new MultiProbeNode(getGraph(),pl);
		getGraph().addNode(n);

		DefaultNodeComponent comp=new DefaultNodeComponent(n);
		
		addComponent(comp);	
		getNodeMap().put(comp, n);
		getComponentMap().put(n, comp);	

		for(Probe p:pl)
		{
			probeToComponent.put(p, comp);
		}
		
		return comp;
	}

	public DefaultNodeComponent addProbe(Probe p)
	{
		MultiProbeNode n=new MultiProbeNode(getGraph(),p);

		getGraph().addNode(n);

		DefaultNodeComponent comp=new DefaultNodeComponent(n);

		addComponent(comp);	
		getNodeMap().put(comp, n);
		getComponentMap().put(n, comp);			
		probeToComponent.put(p, comp);
		return comp;
	}
	
	public List<CanvasComponent> addProbes(Iterable<Probe> probes)
	{
		List<CanvasComponent> res=new ArrayList<CanvasComponent>();
		for(Probe p:probes)
		{
			MultiProbeNode n=new MultiProbeNode(getGraph(),p);	
			getGraph().addNode(n);	
			DefaultNodeComponent comp=new DefaultNodeComponent(n);
	
			getNodeMap().put(comp, n);
			getComponentMap().put(n, comp);			
			probeToComponent.put(p, comp);	
			res.add(comp);
		}
		addComponent(res);
		return res;
	}

	public Map<MIType, DefaultNodeComponent> addMIGroup(MIGroup grp, Collection<Probe> probes)
	{
		MultiHashMap<MIType, Probe> probeMap=new MultiHashMap<MIType, Probe>();
		for(Probe p:probes)
		{
			if(grp.contains(p))
			{
				probeMap.put(grp.getMIO(p), p);
			}
		}
		Map<MIType, DefaultNodeComponent> res= new HashMap<MIType, DefaultNodeComponent>();
		
		for(MIType m: probeMap.keySet())
		{
			DefaultNodeComponent comp=addMIO(grp, probeMap.get(m));	
			res.put(m,comp);
		}		
		return res;
	}
	
	public Map<String, DefaultNodeComponent> addAndSummarizeMIGroup(MIGroup grp, Collection<Probe> probes)
	{
		
		MultiHashMap<String, Probe> probeMap=new MultiHashMap<String, Probe>();
		
		for(Probe p:probes)
		{
			if(grp.contains(p))
			{
				probeMap.put(grp.getMIO(p).toString(), p);
			}
		}		
		Map<String, DefaultNodeComponent> res= new HashMap<String, DefaultNodeComponent>();		
		for(String s: probeMap.keySet())
		{
			DefaultNodeComponent comp=addMIO(grp, probeMap.get(s));	
			res.put(s,comp);
		}		
		return res;
	}
	
	public DefaultNodeComponent addMIO(MIGroup grp, Probe pl)
	{
		MIONode n=new MIONode(getGraph(),pl);		
		n.setMiGroup(grp);
		
		return addNode(n);	
	}
	
	public DefaultNodeComponent addMIO(MIGroup grp, List<Probe> pl)
	{
		MIONode n=new MIONode(getGraph(),pl);		
		n.setMiGroup(grp);
		
		return addNode(n);	
	}
	
	public Edge connect(CanvasComponent comp1, CanvasComponent comp2)
	{
		Edge e=super.connect(comp1, comp2);
		e.setRole(ProcessDiagram.guessEdgeRole(e.getSource().getRole(), e.getTarget().getRole()));
		return e;
	}

	public void connect(MultiProbeComponent comp1, MultiProbeComponent comp2, DistanceMeasurePlugin distance)
	{
		if(comp1.getProbes().isEmpty() || comp2.getProbes().isEmpty()) 
		{
			connect(comp1,comp2);
			return; 
		}
		DoubleVector vec1=new DoubleVector(comp1.getProbes().get(0).getNumberOfExperiments());
		DoubleVector vec2=new DoubleVector(comp1.getProbes().get(0).getNumberOfExperiments());
		for(int i=0; i!= comp1.getProbes().get(0).getNumberOfExperiments(); ++i)
		{
			for(Probe p:comp1.getProbes())
			{
				vec1.set(i,vec1.get(i)+p.getValue(i));
			}
		}
		vec1.divide(comp1.getProbes().size());

		for(int i=0; i!= comp2.getProbes().get(0).getNumberOfExperiments(); ++i)
		{
			for(Probe p:comp2.getProbes())
			{
				vec2.set(i,vec2.get(i)+p.getValue(i));
			}
		}
		vec2.divide(comp2.getProbes().size());

		double w=distance.getDistance(vec1, vec2);
		Edge e=new Edge(getNode(comp1),getNode(comp2));
		e.setRole(Edges.Roles.EDGE_ROLE);
		e.setWeight(w);
		e.setName(NumberFormat.getNumberInstance().format(w));	
		getGraph().connect(e);
		fireEvent(new GraphModelEvent(GraphModelChange.EdgeAdded));
	}

	public DefaultNodeComponent addNode(MultiProbeNode node)
	{
//		DefaultNodeComponent comp=addNode((DefaultNode)node);
		getGraph().addNode(node);
		DefaultNodeComponent comp=new DefaultNodeComponent(node);
		addComponent(comp);
		getNodeMap().put(comp, node);
		getComponentMap().put(node, comp);
		for(Probe p:node.getProbes())
		{
			probeToComponent.put(p, comp);
		}			
		return comp; 
	}
	public DefaultNodeComponent addNode(DefaultNode node)
	{
		getGraph().addNode(node);
		DefaultNodeComponent comp=new DefaultNodeComponent(node);
		addComponent(comp);
		getNodeMap().put(comp, node);
		getComponentMap().put(node, comp);
		return comp; 
	}
	
	public void addModel(SuperModel other)
	{
		this.getGraph().add(other.getGraph());
		components.addAll(other.components);
		nodeMap.putAll(other.nodeMap);
		componentMap.putAll(other.componentMap);
		bags.addAll(other.bags);
		bagToComponent.putAll(other.bagToComponent);
		componentToBag.putAll(other.componentToBag);
		probeToComponent.putAll(other.probeToComponent);
		fireEvent(new GraphModelEvent(GraphModelChange.AllComponentsChanged));
	}
	
	@Override
	public SuperModel buildSubModel(List<Node> selectedNodes) 
	{
		Iterator<Node> iter=selectedNodes.iterator();
		while(iter.hasNext())
		{
			Node n=iter.next();
			if(n==null)
				iter.remove();
		}
		Graph g=Graphs.restrict(getGraph(), selectedNodes);
		SuperModel m=new SuperModel(g);
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
