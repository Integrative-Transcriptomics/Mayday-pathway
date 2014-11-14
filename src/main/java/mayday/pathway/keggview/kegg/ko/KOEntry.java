package mayday.pathway.keggview.kegg.ko;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mayday.pathway.keggview.kegg.DefinitionKEGGObject;

public class KOEntry extends DefinitionKEGGObject
{
	private String ecNumber;
	private List<String> genes;
	private List<String> pathways;
	
	public KOEntry()
	{
		genes=new ArrayList<String>();
	}

	/**
	 * @return the ecNumber
	 */
	public String getEcNumber() {
		return ecNumber;
	}

	/**
	 * @param ecNumber the ecNumber to set
	 */
	public void setEcNumber(String ecNumber) {
		this.ecNumber = ecNumber;
	}

	/**
	 * @return the genes
	 */
	public List<String> getGenes() {
		return genes;
	}

	/**
	 * @param genes the genes to set
	 */
	public void setGenes(List<String> genes) {
		this.genes = genes;
		Collections.sort(genes);
	}
	
	public void setGenes(String[] genes) 
	{
		for(String s:genes)
		{
			this.genes.add(s);
		}
		Collections.sort(this.genes);
	}
	
	/**
	 * @param genes the genes to set
	 */
	public void addGene(String gene) {
		this.genes.add(gene);
	}
	
	public boolean hasGene(String name)
	{
		return Collections.binarySearch(this.genes, name)>0;
	}
	
	public String toString()
	{
		return getName();
	}

	/**
	 * @return the pathways
	 */
	public List<String> getPathways() {
		return pathways;
	}

	/**
	 * @param pathways the pathways to set
	 */
	public void setPathways(List<String> pathways) {
		this.pathways = pathways;
	}
	
	public List<String> getPossibleNames()
	{
		List<String> res=new ArrayList<String>();
		res.addAll(genes);
		res.add(getDefinition());
		res.add(ecNumber);
		res.add(getName());
		String[] tok=getName().split(",");
		for(String t:tok)
		{
			res.add(t.trim());
		}
		return res;
	}
	
	
	
	
}
