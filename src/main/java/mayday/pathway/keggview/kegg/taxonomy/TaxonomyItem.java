package mayday.pathway.keggview.kegg.taxonomy;

public class TaxonomyItem 
{
	private String id;
	private String shortcut;
	private String shortName;
	private String name;
	
	public TaxonomyItem()
	{
		
	}
	
	public void parse(String s)
	{
		String[] tok=s.split("\t");
		
		id=tok[0].substring(0,6);
		shortcut=tok[1];
		shortName=tok[2];
		name=tok[3];		
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */	
	public String toString()
	{
		return name+" ("+shortcut+")";
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the shortcut
	 */
	public String getShortcut() {
		return shortcut;
	}

	/**
	 * @param shortcut the shortcut to set
	 */
	public void setShortcut(String shortcut) {
		this.shortcut = shortcut;
	}

	/**
	 * @return the shortName
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * @param shortName the shortName to set
	 */
	public void setShortName(String shortName) {
		this.shortName = shortName;
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
	
	
}
