package mayday.graphviewer.datasources.graphml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.Probe;
import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.io.GraphFactory;
import mayday.core.structures.graph.io.GraphMLExport;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class ExtendedGraphMLHandler implements ContentHandler 
{
	private static final String ID="id";


	private Map<String, Map<String,String>> graphs;
	private Map<String, Map<String,String>> nodes;
	private Map<String, Map<String,String>> edges;
	private Map<String, Map<String,String>> bags;

	private Map<String, Map<String,Map<String,String>>> graphNodes;
	private Map<String, Map<String,Map<String,String>>> graphEdges; 
	private Map<String, Map<String,Map<String,String>>> graphBags; 

	private boolean isNode;
	private boolean isEdge;
	private boolean isGraph;

//	private boolean isKey;
//	private boolean isData;
	private String currentDataName;
//	private String currentData;

	private Map<String,String> nodeAttributes;
	private Map<String,String> edgeAttributes;
	private Map<String,String> graphAttributes;

	
	
	String characters;
	String currentDefault;
	Key key;

	public void characters(char[] ch, int start, int length) throws SAXException 
	{
		characters=String.copyValueOf(ch, start, length).trim();
	}

	public void endPrefixMapping(String prefix) throws SAXException {}
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {}
	public void processingInstruction(String target, String data) throws SAXException {}
	public void setDocumentLocator(Locator locator){}
	public void skippedEntity(String name) throws SAXException{}
	public void startPrefixMapping(String prefix, String uri) throws SAXException {}


	public void startDocument() throws SAXException 
	{
		graphs=new HashMap<String, Map<String,String>>();
		nodes=new HashMap<String, Map<String,String>>();
		edges=new HashMap<String, Map<String,String>>();
		bags=new HashMap<String, Map<String,String>>();
		new HashMap<String, Key>();
		graphNodes=new HashMap<String, Map<String,Map<String,String>>>();
		graphEdges=new HashMap<String, Map<String,Map<String,String>>>();
		graphBags=new HashMap<String, Map<String,Map<String,String>>>();
	}

	public void endElement(String uri, String localName, String name) throws SAXException 
	{
		if(name.equals("node"))
		{
			isNode=false;
			nodes.put(nodeAttributes.get(ID), nodeAttributes);
			return;
		}

		if(name.equals("edge"))
		{
			isEdge=false;
			edges.put(edgeAttributes.get(ID), edgeAttributes);
			return;
		}

		if(name.equals("graph"))
		{
			isGraph=false;		
			graphs.put(graphAttributes.get(ID), graphAttributes);
			graphNodes.put(graphAttributes.get(ID), nodes);
			graphEdges.put(graphAttributes.get(ID), edges);
			graphBags.put(graphAttributes.get(ID), bags);
			return;
		}

		if(name.equals("default"))
		{
			currentDefault=characters;	
			return;
		}

		if(name.equals("key"))
		{
			if(currentDefault!=null)
				key.defaultValue=currentDefault;
			currentDefault=null;
			return;
		}


		if(name.equals("data"))
		{
			if(currentDataName.equals("id")) 
				return;
			if(isNode)
			{	
				if(!characters.isEmpty())
					nodeAttributes.put(currentDataName, characters);
				characters=new String();
				return;
			}
			if(isEdge)
			{
				if(!characters.isEmpty())
					edgeAttributes.put(currentDataName,characters);
				characters=new String();
				return;
			}
			if(isGraph)
			{
				if(!characters.isEmpty())
					graphAttributes.put(currentDataName, characters);
				characters=new String();
			}
			return;
		}
		// none of the above
		if(!characters.isEmpty())
		{
			if(isNode)
			{
				nodeAttributes.put(name, characters);
				characters=new String();
				return;
			}	
			if(isEdge)
			{
				edgeAttributes.put(name, characters);
				characters=new String();
				return;			
			}
			if(isGraph)
			{
				graphAttributes.put(name, characters);
			}
		}
	}

	public void startElement(String uri, String localName, String name, Attributes atts) throws SAXException 
	{
		//		System.out.println(name);
		if(name.equals("graph"))
		{
			isGraph=true;
			graphAttributes=new HashMap<String, String>();
			graphAttributes.put(ID,atts.getValue(ID) );

			nodes=new HashMap<String, Map<String,String>>();
			edges=new HashMap<String, Map<String,String>>();
			bags=new HashMap<String, Map<String,String>>();
		}
		if(name.equals("node"))
		{
			isNode=true;
			nodeAttributes=new HashMap<String, String>();
			nodeAttributes.put(ID, atts.getValue(ID));
		}

		if(name.equals("edge"))
		{
			isEdge=true;
			edgeAttributes=new HashMap<String, String>();
			edgeAttributes.put(ID, atts.getValue(ID));
			edgeAttributes.put("source", atts.getValue("source"));
			edgeAttributes.put("target", atts.getValue("target"));

		}
		if(name.equals("key"))
		{
			key=new Key();
			key.id=atts.getValue(ID);
			key.name=atts.getValue("attr.name");
			key.target=atts.getValue("for");


		}

		if(name.equals("data"))
		{
//			isData=true;
			currentDataName=atts.getValue("key");
		}
		
		if(name.equals("y:Geometry"))
		{
			if(isNode)
			{
				String geom=atts.getValue("x")+","+atts.getValue("y")+","+atts.getValue("width")+","+atts.getValue("height");
				nodeAttributes.put(GraphMLExport.GEOMETRY_KEY, geom);
			}
		}
		
		if(name.equals(GraphMLIO.BAG_NAME))
		{
			Map<String,String> bagAttributes=new HashMap<String, String>();
			String id=atts.getValue(ID);
			bagAttributes.put(GraphMLIO.COLOR_KEY, atts.getValue(GraphMLIO.COLOR_KEY));
			bagAttributes.put(GraphMLIO.NAME_KEY, atts.getValue(GraphMLIO.NAME_KEY));
			bagAttributes.put(GraphMLIO.NODES_KEY, atts.getValue(GraphMLIO.NODES_KEY));
			bagAttributes.put(GraphMLIO.STYLE_KEY, atts.getValue(GraphMLIO.STYLE_KEY));
			
			bags.put(id,bagAttributes);
		
		}


	}

	public void endDocument() throws SAXException 
	{
	}

	public List<Graph> getGraphs(Collection<Probe> probes)
	{
		GraphFactory factory=new GraphFactory(probes);
		return getGraphs(factory);
	}


	public List<Graph> getGraphs(GraphFactory factory)
	{
		List<Graph> res=new ArrayList<Graph>();
		for(Map<String, String> graphAtt: graphs.values())
		{
			String graphId=graphAtt.get(ID);
			Graph graph=getGraph(graphId,factory);
//				factory.produceGraph(graphAtt.get(GraphMLExport.CLASS_KEY));
//			if(graphAtt.containsKey(GraphMLExport.NAME_KEY))
//				graph.setName(graphAtt.get(GraphMLExport.NAME_KEY));
//
//			//			System.out.println(graphAtt);
//
//			Map<String, Node> idNodeMap=new HashMap<String, Node>();
//
//			for(Map<String, String> nodeAtt: graphNodes.get(graphId).values())
//			{
//				Node n=factory.produceNode(nodeAtt, graph);
//				n.setName(nodeAtt.get(GraphMLExport.NAME_KEY));
//				// try to give the node a name. if no name could be assigned, try the yed node label
//				if(n.getName()==null && nodeAtt.containsKey("y:NodeLabel"))
//					n.setName(nodeAtt.get("y:NodeLabel"));
//				// still no name? use id!
//				if(n.getName()==null)
//					n.setName(nodeAtt.get("id"));
//
//				n.setRole(nodeAtt.get(GraphMLExport.ROLE_KEY));
//				graph.addNode(n);
//				idNodeMap.put(nodeAtt.get(ID), n);
//			}
//			for(Map<String, String> edgeAtt: graphEdges.get(graphId).values())
//			{
//				Node source=idNodeMap.get(edgeAtt.get("source"));
//				Node target=idNodeMap.get(edgeAtt.get("target"));
//				Edge e= factory.produceEdge(edgeAtt.get(GraphMLExport.CLASS_KEY), source,target);
//				if(edgeAtt.containsKey(GraphMLExport.NAME_KEY))
//					e.setName(edgeAtt.get(GraphMLExport.NAME_KEY));
//				if(edgeAtt.containsKey(GraphMLExport.ROLE_KEY))
//					e.setRole(edgeAtt.get(GraphMLExport.ROLE_KEY));
//				if(edgeAtt.containsKey(GraphMLExport.WEIGHT_KEY))
//					e.setWeight(Double.parseDouble(edgeAtt.get(GraphMLExport.WEIGHT_KEY)));
//				graph.connect(e);
//			}
			res.add(graph);
		}
		return res;
	}
	
	public Map<String, Map<String, String>> getBags() {
		return bags;
	}

	public Graph getGraph(String id, Collection<Probe> probes)
	{
		GraphFactory factory=new GraphFactory(probes);
		return getGraph(id, factory);
	}
	
	public Graph getGraph(String id, GraphFactory factory)
	{
		Map<String, String> graphAtt=graphs.get(id);
		
		Graph graph=factory.produceGraph(graphAtt.get(GraphMLExport.CLASS_KEY));
		if(graphAtt.containsKey(GraphMLExport.NAME_KEY))
			graph.setName(graphAtt.get(GraphMLExport.NAME_KEY));

		//			System.out.println(graphAtt);

		Map<String, Node> idNodeMap=new HashMap<String, Node>();

		for(Map<String, String> nodeAtt: graphNodes.get(id).values())
		{
			Node n=factory.produceNode(nodeAtt, graph);
			n.setName(nodeAtt.get(GraphMLExport.NAME_KEY));
			// try to give the node a name. if no name could be assigned, try the yed node label
			if(n.getName()==null && nodeAtt.containsKey("y:NodeLabel"))
				n.setName(nodeAtt.get("y:NodeLabel"));
			// still no name? use id!
			if(n.getName()==null)
				n.setName(nodeAtt.get("id"));

			n.setRole(nodeAtt.get(GraphMLExport.ROLE_KEY));
			
			graph.addNode(n);
			idNodeMap.put(nodeAtt.get(ID), n);
		}
		for(Map<String, String> edgeAtt: graphEdges.get(id).values())
		{
			Node source=idNodeMap.get(edgeAtt.get("source"));
			Node target=idNodeMap.get(edgeAtt.get("target"));
			Edge e= factory.produceEdge(edgeAtt.get(GraphMLExport.CLASS_KEY), source,target);
			if(edgeAtt.containsKey(GraphMLExport.NAME_KEY))
				e.setName(edgeAtt.get(GraphMLExport.NAME_KEY));
			if(edgeAtt.containsKey(GraphMLExport.ROLE_KEY))
				e.setRole(edgeAtt.get(GraphMLExport.ROLE_KEY));
			if(edgeAtt.containsKey(GraphMLExport.WEIGHT_KEY))
				e.setWeight(Double.parseDouble(edgeAtt.get(GraphMLExport.WEIGHT_KEY)));
			
			for(String key:edgeAtt.keySet())
			{
				if(
						key.equals(GraphMLExport.CLASS_KEY) ||
						key.equals("source") ||
						key.equals("target") ||
						key.equals(GraphMLExport.WEIGHT_KEY) ||
						key.equals(GraphMLExport.NAME_KEY) ||
						key.equals(GraphMLExport.ROLE_KEY) )
					continue;
				e.addProperty(key, edgeAtt.get(key));
			}
			
			graph.connect(e);
		}
		
		return graph;
	}
	
	private class Key
	{
		String id;
		String target;
		String name;
		String defaultValue;
		@Override
		public String toString() 
		{
			return id+":"+name+" "+target+" default:"+defaultValue;
		}
	}

	public Map<String, Map<String, String>> getGraphs() {
		return graphs;
	}

	public Map<String, Map<String, String>> getNodes() {
		return nodes;
	}

	public Map<String, Map<String, String>> getEdges() {
		return edges;
	}

	public Map<String, Map<String, Map<String, String>>> getGraphNodes() {
		return graphNodes;
	}

	public Map<String, Map<String, Map<String, String>>> getGraphEdges() {
		return graphEdges;
	}

	public Map<String, Map<String, Map<String, String>>> getGraphBags() {
		return graphBags;
	}

	



}
