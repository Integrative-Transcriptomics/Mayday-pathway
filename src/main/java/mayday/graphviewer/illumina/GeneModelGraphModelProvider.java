//package mayday.graphviewer.illumina;
//
//import java.awt.Component;
//import java.io.File;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.TreeMap;
//
//import mayday.core.DataSet;
//import mayday.core.Probe;
//import mayday.core.pluma.PluginInfo;
//import mayday.core.pluma.PluginManagerException;
//import mayday.core.settings.Setting;
//import mayday.core.settings.generic.HierarchicalSetting;
//import mayday.core.settings.generic.ObjectSelectionSetting;
//import mayday.core.settings.typed.MappingSourceSetting;
//import mayday.core.settings.typed.PathSetting;
//import mayday.core.structures.graph.Graph;
//import mayday.core.structures.maps.MultiHashMap;
//import mayday.core.tasks.AbstractTask;
//import mayday.genetics.coordinatemodel.GBAtom;
//import mayday.graphviewer.core.SuperModel;
//import mayday.graphviewer.graphmodelprovider.AbstractGraphModelProvider;
//import mayday.graphviewer.illumina.GeneModelTableModel.GeneModelTablePanel;
//import mayday.vis3.graph.layout.CanvasLayouter;
//import mayday.vis3.graph.layout.FruchtermanReingoldLayout;
//
//public class GeneModelGraphModelProvider extends AbstractGraphModelProvider
//{
//	private static final GeneModelStyle[] styles={GeneModelStyle.CONDENSED, GeneModelStyle.COMPRESSED, GeneModelStyle.VERBOSE};
//	
//	private PathSetting pathSetting;
//	private ObjectSelectionSetting<GeneModelStyle> styleSetting=new ObjectSelectionSetting<GeneModelStyle>("Style", null, 0, styles);	
//	private GFFGeneModelParser gffParser;
//	private GeneModelTableModel.GeneModelTablePanel modelSelector;
//	private Map<DataSet, MappingSourceSetting> mappings;
//	
//	public GeneModelGraphModelProvider() 
//	{
//		pathSetting=new PathSetting("GFF File", null, null, false, true, false);
//		basicSetting.addSetting(pathSetting).addSetting(styleSetting);
//	}
//
//	@Override
//	public PluginInfo register() throws PluginManagerException 
//	{
//		PluginInfo pli = new PluginInfo(
//				this.getClass(),
//				"mayday.graphModelProvider.gff",
//				new String[]{},
//				MC,
//				new HashMap<String, Object>(),
//				"Stephan Symons",
//				"symons@informatik.uni-tuebingen.de",
//				getDescription(),
//				getName()				
//		);		
//		return pli;			
//	}
//
//	@Override
//	public String getName() 
//	{
//		return "GFF Gene Models";
//	}
//
//	@Override
//	public String getDescription() 
//	{
//		return "Build a graph from reactions stored in a SBML file.";
//	}
//
//	@Override
//	public CanvasLayouter defaultLayouter() 
//	{
//		return new FruchtermanReingoldLayout();
//	}
//
//	@Override
//	public AbstractTask buildGraph() 
//	{
//		return new GraphTask();
//	}
//
//	@Override
//	public AbstractTask parseFile() 
//	{
//		return new ParseTask();
//	}
//
//	@Override
//	public boolean isAskForProbeLists() 
//	{
//		return true;
//	}
//	
//	@Override
//	public boolean isAskForFileSetting() 
//	{
//		return true;
//	}
//	
//	@Override
//	public Component getAdditionalComponent() 
//	{
//		if(modelSelector==null)
//			modelSelector=new GeneModelTablePanel(gffParser);
//		return modelSelector;
//	}
//
//	@Override
//	public Setting getInformedSetting() 
//	{
//		informedSetting=new HierarchicalSetting(getName());
//		mappings=new TreeMap<DataSet, MappingSourceSetting>();
//		for(DataSet ds:probeLists.keySet())
//		{
//			MappingSourceSetting mapping=new MappingSourceSetting(ds);
//			mappings.put(ds, mapping);
//			HierarchicalSetting dsSetting=new HierarchicalSetting(ds.getName());
//			dsSetting.addSetting(mapping);
//			informedSetting.addSetting(dsSetting);
//		}
//		return informedSetting;
//	}
//
//	private class ParseTask extends AbstractTask
//	{
//		public ParseTask() 
//		{
//			super("Parsing GFF file");
//		}
//
//		@Override
//		protected void doWork() throws Exception 
//		{
//			gffParser=new GFFGeneModelParser();
//			gffParser.parse(new File(pathSetting.getStringValue()));	
//		}
//		@Override
//		protected void initialize() {}
//	}
//
//	private class GraphTask extends AbstractTask
//	{
//		public GraphTask() 
//		{
//			super("Building Graph from Gene Models");
//		}
//
//		@Override
//		protected void doWork() throws Exception 
//		{
//			Graph graph=new GeneModelGraphFactory().buildOverallGraph(gffParser, 
//					modelSelector.getSelectedModels(), 
//					new MultiHashMap<GBAtom, Probe>(),
//					styleSetting.getObjectValue()); 
//			
//			
//			annotateNodes(probeLists, graph, mappings);
//			model=new SuperModel(graph);
//		}
//		
//		@Override
//		protected void initialize() {}
//	}
//	
//}
