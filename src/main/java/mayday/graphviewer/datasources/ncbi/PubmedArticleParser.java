package mayday.graphviewer.datasources.ncbi;

import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class PubmedArticleParser  implements EUtilsContentHandler
{
	private List<Map<String,String>> results;
	private Map<String,String> currentArticle;

	private boolean pubDate;
	public static final String PMID="PMID";
	public static final String VOLUME="Volume";
	public static final String ISSUE="Issue";
	private static final String PUBDATE_TAG="PubDate";
	public static final String YEAR="Year";
	public static final String MONTH="Month";
	public static final String JOURNALTITLE="Title";
	public static final String ARTICLETITLE="ArticleTitle";
	public static final String PAGES="MedlinePgn";
	public static final String ABSTRACTTEXT="AbstractText";
	private  static final String ARTICLE_TAG="PubmedArticle";



	private String chars; 

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
	public void endElement(String uri, String localName, String qName)
	throws SAXException 
	{
		if(localName.equals(ARTICLE_TAG))
		{
			results.add(currentArticle);
		}

		if(localName.equals(PUBDATE_TAG))
		{
			pubDate=false;
		}
		if(localName.equals(VOLUME))
			currentArticle.put(VOLUME, chars);
		if(localName.equals(PMID))
			currentArticle.put(PMID, chars);
		if(localName.equals(JOURNALTITLE))
			currentArticle.put("Journal", chars);
		if(localName.equals(ISSUE))
			currentArticle.put(ISSUE, chars);
		if(localName.equals(ARTICLETITLE))
			currentArticle.put("Title", chars);
		if(localName.equals(PAGES))
			currentArticle.put("Pages", chars);
		if(localName.equals(ABSTRACTTEXT))
			currentArticle.put("Abstract",chars);
		if(localName.equals(YEAR) && pubDate)
			currentArticle.put(YEAR, chars);
		if(localName.equals(MONTH) && pubDate)
			currentArticle.put(MONTH, chars);
	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length)
	throws SAXException {}

	@Override
	public void processingInstruction(String target, String data)
	throws SAXException {}

	@Override
	public void setDocumentLocator(Locator locator) {}

	@Override
	public void skippedEntity(String name) throws SAXException {}

	@Override
	public void startDocument() throws SAXException 
	{		
		results=new ArrayList<Map<String,String>>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,Attributes atts) throws SAXException 
	{
		if(localName.equals(ARTICLE_TAG))
		{
			currentArticle=new HashMap<String, String>();
		}
		if(localName.equals(PUBDATE_TAG))
		{
			pubDate=true;
		}

	}

	@Override
	public void startPrefixMapping(String prefix, String uri)
	throws SAXException {}

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
		PubmedArticleParser handler=new PubmedArticleParser();
		parser.setContentHandler(handler);			
		parser.parse(new InputSource(new FileReader("/home/symons/Desktop/efetch.xml")));
	}
	
	@Override
	public List<Map<String, String>> getValues()
	{
		return results;
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


}
