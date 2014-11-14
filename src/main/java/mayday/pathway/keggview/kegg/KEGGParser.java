package mayday.pathway.keggview.kegg;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Generic parser for KEGG files. Uses the strategy pattern to handle details of parsing. 
 * @author Stephan Symons
 *
 */
public class KEGGParser 
{
	private ParsingStrategy parsingStrategy;
	
	public KEGGParser(ParsingStrategy strategy)
	{
		parsingStrategy=strategy;
	}
	
	public Map<String,KEGGObject> parseData(String fileName) throws IOException
	{
		BufferedReader r=new BufferedReader(new FileReader(fileName));
		Map<String, KEGGObject> result=new HashMap<String, KEGGObject>();
		
		String line=r.readLine();

		while(line!=null)
		{
			// a new entry. the first line is the ENTRY line: ENTRY\s+NUMBER\s+Type
			String[] tok=line.split("\\s+");
			GenericKEGGDataItem item=new GenericKEGGDataItem();
			item.setType(tok[2].trim());
			item.addField(tok[0],tok[1]);
			
			line=r.readLine();
			while(!line.startsWith("///"))
			{
				StringBuffer entry=new StringBuffer();
				entry.append(line);
				line=r.readLine();
				while(line.startsWith(" "))
				{
					entry.append("\n"+line.trim());
					line=r.readLine();
				}
				if(entry.length() >= 11)
					item.addField(entry.substring(0,11).trim(), entry.substring(12));				
			}
			KEGGObject o=parsingStrategy.processItem(item);
			if(o!= null) 
				result.put(o.getEntry(),o);
			line=r.readLine();
		}
		return result;
				
	}
	

}
