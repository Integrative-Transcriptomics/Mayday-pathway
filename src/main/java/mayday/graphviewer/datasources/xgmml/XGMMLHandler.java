package mayday.graphviewer.datasources.xgmml;

import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import mayday.graphviewer.datasources.xgmml.XGMMLTypes.XType;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class XGMMLHandler implements ContentHandler
{

	private List<XGMMLTypes.XGraph> graphs;
	
	private XGMMLTypes.XGraph currentGraph;
	private XGMMLTypes.XNode currentNode;
	private XGMMLTypes.XEdge currentEdge;
	private XGMMLTypes.XGraphics currentGraphics;
	private XGMMLTypes.XAtt currentAtt;
	private XGMMLTypes.XLine currentLine;
	
	private boolean isEdge, isNode, isGraph, isAtt;
	
	public static final String GRAPH="graph";
	public static final String ATT="att";
	public static final String NODE="node";
	public static final String EDGE="edge";
	public static final String GRAPHICS="graphics";
	public static final String POINT="point";
	public static final String LINE="line";
	public static final String CENTER="center";
	
	public static void main(String[] args) throws Exception
	{
		String file="/home/symons/Anaconda/cytoscape/CytoscapeSession-2009_03_13-11_11/Net06.txt.xgmml";


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
		
	}
	
	
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException 
	{		
	}

	@Override
	public void endDocument() throws SAXException 
	{
		
	}

	@Override
	public void endElement(String uri, String localName, String qName)throws SAXException 
	{
		if(localName.equalsIgnoreCase(GRAPH))
		{
			isGraph=false;			
		}
		if(localName.equalsIgnoreCase(EDGE))
		{
			isEdge=false;
		}
		if(localName.equalsIgnoreCase(NODE))
		{
			isNode=false;			
		}
		
		if(localName.equalsIgnoreCase(ATT))
		{
			isAtt=false;
		}		
	}


	
	@Override
	public void startDocument() throws SAXException 
	{
		graphs=new ArrayList<XGMMLTypes.XGraph>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,Attributes atts) throws SAXException 
	{
		if(localName.equalsIgnoreCase(GRAPH))
		{
			currentGraph=new XGMMLTypes.XGraph();
			setAtts(currentGraph, atts);
			isGraph=true;
			graphs.add(currentGraph);
		}
		if(localName.equalsIgnoreCase(EDGE))
		{
			currentEdge=new XGMMLTypes.XEdge();
			setAtts(currentEdge, atts);
			currentEdge.source=atts.getValue("source");
			currentEdge.target=atts.getValue("target");
			currentEdge.weight=atts.getValue("weight");
			isEdge=true;
			currentGraph.edges.add(currentEdge);
			
		}
		if(localName.equalsIgnoreCase(NODE))
		{
			currentNode=new XGMMLTypes.XNode();
			setAtts(currentNode, atts);
			currentNode.weight=atts.getValue("weight");	
			isNode=true;
			currentGraph.nodes.add(currentNode);
		}
		
		if(localName.equalsIgnoreCase(ATT))
		{
			currentAtt=new XGMMLTypes.XAtt();
			
			currentAtt.name=atts.getValue("name");	
			currentAtt.value=atts.getValue("value");	
			currentAtt.label=atts.getValue("label");	
			
			if(isEdge)
				currentEdge.atts.add(currentAtt);
			if(isNode)
				currentNode.atts.add(currentAtt);
			if(isAtt)
				currentAtt.atts.add(currentAtt);
			if(isGraph)
				currentGraph.atts.add(currentAtt);
			
			isAtt=true;
		}
		
		if(localName.equalsIgnoreCase(GRAPHICS))
		{
			currentGraphics=new XGMMLTypes.XGraphics();
			for(int i=0; i!=atts.getLength(); ++i)
			{
				currentGraphics.attributes.put(atts.getLocalName(i), atts.getValue(i));				
			}			
			if(isEdge)
				currentEdge.graphics=currentGraphics;
			if(isNode)
				currentNode.graphics=currentGraphics;
			

		}
		
		if(localName.equalsIgnoreCase(LINE))
		{
			currentLine=new XGMMLTypes.XLine();
			currentGraphics.line=currentLine;
		}
		
		if(localName.equalsIgnoreCase(CENTER))
		{
			XGMMLTypes.XCenter c=new XGMMLTypes.XCenter();
			c.x=atts.getValue("x");	
			c.y=atts.getValue("y");	
			c.z=atts.getValue("z");	
			currentGraphics.center=c;
		}
		
		if(localName.equalsIgnoreCase(POINT))
		{			
			XGMMLTypes.XPoint c=new XGMMLTypes.XPoint();
			c.x=atts.getValue("x");	
			c.y=atts.getValue("y");	
			c.z=atts.getValue("z");	
			currentLine.points.add(c);
		}
		
		
	}

	private void setAtts(XType t,Attributes atts)
	{
		t.id=atts.getValue("id");
		t.label=atts.getValue("label");
		t.labelanchor=atts.getValue("labelanchor");
		t.name=atts.getValue("name");
		
		for(int i=0; i!=atts.getLength(); ++i)
		{
			if(atts.getLocalName(i).equals("id") ||  
					atts.getLocalName(i).equals("name") || 
					atts.getLocalName(i).equals("label") || 
					atts.getLocalName(i).equals("labelanchor"))
				continue;
			t.attributes.put(atts.getLocalName(i), atts.getValue(i));				
		}
	}
	
	
	public List<XGMMLTypes.XGraph> getGraphs() 
	{
		return graphs;
	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {}

	@Override
	public void processingInstruction(String target, String data) throws SAXException {}

	@Override
	public void setDocumentLocator(Locator locator) {}

	@Override
	public void skippedEntity(String name) throws SAXException {}

	@Override
	public void startPrefixMapping(String prefix, String uri) throws SAXException {}

}
