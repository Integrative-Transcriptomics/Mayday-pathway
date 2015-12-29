package mayday.graphviewer.illumina;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mayday.core.Probe;
import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.edges.Edges;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.core.structures.maps.MultiHashMap;
import mayday.genetics.basic.Strand;
import mayday.genetics.coordinatemodel.GBAtom;

public class GeneModelGraphFactory 
{

//	public Graph buildOverallGraph(GFFGeneModelParser parser, List<GBAtom> models, MultiHashMap<GBAtom, Probe> probes, GeneModelStyle style)
//	{
//		// go over all genes:
//		Graph graph=new Graph();
//		int c=0; 
//		for(GBAtom gene: models)
//		{
//			//if there are any mrnas for this gene, proceed with those
//			String s=parser.getGeneToID().get(gene);
//			if(parser.getGeneTomRNAs().containsKey(s))
//			{
//				MultiHashMap<String, GBAtom> geneModels=new MultiHashMap<String, GBAtom>();				
//				for(String mRNA: parser.getGeneTomRNAs().get(s))
//				{
//					System.out.println(mRNA);
//					for(GBAtom exon: parser.getExons().get(mRNA))
//						geneModels.put(mRNA, exon);
//				}
//				c+=geneModels.size_everything();
//				System.out.println(c);
//				Graph modelGraph=buildGraph(geneModels, probes, style);		
//				graph.add(modelGraph);
//			}else
//			{
//				if(parser.getExons().containsKey(s) )
//				{
//					MultiHashMap<String, GBAtom> geneModels=new MultiHashMap<String, GBAtom>();	
//					for(GBAtom exon: parser.getExons().get(s))
//						geneModels.put(s, exon);
//					Graph modelGraph=buildGraph(geneModels, probes, style);
//					graph.add(modelGraph);
//				}else
//				{
//					GeneticNode geneNode=new GeneticNode(graph, gene);
//					geneNode.setProperty(GeneticNode.MODEL_NAME, s);
//					geneNode.setProperty("parent", s);
//					graph.addNode(geneNode);
//				}
//			}
//
//		}
//		return graph;
//	}


	public Graph buildGraph(MultiHashMap<String, GBAtom> models, MultiHashMap<GBAtom, Probe> probes, Map<String, Probe> isoformProbes, GeneModelStyle style)
	{		
		Graph resultGraph=null;
		switch(style)
		{
		case COMPRESSED:
			resultGraph= buildCompressedGraph(models,probes, isoformProbes);
			break;
		case CONDENSED:
			resultGraph= buildCondensedGraph(models,probes);
			break;
		case VERBOSE:
			resultGraph= buildVerboseGraph(models,isoformProbes);
			break;
		default:
			resultGraph=  buildVerboseGraph(models,isoformProbes);
			break;
		}
		
		Map<Integer, Integer> count=new HashMap<Integer, Integer>();
		MultiHashMap<Integer, String> modelMap=new MultiHashMap<Integer, String>();
		for(String s: models.keySet() )
		{
			for(GBAtom atom: models.get(s))
			{
				if(count.containsKey(atom.hashCode()))
				{
					count.put(atom.hashCode(), count.get(atom.hashCode())+1);
				}else
				{
					count.put(atom.hashCode(),1);
				}
				modelMap.put(atom.hashCode(), s);
			}
		}
		
		for(Node n: resultGraph.getNodes())
		{
			if(n instanceof GeneticNode)
			{
				GeneticNode gn=(GeneticNode)n;
				gn.setProperty(GeneticNode.TOTAL_MODELS, Integer.toString(models.size()));
				gn.setProperty(GeneticNode.NUMBER_MODELS, Integer.toString(count.get(gn.getAtom().hashCode())));
				for(String s: modelMap.get(gn.getAtom().hashCode()))
					gn.addModel(s);
			}
		}
		
		return resultGraph;
	}

	public Graph buildVerboseGraph(MultiHashMap<String, GBAtom> models, Map<String, Probe> probes)
	{
		Graph g=new Graph();
		for(String s: models.keySet())
		{
			DefaultNode prev=null;

			List<GBAtom> atoms=new ArrayList<GBAtom>(models.get(s));
			Collections.sort(atoms);
			if(atoms.get(0).strand==Strand.MINUS)
			{
				Collections.reverse(atoms);
			}

			for(GBAtom atom:atoms)
			{
				GeneticNode node=new GeneticNode(g, atom);
//				node.addModel(s);	
//				node.setProperty(GeneticNode.TOTAL_MODELS, Integer.toString(models.size()));
				node.setProperty(GeneticNode.MODEL_NAME, s);
				
				Set<Probe> pr=new HashSet<Probe>();
				pr.add(probes.get(s));
//				for(Probe p: probes.get(s))
//					pr.add(p);
				
				node.setProbes(new ArrayList<Probe>(pr));
				
//				for(Probe p: probes.get(atom))
//					node.addProbe(p);
				g.addNode(node);

				if(prev!=null)
				{
					Edge e=new Edge(prev,node);
					e.setRole(Edges.Roles.NO_ARROW_EDGE);
					e.setName(s);
					g.connect(e);					
				}
				prev=node;				
			}
			prev.setProperty(GeneModelRoles.END_PROPERTY, "true");
		}
		return g;
	}

	public Graph buildCondensedGraph(MultiHashMap<String, GBAtom> models, MultiHashMap<GBAtom, Probe> probes)
	{
		Graph g=new Graph();

		//add all stuff:
		Map<Integer, GeneticNode> nodeMap=new HashMap<Integer, GeneticNode>();
		for(GBAtom atom: models.everything())
		{
			GeneticNode node=new GeneticNode(g, atom);
			
//			node.setProperty(GeneticNode.TOTAL_MODELS, Integer.toString(models.size()));

			nodeMap.put(atom.hashCode(), node);
			g.addNode(node);
		}		

		for(String s: models.keySet())
		{
			List<GBAtom> sorted=new LinkedList<GBAtom>(models.get(s));
			Collections.sort(sorted);

			if(sorted.get(0).strand==Strand.MINUS)
			{
				Collections.reverse(sorted);
			}

			GeneticNode prev=null;

			for(GBAtom atom:sorted)
			{
				GeneticNode node=nodeMap.get(atom.hashCode());
//				for(Probe p: probes.get(atom))		
//				{
//					node.addProbe(p);
//				}
				Set<Probe> pr=new HashSet<Probe>();
				for(Probe p: probes.get(atom))
					pr.add(p);
				node.setProbes(new ArrayList<Probe>(pr));
//				for(Probe p:pr)
//					node.addProbe(p);
				
//				node.addModel(s);
				if(prev!=null)
				{
					// find edges emerging from prev:
					boolean found=false;
					for(Edge e:g.getOutEdges(prev))
					{
						if(e.getTarget()==node)
						{
							found=true;
							e.setWeight(e.getWeight()+1);
						}
					}
					if(!found)
					{
						Edge e=new Edge(prev,node);
						e.setRole(Edges.Roles.NO_ARROW_EDGE);
						e.setName(s);
						g.connect(e);
						e.setWeight(1);
					}
				}
				prev=node;				
			}
			prev.setProperty(GeneModelRoles.END_PROPERTY, "true");

		}
		g.removeOrphans();
		return g;
	}

	public Graph buildCompressedGraph(MultiHashMap<String, GBAtom> models, MultiHashMap<GBAtom, Probe> probes, Map<String, Probe> isoformProbes )
	{
		Graph g=new Graph();
		MultiHashMap<String, GBAtom> atoms=new MultiHashMap<String, GBAtom>();
		for(String s: models.keySet())
		{
			List<GBAtom> sorted=new LinkedList<GBAtom>(models.get(s));
			Collections.sort(sorted);
			if(sorted.get(0).strand==Strand.MINUS)
			{
				Collections.reverse(sorted);
			}
			atoms.put(s, sorted);

		}
		buildCompressedGraph(g, atoms, new ArrayList<String>(models.keySet()),0,null,probes, isoformProbes);
		return g;
	}

	private void buildCompressedGraph(Graph g, MultiHashMap<String, GBAtom> models, List<String> subset,  int i, DefaultNode previous,MultiHashMap<GBAtom, Probe> probes, Map<String, Probe> isoformProbes )
	{
		// see if there are any differences in the models:
		MultiHashMap<Integer,String> uu=new MultiHashMap<Integer, String>();
		for(String s: subset)
		{
			if(models.get(s).size() <=i)
			{
				previous.setProperty(GeneModelRoles.END_PROPERTY,"true");
			}else
			{	
				uu.put(models.get(s).get(i).hashCode(), s);
			}
		}
		for(Integer uuid: uu.keySet())
		{
			GeneticNode node=new GeneticNode(g, models.get(uu.get(uuid).get(0)).get(i));
			g.addNode(node);
//			node.setProperty(GeneticNode.TOTAL_MODELS, Integer.toString(models.size()));
			for(String s: uu.get(uuid))
			{
//				node.addModel(s);
				node.addProbe(isoformProbes.get(s));
//				Set<Probe> pr=new HashSet<Probe>();
//				for(Probe p: probes.get(models.get(s).get(i)))
//					pr.add(p);
//				node.setProbes(new ArrayList<Probe>(pr));
//				for(Probe p:pr)
//					node.addProbe(p);

			}
			if(previous!=null)
			{
				Edge e=new Edge(previous,node);	
				e.setRole(Edges.Roles.NO_ARROW_EDGE);
				g.connect(e);
				e.setWeight(uu.get(uuid).size());
			}

			buildCompressedGraph(g, models,uu.get(uuid),i+1, node, probes,isoformProbes);		


		}
	}
}
