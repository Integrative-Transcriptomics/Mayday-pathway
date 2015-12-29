package mayday.pathway.keggview.kegg.pathway;

public class Substance 
{
	private String name;
	private String altName;
	
	public Substance()
	{
		name="";
		altName="";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAltName() {
		return altName;
	}

	public void setAltName(String altName) {
		this.altName = altName;
	}
	
	public String toString()
	{
		return name;
	}
	
}
