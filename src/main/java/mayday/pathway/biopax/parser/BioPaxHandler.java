package mayday.pathway.biopax.parser;

import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class BioPaxHandler  extends DefaultHandler
{
	
	StringBuffer chars;
	Stack<String> path;
	Stack<MasterObject> objects;
	Stack<String> attributes;
	boolean reference;
	MasterObject referenceObject;
	
	boolean hasString;
	
	Map<String, MasterObject> result;
	
	public void characters(char[] ch, int start, int length)
			throws SAXException 
	{
		if(hasString) 
		{
			chars=new StringBuffer();
			chars.append(new String(ch,start,length).trim());
		}
	}

	public void endDocument() throws SAXException 
	{

	}

	public void startDocument() throws SAXException 
	{	
		chars=null;
		path=new Stack<String>();
		objects=new Stack<MasterObject>();
		result=new TreeMap<String, MasterObject>();
		attributes=new Stack<String>();
		reference=false;
	}

	public void startElement(String uri, String localName, String name,
			Attributes atts) throws SAXException 
	{
		if(!name.startsWith("bp:")) return;

		
		path.add(localName);
//		System.out.println(isObject()+" "+path);
		
		if(isObject())
		{
			String id=atts.getValue("rdf:ID");
			MasterObject currentObject=new MasterObject(id);
			currentObject.setObjectType(localName);
			objects.push(currentObject);
//			System.out.println(objects);
		}else
		{
			attributes.push(localName);
			if(atts.getValue("rdf:datatype")!=null)
			{
				hasString=true;
			}
			if(atts.getValue("rdf:resource")!=null)
			{
				reference=true;
				referenceObject=new MasterObject(atts.getValue("rdf:resource").substring(1));
				referenceObject.setResolved(false);				
			}
		}
	}
	
	public void endElement(String uri, String localName, String name)
	throws SAXException 
	{	
		if(!name.startsWith("bp:")) return;

		
		
		if(isObject())
		{
			MasterObject object=objects.pop();
			if(!objects.isEmpty())
			{
				objects.peek().setMember(attributes.peek(), object);
			}
			result.put(object.getId(), object);
		}else
		{
			if(reference)
			{
				reference=false;
				objects.peek().setMember(localName, referenceObject);
				objects.peek().setResolved(false);
				
			}
			if(hasString)
			{
				objects.peek().setProperty(localName, chars.toString());
				hasString=false;
			}
			attributes.pop();
		}
		
		
		path.pop();
	}
	

	private boolean isObject()
	{
		return path.size()%2==1;
	}

	/**
	 * @return the result
	 */
	public Map<String, MasterObject> getResult() {
		return result;
	}


	
}



