package mayday.graphviewer.graphmodelprovider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import mayday.core.DataSet;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.filemanager.FMFile;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.MappingSourceSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.graph.nodes.Nodes;
import mayday.core.tasks.AbstractTask;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.core.Utilities;
import mayday.graphviewer.datasources.obo.OboParser;
import mayday.graphviewer.datasources.obo.OboTerm;
import mayday.graphviewer.datasources.obo.SBGNAssigner;
import mayday.graphviewer.datasources.sbml.SBMLParser;
import mayday.vis3.graph.layout.CanvasLayouter;
import mayday.vis3.graph.layout.FruchtermanReingoldLayout;

public class SBMLGraphModelProvider  extends AbstractGraphModelProvider
{
	private PathSetting pathSetting;
	private BooleanHierarchicalSetting customOBOFile=new BooleanHierarchicalSetting("Custom OBO File", null, false);
	private PathSetting oboSetting;
	private Map<DataSet, MappingSourceSetting> mappings;

	private SBMLParser parser;
	private SBGNAssigner sbgnA;

	private static final String LAST_SBML_FILE="AnacondaLastSBMLDir";
	private static final String LAST_OBO_FILE="AnacondaLastOBODir";
	
	public SBMLGraphModelProvider() 
	{
		String sbml= Utilities.prefs.get(LAST_SBML_FILE, System.getProperty("user.home"));
		String obo= Utilities.prefs.get(LAST_OBO_FILE, System.getProperty("user.home"));
		pathSetting=new PathSetting("SBML File", "The file that contains the SBML model", sbml, false, true, false);
		oboSetting=new PathSetting("Obo File", "Using an OBO file would allow to map SBO terms as used in the\n SBML file to SBGN", obo, false, true, false);
		customOBOFile.addSetting(oboSetting);
		basicSetting.addSetting(pathSetting).addSetting(customOBOFile);		
	}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"mayday.graphModelProvider.sbml",
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
	public String getName() 
	{
		return "SBML";
	}

	@Override
	public String getDescription() 
	{
		return "Build a graph from reactions stored in a SBML file.";
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
		return new ParseTask();
	}

	@Override
	public boolean isAskForProbeLists() 
	{
		return true;
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
			super("Parsing SBML file");
		}

		@Override
		protected void doWork() throws Exception 
		{
			writeLog("Parsing SBML file: "+pathSetting.getStringValue()+"\n");
			parser=new SBMLParser();
			parser.parse(pathSetting.getStringValue());
			Utilities.prefs.put(LAST_SBML_FILE, pathSetting.getStringValue());
			writeLog("Parsing complete: "+pathSetting.getStringValue()+"\n");

			FMFile rconn = PluginManager.getInstance().getFilemanager().getFile("/mayday/sbgn/SBOtoSBGN.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(rconn.getStream()));
			Map<String, String> sbmlTosbgn=SBGNAssigner.parseMappingFile(br);
			Map<String, OboTerm> terms=new HashMap<String, OboTerm>();
			if(customOBOFile.getBooleanValue())
			{
				writeLog("Parsing OBO File: "+oboSetting.getStringValue()+"\n");
				OboParser oboParser=new OboParser();
				terms=oboParser.parse(oboSetting.getStringValue());
				sbgnA=new SBGNAssigner(sbmlTosbgn, terms);
				writeLog("Parsing OBO File complete"+"\n");
				Utilities.prefs.put(LAST_OBO_FILE, oboSetting.getStringValue());
			}
			sbgnA=new SBGNAssigner(sbmlTosbgn, terms);

		}
		@Override
		protected void initialize() {}
	}

	private class GraphTask extends AbstractTask
	{
		public GraphTask() 
		{
			super("Building Graph from SBML file");
		}

		@Override
		protected void doWork() throws Exception 
		{
			Graph graph=null;
			graph=parser.createGraph();			
			annotateNodes(probeLists, graph, mappings);

			for(Node n: graph.getNodes())
			{
				MultiProbeNode node=(MultiProbeNode)n;
				if(node.hasProperty(SBMLParser.SBO_TERM))
				{
					String termId=node.getPropertyValue(SBMLParser.SBO_TERM);
					String role=sbgnA.getRoleForTerm(termId);
					n.setRole(role==null?Nodes.Roles.NODE_ROLE:role);
				}
			}			
			model=new SuperModel(graph);
		}

		@Override
		protected void initialize() {}
	}
}

