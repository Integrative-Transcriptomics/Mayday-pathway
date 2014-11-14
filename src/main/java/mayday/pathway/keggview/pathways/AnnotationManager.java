package mayday.pathway.keggview.pathways;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import mayday.core.Probe;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.meta.types.StringMIO;
import mayday.pathway.keggview.kegg.KEGGObject;
import mayday.pathway.keggview.kegg.KEGGParser;
import mayday.pathway.keggview.kegg.compounds.Compound;
import mayday.pathway.keggview.kegg.compounds.CompoundsParser;
import mayday.pathway.keggview.kegg.ko.KOEntry;
import mayday.pathway.keggview.kegg.ko.KOParser;
import mayday.pathway.keggview.kegg.reaction.Reaction;

public class AnnotationManager 
{
	private Map<String,KEGGObject> genes;
	private Map<String,KEGGObject> compounds;
	private Map<String,KEGGObject> reactions;
	private Map<String,KEGGObject> nameEntryMap;


	public AnnotationManager()
	{
		nameEntryMap=new HashMap<String, KEGGObject>();
	}

	/**
	 * Reads the annotation data from the given directory for the given taxon. 
	 * @param directory
	 * @param taxon
	 * @throws IOException
	 */
	public void init(String directory, String taxon) throws IOException
	{
		if(taxon==null) return;
		if(compounds==null || genes==null)
		{
			KEGGParser koParser=new KEGGParser(new KOParser(taxon.toUpperCase()));
			genes=koParser.parseData(directory+"/ko");

			KEGGParser comParser=new KEGGParser(new CompoundsParser());
			compounds=comParser.parseData(directory+"/compound");

			//			KEGGParser reaParser=new KEGGParser(new ReactionParser());
			//			reactions=reaParser.parseData(directory+"/reaction");

			for(String s:genes.keySet())
			{
				KOEntry e=(KOEntry)genes.get(s);
				for(String n:e.getGenes())
				{
					nameEntryMap.put(n, e);
				}
			}
		}
	}

	public void clear()
	{
		compounds.clear();
		genes.clear();
		compounds=null;
		genes=null;
	}

	public Map<String, KEGGObject> getNameEntryMap() 
	{
		return nameEntryMap;
	}

	/**
	 * @return the genes
	 */
	public Map<String, KEGGObject> getGenes() {
		return genes;
	}

	/**
	 * @return the compounds
	 */
	public Map<String, KEGGObject> getCompounds() {
		return compounds;
	}

	public Compound getCompound(String query)
	{
		return (Compound)compounds.get(query);
	}

	public static  Map<String, Probe> createMapping(MIGroupSelection<MIType> selection, Iterable<Probe> probes)
	{
		if(selection==null) return null;
		Map<String, Probe> probeMapping=new HashMap<String, Probe>();
		for(MIGroup m:selection)
		{
			for(Probe p:probes)
			{
				if(m.getMIO(p)==null) continue;
				String s=((StringMIO)m.getMIO(p)).getValue();
				if(s!=null)
				{
					probeMapping.put(s.toLowerCase(), p);						
				}
			}
		}
		return probeMapping;
	}

	public static Map<String, Probe> createMappingByMIO(MIGroup m, Iterable<Probe> probes)
	{
		Map<String, Probe> probeMapping=new HashMap<String, Probe>();
		for(Probe p:probes)
		{
			if(m.getMIO(p)==null) continue;
			String s=((StringMIO)m.getMIO(p)).getValue();
			if(s!=null)
			{
				probeMapping.put(s.toLowerCase(), p);						
			}
		}
		return probeMapping;
	}

	public static Map<String, Probe> createMappingByName(Iterable<Probe> probes)
	{
		Map<String, Probe> probeMapping=new HashMap<String, Probe>();
		for(Probe p:probes)
		{
			probeMapping.put(p.getName(), p);
		}
		return probeMapping;
	}

	public static Map<String, String> createMappingByName(Iterable<String> names, boolean useString)
	{
		Map<String, String> probeMapping=new HashMap<String, String>();
		for(String s:names)
		{
			probeMapping.put(s, s);
		}
		return probeMapping;
	}
	
	public static Map<String, Probe> createMappingByDisplayName(Iterable<Probe> probes)
	{
		Map<String, Probe> probeMapping=new HashMap<String, Probe>();
		for(Probe p:probes)
		{
			probeMapping.put(p.getDisplayName(), p);
		}
		return probeMapping;
	}

	/**
	 * @return the reactions
	 */
	public Map<String, KEGGObject> getReactions() {
		return reactions;
	}

	/**
	 * @param reactions the reactions to set
	 */
	public void setReactions(Map<String, KEGGObject> reactions) {
		this.reactions = reactions;
	}

	public Reaction reactionForEntry(String id)
	{
		return (Reaction)reactions.get(id);
	}

	public KOEntry geneForEntry(String id)
	{
		return (KOEntry)genes.get(id);
	}

	public KOEntry geneForName(String id)
	{
		return (KOEntry)nameEntryMap.get(id);
	}

	public Compound compoundForEntry(String id)
	{
		return (Compound)compounds.get(id);
	}

}
