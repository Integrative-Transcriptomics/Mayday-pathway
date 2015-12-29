package mayday.graphviewer.datasources.cellML;

class CellMLVariable 
{
	String name;
	String units;
	String initial_value; 
	String publicInterface; 
	String privateInterface;

	@Override
	public String toString() 
	{
		return name+(initial_value!=null?initial_value:"")+units;
	}
	
	public String value() 
	{
		return (initial_value!=null?initial_value:"")+" "+units;
	}
}
