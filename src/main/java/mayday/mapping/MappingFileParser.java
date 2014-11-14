package mayday.mapping;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class MappingFileParser 
{
	public static HashMap<String,String> parse(String file) throws IOException
	{
		return parse(new File(file));
	}
	
	public static HashMap<String,String> parse(File file) throws IOException
	{
		BufferedReader r=new BufferedReader(new FileReader(file));
		HashMap<String, String> res=new HashMap<String, String>();
		
		String line=r.readLine();
		while(line!=null)
		{
			String[] tok=line.split("\\s");
			for(int i=1; i!= tok.length;++i)
			{
				res.put(tok[0], tok[i]);
			}
			line=r.readLine();
		}
		return res;		
	}
	
	public static void main(String[] args) throws Exception {
		MappingFileParser.parse("/home/symons/Desktop/Mapping-test/en.txt");
	}
}
