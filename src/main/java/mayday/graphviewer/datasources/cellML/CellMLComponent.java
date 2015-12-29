package mayday.graphviewer.datasources.cellML;

import java.util.HashMap;
import java.util.Map;

public class CellMLComponent 
{

	String name;
	Map<String, CellMLVariable> variables;
	
	public CellMLComponent() 
	{
		variables=new HashMap<String, CellMLVariable>();
	}
	
	public void addVariable(CellMLVariable var)
	{
		variables.put(var.name,var);
	}
	
	public CellMLVariable getVariable(String s)
	{
		return variables.get(s);
	}
	
	
}
