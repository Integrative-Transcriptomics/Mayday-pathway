package mayday.graphviewer.graphmodelprovider;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import mayday.core.DataSet;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.MappingSourceSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.graph.Graph;
import mayday.core.tasks.AbstractTask;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.core.Utilities;
import mayday.graphviewer.datasources.biopax2.BioPaxSqueezer2;
import mayday.graphviewer.datasources.biopax2.PathwaySelectionPanel;
import mayday.vis3.graph.layout.CanvasLayouter;
import mayday.vis3.graph.layout.FruchtermanReingoldLayout;

public class BioPaxGraphModelProvider extends AbstractGraphModelProvider
{
	private RestrictedStringSetting methodSetting;
	private BooleanSetting ignoreSmallChemicals;
	private PathSetting pathSetting;
	private Map<DataSet, MappingSourceSetting> mappings;

	private BioPaxSqueezer2 squeezer;
	private PathwaySelectionPanel pathwaySelector;
	private static final String[] methods={"Single Pathway","Relation between Reactions","Relations between Pathways"};

	private static final String LAST_BIOPAX_FILE="AnacondaLastBioPaxDir";
	
	public BioPaxGraphModelProvider() 
	{
		methodSetting=new RestrictedStringSetting("Create Graph", null, 0, methods);
		ignoreSmallChemicals=new BooleanSetting("Ignore ubiquitous chemicals", 
				"Do not connect reactions or pathways if they only share\n"+
				"ubuitous chemicals like H+, ATP, H20 or others. ", true);
		String d= Utilities.prefs.get(LAST_BIOPAX_FILE, System.getProperty("user.home"));
		pathSetting=new PathSetting("Pathway File", null, d, false, true, false);
		basicSetting.addSetting(pathSetting).addSetting(methodSetting).addSetting(ignoreSmallChemicals);		
	}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"mayday.graphModelProvider.biopax",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Create a new graph from a biopax file",
				getName()				
		);		
		return pli;			
	}

	@Override
	public String getName() 
	{
		return "BioPax";
	}

	@Override
	public String getDescription() 
	{
		return "Build a graph from information stored in a biopax file.";
	}

	@Override
	public CanvasLayouter defaultLayouter() 
	{
		return new FruchtermanReingoldLayout();
	}

	@Override
	public AbstractTask buildGraph() 
	{
		return new GraphTask();
	}

	@Override
	public AbstractTask parseFile() 
	{
		Utilities.prefs.put(LAST_BIOPAX_FILE, pathSetting.getStringValue());
		return new ParseTask();
	}

	@Override
	public boolean isAskForProbeLists() 
	{
		return true;
	}

	@Override
	public boolean isAskForFileSetting() 
	{
		return true;
	}

	@Override
	public Component getAdditionalComponent() 
	{
		pathwaySelector=new PathwaySelectionPanel(squeezer);
		return pathwaySelector;
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

	private class GraphTask extends AbstractTask
	{
		public GraphTask() 
		{
			super("Building Graph ("+ methodSetting.getStringValue()+")");
		}

		@Override
		protected void doWork() throws Exception 
		{
			List<String> ids=new ArrayList<String>();
			switch (methodSetting.getSelectedIndex()) 
			{
			case 0:
				String id=pathwaySelector.getSelectedObject();
				List<String> reas=squeezer.getReactionsForPathway(id);
				ids.addAll(reas);
				break;
			case 2: 
				ids=pathwaySelector.getSelectedObjects();
				break;
			case 1: 
				for(String pw: pathwaySelector.getSelectedObjects())
				{
					for(String t:squeezer.getReactionsForPathway(pw))
					{
						ids.add(t);
					}
				}
				break;
			default:
				break;
			}

			Graph graph=null;
			Set<String> ignoreSet=ignoreSmallChemicals.getBooleanValue()?BioPaxSqueezer2.getListOfSmallMolecules():new HashSet<String>();
			// create a graph for 
			switch(methodSetting.getSelectedIndex())
			{
			case 0:	graph=squeezer.getReactionGraph(ids);
			break;
			case 1: graph=squeezer.getReactionSubgraph(ids, ignoreSet);
			break;
			case 2: graph=squeezer.getPathwayGraph(ids, ignoreSet);
			break;
			}

			annotateNodes(probeLists, graph, mappings);

			//			//			// build map of mappings
			//			Map<String, Probe> nameProbeMap=new HashMap<String, Probe>();
			//			for(DataSet ds: probeLists.keySet())
			//			{
			//				MappingSourceSetting ms=mappings.get(ds);				
			//				for(ProbeList pl: probeLists.get(ds))
			//				{					
			//					for(Probe p:pl)
			//					{
			//						String s= ms.mappedName(p);
			//						if(s!=null)
			//							nameProbeMap.put(ms.mappedName(p),p);
			//					}
			//				}
			//			}
			//
			//			for(Node n: graph.getNodes())
			//			{
			//				DefaultNode dn=(DefaultNode) n;
			//				for(String s: dn.getProperties().values())
			//				{
			//
			//					if(s.startsWith("[") && s.endsWith("]"))
			//					{
			//						// we have a serialized List<String>						
			//						String[] tok= s.substring(1,s.length()-1).split(",");						
			//						for(String st:tok)
			//						{
			//							if(nameProbeMap.containsKey(st.trim()))
			//							{
			//								((MultiProbeNode)dn).addProbe(nameProbeMap.get(st.trim()));								
			//							}								
			//						}						
			//					}else
			//					{
			//						if(nameProbeMap.containsKey(s))
			//						{
			//							((MultiProbeNode)dn).addProbe(nameProbeMap.get(s));
			//						}					
			//					}					
			//				}
			//
			//
			//
			//			}
			model=new SuperModel(graph);

		}
		@Override
		protected void initialize() {}
	}

}
