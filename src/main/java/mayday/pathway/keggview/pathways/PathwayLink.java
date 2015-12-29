package mayday.pathway.keggview.pathways;

public class PathwayLink implements Comparable<PathwayLink>
{
	private String number;
	private String name;
	private boolean available;
	
	public PathwayLink() 
	{		
	}

	/**
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the available
	 */
	public boolean isAvailable() {
		return available;
	}

	/**
	 * @param available the available to set
	 */
	public void setAvailable(boolean available) {
		this.available = available;
	}
	
	public String toString()
	{
		return name+" ("+number+")";
	}

	public int compareTo(PathwayLink o) 
	{
		return number.compareTo(o.number);
	}
	
	
}
