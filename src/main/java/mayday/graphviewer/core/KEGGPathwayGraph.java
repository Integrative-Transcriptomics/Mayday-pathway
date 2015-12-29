package mayday.graphviewer.core;

import java.util.HashMap;
import java.util.Map;

import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.io.GraphMLExport;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.maps.MultiTreeMap;
import mayday.pathway.keggview.kegg.pathway.Entry;
import mayday.pathway.keggview.kegg.pathway.Graphics;
import mayday.pathway.keggview.kegg.pathway.Pathway;
import mayday.pathway.keggview.kegg.pathway.ReactionEntry;
import mayday.pathway.keggview.kegg.pathway.Relation;
import mayday.pathway.keggview.kegg.pathway.Substance;
import mayday.pathway.keggview.pathways.graph.PathwayNode;

public class KEGGPathwayGraph extends Graph
{
	public KEGGPathwayGraph(Pathway p) 
	{
		setName(p.getName());
		Map<String, MultiProbeNode> compoundMap=new HashMap<String, MultiProbeNode>();
		Map<String, MultiProbeNode> entryIdMap=new HashMap<String, MultiProbeNode>();
		Map<Node,Entry>nodeToEntry=new HashMap<Node, Entry>();
		// create nodes; 
		for(Entry e: p.getEntries())
		{
			if(e.getType().equals("compound"))
			{
				MultiProbeNode node=new MultiProbeNode(this);
				node.setRole(KEGGRoles.COMPOUND_ROLE);
				node.setName(removeScope(e.getName()));
				addNode(node);
				compoundMap.put(e.getName(), node);	
				entryIdMap.put(e.getId(),node);
				nodeToEntry.put(node, e);
				
				node.setProperty(GraphMLExport.GEOMETRY_KEY, getManifiedBounds(e.getGraphics()));
			}
		}
		
		MultiTreeMap<String, PathwayNode> rnGeneMap=new MultiTreeMap<String, PathwayNode>();
		for(Entry e: p.getEntries())
		{			
			if(e.getType().equals("gene"))
			{
				if(e.getGraphics()==null) continue;
				MultiProbeNode node=new MultiProbeNode(this);
				node.setRole(KEGGRoles.GENE_ROLE);
				if(e.getGraphics()!=null)
				{
					node.setName(removeScope(e.getGraphics().getName()));
				}else
				{
					node.setName(removeScope(e.getName()));
				}

				node.setProperty(GraphMLExport.PROBES_KEY, squeezeNames(e.getName()));
				
				addNode(node);				
				entryIdMap.put(e.getId(),node);
				nodeToEntry.put(node, e);
//				System.out.println("-->"+(e.getName()));
				if(e.getReaction()!=null)
				{
//					for(String r:e.getReaction())
//						rnGeneMap.put(r, node);
					rnGeneMap.put(e.getReaction(), node);
				}	
				node.setProperty(GraphMLExport.GEOMETRY_KEY, getManifiedBounds(e.getGraphics()));
			}
			if(e.getType().equals("map"))
			{
				MultiProbeNode node=new MultiProbeNode(this);
				node.setRole(KEGGRoles.MAP_ROLE);
				
				node.setName(removeScope(e.getGraphics().getName()));
				
				addNode(node);
				entryIdMap.put(e.getId(),node);
				nodeToEntry.put(node, e);
				node.setProperty(GraphMLExport.GEOMETRY_KEY, getManifiedBounds(e.getGraphics()));
			}
		}
		
	
		for(ReactionEntry r:p.getReactions())
		{
			if(rnGeneMap.get(r.getName())==null) continue;
			
			for(Substance s:r.getSubstrates())
			{
				Node subst=compoundMap.get(s.getName());
				for(Substance t:r.getProducts())
				{
					
					Node prod=compoundMap.get(t.getName());
					for(Node e:rnGeneMap.get(r.getName()))
					{					
						if(subst==null || prod==null || e==null) continue;
						Edge bind=new Edge(subst,e);
						bind.setName(r.getName());
						bind.setRole(KEGGRoles.REACTION_ROLE);
								
						
						Edge release=new Edge(e,prod);
						release.setName(r.getName());						
						release.setRole(KEGGRoles.REACTION_ROLE);
						
						bind.addProperty(KEGGRoles.REVERSIBLE_KEY, r.isReversible()?KEGGRoles.REVERSIBLE_REVERSIBLE:KEGGRoles.REVERSIBLE_IRREVERSIBLE);
						release.addProperty(KEGGRoles.REVERSIBLE_KEY, r.isReversible()?KEGGRoles.REVERSIBLE_REVERSIBLE:KEGGRoles.REVERSIBLE_IRREVERSIBLE);
		
						connect(bind);
						connect(release);
					}					
				}
			}			
		}
		
				
		for(Relation r: p.getRelations())
		{
			if(r.getType().equals("maplink"))
			{
				if(entryIdMap.get(r.getEntry1()) ==null || entryIdMap.get(r.getEntry2())==null) continue;
				if(r.getSubtypes().containsKey("compound"))
				{
					String t= r.getSubtypes().get("compound");
					Node target=entryIdMap.get(t);
					
					Edge e1=new Edge(entryIdMap.get(r.getEntry1()), target);	
					e1.setRole(KEGGRoles.MAPLINK_ROLE);					
					connect(e1);
										
					Edge e2=new Edge(entryIdMap.get(r.getEntry2()), target);			
					e2.setRole(KEGGRoles.MAPLINK_ROLE);
					connect(e2);
					
				}
			}else
			{
				Edge e=new Edge(entryIdMap.get(r.getEntry1()), entryIdMap.get(r.getEntry2()));			
				e.setRole(r.getSubtypes().keySet().iterator().next());
				e.setName(r.getSubtypes().get(r.getSubtypes().keySet().iterator().next()));
				connect(e);			
			}
		}
	}
	
	private String getManifiedBounds(Graphics g)
	{
		double mag=1.0;
		return 
		(int)((g.x*mag)-(g.width*mag/2.0))+","+
		(int)((g.y*mag)-(g.height*mag/2.0))+","+
		(int)(g.width*mag)+","+
		((int)g.height*mag);
	}
	
	private String squeezeNames(String name)
	{
		StringBuffer sb=new StringBuffer();
		String[] names=name.split(" ");
		boolean first=true;
		for(String n:names)
		{
			if(!first)
			{
				sb.append(",");
			}else
			{
				first=false;
			}
			if(n.contains(":"))
			{
				sb.append("\""+n.substring(n.indexOf(":")+1)+"\"");
			}			
		}
		return sb.toString();
	}
	
	private String removeScope(String name)
	{
		StringBuffer sb=new StringBuffer();
		String[] names=name.split(" ");
		boolean first=true;
		for(String n:names)
		{
			if(!first)
			{
				sb.append(",");
				first=false;
			}
			if(n.contains(":"))
			{
				sb.append(n.substring(n.indexOf(":")+1));
			}else
			{
				sb.append(n);
			}
		}
		return sb.toString();
	}
}
