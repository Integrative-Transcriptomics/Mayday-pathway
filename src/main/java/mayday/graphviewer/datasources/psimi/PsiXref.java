package mayday.graphviewer.datasources.psimi;

import java.util.HashMap;
import java.util.Map;

import mayday.core.structures.maps.MultiHashMap;

public class PsiXref 
{
	private Map<String, String> refs;
	private MultiHashMap<String, String> secondary;

	public PsiXref() 
	{
		refs=new HashMap<String, String>();
		secondary=new MultiHashMap<String, String>();
	}

	public void addRef(String db, String id)
	{
		refs.put(db, id);
	}

	public void addSecondary(String db, String sec)
	{
		secondary.put(db, sec);
	}

	@Override
	public String toString() 
	{
		return refs.toString();
	}

	public Map<String, String> getRefs() {
		return refs;
	}


	public MultiHashMap<String, String> getSecondary() {
		return secondary;
	}



	
	


}
