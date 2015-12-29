package mayday.graphviewer.datasources.sbml;

class SBase
{
	String metaId;
	String sboTerm;
	String id;
	String name;
	
	@Override
	public String toString() 
	{
		return id+"("+(name!=null?name:"") +")";
	}
}
