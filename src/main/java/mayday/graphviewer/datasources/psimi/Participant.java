package mayday.graphviewer.datasources.psimi;

import java.util.ArrayList;
import java.util.List;

public class Participant 
{
	Names names;
	PsiXref xrefs;
	List<Interactor> interactors=new ArrayList<Interactor>();	
	String biologicalRole;
	List<String> experimentalRole=new ArrayList<String>();
	
}
