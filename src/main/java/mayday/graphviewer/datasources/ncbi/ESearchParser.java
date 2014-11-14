package mayday.graphviewer.datasources.ncbi;

import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class ESearchParser implements ContentHandler
{
	private List<String> ids;
	private String webEnv;
	private int queryKey;
	private int count; 
	private String chars; 
	
	
	int d=0;
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException 
	{
		chars=new String(ch,start,length).trim();
	}

	@Override
	public void endDocument() throws SAXException 
	{	
	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {}

	@Override
	public void processingInstruction(String target, String data)throws SAXException {}

	@Override
	public void setDocumentLocator(Locator locator) {}

	@Override
	public void skippedEntity(String name) throws SAXException {}

	@Override
	public void startDocument() throws SAXException 
	{
		ids=new ArrayList<String>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,Attributes atts) throws SAXException 
	{
		d++;
	}

	@Override
	public void startPrefixMapping(String prefix, String uri) throws SAXException {}

	public static void main(String[] args)throws Exception 
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
			ESearchParser handler=new ESearchParser();
			parser.setContentHandler(handler);			
			parser.parse(new InputSource(new FileReader("/home/symons/Desktop/esearch.xml")));
	}
	
	public void parse(InputSource source) throws Exception
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
			parser.setContentHandler(this);			
			parser.parse(source);
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException 
	{
		d--;
		chars=chars.trim();
		if(d==1 && localName.equals("Count"))
		{
			count=Integer.parseInt(chars);
			chars=null;
		}
		if(d==2 && localName.equals("Id"))
		{
			ids.add(chars);
		}
		if(d==1 && localName.equals("WebEnv"))
		{
			webEnv=chars;
		}
		if(d==1 && localName.equals("QueryKey"))
		{
			queryKey=Integer.parseInt(chars);
		}
		
	}

	public List<String> getIds() {
		return ids;
	}

	public void setIds(List<String> ids) {
		this.ids = ids;
	}

	public String getWebEnv() {
		return webEnv;
	}

	public void setWebEnv(String webEnv) {
		this.webEnv = webEnv;
	}

	public int getQueryKey() {
		return queryKey;
	}

	public void setQueryKey(int queryKey) {
		this.queryKey = queryKey;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	
	
}
