package mayday.pathway.biopax.parser;

import java.util.Comparator;

public class MasterObjectNameComparator implements Comparator<MasterObject> 
{
	public int compare(MasterObject o1, MasterObject o2) 
	{
		String name1="";
		String name2="";
		
		if(o1.hasValue("NAME"))
			name1=o1.getFirstValue("NAME");
		if(o2.hasValue("NAME"))
			name2=o2.getFirstValue("NAME");
		return name1.compareToIgnoreCase(name2);
	}
}
