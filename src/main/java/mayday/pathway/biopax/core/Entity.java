package mayday.pathway.biopax.core;

import java.util.ArrayList;
import java.util.List;

import mayday.pathway.biopax.parser.MasterObject;

public abstract class Entity implements Comparable<Entity>
{
	private String id;
	private List<String> synonyms;
	private String comment;
	private String shortName;
	private String name;
	private List<Reference> references;
	
	public Entity(MasterObject object) 
	{
		init(object);
		id=object.getId();
		synonyms=object.getValues().get("SYNONYMS");
		if(object.hasValue("NAME"))
		name=object.getFirstValue("NAME");
		if(object.hasValue("COMMENT"))
			comment=object.getFirstValue("COMMENT");
		if(object.hasValue("SHORT-NAME"))
			shortName=object.getFirstValue("SHORT-NAME");
		references=extractReference(object);
		init(object);
	}
	
	private List<Reference> extractReference(MasterObject object)
	{
		List<Reference> res=new ArrayList<Reference>();
		if(object.hasMember("XREF"))
		{			
			for(MasterObject x:object.getMembers("XREF"))
			{
				res.add(new Reference(x));
			}
		}
		return res;
	}
	
	abstract public void init(MasterObject object);

	
	/**
	 * @return the synonyms
	 */
	public List<String> getSynonyms() {
		return synonyms;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @return the shortName
	 */
	public String getShortName() {
		return shortName;
	}

	/**
	 * @return the references
	 */
	public List<Reference> getReferences() {
		return references;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	public int compareTo(Entity o)
	{
		return name.compareTo(o.name);
	}
	
	@Override
	public String toString() 
	{
		return name;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	
}
