package mayday.graphviewer.datasources.psimi;

import java.util.Map;

public class Element 
{
	String id;
	PsiXref xref;
	Names names;
	
	Map<String,String> attributes;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public PsiXref getXref() {
		return xref;
	}

	public void setXref(PsiXref xref) {
		this.xref = xref;
	}

	public Names getNames() {
		return names;
	}

	public void setNames(Names names) {
		this.names = names;
	}
	
	
	
}
