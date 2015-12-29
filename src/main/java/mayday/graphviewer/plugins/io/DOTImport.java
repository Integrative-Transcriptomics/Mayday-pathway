package mayday.graphviewer.plugins.io;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.edges.Edges;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;
import att.grappa.Attribute;
import att.grappa.Edge;
import att.grappa.Element;
import att.grappa.Graph;
import att.grappa.GraphIterator;
import att.grappa.GrappaConstants;
import att.grappa.Node;
import att.grappa.Parser;

public class DOTImport extends AbstractGraphImportPlugin
{

	@Override
	protected String getFormat() 
	{
		return "DOT";
	}

	@Override
	protected mayday.core.structures.graph.Graph importFile(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) throws Exception 
	{
		Parser parser;

		parser = new Parser(new FileReader(fileSetting.getStringValue()));
		parser.parse();	

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
		return gr;
	}

//	@Override
//	public void run(final GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
//	{
//		String lastExportDir= Utilities.prefs.get(Utilities.LAST_GRAPH_IMPORT_DIR, System.getProperty("user.home"));
//		PathSetting dotFile=new PathSetting("DOT file", "The graph file to be imported", lastExportDir, false, true, false);
//
//
//		HierarchicalSetting setting=new HierarchicalSetting("DOT Import");
//		MappingSourceSetting mapping=new MappingSourceSetting(canvas.getModelHub().getViewModel().getDataSet());
//		setting.addSetting(dotFile).addSetting(mapping);
//
//		SettingsDialog settingDialog=new SettingsDialog(null, "DOT Import", new Settings(setting, getPluginInfo().getPreferences()));
//		settingDialog.setModal(true);
//		settingDialog.setVisible(true);
//
//		if(!settingDialog.closedWithOK())
//			return;
//
//
//
//	} catch (Exception e) {
//		throw new RuntimeException(e);
//
//	}
//
//	Parser parser;
//	try {
//		parser = new Parser(new FileReader(dotFile.getStringValue()));
//		parser.parse();	
//
//		Graph graph=parser.getGraph();
//
//		mayday.core.structures.graph.Graph gr=new mayday.core.structures.graph.Graph();			
//		gr.setName(graph.getName());
//		Map<Node, mayday.core.structures.graph.Node> nodeNodeMap=new HashMap<Node, mayday.core.structures.graph.Node>();	
//
//		GraphIterator iter=graph.elements(GrappaConstants.NODE);
//		while(iter.hasNext())
//		{
//			Element e= iter.nextGraphElement();
//			Node node=(Node)e;
//
//			MultiProbeNode mayNode=new MultiProbeNode(gr);
//			mayNode.setName(e.getName());
//			mayNode.setProperty("Name", e.getName());
//			gr.addNode(mayNode);
//			nodeNodeMap.put(node, mayNode);
//			Iterator<Attribute> atts=node.getAttributePairs();
//			while(atts.hasNext())
//			{
//				Attribute a=atts.next();
//				mayNode.setProperty(a.getName(), a.getStringValue());					
//			}
//		}
//
//		iter=graph.elements(GrappaConstants.EDGE);
//		while(iter.hasNext())
//		{
//			Element e= iter.nextGraphElement();
//			Edge edge=(Edge)e;
//
//			mayday.core.structures.graph.Edge mayEdge=new mayday.core.structures.graph.Edge(
//					nodeNodeMap.get(edge.getTail()), nodeNodeMap.get(edge.getHead()));
//
//			if(edge.goesForward())
//				mayEdge.setRole(Edges.Roles.EDGE_ROLE);
//			if(edge.goesReverse())
//				mayEdge.setRole(Edges.Roles.REVERSE_EDGE);
//
//			Iterator<Attribute> atts=edge.getAttributePairs();
//			while(atts.hasNext())
//			{
//				Attribute a=atts.next();
//				mayEdge.addProperty(a.getName(), a.getStringValue());					
//			}				
//			gr.connect(mayEdge);
//		}
//
//		System.out.println(gr.getNodes());
//		Map<DataSet, MappingSourceSetting> mappings=new HashMap<DataSet,MappingSourceSetting>();
//		mappings.put(canvas.getModelHub().getViewModel().getDataSet(),mapping);
//		MultiHashMap<DataSet, ProbeList> probeLists=new MultiHashMap<DataSet, ProbeList>();
//		for(ProbeList pl:canvas.getModelHub().getViewModel().getProbeLists(false) )
//			probeLists.put(canvas.getModelHub().getViewModel().getDataSet(), pl);
//		AbstractGraphModelProvider.annotateNodes(probeLists, gr, mappings);
//		canvas.setModel(new SuperModel(gr));
//
//
//
//
//	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.DOTImport",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Import a graph from a GraphViz DOT file",
				"DOT"				
		);
		pli.addCategory(IMPORT_CATEGORY);
		return pli;	
	}
}
