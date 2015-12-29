package mayday.graphviewer.datasources.graphml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import mayday.core.Probe;
import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.io.GraphMLExport;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.core.bag.ComponentBag;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.NodeComponent;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class GraphMLIO extends GraphMLExport
{
	public static final String BAG_NAME="bag";
	public static final String COLOR_KEY="color";
	public static final String NODES_KEY="nodes";
	public static final String STYLE_KEY="style";
	
	public static void export(SuperModel model, File f) throws IOException, Exception
	{
		XMLOutputFactory xof = XMLOutputFactory.newInstance();
		XMLStreamWriter writer =xof.createXMLStreamWriter(new FileWriter(f));

		writeHeader(writer);
		writeGraph(writer, model);

		// export bags
		for(ComponentBag bag:model.getBags())
		{
			exportBag(writer,bag,model);
		}
		
		writer.writeEndDocument();
		writer.flush();
		writer.close();
	}
	
	protected static void writeHeader(XMLStreamWriter writer) throws Exception
	{
		writer.writeStartDocument("utf-8", "1.0");
//		writer.writeStartDocument();
		writer.writeStartElement("graphml");
		writer.writeAttribute("xmlns", "http://graphml.graphdrawing.org/xmlns");
		writer.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		writer.writeAttribute("xsi:schemaLocation", "http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd");
		writer.writeComment("Created by Mayday");
		writeDefaultAttributes(writer);		
		writeAttribute(writer, RENDERER_KEY, NODE_TARGET, RENDERER_KEY);
		
		writer.writeStartElement("key");
		writer.writeAttribute("for", NODE_TARGET);
		writer.writeAttribute("id", "DefaultNodeAttributes");
		writer.writeAttribute("attr.type", "m.keyvaluelist");
		writer.writeAttribute("attr.name", "Node Attributes");
		writer.writeEndElement();
	}
	
	protected static void writeGraph(XMLStreamWriter writer, SuperModel model)  throws Exception
	{
		model.getGraph().exportGraphHead(writer);
		for(CanvasComponent comp:model.getComponents())
		{
			if(!comp.isVisible()) continue;
			if(!(comp instanceof NodeComponent))
				continue;
			Node n=((NodeComponent)comp).getNode();
			n.exportNodeHead(writer);
			
			writeDataElement(writer, RENDERER_KEY, comp.getRenderer().getClass().getCanonicalName());
			writeDataElement(writer, GEOMETRY_KEY, rectangleToString(comp.getBounds()));
			writer.writeEndElement();
		}
		
		writer.flush();
		for(Edge e:model.getGraph().getEdges())
		{
			if(  !model.getComponent(e.getSource()).isVisible() || !model.getComponent(e.getTarget()).isVisible() ) continue;
			e.export(writer);
		}
	}
	
	
	protected static String exportBag(XMLStreamWriter writer, ComponentBag bag, SuperModel model) throws Exception
	{
		writer.writeStartElement(BAG_NAME);
		String s="bag"+bag.hashCode();
		writer.writeAttribute("id", s);
		writer.writeAttribute(NAME_KEY, bag.getName());
		writer.writeAttribute(COLOR_KEY, ""+bag.getColor().getRGB());
		writer.writeAttribute(STYLE_KEY, ""+bag.getStyle().toString());
		StringBuffer sb=new StringBuffer();
		for(CanvasComponent comp:bag)
		{
			if(sb.length()==0)
				sb.append("node"+model.getNode(comp).hashCode());
			else
				sb.append(",node"+model.getNode(comp).hashCode());
		}
		writer.writeAttribute(NODES_KEY, sb.toString());
		writer.writeEndElement();
		return s;
	}
	
	public static List<Graph> importGraphML(File file, Collection<Probe> probes) throws Exception
	{
		XMLReader parser;
		parser = XMLReaderFactory.createXMLReader();
		parser.setEntityResolver(new EntityResolver() {
			public InputSource resolveEntity(String publicID, String systemID)
			throws SAXException {
				return new InputSource(new StringReader(""));
			}
		}
		);
		ExtendedGraphMLHandler handler=new ExtendedGraphMLHandler();
		parser.setContentHandler(handler);
		parser.parse(new InputSource(new FileReader(file)));
		return handler.getGraphs(probes);
	}
	
	public static List<GraphMLGraph> importGraphML2(File file, Collection<Probe> probes) throws Exception
	{
		return importGraphML2(new FileInputStream(file), probes);
	}
	
	public static List<GraphMLGraph> importGraphML2(InputStream file, Collection<Probe> probes) throws Exception
	{
		XMLReader parser;
		parser = XMLReaderFactory.createXMLReader();
		parser.setEntityResolver(new EntityResolver() {
			public InputSource resolveEntity(String publicID, String systemID)
			throws SAXException {
				return new InputSource(new StringReader(""));
			}
		}
		);
		ExtendedGraphMLHandler handler=new ExtendedGraphMLHandler();
		parser.setContentHandler(handler);
		parser.parse(new InputSource(file));
		List<GraphMLGraph> graphs=new ArrayList<GraphMLGraph>();
		for(String s:handler.getGraphs().keySet())
		{
			GraphMLGraph g=new GraphMLGraph();
			g.graph=handler.getGraph(s, probes);
			g.id=s;
			g.bags=handler.getGraphBags().get(s);
			graphs.add(g);
		}
		return graphs;
	}
	
}
