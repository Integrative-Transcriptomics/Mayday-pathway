package mayday.graphviewer.datasources.psimi;

public class InteractionType 
{
	Names names;
	PsiXref xref;
	
	@Override
	public String toString() 
	{
		return names.toString()+"---"+xref.toString();
	}
}
