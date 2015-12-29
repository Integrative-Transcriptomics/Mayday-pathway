package mayday.pathway.biopax.core;

import mayday.pathway.biopax.parser.MasterObject;

public class Reference 
{
	private String database;
	private String id;
	private String type;
	
	private static final String defaultType="reference";
	
	public Reference(MasterObject object)
	{
//		System.out.println(object.getObjectType());
		if(object.hasValue("DB"))
			database=object.getFirstValue("DB");
		if(object.hasValue("ID"))
			id=object.getFirstValue("ID");

		
		type= defaultType;
		if(object.hasValue("RELATIONSHIP-TYPE"))
			type=object.getValues().get("RELATIONSHIP-TYPE").get(0);		
	}

	/**
	 * @return the database
	 */
	public String getDatabase() {
		return database;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	public String toString()
	{
		return database+":"+id;
	}
}
