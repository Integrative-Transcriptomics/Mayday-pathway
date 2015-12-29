package mayday.graphviewer.graphmodelprovider;

import java.awt.Color;
import java.awt.Component;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import mayday.core.DataSet;
import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.GUIUtilities;
import mayday.core.math.distance.DistanceMeasureManager;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.meta.types.StringMIO;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.methods.DistanceMeasureSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.MappingSourceSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.nodes.MIONode;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.graph.nodes.Nodes;
import mayday.core.tasks.AbstractTask;
import mayday.genetics.LocusMIO;
import mayday.genetics.advanced.chromosome.LocusChromosomeObject;
import mayday.genetics.advanced.chromosome.LocusGeneticCoordinateObject;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.Strand;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.graphviewer.core.SBGNRoles;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.core.bag.ComponentBag;
import mayday.graphviewer.crossViz3.core.DataSetScope;
import mayday.graphviewer.datasources.biopax2.BioPaxSqueezer2;
import mayday.graphviewer.datasources.string.StringConnector;
import mayday.graphviewer.datasources.string.StringConnector.StringInteraction;
import mayday.graphviewer.datasources.uniprot.UniProtConnector;
import mayday.graphviewer.datasources.uniprot.UniProtParser;
import mayday.graphviewer.layout.OneProbeLayout;
import mayday.graphviewer.plugins.extend.StringImport;
import mayday.vis3.graph.layout.CanvasLayouter;

public class OneProbe extends AbstractGraphModelProvider
{
	private BooleanSetting useProbeLists=new BooleanSetting("ProbeLists", null, true);
	private BooleanSetting useMetaInformation=new BooleanSetting("Meta Information", null, true);
	private BooleanSetting otherDataSets=new BooleanSetting("Other Data Sets", null, true);

	private Map<DataSet, MappingSourceSetting> mappings;
	private Map<DataSet, ObjectSelectionSetting<DataSetScope>> scopes;

	private BooleanHierarchicalSetting useGenomicNeighborhood=new BooleanHierarchicalSetting("Genomic Neighbors", null, true);
	private IntSetting maxDistance=new IntSetting("Distance", null, 1000);

	private BooleanHierarchicalSetting useSimilarProbes=new BooleanHierarchicalSetting("Similar Probes", null, true);
	private DistanceMeasureSetting distanceMeasureSetting;
	private DoubleSetting cutoff=new DoubleSetting("Distance Cutoff", null,0.05,0.0,100.0,true, true);

	private BooleanHierarchicalSetting useBioPax=new BooleanHierarchicalSetting("BioPax file", null, true);
	private BooleanSetting usePathways=new BooleanSetting("Pathways", null, true);
	private BooleanSetting useReactions=new BooleanSetting("Reactions", null, true);
	private PathSetting bioPaxPath=new PathSetting("BioPax file", null, null, false, true, false);
	private BioPaxSqueezer2 squeezer;
	private BooleanSetting useUniProt=new BooleanSetting("UniProt", null,true);


	private ObjectSelectionSetting<ProbeList> coreProbeList;	

	private BooleanHierarchicalSetting useStringInteractors=new BooleanHierarchicalSetting("Interactors (String)",null,true);

	private RestrictedStringSetting database=new RestrictedStringSetting("Database", 
			"String: contains protein-protein interactions\n" +
			"Stitch: also contains protein-metabolite interactions", 0, new String[]{StringConnector.DATABASE_STRING, StringConnector.DATABASE_STITCH});

	private BooleanHierarchicalSetting interactionsSetting=new BooleanHierarchicalSetting("Retrieve Interaction Network", 
			"if selected, fetch the network of closely related interactors," +
			"else, get only direct interactors", true);

	private IntSetting scoreSetting=new IntSetting("Minimum Score", "0: no minimum score", 0,0,1000,true,false);
	private IntSetting maxSetting=new IntSetting("Maximum Interactors", "0: no maximum", 0);

	private MappingSourceSetting stringMapping;
	private MappingSourceSetting bioPaxMapping;
	private MappingSourceSetting uniprotMapping;
	private SettingDialog mappingsDialog;
	
	public OneProbe() 
	{
		useGenomicNeighborhood.addSetting(maxDistance);
		interactionsSetting.addSetting(scoreSetting);
		useStringInteractors.addSetting(database).addSetting(interactionsSetting).addSetting(maxSetting);
		useBioPax.addSetting(bioPaxPath).addSetting(usePathways).addSetting(useReactions);
		basicSetting.
		addSetting(useProbeLists).
		addSetting(useMetaInformation).
		addSetting(otherDataSets).
		addSetting(useSimilarProbes).
		addSetting(useGenomicNeighborhood).
		addSetting(useStringInteractors).
		addSetting(useBioPax).
		addSetting(useUniProt);
	}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"mayday.graphModelProvider.oneprobe",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				getDescription(),
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
		return new OneProbeLayout();
	}

	@Override
	public Setting getBasicSetting() 
	{
		if(!useSimilarProbes.getChildren().contains(distanceMeasureSetting))
		{
			distanceMeasureSetting=new DistanceMeasureSetting("Distance Measure", null, DistanceMeasureManager.get("Pearson Correlation"));
			useSimilarProbes.addSetting(distanceMeasureSetting).addSetting(cutoff);
		}
		return super.getBasicSetting();
	}

	@Override
	public Setting getInformedSetting() 
	{
		scopes=new TreeMap<DataSet, ObjectSelectionSetting<DataSetScope>>();
		mappings=new TreeMap<DataSet, MappingSourceSetting>();
		informedSetting=new HierarchicalSetting(getName());

		ProbeList[] predef=(ProbeList[]) probeLists.everything().toArray(new ProbeList[probeLists.everything().size()]);

		coreProbeList=new ObjectSelectionSetting<ProbeList>("Core Probe List", null, 0, predef);
		informedSetting.addSetting(coreProbeList);

		HierarchicalSetting maps=new HierarchicalSetting("DataSet mappings"); 		
		for(DataSet ds:probeLists.keySet())
		{
			HierarchicalSetting dsSetting=new HierarchicalSetting(ds.getName());
			ObjectSelectionSetting<DataSetScope> scopeSetting=new ObjectSelectionSetting<DataSetScope>("Role",null,0,DataSetScope.values());
			scopes.put(ds, scopeSetting);
			MappingSourceSetting mapping=new MappingSourceSetting(ds);
			mappings.put(ds, mapping);
			dsSetting.addSetting(mapping).addSetting(scopeSetting);
			maps.addSetting(dsSetting);
		}	
				
		informedSetting.addSetting(maps);
		return informedSetting;
	}
	
	
	
	@Override
	public Component getAdditionalComponent() 
	{
		HierarchicalSetting setting=new HierarchicalSetting("Additional Mappings");
		DataSet coreDS=coreProbeList.getObjectValue().getDataSet();
		if(useBioPax.getBooleanValue())
		{
			bioPaxMapping=new MappingSourceSetting(coreDS);
			HierarchicalSetting s=new HierarchicalSetting("BioPax");
			s.addSetting(bioPaxMapping);
			setting.addSetting(s);
		}
		if(useStringInteractors.getBooleanValue())
		{
			stringMapping=new MappingSourceSetting(coreDS);
			HierarchicalSetting s=new HierarchicalSetting("String Interactors");
			s.addSetting(stringMapping);
			setting.addSetting(s);
		}
		if(useUniProt.getBooleanValue())
		{
			uniprotMapping=new MappingSourceSetting(coreDS);
			HierarchicalSetting s=new HierarchicalSetting("UniProt");
			s.addSetting(uniprotMapping);
			setting.addSetting(s);
		}
		if(setting.getChildren().isEmpty())
			return null;
		
		mappingsDialog=new SettingDialog(null, "Settings", setting);
		mappingsDialog.pack();
		
		return mappingsDialog.getContentPane();
		
	}

	@Override
	public String getName() 
	{
		return "All about one probe";
	}

	@Override
	public String getDescription() 
	{
		return "Aggregate all information about one probe";
	}

	@Override
	public boolean isAskForFileSetting() 
	{	
		return (useBioPax.getBooleanValue() || useStringInteractors.getBooleanValue() || useUniProt.getBooleanValue());
	}


	@Override
	public AbstractTask buildGraph() 
	{
		return new BuildGraphTask();
	}


	@Override
	public AbstractTask parseFile() 
	{
		return new ParseTask();
	}

	private class ParseTask extends AbstractTask
	{
		public ParseTask() 
		{
			super("Parsing Files");
		}

		@Override
		protected void doWork() throws Exception 
		{
			if(useBioPax.getBooleanValue())
			{
				//				squeezer=new BioPaxSqueezer2(bioPaxPath.getStringValue());
				squeezer=BioPaxSqueezer2.getSharedInstance(bioPaxPath.getStringValue());
			}
		}
		@Override
		protected void initialize() {}
	}

	private class BuildGraphTask extends AbstractTask
	{
		public BuildGraphTask() 
		{
			super(OneProbe.this.getName());
		}

		@Override
		protected void initialize() {}		

		@Override
		protected void doWork() throws Exception 
		{
			if(isAskForFileSetting())
				mappingsDialog.apply();
			
			Graph graph=new Graph();

			Map<String, List<Node>> bags=new HashMap<String, List<Node>>();
			for(Probe p: coreProbeList.getObjectValue())
			{
				MultiProbeNode pnode=new MultiProbeNode(graph, p);
				graph.addNode(pnode);

				if(useProbeLists.getBooleanValue())
				{
					List<Node> res=addProbeLists(p, pnode, graph);
					bags.put("ProbeLists", res);
				}
				if(useMetaInformation.getBooleanValue())
				{
					List<Node> res=addMIOs(p, pnode, graph);
					bags.put("Meta Information", res);
				}
				if(otherDataSets.getBooleanValue())
				{
					List<Node> res=addCrossDS(p, pnode, graph);
					bags.put("Other DataSets", res);
				}
				if(useSimilarProbes.getBooleanValue())
				{
					List<Node> res=addSimilarProbes(p, pnode, graph);
					bags.put("Similar Profile ("+distanceMeasureSetting.getStringValue()+")", res);
				}
				if(useGenomicNeighborhood.getBooleanValue())
				{
					List<Node> res=addGenomicNeighbors(p, pnode, graph);
					bags.put("Genomic Neighborhood", res);
				}
				if(useStringInteractors.getBooleanValue())
				{
					List<Node> res=addString(p, pnode, graph);
					bags.put("STRING Interactors", res);
				}
				if(useBioPax.getBooleanValue())
				{
					if(usePathways.getBooleanValue())
					{
						List<Node> res=addPathways(p, pnode, graph);
						bags.put("Pathways", res);
					}
					if(useReactions.getBooleanValue())
					{
						List<Node> res=addReactions(p, pnode, graph);
						bags.put("Reactions", res);
					}
				}
				if(useUniProt.getBooleanValue())
				{
					List<Node> res=addUniProt(p, pnode, graph);
					bags.put("UniProt", res);
				}
			}
			model=new SuperModel(graph);			
			Color[] colors=GUIUtilities.rainbow(bags.size(), 1);

			int i=0;
			for(String s: bags.keySet())
			{
				if(!bags.get(s).isEmpty())
				{
					ComponentBag bag=new ComponentBag(model);
					bag.setName(s);
					bag.setColor(colors[i]);
					for(Node n:bags.get(s))
					{
						bag.addComponent(model.getComponent(n));					
					}				
					model.addBag(bag);
				}
				++i;
			}
		}

	}

	private List<Node> addUniProt(Probe p, MultiProbeNode pnode, Graph graph) throws Exception
	{
		List<Node> nodes=new ArrayList<Node>();
		List<String> ids=new ArrayList<String>();
		switch(uniprotMapping.getMappingSource())
		{
		case MappingSourceSetting.PROBE_NAMES: 
			ids.add(p.getName());

			break;
		case MappingSourceSetting.PROBE_DISPLAY_NAMES: 
			ids.add(p.getDisplayName());

			break;	
		case MappingSourceSetting.MIO: 
			if(uniprotMapping.getMappingGroup().contains(p))
			{
				ids.add(((StringMIO)uniprotMapping.getMappingGroup().getMIO(p)).getValue());
			}
			break;		
		}
		UniProtConnector uc=new UniProtConnector();
		UniProtParser parser=null;
		parser=uc.uniProtFetch(ids);

		for(String s: parser.getIdMaps().keySet())
		{
			int idx=parser.getIdMaps().get(s);
			// add protein
			Map<String, String> current=parser.getProtein().get(idx);
			nodes.addAll(addMap(current, pnode, graph));
			//gene
			current=parser.getGene().get(idx);
			nodes.addAll(addMap(current, pnode, graph));
			//comments
			current=parser.getComments().get(idx);
			nodes.addAll(addMap(current, pnode, graph));
			
			List<Map<String,String>> refs=parser.getReferences().get(idx);
			for(Map<String,String> ref:refs)
			{
				if(!ref.containsKey("title"))
					continue;
				MultiProbeNode node=new MultiProbeNode(graph);
				node.setName(ref.get("title"));
				node.setProperties(ref);
				node.setRole(Nodes.Roles.NOTE_ROLE);
				graph.addNode(node);
				graph.connect(pnode,node);
				nodes.add(node);
			}
			//interactors
			List<String> interactors=parser.getInteractors().get(idx);
			for(String st:interactors)
			{
				Probe pr=StringImport.getMappedProbe(mappings.get(p.getMasterTable().getDataSet()), p.getMasterTable().getDataSet().getMasterTable(), st);
				if(pr!=null)
				{
					MultiProbeNode node=new MultiProbeNode(graph, pr);
					graph.addNode(node);
					graph.connect(pnode, node);
				}
			}
		}
		
		return nodes;
	}
	
	private List<Node> addMap(Map<String, String> current, Node center, Graph g)
	{
		List<Node> res=new ArrayList<Node>();
		for(String k:current.keySet())
		{
			if(k==null || current.get(k)==null)
				continue;
			MultiProbeNode node=new MultiProbeNode(g);
			node.setName(k);
			node.setRole(Nodes.Roles.NOTE_ROLE);
			node.setProperty(Nodes.NOTE_TEXT, current.get(k));
			g.addNode(node);
			g.connect(center, node);
			res.add(node);
		}
		return res;
	}

	private List<Node> addProbeLists(Probe p, Node pnode, Graph g)
	{
		List<Node> res=new ArrayList<Node>();
		for(ProbeList pl: p.getProbeLists())
		{
			if(pl.getName().equals("global"))
				continue;
			MultiProbeNode plNode=new MultiProbeNode(g, pl);
			g.addNode(plNode);
			res.add(plNode);
			Edge e=new Edge(pnode, plNode);
			e.setName("contained in");
			g.connect(e);
		}
		return res;
	}

	private List<Node> addMIOs(Probe p, Node pnode, Graph g)
	{
		List<Node> resList=new ArrayList<Node>();
		MIGroupSelection<MIType> res=p.getMasterTable().getDataSet().getMIManager().getGroupsForObject(p);
		for(int i=0; i!= res.size(); ++i)
		{
			MIGroup grp=res.get(i);			
			MIONode node=new MIONode(g, p);
			node.setMiGroup(grp);
			g.addNode(node);
			resList.add(node);
			Edge e=new Edge(pnode, node);
			e.setName(grp.getName());
			g.connect(e);
		}
		return resList;
	}

	private List<Node> addCrossDS(Probe p, Node pnode, Graph g)
	{
		String mappedName=mappings.get(p.getMasterTable().getDataSet()).mappedName(p);
		List<Node> resList=new ArrayList<Node>();
		for(DataSet ds:probeLists.keySet())
		{
			if(ds==p.getMasterTable().getDataSet())
				continue;
			for(ProbeList pl:probeLists.get(ds))
			{
				for(Probe pr: pl)
				{
					if(mappings.get(ds).mappedName(pr)!=null && mappings.get(ds).mappedName(pr).equals(mappedName))
					{
						MultiProbeNode pn=new MultiProbeNode(g, pr);
						g.addNode(pn);
						resList.add(pn);
						Edge e=new Edge(pnode, pn);
						e.setName(ds.getName());
						g.connect(e);					
					}
				}
			}
		}
		return resList;
	}

	private  List<Node> addSimilarProbes(Probe p, Node pNode, Graph g)
	{
		DistanceMeasurePlugin distance=distanceMeasureSetting.getInstance();
		double tol=cutoff.getDoubleValue();
		List<Node> resList=new ArrayList<Node>();
		for(Probe probe:p.getMasterTable().getProbes().values())
		{
			if(probe==p) continue;
			double d=distance.getDistance(p.getValues(), probe.getValues());
			if(d < tol)
			{
				MultiProbeNode pn=new MultiProbeNode(g, probe);
				g.addNode(pn);
				resList.add(pn);
				Edge e=new Edge(pNode, pn);
				e.setName(distance.getPluginInfo().getName()+"="+NumberFormat.getNumberInstance().format(d));
				g.connect(e);	
			}
		}
		return resList;
	}

	@SuppressWarnings("unchecked")
	private List<Node> addGenomicNeighbors(Probe center, Node pNode, Graph g)
	{
		MIGroupSelection<MIType> sel= center.getMasterTable().getDataSet().getMIManager().getGroupsForType(LocusMIO.myType);
		if(sel.isEmpty())
			return new ArrayList<Node>();

		MIGroup group=sel.get(0);
		ChromosomeSetContainer csc=new ChromosomeSetContainer(new LocusChromosomeObject.Factory<Probe>());

		for(Probe p:center.getMasterTable().getProbes().values())
		{
			if(!group.contains(p))
				continue;
			AbstractGeneticCoordinate pc=((LocusMIO) group.getMIO(p)).getValue().getCoordinate();
			((LocusChromosomeObject<Probe>)csc.getChromosome(pc.getChromosome())).addLocus(pc.getFrom(), pc.getTo(), pc.getStrand(), p);		
		}

		if(!group.contains(center))
			return new ArrayList<Node>();

		AbstractGeneticCoordinate pc=((LocusMIO)group.getMIO(center)).getValue().getCoordinate();

		List<LocusGeneticCoordinateObject<Probe>> lolgcp = 
			((LocusChromosomeObject<Probe>)csc.getChromosome(pc.getChromosome())).getOverlappingLoci(
					pc.getFrom()-maxDistance.getIntValue(),	pc.getTo()+maxDistance.getIntValue(),
					Strand.UNSPECIFIED);

		List<Node> resList=new ArrayList<Node>();

		for(LocusGeneticCoordinateObject<Probe> olgcp:lolgcp)
		{
			long d=pc.getDistanceTo(olgcp, true);
			Probe np=olgcp.getObject();
			if(np==center)
				continue;
			if(olgcp.isUpstreamOf(pc))
			{
				d*=-1;
			}

			MultiProbeNode plNode=new MultiProbeNode(g, np);
			g.addNode(plNode);
			resList.add(plNode);
			Edge e=new Edge(pNode, plNode);
			e.setName("chromosomal distance "+d);
			g.connect(e);		
		}
		return resList;
	}

	private List<Node> addString(Probe center, Node pNode, Graph g)
	{
		List<String> ids=new ArrayList<String>();
		MasterTable mt=center.getMasterTable();
		MappingSourceSetting mappingSetting=stringMapping;
		//=mappings.get(mt.getDataSet());

		ids.add(mappingSetting.mappedName(center));
		StringConnector sc=new StringConnector(database.getStringValue());		
		List<StringInteraction> interactions=null;
		try{
			if(interactionsSetting.getBooleanValue())
			{
				interactions=sc.getInteractions(ids, maxSetting.getIntValue(), scoreSetting.getIntValue());
			}else
			{
				interactions=sc.getInteractors(ids, maxSetting.getIntValue(), scoreSetting.getIntValue());
			}
		}catch(Exception e)
		{			
			return new ArrayList<Node>();
		}

		List<Node> resList=new ArrayList<Node>();
		for(StringInteraction i:interactions)
		{
			i.trimIDs();
			Node leftNode=g.findNode(i.left);
			if(leftNode==null)
			{
				leftNode=new MultiProbeNode(g);
				leftNode.setName(i.left);
				g.addNode(leftNode);
				resList.add(leftNode);
				Probe p=StringImport.getMappedProbe(mappingSetting, mt, i.left);
				if(p!=null)
				{
					((MultiProbeNode)leftNode).addProbe(p);
					leftNode.setRole(Nodes.Roles.PROBE_ROLE);					
				}
			}
			Node rightNode=g.findNode(i.right);
			if(rightNode==null)
			{
				rightNode=new MultiProbeNode(g);
				rightNode.setName(i.right);
				g.addNode(rightNode);
				resList.add(rightNode);
				Probe p=StringImport.getMappedProbe(mappingSetting, mt, i.left);
				if(p!=null)				
				{
					((MultiProbeNode)rightNode).addProbe(p);
					rightNode.setRole(Nodes.Roles.PROBE_ROLE);				
				}
			}
			if(leftNode!=rightNode)
			{
				Edge e=new Edge(leftNode, rightNode);
				e.setName("Interaction");
				g.connect(e);
			}			
		}
		return resList;
	}

	private List<Node> addPathways(Probe center, Node pNode, Graph g) throws Exception
	{
//		MasterTable mt=center.getMasterTable();
//		MappingSourceSetting mappingSetting=mappings.get(mt.getDataSet());
		MappingSourceSetting mappingSetting=bioPaxMapping;
		String mn=mappingSetting.mappedName(center);


		String s=squeezer.getProteinForXref(mn);
		List<String> ids=squeezer.getPathwaysForProtein(s);
		List<Node> resList=new ArrayList<Node>();
		Graph grph=squeezer.getPathwayGraph(ids, BioPaxSqueezer2.getListOfSmallMolecules());
		annotateNodes(probeLists, grph, mappings);
		for(Node n:grph.getNodes())
		{
			g.addNode(n);
			resList.add(n);
			Edge e=new Edge(pNode, n);
			e.setName("Pathway");
			g.connect(e);
		}
		grph.clear();
		return resList;
	}

	private List<Node> addReactions(Probe center, Node pNode, Graph g) throws Exception
	{
//		MasterTable mt=center.getMasterTable();
//		MappingSourceSetting mappingSetting=mappings.get(mt.getDataSet());
		MappingSourceSetting mappingSetting=bioPaxMapping;
		String mn=mappingSetting.mappedName(center);

		String s=squeezer.getProteinForXref(mn);
		List<String> trs=squeezer.getReactionsForProtein(s);
		Set<String> ids=new HashSet<String>();
		for(String t:trs)
		{
			ids.add(t);
		}	
		List<Node> resList=new ArrayList<Node>();
		for(String id:ids)
		{
			Graph rg=squeezer.getReactionGraph(id);
			annotateNodes(probeLists, rg, mappings);
			g.add(rg);
			resList.addAll(rg.getNodes());
			for(Node n: rg.getNodes())
			{
				if(n.getRole().equals(SBGNRoles.PROCESS_ROLE))
				{
					Edge e=new Edge(pNode, n);
					e.setName("Reaction");
					g.connect(e);
				}
			}
			rg.clear();
		}
		return resList;
	}

}
