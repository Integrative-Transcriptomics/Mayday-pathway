package mayday.graphviewer.datasources.obo;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import mayday.core.structures.maps.MultiHashMap;


public class OboParser 
{

	Map<String, OboTerm> terms;
	MultiHashMap<OboTerm, String> relationMap;
	
	public Map<String, OboTerm> parse(String file) throws Exception
	{
		terms=new HashMap<String, OboTerm>();
		relationMap=new MultiHashMap<OboTerm, String>();
		BufferedReader r=new BufferedReader(new FileReader(file));
		//seek to term
		String line=r.readLine();
		while(!line.startsWith("[Term]"))
		{
			line=r.readLine();
		}
		while(r.ready() && line!=null)
		{
			OboTerm term=parseTerm(r);
			terms.put(term.getId(),term);
			line=r.readLine();
			if(line==null)
				break;
			while(!line.startsWith("[Term]"))
			{
				line=r.readLine();
				if(line==null)
					break;
			}
		}
		// add references to build ontology structure
		for(OboTerm t:terms.values())
		{
			for(String id: relationMap.get(t))
			{
				t.addIsA(terms.get(id));
			}
		}
		return terms;		
	}
	
	private OboTerm parseTerm(BufferedReader r) throws Exception
	{
		String l=r.readLine();
		OboTerm term=new OboTerm();
		while(!l.trim().isEmpty())
		{
			if(l.startsWith("id:"))
				term.setId(l.substring(4));
			if(l.startsWith("name:"))
				term.setName(l.substring(6));
			if(l.startsWith("is_a:"))
			{
				String id=(l.substring(6,17));
				relationMap.put(term,id);
			}
			l=r.readLine();
		}
		return term;
	}
	
	public static void main(String[] args) throws Exception 
	{
		OboParser parser=new OboParser();
		Map<String,OboTerm> terms=parser.parse("/home/symons/Dropbox/Projects/Laubfrosch/SBO_OBO.obo");
		for(String s: terms.keySet())
		{
			System.out.println(s+"\t"+terms.get(s).getName()+"\t"+terms.get(s).getIsA());
		}
		
	}
	
	
	
}
