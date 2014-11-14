package mayday.graphviewer.datasources.uniprot;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class UniProtParser 
{
	private DocumentBuilder builder;

	private static final String ENTRY="entry";
	private static final String PROTEIN="protein";
	private static final String REC_NAME="recommendedName";
	private static final String FULL_NAME="fullName";
	private static final String ALT_NAME="alternativeName";
	private static final String ACCESSION="accession";
	private static final String GENE="gene";
	private static final String NAME="name" ;
	private static final String TYPE="type";
	private static final String REFERENCE="reference";
	private static final String CITATION="citation";
	private static final String TITLE="title";
	private static final String DB_REF="dbReference";
	private static final String COMMENT="comment";
	private static final String INTERACTION="interaction";
	private static final String INTERACTANT="interactant";
	private static final String SUBCELLULAR_LOCATION="subcellular location";

	private Map<String, Integer > idMaps;
	private List<Map<String, String>> protein;
	private List<Map<String, String>> gene;
	private List< List< Map<String, String >>> references;
	private List<Map<String, String>> comments;
	private List<List<String>> interactors;

	public UniProtParser() throws Exception
	{
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		builder=dbf.newDocumentBuilder();
	}

	public void parse(InputSource input) throws Exception
	{
		Document document =builder.parse(input);
		idMaps=new HashMap<String, Integer>();
		protein=new ArrayList<Map<String,String>>();
		gene=new ArrayList<Map<String,String>>();
		references=new ArrayList<List<Map<String,String>>>();
		comments=new ArrayList<Map<String,String>>();
		interactors=new ArrayList<List<String>>();

		NodeList l=document.getElementsByTagName(ENTRY);
		for(int i=0; i!=l.getLength(); ++i)
		{
			Element e=(Element)l.item(i);
			NodeList accessions=e.getElementsByTagName(ACCESSION);
			for(int j=0; j!=accessions.getLength(); ++j)
			{
				Element accEl=(Element)accessions.item(j);
				idMaps.put(accEl.getTextContent(), i);
			}
			parseEntry(e);			
		}
	}

	public void parse(String fileName) throws Exception
	{
		parse(new InputSource(new FileInputStream(fileName)));
	}

	private void parseEntry(Element e)
	{
		NodeList proteinEnties=e.getElementsByTagName(PROTEIN);
		Map<String,String> protein=new HashMap<String, String>();
		for(int i=0; i!=proteinEnties.getLength(); ++i)
		{
			Element el=(Element)proteinEnties.item(i);
			NodeList els=el.getElementsByTagName(REC_NAME);
			if(els.getLength()!=0)
			{
				Element recNameEl=(Element) els.item(0);
				Element nameEl=(Element)recNameEl.getElementsByTagName(FULL_NAME).item(0);
				protein.put(REC_NAME, nameEl.getTextContent());
			}
			els=el.getElementsByTagName(ALT_NAME);
			if(els.getLength()!=0)
			{
				Element recNameEl=(Element) els.item(0);
				Element nameEl=(Element)recNameEl.getElementsByTagName(FULL_NAME).item(0);
				protein.put(ALT_NAME, nameEl.getTextContent());
			}
		}
		this.protein.add(protein);

		NodeList geneEnties=e.getElementsByTagName(GENE);
		Map<String,String> gene=new HashMap<String, String>();
		for(int i=0; i!=geneEnties.getLength(); ++i)
		{
			Element el=(Element)geneEnties.item(i);
			NodeList els=el.getElementsByTagName(NAME);
			for(int j=0; j!=els.getLength(); ++j)
			{
				gene.put(((Element)els.item(j)).getAttribute(TYPE), ((Element)els.item(j)).getTextContent()  );
			}

		}
		this.gene.add(gene);

		NodeList refEntries=e.getElementsByTagName(REFERENCE);
		List<Map<String,String>> refs=new ArrayList<Map<String,String>>();
		for(int i=0; i!=refEntries.getLength(); ++i)
		{
			Element re=(Element)refEntries.item(i);
			refs.add(parseReference(re));
		}
		references.add(refs);

		NodeList commentList=e.getElementsByTagName(COMMENT);
		Map<String,String> comments=new HashMap<String, String>();
		List<String> interacts=new ArrayList<String>();
		for(int i=0; i!=commentList.getLength(); ++i)
		{
			Element cel=(Element)commentList.item(i);
			String type=cel.getAttribute("type");

			if(type.equals(SUBCELLULAR_LOCATION))
			{
				comments.put(SUBCELLULAR_LOCATION, parseSubcellularLocatio(cel));
			}else
			{
				if(type.equals(INTERACTION))
				{
					interacts.add(parseInteractant(cel));
				}else
				{
					comments.put(type, parseCommentText(cel));
				}
			}

		}
		interactors.add(interacts);
		this.comments.add(comments);
	}

	private String parseCommentText(Element e)
	{
		NodeList l=e.getElementsByTagName("text");
		String res=null;
		for(int i=0; i!=l.getLength(); ++i)
		{
			Element el=(Element)l.item(i);
			res= el.getTextContent();
		}
		return res;
	}

	private String parseInteractant(Element e)
	{
		NodeList iaList=e.getElementsByTagName(INTERACTANT);
		String res=null;
		for(int i=0; i!=iaList.getLength(); ++i)
		{
			Element iel=(Element)iaList.item(i);
			NodeList il=iel.getElementsByTagName("id");
			if(il.getLength()==0)
				continue; // we don't want this one;
			Element idEl=(Element)il.item(0);
			res=idEl.getTextContent();
		}
		return res;
	}

	private String parseSubcellularLocatio(Element e)
	{
		NodeList iaList=e.getElementsByTagName("subcellularLocation");
		String res=null;
		for(int i=0; i!=iaList.getLength(); ++i)
		{
			Element iel=(Element)iaList.item(i);
			NodeList il=iel.getElementsByTagName("location");
			if(il.getLength()==0)
				continue; // we don't want this one;
			Element idEl=(Element)il.item(0);
			res=idEl.getTextContent();
		}
		return res;	
	}

	private Map<String, String> parseReference(Element e)
	{
		NodeList citList=e.getElementsByTagName(CITATION);
		Map<String,String> ref=new HashMap<String, String>();
		for(int i=0; i!= citList.getLength(); ++i)
		{
			Element cit=(Element)citList.item(i);
			for(int j=0; j!=cit.getAttributes().getLength(); ++j)
			{
				ref.put(cit.getAttributes().item(j).getNodeName(), cit.getAttributes().item(j).getNodeValue());
			}
		}
		NodeList tList=e.getElementsByTagName(TITLE);
		if(tList.getLength()!=0)
		{
			Element tel=(Element)tList.item(0);
			ref.put(TITLE, tel.getTextContent());
		}
		NodeList dbList=e.getElementsByTagName(DB_REF);
		for(int i=0; i!= dbList.getLength(); ++i)
		{
			Element cit=(Element)dbList.item(i);
			ref.put(cit.getAttribute("type"), cit.getAttribute("id"));
		}
		return ref;
	}

	public static void main(String[] args) throws Exception
	{
		UniProtParser parser=new UniProtParser();
		parser.parse("/Users/sq/Desktop/P33413.xml");		
		parser.parse("/Users/sq/Desktop/Q04895.xml");		

	}

	public List<Map<String, String>> getProtein() {
		return protein;
	}

	public Map<String, Integer> getIdMaps() {
		return idMaps;
	}

	public List<Map<String, String>> getGene() {
		return gene;
	}

	public List<Map<String, String>> getComments() {
		return comments;
	}

	public List<List<String>> getInteractors() {
		return interactors;
	}

	public List<List<Map<String, String>>> getReferences() {
		return references;
	}
}
