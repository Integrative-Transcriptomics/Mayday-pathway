package mayday.pathway.keggview.pathways.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.maps.MultiTreeMap;
import mayday.pathway.keggview.kegg.KEGGObject;
import mayday.pathway.keggview.kegg.compounds.Compound;
import mayday.pathway.keggview.kegg.ko.KOEntry;
import mayday.pathway.keggview.kegg.pathway.Entry;
import mayday.pathway.keggview.kegg.pathway.Pathway;
import mayday.pathway.keggview.kegg.pathway.ReactionEntry;
import mayday.pathway.keggview.kegg.pathway.Relation;
import mayday.pathway.keggview.kegg.pathway.Substance;

public class PathwayGraph extends Graph
{
	private String name;
	private String title;
	
	private List<ReactionEdge> reactions;
	private List<RelationEdge> relations;
	
	private HashMap<PathwayNode, Entry> nodeToEntry;
	
	public PathwayGraph(Pathway p)
	{
		setName(p.getName());
		setTitle(p.getTitle());
		Map<String, CompoundNode> compoundMap=new HashMap<String, CompoundNode>();
		Map<String, PathwayNode> entryIdMap=new HashMap<String, PathwayNode>();
		nodeToEntry=new HashMap<PathwayNode, Entry>();
		// create nodes; 
		for(Entry e: p.getEntries())
		{
			if(e.getType().equals("compound"))
			{
				CompoundNode node=new CompoundNode(this,e);
				addNode(node);
				compoundMap.put(e.getName(), node);	
				entryIdMap.put(e.getId(),node);
				nodeToEntry.put(node, e);
			}
		}
		
		MultiTreeMap<String, PathwayNode> rnGeneMap=new MultiTreeMap<String, PathwayNode>();
		for(Entry e: p.getEntries())
		{			
			if(e.getType().equals("gene"))
			{
				if(e.getGraphics()==null) continue;
				GeneNode node=new GeneNode(this,e);
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
			}
			if(e.getType().equals("map"))
			{
				MapNode node=new MapNode(this,e);
				addNode(node);
				entryIdMap.put(e.getId(),node);
				nodeToEntry.put(node, e);
			}
		}
		
		reactions=new ArrayList<ReactionEdge>();
		
		for(ReactionEntry r:p.getReactions())
		{
			
			if(rnGeneMap.get(r.getName())==null) continue;
			
			for(Substance s:r.getSubstrates())
			{
				CompoundNode subst=compoundMap.get(s.getName());
				for(Substance t:r.getProducts())
				{
					
					CompoundNode prod=compoundMap.get(t.getName());
					for(PathwayNode e:rnGeneMap.get(r.getName()))
					{					
						if(subst==null || prod==null || e==null) continue;
						ReactionEdge bind=new ReactionEdge(subst,e,r.getName());
						bind.setReaction(r);
						ReactionEdge release=new ReactionEdge(e,prod,r.getName());
						release.setReaction(r);
						bind.setReversible(r.isReversible());
						release.setReversible(r.isReversible());
						connect(bind);
						connect(release);
						reactions.add(bind);
						reactions.add(release);
					}					
				}
			}			
		}
		
		relations=new ArrayList<RelationEdge>();
		
		for(Relation r: p.getRelations())
		{
			if(r.getType().equals("maplink"))
			{
				if(entryIdMap.get(r.getEntry1()) ==null || entryIdMap.get(r.getEntry2())==null) continue;
				if(r.getSubtypes().containsKey("compound"))
				{
					String t= r.getSubtypes().get("compound");
					PathwayNode target=entryIdMap.get(t);
					
					RelationEdge e1=new RelationEdge(entryIdMap.get(r.getEntry1()), target);			
					e1.setType(r.getType());
					connect(e1);
					relations.add(e1);
					
					RelationEdge e2=new RelationEdge(entryIdMap.get(r.getEntry2()), target);			
					e2.setType(r.getType());
					connect(e2);
					relations.add(e2);
				}else
				{
					RelationEdge e=new RelationEdge(entryIdMap.get(r.getEntry1()), entryIdMap.get(r.getEntry2()));			
					e.setType(r.getType());
					connect(e);
					relations.add(e);
				}
			}			
		}
		
	}
	
	public void setCompounds(Map<String, KEGGObject> compounds)
	{
		for(Node n:getNodes())
		{
			if(n instanceof CompoundNode)
			{
				String handle=n.getName();
				String[] tok=handle.split("\\s");
				List<Compound> res=new ArrayList<Compound>();
				for(String s:tok)
				{
					((PathwayNode)n).addProbeName(s.substring(s.indexOf(":")+1));
					res.add((Compound)compounds.get(s.substring(s.indexOf(":")+1)));
				}
				((CompoundNode)n).setCompounds(res);					
				
			}
			
		}
	}
	
	public void setGenes(Map<String, KEGGObject> genes)
	{
		for(Node n:getNodes())
		{
			if(n instanceof GeneNode)
			{
				String[] tok=n.getName().split(" ");
				for(String s:tok)
				{
					((PathwayNode)n).addProbeName(s.substring(4));
					if(genes.get(s.substring(4))!=null)
					{
						((GeneNode)n).addGene((KOEntry)genes.get(s.substring(4)));	
					}
				}				
			}
		}
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	public List<GeneNode> getGeneNodes()
	{
		List<GeneNode> res=new ArrayList<GeneNode>();
		for(Node n:getNodes())
		{
			if(n instanceof GeneNode) res.add((GeneNode)n);
		}
		return res; 
	}

	/**
	 * @return the reactions
	 */
	public List<ReactionEdge> getReactions() {
		return reactions;
	}

	/**
	 * @param reactions the reactions to set
	 */
	public void setReactions(List<ReactionEdge> reactions) {
		this.reactions = reactions;
	}

	/**
	 * @return the relations
	 */
	public List<RelationEdge> getRelations() {
		return relations;
	}

	/**
	 * @param relations the relations to set
	 */
	public void setRelations(List<RelationEdge> relations) {
		this.relations = relations;
	}
	
	public Entry getEntry(PathwayNode node)
	{
		return nodeToEntry.get(node);
	}

	@Override
	public void removeEdge(Edge e) 
	{
		super.removeEdge(e);
		if(reactions.contains(e))
			reactions.remove(e);
		if(relations.contains(e))
			relations.remove(e);
	}
	
	
}
