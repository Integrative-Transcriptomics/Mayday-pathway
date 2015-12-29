package mayday.pathway.keggview.kegg.pathway;

import java.util.HashMap;
import java.util.Map;

public class Relation {
	private String compound;
	private String entry1;
	private String entry2;
	private String type;
	
	private Map<String, String> subtypes;
	
	public Relation()
	{
		subtypes=new HashMap<String, String>();
	}

	public String getCompound() {
		return compound;
	}

	public void setCompound(String compound) {
		this.compound = compound;
	}

	public String getEntry1() {
		return entry1;
	}

	public void setEntry1(String entry1) {
		this.entry1 = entry1;
	}

	public String getEntry2() {
		return entry2;
	}

	public void setEntry2(String entry2) {
		this.entry2 = entry2;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Map<String, String> getSubtypes() {
		return subtypes;
	}

	public void setSubtypes(Map<String, String> subtypes) {
		this.subtypes = subtypes;
	}
	
	public void addSubtype(String k, String v) {
		this.subtypes.put(k, v);
	}
	
	
}
