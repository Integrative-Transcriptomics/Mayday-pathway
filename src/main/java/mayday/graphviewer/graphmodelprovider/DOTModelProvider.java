package mayday.graphviewer.graphmodelprovider;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import mayday.core.DataSet;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.PluginManager.IGNORE_PLUGIN;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.MappingSourceSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.core.structures.graph.edges.Edges;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.tasks.AbstractTask;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.core.Utilities;
import mayday.vis3.graph.layout.CanvasLayouter;
import mayday.vis3.graph.layout.FruchtermanReingoldLayout;
import att.grappa.Attribute;
import att.grappa.Edge;
import att.grappa.Element;
import att.grappa.Graph;
import att.grappa.GraphIterator;
import att.grappa.GrappaConstants;
import att.grappa.Node;
import att.grappa.Parser;

@IGNORE_PLUGIN
public class DOTModelProvider  extends AbstractGraphModelProvider
{
	private PathSetting pathSetting;
	private Map<DataSet, MappingSourceSetting> mappings;
	
	private Parser parser;
	
	public DOTModelProvider() 
	{
		String lastImportDir= Utilities.prefs.get(Utilities.LAST_GRAPH_IMPORT_DIR, System.getProperty("user.home"));
		pathSetting=new PathSetting("File", null, lastImportDir, false, true, false);
		basicSetting.addSetting(pathSetting);	
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"mayday.graphModelProvider.dot",
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
		return "DOT";
	}
	
	@Override
	public String getDescription() 
	{
		return "load graphs from dot files";
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
			super("Parsing DOT file");
		}
		
		@Override
		protected void doWork() throws Exception 
		{
			parser=new Parser(new FileReader(pathSetting.getStringValue()));
			parser.parse();		
		}
		@Override
		protected void initialize() {}
	}
	
	private class GraphTask extends AbstractTask
	{
		public GraphTask() 
		{
			super("Building Graph from Dot file");
		}
		
		@Override
		protected void doWork() throws Exception 
		{
			Graph graph=parser.getGraph();
			
			mayday.core.structures.graph.Graph gr=new mayday.core.structures.graph.Graph();			
			gr.setName(graph.getName());
			Map<Node, mayday.core.structures.graph.Node> nodeNodeMap=new HashMap<Node, mayday.core.structures.graph.Node>();	
			
			GraphIterator iter=graph.elements(GrappaConstants.NODE);
			while(iter.hasNext())
			{
				Element e= iter.nextGraphElement();
				Node node=(Node)e;
				
				MultiProbeNode mayNode=new MultiProbeNode(gr);
				mayNode.setName(e.getName());
				mayNode.setProperty("Name", e.getName());
				gr.addNode(mayNode);
				nodeNodeMap.put(node, mayNode);
				Iterator<Attribute> atts=node.getAttributePairs();
				while(atts.hasNext())
				{
					Attribute a=atts.next();
					mayNode.setProperty(a.getName(), a.getStringValue());					
				}
			}
			
			iter=graph.elements(GrappaConstants.EDGE);
			while(iter.hasNext())
			{
				Element e= iter.nextGraphElement();
				Edge edge=(Edge)e;
				
				mayday.core.structures.graph.Edge mayEdge=new mayday.core.structures.graph.Edge(
						nodeNodeMap.get(edge.getTail()), nodeNodeMap.get(edge.getHead()));
				
				if(edge.goesForward())
					mayEdge.setRole(Edges.Roles.EDGE_ROLE);
				if(edge.goesReverse())
					mayEdge.setRole(Edges.Roles.REVERSE_EDGE);
				
				Iterator<Attribute> atts=edge.getAttributePairs();
				while(atts.hasNext())
				{
					Attribute a=atts.next();
					mayEdge.addProperty(a.getName(), a.getStringValue());					
				}
				
				gr.connect(mayEdge);
			}

			System.out.println(gr.getNodes());
			annotateNodes(probeLists, gr, mappings);
			model=new SuperModel(gr);
			
		}
		@Override
		protected void initialize() {}
	}
	

}
