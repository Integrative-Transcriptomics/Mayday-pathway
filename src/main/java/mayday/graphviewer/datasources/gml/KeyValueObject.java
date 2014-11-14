package mayday.graphviewer.datasources.gml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("serial")
public class KeyValueObject extends HashMap<String, String>
{
	private String key;
	private List<KeyValueObject> children;
	
	public KeyValueObject() 
	{
		super();
		children=new ArrayList<KeyValueObject>();
	}
	
	public KeyValueObject(String key) {
		super();
		this.key = key;
		children=new ArrayList<KeyValueObject>();
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public void addChild(KeyValueObject o)
	{
		children.add(o);
	}

	public List<KeyValueObject> getChildren() {
		return children;
	}
	
	
	

	
	
}
