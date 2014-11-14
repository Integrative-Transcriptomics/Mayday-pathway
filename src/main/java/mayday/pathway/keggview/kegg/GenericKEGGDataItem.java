package mayday.pathway.keggview.kegg;

import java.util.HashMap;
import java.util.Map;

public class GenericKEGGDataItem 
{
	private Map<String, String> fields;
	private String type; 
	
	public GenericKEGGDataItem()
	{
		fields=new HashMap<String, String>();		
	}

	/**
	 * @return the fields
	 */
	public Map<String, String> getFields() {
		return fields;
	}

	/**
	 * @param fields the fields to set
	 */
	public void setFields(Map<String, String> fields) {
		this.fields = fields;
	}
	
	public void addField(String key, String value)
	{
		fields.put(key, value);
	}
	
	public String get(String key)
	{
		return fields.get(key);
	}
	
	public String getSingleLine(String key)
	{
		if(!fields.containsKey(key)) return null;
		return fields.get(key).replaceAll("\\n", " ");
	}
	
	public String[] getLineArray(String key)
	{
		if(!fields.containsKey(key)) return null;
		return fields.get(key).split("\\n");
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	
	
}
