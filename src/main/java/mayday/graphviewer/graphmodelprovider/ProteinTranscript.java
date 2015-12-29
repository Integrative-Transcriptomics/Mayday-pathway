package mayday.graphviewer.graphmodelprovider;

import java.util.HashMap;
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
import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.maps.MultiHashMap;
import mayday.core.tasks.AbstractTask;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.crossViz3.core.DataSetScope;
import mayday.graphviewer.layout.CentralDogmaLayouter;
import mayday.pathway.sbgn.processdiagram.ProcessDiagram;
import mayday.vis3.graph.layout.CanvasLayouter;

public class ProteinTranscript extends AbstractGraphModelProvider
{
	private BooleanSetting createSBGNGraph=new BooleanSetting("Create SBGN Graph", 
			"If selected, use SBGN Notation to display the components. If not selected, transcript and protein nodes are connected", true);
	
	private Map<DataSet, ObjectSelectionSetting<DataSetScope>> scopes;
	private Map<DataSet, MappingSourceSetting> mappings;
	
	public ProteinTranscript() 
	{
		basicSetting.addSetting(createSBGNGraph);		
	}
		
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"mayday.graphModelProvider.proteintranscript",
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
		return "Transcript - Protein";
	}
	
	@Override
	public String getDescription() 
	{
		return "Create a graphical representation of translation; showing transcripts and proteins.";
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
			super(ProteinTranscript.this.getName());
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
			
			
			for(DataSet ds: probeLists.keySet())
			{
				DataSetScope scope=scopes.get(ds).getObjectValue();
				MappingSourceSetting mapping=mappings.get(ds);
				
				for(ProbeList pl: probeLists.get(ds))
				{
					for(Probe p: pl)
					{
						if(scope==DataSetScope.TRANSCRIPTOME)
							mappedTranscriptProbes.put(mapping.mappedName(p), p);
						if(scope==DataSetScope.PROTEOME)
							mappedProteinProbes.put(mapping.mappedName(p), p);
					}
				}
			}
			
			boolean sbgn=createSBGNGraph.getBooleanValue();
			
			Set<String> commonNames=new TreeSet<String>();
			commonNames.addAll(mappedTranscriptProbes.keySet());
			commonNames.retainAll(mappedProteinProbes.keySet());
			for(String s:commonNames)
			{
				MultiProbeNode transNode=new MultiProbeNode(graph,mappedTranscriptProbes.get(s));
				graph.addNode(transNode);
				transNode.setName(s);
				transNode.setRole(ProcessDiagram.NUCLEIC_ACID_FEATURE_ROLE);
				MultiProbeNode protNode=new MultiProbeNode(graph,mappedProteinProbes.get(s));
				graph.addNode(protNode);
				protNode.setName(s);
				protNode.setRole(ProcessDiagram.MACROMOLECULE_ROLE);
				
				if(sbgn)
				{
					MultiProbeNode opNode=new MultiProbeNode(graph);
					opNode.setRole(ProcessDiagram.OMITTED_PROCESS_ROLE);
					graph.addNode(opNode);
					Edge e1=new Edge(transNode,opNode);
					e1.setRole(ProcessDiagram.NECESSARY_STIMULATION_ROLE);
					
					Edge e2=new Edge(opNode, protNode);
					e2.setRole(ProcessDiagram.PRODUCTION_ROLE);
					
					graph.connect(e1);
					graph.connect(e2);
				}else
				{
					graph.connect(transNode,protNode);
				}
				
			}		
			model=new SuperModel(graph);
			
		}		
	}
}
