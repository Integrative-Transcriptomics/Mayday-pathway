package mayday.pathway.keggview.kegg.pathway;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Pathway 
{

	private String name;
	private String number;
	private String org;
	private String title;
	private URL image;
	private URL link;
	
	private List<Entry> entries;
	private List<Relation> relations;
	private List<ReactionEntry> reactions;
	
	
	public Pathway()
	{
		entries=new ArrayList<Entry>();
		relations=new ArrayList<Relation>();
		reactions=new ArrayList<ReactionEntry>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getOrg() {
		return org;
	}

	public void setOrg(String org) {
		this.org = org;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		if(title==null){
			title=""; return;
		}
		this.title = title;
	}

	public URL getImage() {
		return image;
	}

	public void setImage(URL image) {
		this.image = image;
	}

	public URL getLink() {
		return link;
	}

	public void setLink(URL link) {
		this.link = link;
	}

	public List<Entry> getEntries() {
		return entries;
	}

	public void setEntries(List<Entry> entries) {
		this.entries = entries;
	}
	
	public void addEntry(Entry e)
	{
		entries.add(e);
	}

	public List<Relation> getRelations() {
		return relations;
	}

	public void setRelations(List<Relation> relations) {
		this.relations = relations;
	}

	public List<ReactionEntry> getReactions() {
		return reactions;
	}
	
	public void addRelation(Relation e)
	{
		relations.add(e);
	}

	public void setReactions(List<ReactionEntry> reactions) {
		this.reactions = reactions;
	}
	
	public void addReaction(ReactionEntry e)
	{
		reactions.add(e);
	}
	
	
}
