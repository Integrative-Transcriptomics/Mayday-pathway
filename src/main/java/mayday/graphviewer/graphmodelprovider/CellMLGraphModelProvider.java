package mayday.graphviewer.graphmodelprovider;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import mayday.core.DataSet;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.MappingSourceSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.core.structures.graph.Graph;
import mayday.core.tasks.AbstractTask;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.datasources.cellML.CellMLParser;
import mayday.vis3.graph.layout.CanvasLayouter;
import mayday.vis3.graph.layout.FruchtermanReingoldLayout;

public class CellMLGraphModelProvider extends AbstractGraphModelProvider
{
	private PathSetting pathSetting;
	private Map<DataSet, MappingSourceSetting> mappings;

	private CellMLParser parser;

	public CellMLGraphModelProvider() 
	{
		pathSetting=new PathSetting("File", null, null, false, true, false);
		basicSetting.addSetting(pathSetting);		
	}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"mayday.graphModelProvider.cellml",
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
		return "CellML";
	}

	@Override
	public String getDescription() 
	{
		return "Build a graph from interactions stored in a CellML-file.";
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
			super("Parsing CellML-file");
		}

		@Override
		protected void doWork() throws Exception 
		{
			parser=new CellMLParser();
			parser.parse(pathSetting.getStringValue());				
		}
		@Override
		protected void initialize() {}
	}

	private class GraphTask extends AbstractTask
	{
		public GraphTask() 
		{
			super("Building Graph from CellML-file");
		}

		@Override
		protected void doWork() throws Exception 
		{
			Graph graph=null;
			graph=parser.createGraph();			
			annotateNodes(probeLists, graph, mappings);
			model=new SuperModel(graph);
		}
		@Override
		protected void initialize() {}
	}
}
