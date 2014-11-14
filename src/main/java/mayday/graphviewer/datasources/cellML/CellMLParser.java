package mayday.graphviewer.datasources.cellML;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.edges.Edges;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.graphviewer.datasources.cellML.CellMLConnection.CellMLMapVariables;
import mayday.vis3.graph.GraphCanvas;
import mayday.vis3.graph.layout.FruchtermanReingoldLayout;
import mayday.vis3.graph.model.DefaultGraphModel;
import mayday.vis3.graph.model.GraphModel;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CellMLParser 
{
	private DocumentBuilder builder;

	public static final String COMPONENT="component";
	public static final String VARIABLE="variable";
	public static final String CONNECTION="connection";

	public static final String MAP_COMPONENTS="map_components";
	public static final String MAP_VARIABLES="map_variables";

	public static final String OUT="out";
	public static final String IN="in";

	private Map<String, CellMLComponent> componentMap;
	private List<CellMLConnection> connections;

	public CellMLParser() throws Exception
	{
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		builder=dbf.newDocumentBuilder();
	}

	private CellMLVariable buildVariable(Node node)
	{
		CellMLVariable var=new CellMLVariable();
		var.name=node.getAttributes().getNamedItem("name").getNodeValue();
		var.units=node.getAttributes().getNamedItem("units").getNodeValue();

		if(node.getAttributes().getNamedItem("public_interface")!=null)
		{
			var.publicInterface=node.getAttributes().getNamedItem("public_interface").getNodeValue();
		}		
		if(node.getAttributes().getNamedItem("private_interface")!=null)
		{
			var.privateInterface=node.getAttributes().getNamedItem("private_interface").getNodeValue();
		}		
		if(node.getAttributes().getNamedItem("initial_value")!=null)
		{
			var.initial_value=node.getAttributes().getNamedItem("initial_value").getNodeValue();
		}
		return var;
	}

	private CellMLComponent buildComponent(Node node)
	{
		CellMLComponent comp=new CellMLComponent();
		comp.name=node.getAttributes().getNamedItem("name").getNodeValue();
		NodeList children=node.getChildNodes();
		for(int i=0; i!=children.getLength(); ++i)
		{
			Node ch=children.item(i);
			if(ch.getNodeType()!= Node.ELEMENT_NODE)
				continue;
			Element e=(Element)ch;		
			if(e.getTagName().equals(VARIABLE))
				comp.addVariable(buildVariable(e));

		}
		return comp;
	}

	private CellMLConnection buildConnection(Node node)
	{
		CellMLConnection con=new CellMLConnection();
		NodeList children=node.getChildNodes();
		for(int i=0; i!=children.getLength(); ++i)
		{
			Node ch=children.item(i);
			if(ch.getNodeType()!= Node.ELEMENT_NODE)
				continue;
			Element e=(Element)ch;		
			if(e.getTagName().equals(MAP_COMPONENTS))
			{
				con.component1=e.getAttribute("component_1");
				con.component2=e.getAttribute("component_2");
			}
			if(e.getTagName().equals(MAP_VARIABLES))
			{
				CellMLMapVariables mv=new CellMLMapVariables();
				mv.variable1=e.getAttribute("variable_1");
				mv.variable2=e.getAttribute("variable_2");
				con.mapVariables.add(mv);
			}		
		}		
		return con;
	}

	public void parse(String fileName) throws Exception
	{
		Document document = builder.parse(fileName);
		connections=new ArrayList<CellMLConnection>();
		componentMap=new HashMap<String, CellMLComponent>();

		NodeList components=document.getElementsByTagName(COMPONENT);
		for(int i=0; i!=components.getLength(); ++i)
		{
			Node compNode=components.item(i);
			CellMLComponent comp=buildComponent(compNode);
			componentMap.put(comp.name, comp);
		}
		NodeList conList=document.getElementsByTagName(CONNECTION);
		for(int i=0; i!=conList.getLength(); ++i)
		{
			Node conNode=conList.item(i);
			CellMLConnection con=buildConnection(conNode);
			connections.add(con);
		}
	}	

	public Graph createGraph()
	{
		Graph graph=new Graph();
		Map<String, MultiProbeNode> nodeMap=new HashMap<String, MultiProbeNode>();
		for(CellMLConnection con:connections)
		{
			CellMLComponent c1=componentMap.get(con.component1);
			CellMLComponent c2=componentMap.get(con.component2);
			MultiProbeNode node1=nodeMap.get(c1.name);
			MultiProbeNode node2=nodeMap.get(c2.name);
			if(node1==null)
			{
				node1=new MultiProbeNode(graph);
				node1.setName(c1.name);
				graph.addNode(node1);
				for(CellMLVariable var:c1.variables.values())
					node1.setProperty(var.name, var.value());
				nodeMap.put(c1.name, node1);
			}
			if(node2==null)
			{
				node2=new MultiProbeNode(graph);
				graph.addNode(node2);
				node2.setName(c2.name);
				for(CellMLVariable var:c2.variables.values())
					node2.setProperty(var.name, var.value());
				nodeMap.put(c2.name, node2);
			}
			for(CellMLMapVariables mv:con.mapVariables)
			{
				CellMLVariable var1= c1.getVariable(mv.variable1);
				CellMLVariable var2= c2.getVariable(mv.variable2);	

				Edge e=null;
				if(var1.publicInterface.equals(OUT) && var1.publicInterface.equals(IN))
				{
					e=new Edge(node1,node2);					
				}else
				{
					if(var1.publicInterface.equals(IN) && var1.publicInterface.equals(OUT))
					{
						e=new Edge(node2,node1);					
					}else
					{
						e=new Edge(node1,node2);
						e.setRole(Edges.Roles.NO_ARROW_EDGE);
					}
				}
				if(var1.name.equals(var2.name))
					e.setName(var1.name);
				else
					e.setName(var1.name+"-"+var2.name);
				graph.connect(e);
			}
		}

		return graph;
	}

	public static void main(String[] args) throws Exception
	{
		CellMLParser parser=new CellMLParser();
		parser.parse("/home/symons/Anaconda/cellml/albrecht_colegrove_friel_2002.cellml");
		Graph g=parser.createGraph();

		parser.parse("/home/symons/Anaconda/cellml/aguda_b_1999-56788658c953/aguda_b_1999.cellml");
		g.add(parser.createGraph());
		parser.parse("/home/symons/Anaconda/cellml/bhalla_iyengar_1999-9958a50ee47c/bhalla_iyengar_1999_a.cellml");
		g.add(parser.createGraph());
		parser.parse("/home/symons/Anaconda/cellml/bhalla_iyengar_1999-9958a50ee47c/bhalla_iyengar_1999_b.cellml");
		g.add(parser.createGraph());
		parser.parse("/home/symons/Anaconda/cellml/bhalla_iyengar_1999-9958a50ee47c/bhalla_iyengar_1999_c.cellml");
		g.add(parser.createGraph());
		parser.parse("/home/symons/Anaconda/cellml/bhalla_iyengar_1999-9958a50ee47c/bhalla_iyengar_1999_d.cellml");
		g.add(parser.createGraph());
		parser.parse("/home/symons/Anaconda/cellml/bhalla_iyengar_1999-9958a50ee47c/bhalla_iyengar_1999_e.cellml");
		g.add(parser.createGraph());
		parser.parse("/home/symons/Anaconda/cellml/bhalla_iyengar_1999-9958a50ee47c/bhalla_iyengar_1999_f.cellml");
		g.add(parser.createGraph());
		parser.parse("/home/symons/Anaconda/cellml/hynne_dano_sorensen_2001-1823b1756d36/hynne_dano_sorensen_2001.cellml");
		g.add(parser.createGraph());

		GraphModel model=new DefaultGraphModel(g);
		GraphCanvas canvas=new GraphCanvas(model);
		canvas.setLayouter(new FruchtermanReingoldLayout());
		JFrame frame=new JFrame("Zorp");
		frame.add(canvas);
		frame.pack();
		frame.setVisible(true);
		
	}

}
