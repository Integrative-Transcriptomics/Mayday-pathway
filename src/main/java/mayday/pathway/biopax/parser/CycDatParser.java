package mayday.pathway.biopax.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CycDatParser 
{
	public static List<String> parseAttributes(String file) throws IOException 
	{
		return parseAttributes(new File(file));
	}
	
	
	public static List<String> parseAttributes(File file) throws IOException 
	{
		List<String> res=new ArrayList<String>();
		BufferedReader r=new BufferedReader(new FileReader(file));
		
		String line=r.readLine();
		
		while(line!= null && !line.startsWith("# Attributes:") )
			line=r.readLine();
		
		line=r.readLine();
		
		while(!line.trim().equals("#"))
		{
			res.add(line.substring(1).trim());
			line=r.readLine();			
		}
		return res;
	}
	
	public static Map<String,String> parseMapping(String file, String key, String value) throws IOException
	{
		return parseMapping(new File(file),key,value);
	}
	
	public static Map<String,String> parseMapping(File file, String key, String value) throws IOException
	{
		HashMap<String, String> res=new HashMap<String, String>();
		
		BufferedReader r=new BufferedReader(new FileReader(file));
		
		String line=r.readLine();

		while(line!=null)
		{
			// skip leading comments
			if(line.startsWith("#"))
			{
				line=r.readLine();
				continue;
			}
			// read entry
			String keyV=null;
			String valueV=null;
			while(!line.trim().equals("//"))
			{

				if(line.startsWith(key))
				{
					String[] tok=line.split(" - ");
					if(tok.length==2)
						keyV=tok[1].trim();				
				}
				if(line.startsWith(value))
				{
					String[] tok=line.split(" - ");
					if(tok.length==2)
						valueV=tok[1].trim();
				}
				line=r.readLine();
			}
			if(keyV!= null && valueV!=null)
			{
				res.put(keyV, valueV);				
			}
			line=r.readLine();
		}
		return res;
	}
		
}
