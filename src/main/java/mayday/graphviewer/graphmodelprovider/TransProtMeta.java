package mayday.graphviewer.graphmodelprovider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.MappingSourceSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.maps.MultiHashMap;
import mayday.core.tasks.AbstractTask;
import mayday.graphviewer.core.SBGNRoles;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.core.Utilities;
import mayday.graphviewer.crossViz3.core.DataSetScope;
import mayday.graphviewer.datasources.biopax2.BioPaxSqueezer2;
import mayday.graphviewer.layout.CentralDogmaLayouter;
import mayday.pathway.sbgn.processdiagram.ProcessDiagram;
import mayday.vis3.graph.layout.CanvasLayouter;

public class TransProtMeta extends AbstractGraphModelProvider
{
	private Map<DataSet, ObjectSelectionSetting<DataSetScope>> scopes;
	private Map<DataSet, MappingSourceSetting> mappings;

	private PathSetting pathSetting;	
	private BooleanSetting ignoreSmallChemicals;
	private BooleanSetting showOtherControllers;
	private BooleanSetting strictMapping;
	private BioPaxSqueezer2 squeezer;	
	private static final String LAST_BIOPAX_FILE="AnacondaLastBioPaxDir";

	public TransProtMeta() 
	{
		String d= Utilities.prefs.get(LAST_BIOPAX_FILE, System.getProperty("user.home"));
		pathSetting=new PathSetting("Pathway File", null, d, false, true, false);
		ignoreSmallChemicals=new BooleanSetting("Ignore ubiquitous chemicals", 
				"Do not connect reactions or pathways if they only share\n"+
				"ubuitous chemicals like H+, ATP, H20 or others. ", true);
		showOtherControllers=new BooleanSetting("Show other Proteins in reactions", null, true);
		strictMapping=new BooleanSetting("Strict Mapping", null, true);
		basicSetting.addSetting(pathSetting).addSetting(ignoreSmallChemicals)
			.addSetting(showOtherControllers).addSetting(strictMapping);	
	}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"mayday.graphModelProvider.proteintranscriptmeta",
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
		return new CentralDogmaLayouter();
	}

	@Override
	public Setting getInformedSetting() 
	{
		scopes=new TreeMap<DataSet, ObjectSelectionSetting<DataSetScope>>();
		mappings=new TreeMap<DataSet, MappingSourceSetting>();
		informedSetting=new HierarchicalSetting(getName());
		for(DataSet ds:probeLists.keySet())
		{
			HierarchicalSetting dsSetting=new HierarchicalSetting(ds.getName());
			ObjectSelectionSetting<DataSetScope> scopeSetting=new ObjectSelectionSetting<DataSetScope>("Role",null,0,DataSetScope.values());
			scopes.put(ds, scopeSetting);
			MappingSourceSetting mapping=new MappingSourceSetting(ds);
			mappings.put(ds, mapping);
			dsSetting.addSetting(mapping).addSetting(scopeSetting);
			informedSetting.addSetting(dsSetting);
		}				
		return informedSetting;
	}

	@Override
	public String getName() 
	{
		return "Transcript - Protein - Metabolites";
	}

	@Override
	public String getDescription() 
	{
		return "Create a graphical representation of translation and protein activity; showing transcripts,proteins and affected metabolites";
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
			super("Parsing BioPax file");
		}

		@Override
		protected void doWork() throws Exception 
		{
			squeezer=new BioPaxSqueezer2(pathSetting.getStringValue());
		}
		@Override
		protected void initialize() {}
	}

	private class BuildGraphTask extends AbstractTask
	{
		public BuildGraphTask() 
		{
			super(TransProtMeta.this.getName());
		}

		@Override
		protected void initialize() 
		{						
		}		

		@Override
		protected void doWork() throws Exception 
		{
			Graph graph=new Graph();

			MultiHashMap<String,Probe> mappedTranscriptProbes=new MultiHashMap<String, Probe>();
			MultiHashMap<String,Probe> mappedProteinProbes=new MultiHashMap<String, Probe>();
			MultiHashMap<String,Probe> mappedMetaboliteProbes=new MultiHashMap<String, Probe>();


			for(DataSet ds: probeLists.keySet())
			{
				DataSetScope scope=scopes.get(ds).getObjectValue();
				MappingSourceSetting mapping=mappings.get(ds);

				for(ProbeList pl: probeLists.get(ds))
				{
					for(Probe p: pl)
					{
						if(scope==DataSetScope.TRANSCRIPTOME)
						{
							String s=mapping.mappedName(p);
							if(s!=null)
								mappedTranscriptProbes.put(s, p);
						}
						if(scope==DataSetScope.PROTEOME)
						{
							String s=mapping.mappedName(p);
							if(s!=null)
								mappedProteinProbes.put(s, p);
						}
						if(scope==DataSetScope.METABOLOME)
						{
							String s=mapping.mappedName(p);
							if(s!=null)
								mappedMetaboliteProbes.put(s, p);
						}
					}
				}
			}



			Set<String> commonNames=new TreeSet<String>();
			commonNames.addAll(mappedTranscriptProbes.keySet());
			if(strictMapping.getBooleanValue())
				commonNames.retainAll(mappedProteinProbes.keySet());
			
			writeLog("Found "+commonNames.size()+ " transcript-protein pairs\n");
			List<String> reas=new ArrayList<String>();
			
			System.out.println(commonNames.size()+" proteins");
			for(String n:commonNames)
			{
				String pn=squeezer.getProteinForXref(n);
				reas.addAll(squeezer.getReactionsForProtein(pn));
			}
			Map<String, Graph> graphs=new HashMap<String, Graph>();
			System.out.println(reas.size()+" reactions");
			for(String rea:reas)
			{
				graphs.put(rea, squeezer.getReactionGraph(rea));
				System.out.println(".");
			}
			writeLog("Parsed  "+graphs.size()+ "  reactions\n");
			
			int i=0;
			setProgress(0, "Creating graph");
			
			for(String s:commonNames)
			{
				setProgress( ((10000*i)/commonNames.size()));
				if(i%100==0)
					writeLog("");
				MultiProbeNode transNode=new MultiProbeNode(graph,mappedTranscriptProbes.get(s));
				graph.addNode(transNode);
				transNode.setName(s);
				transNode.setRole(ProcessDiagram.NUCLEIC_ACID_FEATURE_ROLE);
				MultiProbeNode protNode=new MultiProbeNode(graph,mappedProteinProbes.get(s));
				graph.addNode(protNode);
				protNode.setName(s);
				protNode.setRole(ProcessDiagram.MACROMOLECULE_ROLE);

				MultiProbeNode opNode=new MultiProbeNode(graph);
				opNode.setRole(ProcessDiagram.OMITTED_PROCESS_ROLE);
				graph.addNode(opNode);
				Edge e1=new Edge(transNode,opNode);
				e1.setRole(ProcessDiagram.NECESSARY_STIMULATION_ROLE);

				Edge e2=new Edge(opNode, protNode);
				e2.setRole(ProcessDiagram.PRODUCTION_ROLE);

				graph.connect(e1);
				graph.connect(e2);

				//getCatalyzedReaction
				
				String prot=squeezer.getProteinForXref(s);
				if(prot==null)
				{
					++i;
					continue;
				}				
				List<String>  controls=squeezer.getControlsForProtein(prot);
				for(String ctrl:controls)
				{
					String rea=squeezer.getObject(ctrl, BioPaxSqueezer2.CONTROLLED).get(0);
					Graph subGraph=graphs.get(rea);	
					if(subGraph==null)
						continue;
					Node reaNode=prepareReactionSubgraph(subGraph, s);
					graph.add(subGraph);
					Edge e=new Edge(protNode,reaNode);
					String ot=squeezer.getObjectType(ctrl);
					
					if(ot.equals(BioPaxSqueezer2.CATALYSIS))
					{
						e.setRole(SBGNRoles.CATALYSIS_ROLE);
					}
					if(ot.equals(BioPaxSqueezer2.MODULATION))
					{
						List<String> ct=squeezer.getObject(ctrl, BioPaxSqueezer2.CONTROL_TYPE);
						if(!ct.isEmpty())
						{
							String cts=ct.get(0);
							if(cts.startsWith(BioPaxSqueezer2.INHIBITION))
								e.setRole(SBGNRoles.INHIBITION_ROLE);
							if(cts.startsWith(BioPaxSqueezer2.ACTIVATION))
								e.setRole(SBGNRoles.NECESSARY_STIMULATION_ROLE);
						}else
							e.setRole(SBGNRoles.MODULATION_ROLE);	
					}
					
					graph.connect(e);		
				}
				++i;
			}	

			annotateNodesIgnoreCase(probeLists, graph, mappings);			
			model=new SuperModel(graph);

		}	

		private Node prepareReactionSubgraph(Graph subGraph, String s)
		{
			Node reaNode=null;
			for(Node n: subGraph.getNodes())
			{
				if(n.getRole().equals(SBGNRoles.PROCESS_ROLE))
				{
					reaNode=n;
					break;
				}
			}
			if(showOtherControllers.getBooleanValue())
			{
				Node killNode=null;
				for(Node n: subGraph.getNodes())
				{
					if(n.getRole().equals(SBGNRoles.MACROMOLECULE_ROLE))
					{
						if(n.getName().equals(s))
						{
							killNode=n;
							break;
						}
						DefaultNode dn=(DefaultNode)n;
						for(String sp: dn.getProperties().values())
						{
							if(sp.startsWith("[") && sp.endsWith("]"))
							{
								// we have a serialized List<String>						
								String[] tok= sp.substring(1,sp.length()-1).split(",");						
								for(String st:tok)
								{
									if(st.equals(s))
									{
										killNode=n;
										break;
									}
								}						
							}else
							{
								if(sp.equals(s))
								{
									killNode=n;
									break;
								}					
							}					
						}							
					}
				}
				subGraph.removeNode(killNode);
			}else
			{
				List<Node> killNodes=new ArrayList<Node>();
				for(Node n: subGraph.getNodes())
				{
					if(n.getRole().equals(SBGNRoles.MACROMOLECULE_ROLE))
					{
						killNodes.add(n);
					}					
				}
				for(Node n: killNodes)
				{
					subGraph.removeNode(n);
				}				
			}			
			return reaNode;
		}
	}

}
