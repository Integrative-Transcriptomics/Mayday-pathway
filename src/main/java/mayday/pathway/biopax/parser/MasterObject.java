package mayday.pathway.biopax.parser;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import mayday.core.structures.maps.MultiTreeMap;

public class MasterObject
{
	private String comment;
	private String id; 
	
	private boolean resolved;
	
	
	private String objectType;
	
	private MultiTreeMap<String, MasterObject> members;
	private MultiTreeMap<String, String> values;
	
	public MasterObject() 
	{
		members=new MultiTreeMap<String, MasterObject>();
		values=new MultiTreeMap<String, String>();
	}
	
	public MasterObject(String id) 
	{
		this.id=id;
		resolved=false;
		members=new MultiTreeMap<String, MasterObject>();
		values=new MultiTreeMap<String, String>();
	}
	

	public String getObjectType() 
	{
		return objectType;
	}
	
	
	public void setObjectType(String o) 
	{
		objectType=o;
	}


	public boolean resolve(Map<String, MasterObject> objects) 
	{
		if(isResolved()) return true;
//		System.out.println("Object: "+getObjectType());
		MultiTreeMap<String, MasterObject> newMembers=new MultiTreeMap<String, MasterObject>();
		// iterate over tree and resolve:
		for(String key:members.keySet())
		{
//			System.out.println("Key:"+key);
			List<MasterObject> memb= members.get(key);
			for(int i=0; i!= memb.size(); ++i)
			{
				MasterObject bp=null;
				if(!memb.get(i).isResolved())	
				{
					bp=objects.get(memb.get(i).getId());
					
					if(bp==null)
					{
//						System.out.println("Problem!"+memb.get(i).getId());						
					}
				}else
				{
					bp=memb.get(i);
				}
//				System.out.println("\t"+bp.getId()+" "+bp.getObjectType()+" "+bp.isResolved());
				
//				bp.resolve(objects);
				newMembers.put(key,bp);
			}			
		}		
		members=newMembers;
		return true;
	}


	public boolean setMember(String name, MasterObject object)
	{
		members.put(name, object);
		return true;
	}


	public boolean setUnresolvedMember(String name, String id) 
	{
		members.put(name, new MasterObject(id));
		return true;
	}

	/* (non-Javadoc)
	 * @see mayday.biopax.BioPaxObject#setProperty(java.lang.String, java.lang.String)
	 */

	public boolean setProperty(String name, String value) 
	{
		values.put(name, value);
		return true;
	}

	/**
	 * @return the members
	 */
	public MultiTreeMap<String, MasterObject> getMembers() {
		return members;
	}

	/**
	 * @return the values
	 */
	public MultiTreeMap<String, String> getValues() {
		return values;
	}
	
	public Collection<MasterObject> members()
	{
		return members.everything();
	}
	
	public Collection<String> values()
	{
		return values.everything();
	}
	
	public String toString()
	{
		if(values.get("NAME")!=null)
		{
			if(!values.get("NAME").isEmpty())
				return values.get("NAME").get(0);
		}
		return getObjectType()+" "+getId();
	}
	
	public boolean hasValue(String v)
	{
		return values.containsKey(v);
	}
	
	public List<String> getValue(String v)
	{
		return values.get(v);
	}
	
	public String getFirstValue(String v)
	{
		return values.get(v).get(0);
	}
	
	public boolean hasMember(String v)
	{
		return members.containsKey(v);
	}
	
	public List<MasterObject> getMembers(String v)
	{
		return members.get(v);
	}
	
	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
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
	 * @return the resolved
	 */
	public boolean isResolved() {
		return resolved;
	}

	/**
	 * @param resolved the resolved to set
	 */
	public void setResolved(boolean resolved) {
		this.resolved = resolved;
	}
}
