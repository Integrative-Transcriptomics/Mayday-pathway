package mayday.pathway.keggview.kegg.reaction;

import java.util.List;

import mayday.pathway.keggview.kegg.DefinitionKEGGObject;

public class Reaction extends DefinitionKEGGObject
{

	private List<Stoichometry> left;
	private List<Stoichometry> right;
	private List<String> enzymes;
	private List<String> pathways;
	private String comment;
	
	public Reaction() 
	{
		super();
	}
	
	public Reaction(String entry) 
	{
		super();
		setEntry(entry);
	}


	/**
	 * @return the left
	 */
	public List<Stoichometry> getLeft() {
		return left;
	}


	/**
	 * @param left the left to set
	 */
	public void setLeft(List<Stoichometry> left) {
		this.left = left;
	}


	/**
	 * @return the right
	 */
	public List<Stoichometry> getRight() {
		return right;
	}


	/**
	 * @param right the right to set
	 */
	public void setRight(List<Stoichometry> right) {
		this.right = right;
	}


	/**
	 * @return the enzyme
	 */
	public List<String> getEnzymes() {
		return enzymes;
	}


	/**
	 * @param enzyme the enzyme to set
	 */
	public void addEnzyme(String enzyme) {
		this.enzymes.add(enzyme);
	}


	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}


	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the pathways
	 */
	public List<String> getPathways() {
		return pathways;
	}

	/**
	 * @param pathways the pathways to set
	 */
	public void setPathways(List<String> pathways) {
		this.pathways = pathways;
	}

	/**
	 * @param enzymes the enzymes to set
	 */
	public void setEnzymes(List<String> enzymes) {
		this.enzymes = enzymes;
	}


	
	
	
	
}
