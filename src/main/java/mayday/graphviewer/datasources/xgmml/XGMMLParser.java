package mayday.graphviewer.datasources.xgmml;

import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.io.GraphExport;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.graphviewer.datasources.xgmml.XGMMLTypes.XAtt;
import mayday.graphviewer.datasources.xgmml.XGMMLTypes.XEdge;
import mayday.graphviewer.datasources.xgmml.XGMMLTypes.XGraph;
import mayday.graphviewer.datasources.xgmml.XGMMLTypes.XGraphics;
import mayday.graphviewer.datasources.xgmml.XGMMLTypes.XNode;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class XGMMLParser 
{
	public List<Graph> parse(String file) throws Exception
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
		XGMMLHandler handler=new XGMMLHandler();
		parser.setContentHandler(handler);
		parser.parse(new InputSource(new FileReader(file)));

		List<XGraph> graphs=handler.getGraphs();
		List<Graph> res=new ArrayList<Graph>();
		for(XGraph gr:graphs)
		{
			Graph g=new Graph();
			g.setName(gr.label);

			Map<String, Node> nodeMap=new HashMap<String, Node>();
			for(XNode no:gr.nodes)
			{
				MultiProbeNode node=new MultiProbeNode(g);
				node.setName(no.label);
				nodeMap.put(no.id, node);
				g.addNode(node);
				for(XAtt att:no.atts)
				{
					node.setProperty(att.name, att.value);
				}

				if(no.weight!=null)
					node.setProperty("weight", no.weight);

				XGraphics gfx=no.graphics;
				if(gfx!=null)
				{
					String s=gfx.attributes.get("x")+","+gfx.attributes.get("y")+","+gfx.attributes.get("w")+","+gfx.attributes.get("h");
					node.setProperty(GraphExport.GEOMETRY_KEY, s);
				}
			}
			
			for(XEdge ed:gr.edges)
			{
				Node s=nodeMap.get(ed.source);
				Node t=nodeMap.get(ed.target);
				Edge e=new Edge(s, t);
				if(ed.weight!=null)
					e.setWeight(Double.parseDouble(ed.weight));
				
				
				e.setName(ed.label);
				
				for(XAtt att:ed.atts)
				{
					e.addProperty(att.name, att.value);
				}
				g.connect(e);
			}
			res.add(g);
		}

		return res;

	}
}
