package mayday.pathway.keggview.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

import mayday.core.structures.maps.MultiTreeMap;
import mayday.pathway.keggview.kegg.pathway.Entry;
import mayday.pathway.keggview.kegg.pathway.Pathway;
import mayday.pathway.keggview.kegg.pathway.ReactionEntry;
import mayday.pathway.keggview.kegg.pathway.Substance;
import mayday.pathway.keggview.pathways.PathwayManager;

public class CompoundToMultiProbeList 
{
	public static void main(String[] args) throws Exception
	{

		// read in file:
		MultiTreeMap<String, String> nameCompoundMap=readInCompounds();
		MultiTreeMap<String, String> resMap=new MultiTreeMap<String, String>();


		PathwayManager manager=new PathwayManager("/home/symons/sco/", "sco");
		manager.readDirectory();

		for(int i=0; i!= manager.numPathways(); ++i)
		{

			if(manager.isAvailable(i)==false) continue;
			Pathway pathway=manager.loadPathway(i);

			MultiTreeMap<String, String> rnGeneMap=new MultiTreeMap<String, String>();
			for(Entry e: pathway.getEntries())
			{			
				if(e.getType().equals("gene") && e.getReaction()!=null)
				{

//					for(String r:e.getReaction())
//					{
//						String[] tok=e.getName().split("\\s");
//						for(String t:tok)
//						{
//							rnGeneMap.put(r, t.substring(4));	
//						}
//					}
					String[] tok=e.getName().split("\\s");
					for(String t:tok)
					{
						rnGeneMap.put(e.getReaction(), t.substring(4));	
					}
				}
			}




			MultiTreeMap<String, String> rnSubstanceMap=new MultiTreeMap<String, String>();


			for(ReactionEntry r:pathway.getReactions())
			{
				for(Substance subst:r.getProducts())
				{
					rnSubstanceMap.put(subst.getName(),r.getName());
				}
				for(Substance subst:r.getSubstrates())
				{
					rnSubstanceMap.put(subst.getName(),r.getName());
				}
			}

			for(String name:nameCompoundMap.keySet())
			{
				List<String> lst=nameCompoundMap.get(name);
				for(String comp:lst)
				{
					if(rnSubstanceMap.get("cpd:"+comp)==null) continue;
					for(String rn:rnSubstanceMap.get("cpd:"+comp))
					{
						for(String gene: rnGeneMap.get(rn))
						{
							resMap.put(name, gene);
						}

					}
				}
			}

		}
		System.out.println(resMap);
		BufferedWriter w=new BufferedWriter(new FileWriter("/home/symons/compoundProbeLists.txt"));
		for(String name:resMap.keySet())
		{
			w.write(name);
			for(String sco:resMap.get(name))
			{
				w.write("\t"+sco);
			}
			w.write("\n");
		}
		w.close();
	}

	private static MultiTreeMap<String,String> readInCompounds() throws Exception
	{
		MultiTreeMap<String,String> res=new MultiTreeMap<String,String>();
		BufferedReader r=new BufferedReader(new FileReader("/home/symons/sco/measuredCompounds2.txt"));
		String line=r.readLine();
		while(line!=null)
		{
			String[] tok=line.split("\\s");
			for(int i=1; i!= tok.length; ++i)
			{
				res.put(tok[0], tok[i]);
			}			
			line=r.readLine();
		}
		return res;
	}
}
