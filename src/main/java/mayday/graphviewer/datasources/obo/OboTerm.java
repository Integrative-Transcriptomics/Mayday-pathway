package mayday.graphviewer.datasources.obo;
import java.util.ArrayList;
import java.util.List;


public class OboTerm 
{
	private String id;
	private String name; 
	private List<OboTerm> isA=new ArrayList<OboTerm>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<OboTerm> getIsA() {
		return isA;
	}
	public void setIsA(List<OboTerm> isA) {
		this.isA = isA;
	}
	
	public void addIsA(OboTerm term)
	{
		isA.add(term);
	}
	
	public static boolean isA(OboTerm queryTerm, OboTerm targetTerm)
	{
		if(queryTerm.id.equals(targetTerm.id))
			return true;
		for(OboTerm t:queryTerm.isA)
		{
			boolean b=isA(t, targetTerm);
			if(b)
				return true;
		}
		return false;
	}
	
	@Override
	public String toString() 
	{
		return id+" ("+name+")";
	}
	
	
	
}
