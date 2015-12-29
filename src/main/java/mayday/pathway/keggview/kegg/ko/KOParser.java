package mayday.pathway.keggview.kegg.ko;

import java.util.ArrayList;
import java.util.List;

import mayday.pathway.keggview.kegg.GenericKEGGDataItem;
import mayday.pathway.keggview.kegg.KEGGObject;
import mayday.pathway.keggview.kegg.ParsingStrategy;

public class KOParser implements ParsingStrategy
{
	public String taxon;
	
	public KOParser(String taxon)
	{
		this.taxon=taxon;
	}

	public KEGGObject processItem(GenericKEGGDataItem item) 
	{
		KOEntry entry=new KOEntry();
		entry.setEntry(item.get("ENTRY"));
		
		entry.setName(item.get("NAME"));
		entry.setDefinition(item.getSingleLine("DEFINITION"));
		if(entry.getDefinition()!=null)
		{
			
//			System.out.println(entry.getDefinition());
			String[] tok=entry.getDefinition().split("[\\[\\]]");
			if(tok.length >1) 
			{
				if(tok[tok.length-1].matches("EC:.*"))
				{
					entry.setEcNumber(tok[tok.length-1]);					
					entry.setDefinition(entry.getDefinition().substring(0,entry.getDefinition().lastIndexOf("[")));
				}
			}
			
			
//			if(tok.length==2)
//			{
//				System.out.println(entry);
//				System.out.println(Arrays.toString(tok));
//				System.out.println("-----------------");
//			}
//			
//			entry.setDefinition(tok[0]);
//			if(tok.length >1) entry.setEcNumber(tok[1]);
			
		}
		
		List<String> pathways=new ArrayList<String>();
		for(String s:item.get("CLASS").split("[\\[\\]]"))
		{
			pathways.add(s.substring(7));
		}
		
		String[] genes=item.getLineArray("GENES");
		if(genes==null) return null;
		StringBuffer sb=new StringBuffer();
		boolean f=false;
		for(String s:genes)
		{
			if(f)
			{
				if(s.matches("^\\w{3}:\\s.*"))
				{
					f=false;
				}else
				{
					sb.append(s);						
				}
			}
			if(s.startsWith(taxon))
			{					
				sb.append(s);
				f=true;
			}
		}
		if(sb.length()!=0)
		{
			String[] tok=sb.toString().split("[() ]+");
			entry.setGenes(tok);			
		}else
		{
			entry=null;
		}		
		return entry;
	}
	
}
