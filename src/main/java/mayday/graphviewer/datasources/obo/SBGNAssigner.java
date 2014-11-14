package mayday.graphviewer.datasources.obo;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;


public class SBGNAssigner {

	Map<String, String>	sbgnMapping;
	Map<String, OboTerm> terms;
	
	
	public SBGNAssigner(Map<String, String> sbgnMapping, Map<String, OboTerm> terms) 
	{
		super();
		this.sbgnMapping = sbgnMapping;
		this.terms = terms;
	}
	
	public String getRoleForTerm(String term)
	{
		if(sbgnMapping.containsKey(term))
			return sbgnMapping.get(term);
		return getRoleForTerm(terms.get(term));
	}

	public String getRoleForTerm(OboTerm oboTerm) 
	{
		if(sbgnMapping.containsKey(oboTerm.getId()))
				return sbgnMapping.get(oboTerm.getId());
		// no term found so far: we will now look at the is_a relationships of the term:
		for(OboTerm t: oboTerm.getIsA())
		{
			String role=getRoleForTerm(t);
			if(role!=null)
				return role;
		}
		return null;
	}
	
	public static Map<String,String> parseMappingFile(String file) throws Exception
	{
		BufferedReader r=new BufferedReader(new FileReader(file));
		Map<String, String> res=new HashMap<String, String>();		
		String l=r.readLine(); // skip header		
		while(r.ready())
		{
			l=r.readLine();
			res.put(l.substring(0, 11), l.substring(12));
		}		
		return res;		
	}
	
	public static Map<String,String> parseMappingFile(BufferedReader r) throws Exception
	{
		Map<String, String> res=new HashMap<String, String>();		
		String l=r.readLine(); // skip header		
		while(r.ready())
		{
			l=r.readLine();
			res.put(l.substring(0, 11), l.substring(12));			
		}		
		return res;		
	}

	
	
	
	
}
