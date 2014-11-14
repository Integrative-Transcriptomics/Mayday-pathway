package mayday.pathway.keggview.kegg;

import java.util.HashMap;
import java.util.List;

/**
 * Abstract base class for all KEGG objects.
 * @author symons
 *
 */
public abstract class KEGGObject 
{
	private String entry;
	private String name;
	/**
	 * @return the entry
	 */
	public String getEntry() {
		return entry;
	}
	/**
	 * @param entry the entry to set
	 */
	public void setEntry(String entry) {
		this.entry = entry;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Puts all objects in objects into a HashMap: Entry -> Object
	 * @param objects  A list of KEGGObjects
	 * @return 
	 */
	public static HashMap<String, KEGGObject> produceEntryObjectMap(List<KEGGObject> objects)
	{
		HashMap<String, KEGGObject> res=new HashMap<String, KEGGObject>(objects.size());
		for(KEGGObject o:objects)
		{
			res.put(o.getEntry(), o);
		}
		return res;
	}
}
