package mayday.graphviewer.datasources.uniprot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.xml.sax.InputSource;

public class UniProtConnector {
	
	private String uniprotBaseUrl="http://www.ebi.ac.uk/Tools/dbfetch/dbfetch?db=uniprot&style=raw&format=uniprotxml";
	//&id=P38703";
	
	private boolean ready=false;
	
	public UniProtParser uniProtFetch(List<String>  ids) throws Exception
	{
		StringBuffer term=new StringBuffer();
		boolean first=true;
		for(String i:ids)
		{
			if(first)
			{
				term.append(i);
				first=false;
				continue;
			}
			term.append(",").append(i);			
		}

		String query=uniprotBaseUrl+"&id="+term.toString();
		URL url=new URL(query);
		URLConnection con = url.openConnection();

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		inputLine=in.readLine();
		if(inputLine.startsWith("ERROR"))
		{
			in.close();
			ready=false;
			return null;
		}
		StringWriter wri=new StringWriter();
		
		while(inputLine!=null)
		{
			wri.write(inputLine);
			inputLine=in.readLine();
		}
		StringReader reader=new StringReader(wri.toString());
		UniProtParser parser=new UniProtParser();
		parser.parse(new InputSource(reader));
		ready=true;
		return parser;
	}
	
	public boolean isReady() {
		return ready;
	}
}
