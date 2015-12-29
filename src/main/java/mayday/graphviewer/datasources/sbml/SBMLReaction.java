package mayday.graphviewer.datasources.sbml;

import java.util.ArrayList;
import java.util.List;

class SBMLReaction extends SBase
{
	boolean reversible=true;
	boolean fast=false;

	List<SBMLSpeciesRef> listOfReactants=new ArrayList<SBMLSpeciesRef>();
	List<SBMLSpeciesRef> listOfProducts=new ArrayList<SBMLSpeciesRef>();
	List<SBMLSpeciesRef> listOfModifiers=new ArrayList<SBMLSpeciesRef>();
	
	

}
