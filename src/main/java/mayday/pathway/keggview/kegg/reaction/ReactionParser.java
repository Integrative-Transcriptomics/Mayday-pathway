package mayday.pathway.keggview.kegg.reaction;

import java.util.ArrayList;
import java.util.List;

import mayday.pathway.keggview.kegg.GenericKEGGDataItem;
import mayday.pathway.keggview.kegg.KEGGObject;
import mayday.pathway.keggview.kegg.KEGGParser;
import mayday.pathway.keggview.kegg.ParsingStrategy;

public class ReactionParser implements ParsingStrategy
{
	
	
	public KEGGObject processItem(GenericKEGGDataItem item) 
	{
		Reaction rea=new Reaction();
		rea.setEntry(item.get("ENTRY"));
		rea.setName(item.getSingleLine("NAME"));
		rea.setDefinition(item.getSingleLine("DEFINITION"));
		rea.setComment(item.getSingleLine("COMMENT"));
		
		String enz=item.getSingleLine("ENZYME");		
		if(enz!=null)
		{
			ArrayList<String> enzymes=new ArrayList<String>();
			for(String s: enz.split("\\s+"))
				enzymes.add(s);		
			rea.setEnzymes(enzymes);
		}
		
		if(item.get("PATHWAY")!=null)
		{
			List<String> paths=new ArrayList<String>();
			for(String s: item.getLineArray("PATHWAY"))
			{
				if(s.length() < 13) continue;
				paths.add(s.substring(8, 13));
			}
		}
		
		rea.setRight(getRight(item.getSingleLine("EQUATION")));
		rea.setLeft(getLeft(item.getSingleLine("EQUATION")));

		return rea;
	}
	
	private List<Stoichometry> splitDefinition(String def)
	{
		List<Stoichometry> res=new ArrayList<Stoichometry>();
		
		String[] comps=def.split("\\s\\+\\s");
		for(String c:comps)
		{
			Stoichometry sto=new Stoichometry();
			String[] tok= c.trim().split("\\s",2);
			if(tok.length==1)
			{
				sto.setCoefficient(1);
				sto.setSubstance(tok[0]);
				res.add(sto);
			}
			if(tok.length==2)
			{
				if(tok[0].matches("\\d*"))
				{
					sto.setCoefficient(Integer.parseInt(tok[0]));
				}else
				{
					sto.setVarCoefficient(tok[0]);
				}
				sto.setSubstance(tok[1]);
				res.add(sto);				
			}
			if(tok.length > 2)
			{
				throw new RuntimeException("String can not be parsed. too bad!");
			}			
		}
		return res;
	}
	
	private List<Stoichometry> getRight(String def)
	{
		String right= def.substring(def.indexOf("<=>")+4).trim();
		return splitDefinition(right);
	}
	
	private List<Stoichometry> getLeft(String def)
	{
		String left=  def.substring(0,def.indexOf("<=>")).trim();
		return splitDefinition(left);
	}
	
	
	

	
	public static void main(String[] args) throws Exception
	{
		KEGGParser parser=new KEGGParser(new ReactionParser());
		System.out.println(parser.parseData("/home/symons/data/kegg/reaction").size());
	}


}
