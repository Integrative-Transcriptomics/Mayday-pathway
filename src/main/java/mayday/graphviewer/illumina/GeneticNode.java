package mayday.graphviewer.illumina;

import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.coordinatemodel.GBAtom;

public class GeneticNode extends MultiProbeNode
{
	private GBAtom atom;
	private Chromosome chromosome; 
	private double[] exonValues;
	
	public static final String FROM="from";
	public static final String TO="to";
	public static final String STRAND="strand";
	public static final String MODELS="models";
	public static final String TOTAL_MODELS="total models";
	public static final String NUMBER_MODELS="number models";
	public static final String MODEL_NAME="model";
	

	
	public GeneticNode(Graph graph, GBAtom atom) 
	{
		super(graph);
		this.atom=atom;
		setName(atom.from+"-"+atom.to);
		setRole(GeneModelRoles.EXON_ROLE);
		setProperty(FROM, Long.toString(atom.from));
		setProperty(TO, Long.toString(atom.to));
		setProperty(STRAND, atom.strand.toString());		
	}
	
	public void addModel(String model)
	{
		String models="";
		if(hasProperty(MODELS))
		{
			models=getPropertyValue(MODELS);
			models=models+",";
		}
		models=models+model;
		setProperty(MODELS, models);
	}
	
	
	
	public GeneticNode(Graph graph) 
	{
		super(graph);
	}


	public GBAtom getAtom() {
		return atom;
	}


	public void setAtom(GBAtom atom) {
		this.atom = atom;
	}
	
	public double[] getExonValues() {
		return exonValues;
	}
	
	public void setExonValues(double[] exonValues) {
		this.exonValues = exonValues;
	}
	
	public void setChromosome(Chromosome chromosome) {
		this.chromosome = chromosome;
	}
	
	public Chromosome getChromosome() {
		return chromosome;
	}

}
