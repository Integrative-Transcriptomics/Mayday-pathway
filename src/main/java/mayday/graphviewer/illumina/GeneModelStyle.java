package mayday.graphviewer.illumina;

public enum GeneModelStyle 
{
	VERBOSE("Verbose"), 
	COMPRESSED("Simplified"), 
	CONDENSED("Compressed");
	
	private String rep;

	private GeneModelStyle(String rep) {
		this.rep = rep;
	}
	
	@Override
	public String toString() 
	{
		return rep;
	}
	
	
}
