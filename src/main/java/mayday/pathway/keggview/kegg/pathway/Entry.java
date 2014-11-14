package mayday.pathway.keggview.kegg.pathway;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Entry 
{
	private String id;
	private String name;
	private String type;
	private URL link;
	private String reaction;
	private String map;
	private Graphics graphics;
	private List<Integer> components;
	
	public Entry()
	{
		components=new ArrayList<Integer>();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public URL getLink() {
		return link;
	}

	public void setLink(URL link) {
		this.link = link;
	}

	public String getReaction() {
		return reaction;
	}

	public void setReaction(String reactions) {
//		if(reactions==null) return;
//		reaction=reactions.split(" ");
		this.reaction=reactions;
	}

	public String getMap() {
		return map;
	}

	public void setMap(String map) {
		this.map = map;
	}

	public Graphics getGraphics() {
		return graphics;
	}

	public void setGraphics(Graphics graphics) {
		this.graphics = graphics;
	}

	public List<Integer> getComponents() {
		return components;
	}

	public void setComponents(List<Integer> components) {
		this.components = components;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return id+": "+name+" ("+type+")";
		
	}
	
	
	
	
}
