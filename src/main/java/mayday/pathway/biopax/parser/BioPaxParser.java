package mayday.pathway.biopax.parser;

import java.io.FileReader;
import java.io.StringReader;
import java.util.Map;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class BioPaxParser 
{
	public Map<String, MasterObject> parse(String file) throws Exception
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
			BioPaxHandler handler=new BioPaxHandler();
			parser.setContentHandler(handler);			
			parser.parse(new InputSource(new FileReader(file)));

			Map<String, MasterObject> m=handler.getResult();
			for(MasterObject o:m.values())
			{
				o.resolve(m);			
			}
		return m;
	}
}
