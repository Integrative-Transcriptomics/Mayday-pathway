package mayday.pathway.keggview.pathways.graph;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mayday.core.Probe;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.pathway.keggview.kegg.pathway.Entry;

public class PathwayNode extends MultiProbeNode
{
	private URL link;
	protected List<String> probeNames;

	public PathwayNode(Graph graph)
	{
		super(graph,new ArrayList<Probe>());
		probeNames=new ArrayList<String>();
	}

	public PathwayNode(PathwayGraph graph, Entry e)
	{
		super(graph, new ArrayList<Probe>());
		fromEntry(e);
	}

	/**
	 * Build a Node from a KGML entry. Sets all necessary parameters. 
	 * @param e
	 */
	protected void fromEntry(Entry e)
	{
		setName(e.getName());
		setLink(e.getLink());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o)
	{
		return o.hashCode()==hashCode();
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
	}



	/**
	 * @return the link
	 */
	public URL getLink() {
		return link;
	}

	/**
	 * @param link the link to set
	 */
	public void setLink(URL link) {
		this.link = link;
	}

	public int getImportance(Graph g)
	{
		return g.getDegree(this);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * @return the probeNames
	 */
	public List<String> getProbeNames() {
		return probeNames;
	}

	/**
	 * @param probeNames the probeNames to set
	 */
	public void setProbeNames(List<String> probeNames) {
		this.probeNames = probeNames;
	}

	/**
	 * @param probeName the probeNames to add to the list
	 */
	public void addProbeName(String probeName) {
		this.probeNames.add(probeName);
	}

}
