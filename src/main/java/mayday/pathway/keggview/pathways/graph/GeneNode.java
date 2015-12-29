package mayday.pathway.keggview.pathways.graph;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import mayday.core.Probe;
import mayday.pathway.keggview.kegg.ko.KOEntry;
import mayday.pathway.keggview.kegg.pathway.Entry;

public class GeneNode extends PathwayNode 
{
	
	private Set<KOEntry> genes;
	
	
	/**
	 * Create a new GeneNode carrying the properties of Entry e. 
	 * @param e
	 */
	public GeneNode(PathwayGraph graph, Entry e)
	{
		super(graph);
		fromEntry(e);
		genes=new HashSet<KOEntry>();
		role="Enzyme";
	}
	
	public GeneNode(PathwayGraph graph, KOEntry entry)
	{
		super(graph);
		setName(entry.getName());
		genes=new HashSet<KOEntry>();
		genes.add(entry);
		role="Enzyme";
	}

	/**
	 * @return the genes
	 */
	public Set<KOEntry> getGenes() {
		return genes;
	}

	/**
	 * @param genes the genes to set
	 */
	public void setGenes(Set<KOEntry> genes) {
		this.genes = genes;
	}
	
	/**
	 * @param gene the gene to add
	 */
	public void addGene(KOEntry gene) 
	{
		this.genes.add(gene);
	}
	
	public void setPlotData(Map<String,Probe> mapping)
	{	
		if(mapping==null) return;
		probes.clear();

		for(String s: probeNames)
		{
			if(mapping.containsKey(s))
				probes.add(mapping.get(s));
			else
				if(mapping.containsKey(s.toUpperCase()) )
					probes.add(mapping.get(s.toUpperCase()));
				else
					if(mapping.containsKey(s.toLowerCase()) )
						probes.add(mapping.get(s.toLowerCase()));
		}

		for(KOEntry e:getGenes())
		{
			if(e==null) continue;
			for(String s:e.getGenes())
			{
				Probe p= mapping.get(s.toLowerCase());
				if(p!=null) 
				{
					probes.add(p);	
				}
			}			
		}
	}
	
	
	

}
