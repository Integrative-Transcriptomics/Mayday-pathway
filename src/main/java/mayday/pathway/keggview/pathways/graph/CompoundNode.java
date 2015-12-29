package mayday.pathway.keggview.pathways.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mayday.core.Probe;
import mayday.core.structures.graph.Graph;
import mayday.pathway.keggview.kegg.compounds.Compound;
import mayday.pathway.keggview.kegg.pathway.Entry;

public class CompoundNode extends PathwayNode 
{	
	private List<Compound> compounds;

	public CompoundNode(Graph graph, Entry e)
	{
		super(graph);
		fromEntry(e);
		role="Compound";
	}

	/**
	 * @return the compound
	 */
	public Compound getCompound() 
	{
		return compounds.get(0);
	}

	/**
	 * @param compound the compound to set
	 */
	public void setCompound(Compound compound) 
	{
		compounds=new ArrayList<Compound>();
		compounds.add(compound);
	}

	/**
	 * @return the compounds
	 */
	public List<Compound> getCompounds() {
		return compounds;
	}

	/**
	 * @param compounds the compounds to set
	 */
	public void setCompounds(List<Compound> compounds) {
		this.compounds = compounds;
	}

	@Override
	public void setPlotData(Map<String,Probe> mapping)
	{	
		if(mapping==null) return;
		probes.clear();
		for(Compound c:compounds)
		{
			if(c==null) continue;
			for(String s:c.getNames())
			{
				if(mapping.containsKey(s))
					probes.add(mapping.get(s));
				else
					if(mapping.containsKey(s.toLowerCase()))
						probes.add(mapping.get(s.toLowerCase()));
					else
						if(mapping.containsKey(s.toUpperCase()))
							probes.add(mapping.get(s.toUpperCase()));

//				Probe p= mapping.get(s);
//				if(p!=null) 
//				{
//					System.out.println(p.getDisplayName());
//					probes.add(p);	
//				}
			}			
		}
	}





}
