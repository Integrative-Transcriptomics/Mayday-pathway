package mayday.pathway.keggview.kegg.ko;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeSet;

import mayday.pathway.keggview.kegg.KEGGObject;
import mayday.pathway.keggview.kegg.KEGGParser;

public class KOExtractor {
	public static void main(String[] args) throws IOException 
	{
		String taxon="SCO";
		String file="/home/symons/data/sco2/ko"; 
		KEGGParser koParser=new KEGGParser(new KOParser(taxon.toUpperCase()));
		Map<String,KEGGObject> genes=koParser.parseData(file);
		
		TreeSet<String> res=new TreeSet<String>();
		for(String s: genes.keySet())
		{
			KOEntry e=(KOEntry)genes.get(s);
			for(String g:e.getGenes())
			{
				if(g.matches("SCO\\d+") )
				{
					if(e.getDefinition()!=null )
					res.add(g+"\t"+e.getDefinition()+"\t"+ (e.getEcNumber()==null?"":e.getEcNumber()));
				}
			}		
		}
		BufferedWriter w=new BufferedWriter(new FileWriter(new File("/home/symons/sco_an.txt")));
		w.write("sconumber\tdefinition\tec\n");
		for(String s:res)
		{
			w.write(s+"\n");
		}
		w.close();
		
		
	}
}
