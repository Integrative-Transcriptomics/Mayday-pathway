package mayday.pathway.keggview.kegg.pathway;

import java.util.ArrayList;
import java.util.List;

public class ReactionEntry 
{
	private String name;
	private String type;
	
	private List<Substance> substrates;
	private List<Substance> products;
	
	public ReactionEntry()
	{
		substrates=new ArrayList<Substance>();
		products=new ArrayList<Substance>();
	}
	
	public boolean isReversible()
	{
		if(type.equals("reversible")) return true;
		return false;
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

	public List<Substance> getSubstrates() {
		return substrates;
	}

	public void setSubstrates(List<Substance> substrates) {
		this.substrates = substrates;
	}

	public List<Substance> getProducts() {
		return products;
	}

	public void setProducts(List<Substance> products) {
		this.products = products;
	}
	
	public void addSubstrate(Substance s)
	{
		substrates.add(s);
	}
	
	public void addProduct(Substance p)
	{
		products.add(p);
	}
	
	public String toString()
	{
		return name+": "+type+":" + substrates.size()+"-->"+products.size();
	}
}
