package mayday.pathway.sbgn.graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.algorithm.DepthFirstIterator;
import mayday.core.structures.graph.nodes.ContainerNode;
import mayday.core.structures.maps.MultiHashMap;
import mayday.pathway.biopax.core.BioPaxDefaults;
import mayday.pathway.biopax.core.BiochemicalReaction;
import mayday.pathway.biopax.core.Control;
import mayday.pathway.biopax.core.Interaction;
import mayday.pathway.biopax.core.Participant;
import mayday.pathway.biopax.core.PathwayStep;
import mayday.pathway.biopax.parser.MasterObject;
import mayday.pathway.sbgn.processdiagram.arcs.Catalysis;
import mayday.pathway.sbgn.processdiagram.arcs.Consumption;
import mayday.pathway.sbgn.processdiagram.arcs.Modulation;
import mayday.pathway.sbgn.processdiagram.arcs.Production;
import mayday.pathway.sbgn.processdiagram.entitypool.EntityPoolNode;
import mayday.pathway.sbgn.processdiagram.entitypool.EntityPoolNodeFactory;
import mayday.pathway.sbgn.processdiagram.entitypool.unitsofinformation.Cardinality;
import mayday.pathway.sbgn.processdiagram.process.Transition;

public class SBGNPathwayGraph extends AbstractSBGNPathwayGraph
{
	private MultiHashMap<Node, Transition> controllerMap=new MultiHashMap<Node, Transition>();
	private Map<String, EntityPoolNode> idToChemical=new HashMap<String, EntityPoolNode>();
	private Map<Transition, PathwayStep> transitionToStep=new HashMap<Transition, PathwayStep>();
	private Map<PathwayStep, Transition> stepToTransition=new HashMap<PathwayStep, Transition>();
	private MultiHashMap<EntityPoolNode, PathwayStep> chemicalToTransition=new MultiHashMap<EntityPoolNode, PathwayStep>();
	private Set<String> smallMolecules=new HashSet<String>();
	private Graph backbone=new Graph();
	
	public SBGNPathwayGraph()
	{
		super();		
	}
	
	@SuppressWarnings("unchecked")
	public SBGNPathwayGraph(MasterObject pathway) 
	{
		super(pathway);
		clear();
		reactionGraph.clear();
		smallMolecules.add("H2O");
		smallMolecules.add("O2");
		smallMolecules.add("CO2");
		smallMolecules.add("ATP");
		smallMolecules.add("ADP");
		smallMolecules.add("H+");
		smallMolecules.add("CoA");
		smallMolecules.add("coenzyme A");
		smallMolecules.add("phosphate");
		smallMolecules.add("AMP");
		smallMolecules.add("NADH");
		smallMolecules.add("NADPH");
		smallMolecules.add("NADP+");
		smallMolecules.add("NAD+");
		smallMolecules.add("oxygen");
		smallMolecules.add("ammonia");
		smallMolecules.add("a reduced electron acceptor");
		smallMolecules.add("an oxidized electron acceptor");
		
		name=pathway.getFirstValue("NAME");
		if(pathway.hasMember("ORGANISM"))
			organism=pathway.getMembers("ORGANISM").get(0).getFirstValue("NAME");
	
		stepGraph=new Graph();
		
		Map<String, ContainerNode<PathwayStep>> stepMap=new HashMap<String, ContainerNode<PathwayStep>>();
		
//		Map<PathwayStep, Transition> stepToTransition=new HashMap<PathwayStep, Transition>();
		
		for(MasterObject s: pathway.getMembers("PATHWAY-COMPONENTS"))
		{
			PathwayStep stp=new PathwayStep(s);
			ContainerNode<PathwayStep> step=new ContainerNode<PathwayStep>(stepGraph,stp);
			step.setRole(BioPaxDefaults.STEP_ROLE);
			stepGraph.addNode(step);
			stepMap.put(s.getId(), step);	
		}
		
		for(MasterObject s: pathway.getMembers("PATHWAY-COMPONENTS"))
		{			
			for(MasterObject o:s.getMembers("NEXT-STEP"))
			{
				stepGraph.connect(stepMap.get(s.getId()), stepMap.get(o.getId()));
			}
		}
		
		DepthFirstIterator dfi=new DepthFirstIterator(stepGraph);
		while(dfi.hasNext())
		{
			ContainerNode<PathwayStep> n=(ContainerNode<PathwayStep>)dfi.next();
			PathwayStep step=n.getPayload();
			Transition t=createTransition(step);
			addControls(step, t);
			stepToTransition.put(step, t);
			reactionGraph.addNode(t);
		}
		
		for(Node n: stepGraph.getNodes())
		{
			PathwayStep step1=((ContainerNode<PathwayStep>)n).getPayload();
			for(Node m: stepGraph.getOutNeighbors(n))
			{
				PathwayStep step2=((ContainerNode<PathwayStep>)m).getPayload();
				reactionGraph.connect(stepToTransition.get(step1), stepToTransition.get(step2));
			}
		}
		cleanTransitionGraph();
	}
	
	private Transition createTransition(PathwayStep step)
	{
		// search for a biochemical reaction
		Transition tRes=null;
		for(Interaction i:step.getInteractions())
		{
			if(i instanceof BiochemicalReaction)
			{
				tRes=new Transition(this, i.getName());
				addNode(tRes);
				backbone.addNode(tRes);
				transitionToStep.put(tRes, step);
				addReactands((BiochemicalReaction)i, tRes);			
			}
		}		
		return tRes;	
	}
	
	private void cleanTransitionGraph()
	{
		// remove any feedback egdge
		for(Node n: reactionGraph.getNodes())
		{
			for(Node m: reactionGraph.getNodes())
			{
				if(reactionGraph.isConnected(n, m) && reactionGraph.isConnected(m, n))
				{
					reactionGraph.removeEdge(m, n);
				}
			}
		}
	}
	
	private void addControls(PathwayStep step, Transition t)
	{
		for(Interaction i:step.getInteractions())
		{
			if(i instanceof Control)
			{
				// iterate over controllers and add controllers to graph:
				for(Participant p:((Control)i).getController())
				{
					EntityPoolNode node=EntityPoolNodeFactory.createEntityPoolNode(p, this);
					addNode(node);							
					SBGNEdge rea=null;
					if(i instanceof mayday.pathway.biopax.core.Catalysis)
					{
						rea=new Catalysis(node,t);
//						System.out.println(((mayday.pathway.biopax.core.Catalysis) i).getDirection());
					}
					if(i instanceof mayday.pathway.biopax.core.Modulation)
					{
						rea=Modulation.createModulation((mayday.pathway.biopax.core.Modulation)i, node, t );
					}
					controllerMap.put(node, t);
					connect(rea);
				}
			}
		}
	}
	
	private void addReactands(BiochemicalReaction reaction, Transition t)
	{
		System.out.println(t.getName());
		for(Participant left:reaction.getLeft())
		{
			EntityPoolNode leftNode=null;
			System.out.println("Left: "+left.getEntity().getName());
			if(idToChemical.containsKey(left.getEntity().getId()) && !smallMolecules.contains(left.getEntity().getName()))
			{
				
				
				leftNode=idToChemical.get(left.getEntity().getId());
				if(! backbone.contains(leftNode))
				{
					backbone.addNode(leftNode);
					
					for(Node n: getOutNeighbors(leftNode))
					{
						if(n instanceof Transition)
						{
							backbone.connect(leftNode,n);
						}
							
					}
					for(Node n: getInNeighbors(leftNode))
					{
						if(n instanceof Transition)
						{
							backbone.connect(n,leftNode);
						}
					}
					

				}
				backbone.connect(leftNode,t);
//				backbone.connect(t,leftNode);
				
//				System.out.println(leftNode + " "+isAdjacent(chemicalToTransition.get(leftNode),  transitionToStep.get(t)  ) + "  "+chemicalToTransition.get(leftNode) );
//				if(! isAdjacent1(chemicalToTransition.get(leftNode),  transitionToStep.get(t)  ))
//				{
//					leftNode=EntityPoolNodeFactory.createEntityPoolNode(left, this);
//					leftNode.setCloneMarker(true);
//					idToChemical.put(left.getEntity().getId(), leftNode);
//					chemicalToTransition.put(leftNode, transitionToStep.get(t));
//					addNode(leftNode);
//				}
			}else
			{
				leftNode=EntityPoolNodeFactory.createEntityPoolNode(left, this);
				idToChemical.put(left.getEntity().getId(), leftNode);
				chemicalToTransition.put(leftNode, transitionToStep.get(t));
				if(smallMolecules.contains(left.getEntity().getName()))
					leftNode.setCloneMarker(true);
				addNode(leftNode);
			}
			Consumption c=new Consumption(leftNode,t);
			c.setCardinality(new Cardinality(Integer.parseInt(left.getStoichometricCoefficient())));
			
			connect(c);			
			
			
		}
		
		for(Participant right:reaction.getRight())
		{
			System.out.println("Right: "+right.getEntity().getName());
			EntityPoolNode rightNode=null;
			if(idToChemical.containsKey(right.getEntity().getId()) && !smallMolecules.contains(right.getEntity().getName()) )
			{				
				rightNode=idToChemical.get(right.getEntity().getId());
				if(! backbone.contains(rightNode))
				{
					backbone.addNode(rightNode);
					for(Node n: getOutNeighbors(rightNode))
					{
						if(n instanceof Transition)
							backbone.connect(rightNode,n);
					}
					for(Node n: getInNeighbors(rightNode))
					{
						if(n instanceof Transition)
							backbone.connect(n,rightNode);
					}
				}
				backbone.connect(t,rightNode);
//				backbone.connect(rightNode,t);
//				System.out.println(rightNode + " "+isAdjacent(chemicalToTransition.get(rightNode),  transitionToStep.get(t)  ) + "  "+chemicalToTransition.get(rightNode) );
//				if(! isAdjacent1(chemicalToTransition.get(rightNode),  transitionToStep.get(t)  ))
//				{
//					rightNode=EntityPoolNodeFactory.createEntityPoolNode(right, this);
//					rightNode.setCloneMarker(true);
//					idToChemical.put(right.getEntity().getId(),rightNode );
//					chemicalToTransition.put(rightNode, transitionToStep.get(t));
//					addNode(rightNode);
//				}
			}else
			{
				rightNode=EntityPoolNodeFactory.createEntityPoolNode(right, this);
				idToChemical.put(right.getEntity().getId(), rightNode);
				if(smallMolecules.contains(right.getEntity().getName()))
					rightNode.setCloneMarker(true);
				addNode(rightNode);
				chemicalToTransition.put(rightNode, transitionToStep.get(t));
			}
			
			Production c=new Production(t,rightNode);
			c.setCardinality(new Cardinality(Integer.parseInt(right.getStoichometricCoefficient())));			
			connect(c);
			
			
		}
		System.out.println("\n\n\n");
	}
		
}
