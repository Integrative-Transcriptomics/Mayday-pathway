//package mayday.graphviewer.datasources.biopax2;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Stack;
//
//import mayday.core.structures.maps.MultiHashMap;
//import mayday.pathway.biopax.parser.MasterObject;
//
//import org.xml.sax.Attributes;
//import org.xml.sax.SAXException;
//import org.xml.sax.helpers.DefaultHandler;
//
//public class BioPaxHandler2  extends DefaultHandler
//{
//	private StringBuffer chars;
//	private int p;
//	private Stack<String> ids;
//	private Stack<String> attributes;
//	private boolean hasChild;
//	private TripleStorage triples;	
//	private Map<String,Boolean> skipList;
//
//	private MultiHashMap<String,String> objectTypes;
//
//	public BioPaxHandler2() 
//	{
//		skipList=new HashMap<String, Boolean>();
//		skipList.put("unificationXref",true);
//		skipList.put("relationshipXref",true);
//		skipList.put("publicationXref",true);
//		skipList.put("chemicalStructure",true);
//		skipList.put("SEQUENCE",true);
//		skipList.put("RELATIONSHIP-TYPE",true);
//		skipList.put("DB",true);
//		skipList.put("ID",true);
//		skipList.put("XREF",true);
//		skipList.put("STRUCTURE-FORMAT",true);
//		skipList.put("STRUCTURE-DATA",true);
//	}
//	
//	public BioPaxHandler2(Map<String,Boolean> skipList) 
//	{
//		this.skipList=skipList;
//	}
//
//	public void characters(char[] ch, int start, int length) throws SAXException 
//	{
//		chars=new StringBuffer();
//		chars.append(new String(ch,start,length).trim());
//	}
//
//	public void endDocument() throws SAXException 
//	{
//		System.out.println("parsed "+triples.size()+" triples");
//	}
//
//	public void startDocument() throws SAXException 
//	{	
//		chars=null;
//		p=0;
//		attributes=new Stack<String>();
//		ids=new Stack<String>();
//		triples=new TripleStorage();	
//		objectTypes=new MultiHashMap<String, String>();
//	}
//
//	public void startElement(String uri, String localName, String name,	Attributes atts) throws SAXException 
//	{
//		if(!name.startsWith("bp:")) return;
//
//		p++;
//		if(isObject())
//		{
//			String id=atts.getValue("rdf:ID");
//
//
//			if(hasChild)
//			{
//				//				System.out.println(">>"+ids.peek()+" "+attributes.peek()+" "+id);
//				if(!skipList.containsKey(localName))
//					triples.add(ids.peek(),id,attributes.peek());
//				hasChild=false;
//			}
//			ids.push(id);
//			//			System.out.println(localName+" OBJECT-TYPE "+id);
//			if(!skipList.containsKey(localName))
//				triples.add(id, localName, "OBJECT-TYPE");
//			objectTypes.put(localName, id);
//			MasterObject currentObject=new MasterObject(id);
//			currentObject.setObjectType(localName);
//		}else
//		{
//			attributes.push(localName);
//			if(atts.getValue("rdf:datatype")!=null)
//			{
//
//			}else
//				if(atts.getValue("rdf:resource")!=null)
//				{
//					//					System.out.println(ids.peek()+" "+localName+" "+atts.getValue("rdf:resource").substring(1));
//					if(!skipList.containsKey(localName))
//						triples.add(ids.peek(),atts.getValue("rdf:resource").substring(1),localName);
//				}else
//					hasChild=true;
//		}
//	}
//
//	public void endElement(String uri, String localName, String name) throws SAXException 
//	{	
//
//		if(!name.startsWith("bp:")) 
//			return;		
//		if(isObject())
//		{
//			ids.pop();
//		}else
//		{
//			if(chars.length()!=0)
//			{				
//				if(!skipList.containsKey(localName))
//					triples.add(ids.peek(),chars.toString(),attributes.peek());
//				chars=new StringBuffer();
//				hasChild=false;
//			}
//			attributes.pop();
//		}		
//		p--;
//	}	
//
//	private boolean isObject()
//	{
//		return p%2==1;
//	}
//
//	public MultiHashMap<String, String> getObjectTypes()
//	{
//		return objectTypes;
//	}
//
//	public TripleStorage getTriples()
//	{
//		return triples;
//	}
//	
//	
//}
//
//
//

