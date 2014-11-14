package mayday.graphviewer.datasources.psimi;

import java.util.ArrayList;
import java.util.List;

public class Names 
{
	String name;
	String shortName;
	List<String> alias=new ArrayList<String>();
	
	@Override
	public String toString() 
	{
		return name + "("+shortName+")";
	}
}
