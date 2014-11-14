package mayday.graphviewer.graphmodelprovider;

import java.awt.Component;
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
import mayday.core.tasks.AbstractTask;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.core.Utilities;
import mayday.graphviewer.datasources.biopax2.BioPaxSBGNGraph;
import mayday.pathway.biopax.parser.BioPaxParser;
import mayday.pathway.biopax.parser.MasterObject;
import mayday.pathway.viewer.canvas.PathwayLayouter;
import mayday.pathway.viewer.canvas.SBGNLayouterWrapper;
import mayday.pathway.viewer.gui.PathwaySelectionDialog;
import mayday.vis3.graph.layout.CanvasLayouter;
import mayday.vis3.graph.layout.FruchtermanReingoldLayout;
import mayday.vis3.graph.layout.SimpleCircularLayout;
import mayday.vis3.graph.layout.SnakeLayout;
import mayday.vis3.graph.layout.SugiyamaLayout;

public class BioPaxPathwayGraphModelProvider extends AbstractGraphModelProvider
{
	private PathSetting pathSetting;
	private Map<DataSet, MappingSourceSetting> mappings;
	private Map<String, MasterObject> bioPaxObjects;
	private PathwaySelectionDialog pathwaySelector;
	private static final String LAST_BIOPAX_FILE="AnacondaLastBioPaxDir";
	
	public BioPaxPathwayGraphModelProvider() 
	{
		String d= Utilities.prefs.get(LAST_BIOPAX_FILE, System.getProperty("user.home"));
		pathSetting=new PathSetting("Pathway File", null, d, false, true, false);
		basicSetting.addSetting(pathSetting);		
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"mayday.graphModelProvider.biopaxpathway",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Create a new graph from a biopax pathway",
				getName()				
		);		
		return pli;			
	}
	
	@Override
	public String getName() 
	{
		return "BioPax Pathway";
	}

	@Override
	public String getDescription() 
	{
		return "Build a graph from information stored in a biopax file.";
	}

	@Override
	public CanvasLayouter defaultLayouter() 
	{
		PathwayLayouter layouter=new PathwayLayouter();
		layouter.setBranchedLayouter(new SugiyamaLayout());
		layouter.setCircularLayouter(new SimpleCircularLayout());
		layouter.setLinearLayouter(new SnakeLayout());
		layouter.setComplexLayouter(new FruchtermanReingoldLayout());
		return new SBGNLayouterWrapper(layouter);
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
		pathwaySelector=new PathwaySelectionDialog(bioPaxObjects);
		return pathwaySelector.getContentPane();
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
			BioPaxParser parser=new BioPaxParser();
			bioPaxObjects = parser.parse(pathSetting.getStringValue());			
			
		}
		@Override
		protected void initialize() {}
	}
	
	private class GraphTask extends AbstractTask
	{
		public GraphTask() 
		{
			super("Building Graph (BioPax Pathway)");
		}

		@Override
		protected void doWork() throws Exception 
		{
			MasterObject selected=pathwaySelector.getSelectedPathway();
			BioPaxSBGNGraph g=new BioPaxSBGNGraph(selected);
			annotateNodesIgnoreCase(getProbeLists(), g, mappings);
			model=new SuperModel(g);
		}
		
		@Override
		protected void initialize() {}
	}
	
	
}
