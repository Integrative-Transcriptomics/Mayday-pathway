package mayday.graphviewer.datasources.biopax2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import mayday.core.structures.maps.MultiHashMap;
import mayday.pathway.biopax.parser.MasterObject;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class BioPaxHandler3 extends DefaultHandler
{
	private StringBuffer chars;
	private int p;
	private Stack<String> ids;
	private Stack<String> attributes;
	private boolean hasChild;
	
	private PreparedStatement statement; 
	
	private Map<String,Boolean> skipList;

	private MultiHashMap<String,String> objectTypes;

	private Connection con;
	
	public BioPaxHandler3(Connection con) 
	{
		skipList=new HashMap<String, Boolean>();
		skipList.put("unificationXref",true);
		skipList.put("relationshipXref",true);
		skipList.put("publicationXref",true);
		skipList.put("chemicalStructure",true);
		skipList.put("SEQUENCE",true);
		skipList.put("RELATIONSHIP-TYPE",true);
		skipList.put("DB",true);
		skipList.put("ID",true);
		skipList.put("XREF",true);
		skipList.put("STRUCTURE-FORMAT",true);
		skipList.put("STRUCTURE-DATA",true);
		
		this.con=con;
	}
	
	public BioPaxHandler3(Connection con, Map<String,Boolean> skipList) 
	{
		this.skipList=skipList;
		this.con=con;
	}

	public void characters(char[] ch, int start, int length) throws SAXException 
	{
		chars=new StringBuffer();
		chars.append(new String(ch,start,length).trim());
	}

	public void endDocument() throws SAXException 
	{
//		System.out.println("parsed "+triples.size()+" triples");
	}

	public void startDocument() throws SAXException 
	{	
		chars=null;
		p=0;
		attributes=new Stack<String>();
		ids=new Stack<String>();
		objectTypes=new MultiHashMap<String, String>();
		
		try {
			statement=con.prepareStatement("INSERT INTO tuples VALUES(?, ?, ?)");
		} catch (SQLException e) 
		{
			throw new SAXException("Error preparing the document handling");
		}
		
	}

	public void startElement(String uri, String localName, String name,	Attributes atts) throws SAXException 
	{
		if(!name.startsWith("bp:")) return;

		p++;
		if(isObject())
		{
			String id=atts.getValue("rdf:ID");


			if(hasChild)
			{
				//				System.out.println(">>"+ids.peek()+" "+attributes.peek()+" "+id);
				if(!skipList.containsKey(localName))
					add(ids.peek(),id,attributes.peek());
				hasChild=false;
			}
			ids.push(id);
			//			System.out.println(localName+" OBJECT-TYPE "+id);
			if(!skipList.containsKey(localName))
				add(id, localName, "OBJECT-TYPE");
			objectTypes.put(localName, id);
			MasterObject currentObject=new MasterObject(id);
			currentObject.setObjectType(localName);
		}else
		{
			attributes.push(localName);
			if(atts.getValue("rdf:datatype")!=null)
			{

			}else
				if(atts.getValue("rdf:resource")!=null)
				{
					//					System.out.println(ids.peek()+" "+localName+" "+atts.getValue("rdf:resource").substring(1));
					if(!skipList.containsKey(localName))
						add(ids.peek(),atts.getValue("rdf:resource").substring(1),localName);
				}else
					hasChild=true;
		}
	}

	public void endElement(String uri, String localName, String name) throws SAXException 
	{	

		if(!name.startsWith("bp:")) 
			return;		
		if(isObject())
		{
			ids.pop();
		}else
		{
			if(chars.length()!=0)
			{				
				if(!skipList.containsKey(localName))
					add(ids.peek(),chars.toString(),attributes.peek());
				chars=new StringBuffer();
				hasChild=false;
			}
			attributes.pop();
		}		
		p--;
	}	

	private boolean isObject()
	{
		return p%2==1;
	}

	public MultiHashMap<String, String> getObjectTypes()
	{
		return objectTypes;
	}

	private void add(String s, String o, String p) 
	{
		try {
			statement.setString(1, s);
			statement.setString(2, p);
			statement.setString(3, o);
			statement.execute();
		} catch (SQLException e) 
		{
			e.printStackTrace();
		}
		
	}
	
}

