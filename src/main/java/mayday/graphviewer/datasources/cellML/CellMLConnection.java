package mayday.graphviewer.datasources.cellML;

import java.util.ArrayList;
import java.util.List;

public class CellMLConnection 
{
	String component1;
	String component2;

	List<CellMLMapVariables> mapVariables;
	
	public CellMLConnection() 
	{
		mapVariables=new ArrayList<CellMLMapVariables>();
	}
	
	@Override
	public String toString() 
	{
		return component1+"--"+component2;
	}
	
	public static class CellMLMapVariables
	{
		String variable1;
		String variable2;
	}
	
	
}
