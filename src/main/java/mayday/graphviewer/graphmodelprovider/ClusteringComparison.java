package mayday.graphviewer.graphmodelprovider;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.math.Hypergeometric;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.MappingSourceSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.edges.Edges;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.graph.nodes.Nodes;
import mayday.core.structures.maps.MultiHashMap;
import mayday.core.tasks.AbstractTask;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.layout.DataSetLayouter;
import mayday.graphviewer.util.components.DegreeComparator;
import mayday.graphviewer.util.components.NameComparator;
import mayday.graphviewer.util.components.WeightComparator;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.layout.CanvasLayouter;

public class ClusteringComparison extends AbstractGraphModelProvider
{
	private RestrictedStringSetting probesOption=new RestrictedStringSetting("Use Probes", 
			"The probes to be displayed for each probe list", 0, new String[]{"All Probes", "Mean","Q1, Median, Q3" });

	@SuppressWarnings("unchecked")
	private ObjectSelectionSetting<Comparator<CanvasComponent>> sortingOption=new ObjectSelectionSetting<Comparator<CanvasComponent>>
	("Sort probes by", null,0, new Comparator[]{new WeightComparator(),
			new NameComparator(),
			new DegreeComparator(DegreeComparator.OVERALL_DEGREE),
			new DegreeComparator(DegreeComparator.IN_DEGREE),
			new DegreeComparator(DegreeComparator.OUT_DEGREE)});

	private BooleanSetting removeOrphans=new BooleanSetting("Remove unconnected Probe Lists", 
			"Remove all Probe Lists from the graph " +
			"that have no incoming or outgoing edges", true);

	private BooleanHierarchicalSetting filterNodesSetting=new BooleanHierarchicalSetting("Filter Probe Lists", null, false);
	private BooleanHierarchicalSetting filterEdgesSetting=new BooleanHierarchicalSetting("Filter Edges", null, false);

	private IntSetting nodeCutoff=new IntSetting("Exclude Probe Lists with fewer Probes than ", null, 10);
	private IntSetting edgeCutoff=new IntSetting("Don't show edges representing intersects smaller than ", null, 5);

	private BooleanHierarchicalSetting filterPValue=new BooleanHierarchicalSetting("Significance Test", null, true);
	private DoubleSetting pValueCutoff=new DoubleSetting("p-Value Cutoff",null, 0.05, 0.0,1.0,true,true);

	private String[] edgeWeightModes={"Fraction of smaller Probelist","Fraction of larger Probelist", "Fraction of common Probes", "Number of Probes"};
	private RestrictedStringSetting edgeWeightSetting=new RestrictedStringSetting("Edge Weight", null, 0, edgeWeightModes);
	
	private Map<DataSet, MappingSourceSetting> mappings;

	
	public ClusteringComparison()
	{			
		basicSetting.addSetting(probesOption).addSetting(sortingOption).addSetting(removeOrphans);
		filterNodesSetting.addSetting(nodeCutoff);
		filterEdgesSetting.addSetting(edgeCutoff);
		basicSetting.addSetting(filterNodesSetting).addSetting(filterEdgesSetting);
		filterPValue.addSetting(pValueCutoff);
		basicSetting.addSetting(filterPValue);
		basicSetting.addSetting(edgeWeightSetting);
	}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"mayday.graphModelProvider.compareClusterings",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Create a bipartite graph from sets of probe lists to compare clusterings",
				getName()				
		);		
		return pli;			
	}


	@Override
	public boolean isAskForProbeLists() 
	{
		return true;
	}

	@Override
	public CanvasLayouter defaultLayouter() 
	{
		return new DataSetLayouter(sortingOption.getObjectValue());		
	}

	@Override
	public Setting getInformedSetting() 
	{
		informedSetting=new HierarchicalSetting(getName());
		mappings=new TreeMap<DataSet, MappingSourceSetting>();
		for(DataSet ds:probeLists.keySet())
		{
			MappingSourceSetting mapping=new MappingSourceSetting(ds);
			mappings.put(ds, mapping);
			HierarchicalSetting dsSetting=new HierarchicalSetting(ds.getName());
			dsSetting.addSetting(mapping);
			informedSetting.addSetting(dsSetting);
		}
		return informedSetting;
	}

	@Override
	public String getName() 
	{
		return "Compare Clusterings";
	}

	@Override
	public String getDescription() 
	{
		return "Build a simple graph with no connections from the probes ";
	}

	@Override
	public AbstractTask buildGraph() 
	{
		return new BuildGraphTask();
	}


	private class BuildGraphTask extends AbstractTask
	{
		public BuildGraphTask() 
		{
			super(ClusteringComparison.this.getName());
		}

		@Override
		protected void initialize() 
		{						
		}		

		@Override
		protected void doWork() throws Exception 
		{
			Graph graph=new Graph();
			graph.setName("Cluster comparison for "+probeLists.keySet().size()+" datasets");

			Map<ProbeList, DefaultNode> plNode=new HashMap<ProbeList, DefaultNode>();

			MultiHashMap<DataSet, String> nameMap=new MultiHashMap<DataSet, String>();

			for(DataSet ds: probeLists.keySet())
			{
				for(ProbeList pl:probeLists.get(ds))
				{
					for(Probe p:pl)
					{
						nameMap.put(ds, mappings.get(ds).mappedName(p));
					}
					MultiProbeNode node=new MultiProbeNode(graph);
					node.setName(pl.getDataSet()+":"+pl.getName());
					node.setRole(Nodes.Roles.PROBES_ROLE);	
					switch (probesOption.getSelectedIndex()) {
					case 1:
						Probe p=pl.getStatistics().getMean();
						p.setName(pl.getName()+" (mean)");
						p.addProbeList(pl);
						node.addProbe(p);
						break;
					case 2:
						p=pl.getStatistics().getMedian();
						p.setName(pl.getName()+" (median)");
						p.addProbeList(pl);
						node.addProbe(p);
						p=pl.getStatistics().getQ1();
						p.setName(pl.getName()+" (Q1)");
						p.addProbeList(pl);
						node.addProbe(p);
						p=pl.getStatistics().getQ3();	
						p.setName(pl.getName()+" (Q3)");
						p.addProbeList(pl);
						node.addProbe(p);
						break;
					case 0:
						node.setProbes(new ArrayList<Probe>(pl.getAllProbes()));								
						break;
					default:
						break;
					}	
					node.setProperty(Nodes.Roles.DATASET_ROLE, ds.getName());
					graph.addNode(node);	
					plNode.put(pl, node);
				}			
			}

			for(DataSet ds1: probeLists.keySet())
			{
				for(DataSet ds2: probeLists.keySet())
				{
					if(ds1==ds2) continue;
					Set<String> s1=new HashSet<String>(nameMap.get(ds1));
					Set<String> s2=new HashSet<String>(nameMap.get(ds2));
					s1.retainAll(s2);
					int n=s1.size();
					boolean doTest=n==s2.size();
					if(!doTest)
					{
						writeLog("Warning: Can't calculate p-values for datasets "+ds1.getName()+" and "+ds2.getName()+": probes are not identical.\n");
					}
					System.out.println(doTest);
					for(ProbeList pl1: probeLists.get(ds1))
					{
						int n1=pl1.getNumberOfProbes();
						for(ProbeList pl2: probeLists.get(ds2))
						{
							if(graph.isConnected(plNode.get(pl1), plNode.get(pl2)) || 
									graph.isConnected(plNode.get(pl2), plNode.get(pl1))	)
								continue;
							int n2=pl2.getNumberOfProbes();
							Set<String> pl1Names=new HashSet<String>();
							Set<String> pl2Names=new HashSet<String>();
							for(Probe p: pl1)
							{
								pl1Names.add(mappings.get(pl1.getDataSet()).mappedName(p));
							}
							for(Probe p: pl2)
							{
								pl2Names.add(mappings.get(pl2.getDataSet()).mappedName(p));
							}
							pl1Names.retainAll(pl2Names);
							int m=pl1Names.size();
							double p=0.0; // set to 0 in order to pass if no test is possible. 
							if(doTest)
							{
								System.out.println("n="+n+"; n1="+n1+"; n2="+n2+"; m="+m);
								p=Hypergeometric.overlapProbability(n, n1, n2, m);
								System.out.println("p(m)="+p);
							}
							if(!filterPValue.getBooleanValue() || p < pValueCutoff.getDoubleValue()){
								Edge e=new Edge(plNode.get(pl1),plNode.get(pl2));
								e.setName(m+" Probes");
								e.setRole(Edges.Roles.INTERSECT_EDGE);
								e.addProperty("Probes", pl1Names.toString());
								e.addProperty("#Probes", Integer.toString(pl1Names.size() ));
								
								
								double w=0;
								switch(edgeWeightSetting.getSelectedIndex())
								{
								case 0: w=((double) m) / (double)Math.min(pl1.getNumberOfProbes(), pl2.getNumberOfProbes());
									break;
								case 1: w=((double) m) / (double)Math.max(pl1.getNumberOfProbes(), pl2.getNumberOfProbes());
									break;
								case 2: w=((double) m) / (double)n;
									break;
								case 3: w=(double)m;
									break;
								default: w=0;
								}
								e.setWeight(w);
								if(doTest)
									e.addProperty("p-value", Double.toString(p));
								if(!graph.isConnected(e.getSource(), e.getTarget()) && !pl1Names.isEmpty())
									graph.connect(e);						
							}
						}
					}
				}
			}

			if(filterEdgesSetting.getBooleanValue())
			{
				List<Edge> killEdges=new LinkedList<Edge>();
				for(Edge e: graph.getEdges())
				{
					if(e.getWeight() < edgeCutoff.getIntValue())
					{
						killEdges.add(e);
					}
				}
				for(Edge e: killEdges)
					graph.removeEdge(e);
			}
			if(filterNodesSetting.getBooleanValue())
			{
				for(ProbeList pl: probeLists.everything())
				{
					if(pl.getNumberOfProbes() < nodeCutoff.getIntValue())
					{
						graph.removeNode(plNode.get(pl));
					}
				}
			}

			if(removeOrphans.getBooleanValue())
				graph.removeOrphans();

			model=new SuperModel(graph);
		}		
	}
}
