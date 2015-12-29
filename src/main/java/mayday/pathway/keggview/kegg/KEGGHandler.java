package mayday.pathway.keggview.kegg;

import java.net.URL;


import mayday.pathway.keggview.kegg.pathway.Entry;
import mayday.pathway.keggview.kegg.pathway.Graphics;
import mayday.pathway.keggview.kegg.pathway.Pathway;
import mayday.pathway.keggview.kegg.pathway.ReactionEntry;
import mayday.pathway.keggview.kegg.pathway.Relation;
import mayday.pathway.keggview.kegg.pathway.Substance;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class KEGGHandler implements ContentHandler {

	private Pathway currentPathway;
	private Entry currentEntry;
	private Relation currentRelation;
	private ReactionEntry currentReaction;
	private Substance currentSubstance;
	
	public void characters(char[] arg0, int arg1, int arg2) throws SAXException 
	{
	}

	public void endDocument() throws SAXException 
	{

	}

	public void endElement(String arg0, String localName, String arg2)
			throws SAXException 
	{
		if(localName.equals("entry"))
		{
			currentPathway.addEntry(currentEntry);
		}
		if(localName.equals("relation"))
		{
			currentPathway.addRelation(currentRelation);
		}
		if(localName.equals("substrate"))
		{
			currentReaction.addSubstrate(currentSubstance);
		}
		if(localName.equals("product"))
		{
			currentReaction.addProduct(currentSubstance);
		}
		if(localName.equals("reaction"))
		{
			currentPathway.addReaction(currentReaction);
			
		}		
			

	}

	public void endPrefixMapping(String arg0) throws SAXException 
	{
	}

	public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
			throws SAXException {
	}

	public void processingInstruction(String arg0, String arg1)
			throws SAXException {
	}

	public void setDocumentLocator(Locator arg0) {
	}

	public void skippedEntity(String arg0) throws SAXException {
	}

	public void startDocument() throws SAXException 
	{
	}

	public void startElement(String uri, String localName, String qname,
			Attributes atts) throws SAXException 
	{
		if(localName.equals("pathway"))
		{
			currentPathway=new Pathway();
			currentPathway.setName(atts.getValue("name"));
			try{
			currentPathway.setImage(new URL(atts.getValue("image")));
			currentPathway.setLink(new URL(atts.getValue("link")));
			}catch(Exception e){}
			
			currentPathway.setTitle(atts.getValue("title"));
			currentPathway.setOrg(atts.getValue("org"));
			currentPathway.setNumber(atts.getValue("number"));	
			return;
		}
		if(localName.equals("entry"))
		{
			currentEntry=new Entry();
			currentEntry.setId(atts.getValue("id"));
			currentEntry.setName(atts.getValue("name"));
			currentEntry.setType(atts.getValue("type"));
			currentEntry.setReaction(atts.getValue("reaction"));
			try{
				currentEntry.setLink(new URL(atts.getValue("link")));
			}catch(Exception e){}
			currentEntry.setMap(atts.getValue("link"));
			return;
		}
		if(localName.equals("graphics"))
		{
			Graphics g=new Graphics();
			g.setName(atts.getValue("name"));
			g.setFgColor(atts.getValue("fgcolor"));
			g.setBgColor(atts.getValue("bgcolor"));
			g.setType(atts.getValue("type"));
			if(atts.getValue("coords")!=null)
			{
				String[] coords=atts.getValue("coords").split(",");
				g.setX(Integer.parseInt(coords[0]));
				g.setY(Integer.parseInt(coords[1]));
			}else
			{
			g.setX(Integer.parseInt(atts.getValue("x")));
			g.setY(Integer.parseInt(atts.getValue("y")));
			g.setWidth(Integer.parseInt(atts.getValue("width")));
			g.setHeight(Integer.parseInt(atts.getValue("height")));	
			}
			currentEntry.setGraphics(g);
			return;
		}
		if(localName.equals("relation"))
		{
			currentRelation=new Relation();
			currentRelation.setCompound(atts.getValue("compound"));
			currentRelation.setEntry1(atts.getValue("entry1"));
			currentRelation.setEntry2(atts.getValue("entry2"));
			currentRelation.setType(atts.getValue("type"));
			return;
		}
		
		if(localName.equals("subtype"))
		{
			currentRelation.addSubtype(atts.getValue("name"), atts.getValue("value"));
			return;
		}
		if(localName.equals("reaction"))
		{
			currentReaction=new ReactionEntry();
			currentReaction.setName(atts.getValue("name"));
			currentReaction.setType(atts.getValue("type"));
			return;
		}
		if(localName.equals("substrate") || localName.equals("product"))
		{
			currentSubstance=new Substance();
			currentSubstance.setName(atts.getValue("name"));
			return;
		}
		if(localName.equals("alt"))
		{
			currentSubstance.setAltName(atts.getValue("name"));
			return;
		}
		//System.out.println(uri+"\t"+localName+"\t"+qname+"\t"+atts.toString());
	

	}

	public void startPrefixMapping(String arg0, String arg1)
			throws SAXException 
	{

	}
	
	/**
	 * Returns the pathway stored in the XML file.
	 * @return
	 */
	public Pathway getPathway()
	{
		return currentPathway;
	}

}
