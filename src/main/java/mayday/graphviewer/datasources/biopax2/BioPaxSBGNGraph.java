package mayday.graphviewer.datasources.biopax2;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.algorithm.DepthFirstIterator;
import mayday.core.structures.graph.nodes.ContainerNode;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.maps.MultiHashMap;
import mayday.pathway.biopax.core.BioPaxDefaults;
import mayday.pathway.biopax.core.BiochemicalReaction;
import mayday.pathway.biopax.core.Catalysis;
import mayday.pathway.biopax.core.Complex;
import mayday.pathway.biopax.core.Control;
import mayday.pathway.biopax.core.DNA;
import mayday.pathway.biopax.core.Interaction;
import mayday.pathway.biopax.core.Modulation;
import mayday.pathway.biopax.core.Participant;
import mayday.pathway.biopax.core.PathwayStep;
import mayday.pathway.biopax.core.Protein;
import mayday.pathway.biopax.core.RNA;
import mayday.pathway.biopax.core.Reference;
import mayday.pathway.biopax.core.SmallMolecule;
import mayday.pathway.biopax.parser.MasterObject;
import mayday.pathway.sbgn.graph.AbstractSBGNPathwayGraph;
import mayday.pathway.sbgn.graph.SBGNNode;
import mayday.pathway.sbgn.processdiagram.ProcessDiagram;
import mayday.pathway.sbgn.processdiagram.process.Transition;

public class BioPaxSBGNGraph extends AbstractSBGNPathwayGraph 
{
	private Set<String> smallMolecules;
	private MultiHashMap<MultiProbeNode, Edge> controllerMap=new MultiHashMap<MultiProbeNode, Edge>();
	private Map<String, MultiProbeNode> idToChemical=new HashMap<String, MultiProbeNode>();
	private Map<MultiProbeNode, PathwayStep> transitionToStep=new HashMap<MultiProbeNode, PathwayStep>();
	private Map<PathwayStep, MultiProbeNode> stepToTransition=new HashMap<PathwayStep, MultiProbeNode>();
	private MultiHashMap<MultiProbeNode, PathwayStep> chemicalToTransition=new MultiHashMap<MultiProbeNode, PathwayStep>();
	private MultiHashMap<String, MultiProbeNode> cellularComponents=new MultiHashMap<String, MultiProbeNode>();
	private Graph backbone=new Graph();

	@SuppressWarnings("unchecked")
	public BioPaxSBGNGraph(MasterObject pathway) 
	{
		super(pathway);
		clear();
		reactionGraph.clear();	

		smallMolecules=BioPaxSqueezer2.getListOfSmallMolecules();

		name=pathway.getFirstValue("NAME");
		if(pathway.hasMember("ORGANISM"))
			organism=pathway.getMembers("ORGANISM").get(0).getFirstValue("NAME");

		Map<String, ContainerNode<PathwayStep>> stepMap=new HashMap<String, ContainerNode<PathwayStep>>();
		for(MasterObject s: pathway.getMembers("PATHWAY-COMPONENTS"))
		{
			PathwayStep stp=new PathwayStep(s);
			ContainerNode<PathwayStep> step=new ContainerNode<PathwayStep>(stepGraph,stp);
			step.setRole(ProcessDiagram.PROCESS_ROLE);
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
			MultiProbeNode t=createTransition(step);
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

	private MultiProbeNode createTransition(PathwayStep step)
	{
		// search for a biochemical reaction
		MultiProbeNode tRes=null;
		for(Interaction i:step.getInteractions())
		{
			if(i instanceof BiochemicalReaction)
			{
				tRes=new MultiProbeNode(this);
				tRes.setName(i.getName());
				tRes.setRole(ProcessDiagram.PROCESS_ROLE);	

				if(i.getComment()!=null)
					tRes.setProperty("Comment", i.getComment());
				if(i.getShortName()!=null)
					tRes.setProperty("Comment", i.getShortName());
				for(Reference ref: i.getReferences())
					tRes.setProperty(ref.getDatabase(), ref.getId());
				addNode(tRes);
				backbone.addNode(tRes);
				transitionToStep.put(tRes, step);
				addReactands((BiochemicalReaction)i, tRes);	
				return tRes;
			}
		}		
		return tRes;	
	}

	private void addReactands(BiochemicalReaction reaction, MultiProbeNode t)
	{
		for(Participant left:reaction.getLeft())
		{
			MultiProbeNode leftNode=null;
			if(idToChemical.containsKey(left.getEntity().getId()) && !smallMolecules.contains(left.getEntity().getName()))
			{
				leftNode=idToChemical.get(left.getEntity().getId());
				if(! backbone.contains(leftNode))
				{
					backbone.addNode(leftNode);					
					for(Node n: getOutNeighbors(leftNode))
					{
						if(n.getRole().equals(ProcessDiagram.PROCESS_ROLE))
						{
							backbone.connect(leftNode,n);
						}							
					}
					for(Node n: getInNeighbors(leftNode))
					{
						if(n.getRole().equals(ProcessDiagram.PROCESS_ROLE))
						{
							backbone.connect(n,leftNode);
						}
					}
				}
				backbone.connect(leftNode,t);
			}else
			{
				leftNode=createParticipantNode(left,this);			
				idToChemical.put(left.getEntity().getId(), leftNode);
				chemicalToTransition.put(leftNode, transitionToStep.get(t));
				if(smallMolecules.contains(left.getEntity().getName()))
				{
					leftNode.setProperty(ProcessDiagram.CLONE_MARKER_KEY, ProcessDiagram.CLONE_MARKER_PRESENT_VALUE);
				}				
				addNode(leftNode);
			}
			Edge e=new Edge(leftNode, t);
			e.setRole(ProcessDiagram.CONSUMPTION_ROLE);
			try{
				e.setWeight(Double.parseDouble(left.getStoichometricCoefficient()));
			}catch(Exception ex){} // tja. 
			connect(e);			
		}

		for(Participant right:reaction.getRight())
		{
			MultiProbeNode rightNode=null;
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
			}else
			{
				rightNode=createParticipantNode(right, this);
				idToChemical.put(right.getEntity().getId(), rightNode);
				if(smallMolecules.contains(right.getEntity().getName()))
				{
					rightNode.setProperty(ProcessDiagram.CLONE_MARKER_KEY, ProcessDiagram.CLONE_MARKER_PRESENT_VALUE);
				}					
				addNode(rightNode);
				chemicalToTransition.put(rightNode, transitionToStep.get(t));
			}

			Edge e=new Edge(t, rightNode);
			e.setRole(ProcessDiagram.PRODUCTION_ROLE);
			try{
				e.setWeight(Double.parseDouble(right.getStoichometricCoefficient()));
			}catch(Exception ex){} // tja. 
			connect(e);								
		}
	}

	private MultiProbeNode createParticipantNode(Participant p, Graph g)
	{
		MultiProbeNode node=new MultiProbeNode(g);
		node.setName(p.getEntity().getName());
		if(p.getComment()!=null)		
			node.setProperty("Comment", p.getComment());
		if(p.getCellularLocation()!=null)
			cellularComponents.put(p.getCellularLocation(), node);

		if(p.getEntity() instanceof SmallMolecule){
			node.setRole(ProcessDiagram.SIMPLE_CHEMICAL_ROLE);			
		}

		if(p.getEntity() instanceof Protein){
			node.setRole(ProcessDiagram.MACROMOLECULE_ROLE);			
		}

		if(p.getEntity() instanceof DNA || p.getEntity() instanceof RNA){
			node.setRole(ProcessDiagram.NUCLEIC_ACID_FEATURE_ROLE);			
		}

		if(p.getEntity() instanceof Complex ){
			node.setRole(ProcessDiagram.COMPLEX_ROLE);
		}

		if(p.getEntity().getName()!=null)
			node.setProperty(SBGNNode.NAME_ANNOTATION, p.getEntity().getName());
		
		if(p.getEntity().getShortName()!=null)
			node.setProperty(SBGNNode.SHORTNAME_ANNOTATION, p.getEntity().getShortName());
		
		for(String s: p.getEntity().getSynonyms())
		{
			node.setProperty(SBGNNode.SYNONYM_ANNOTATION, s);
		}
		
		if(p.getEntity().getComment()!=null)
			node.setProperty(SBGNNode.COMMENT_ANNOTATION, p.getEntity().getComment());		
		
		if(p.getCellularLocation()!=null)
			node.setProperty("Cellular Component", p.getCellularLocation());

		for(Reference r: p.getEntity().getReferences())
		{
			node.setProperty(r.getDatabase(), r.getId());
		}		
		return node;
	}

	private void addControls(PathwayStep step, MultiProbeNode t)
	{
		for(Interaction i:step.getInteractions())
		{
			if(i instanceof Control)
			{
				// iterate over controllers and add controllers to graph:
				for(Participant p:((Control)i).getController())
				{
					MultiProbeNode node=createParticipantNode(p, this);
					addNode(node);							
					Edge e=new Edge(node,t);
					e.setName(i.getName());

					if(i instanceof Catalysis)
					{
						e.setRole(ProcessDiagram.CATALYSIS_ROLE);
					}
					if(i instanceof mayday.pathway.biopax.core.Modulation)
					{
						String ct=((Modulation)i).getControlType();
						e.setRole(ProcessDiagram.MODULATION_ROLE);
						if(ct.startsWith(BioPaxSqueezer2.INHIBITION))
							e.setRole(ProcessDiagram.INHIBITION_ROLE);
						if(ct.startsWith(BioPaxSqueezer2.ACTIVATION))
							e.setRole(ProcessDiagram.NECESSARY_STIMULATION_ROLE);
					}
					controllerMap.put(node, t);
					connect(e);
				}
			}
		}
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

}
