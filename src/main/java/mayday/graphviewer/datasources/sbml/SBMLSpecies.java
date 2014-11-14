package mayday.graphviewer.datasources.sbml;

public class SBMLSpecies extends SBase
{
	double initialAmount;
	double initialConcentration;
	SBMLCompartment compartment;
	boolean boundaryCondition=false;
	int charge;
	boolean constant=false;
}
