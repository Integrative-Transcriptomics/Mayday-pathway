package mayday.pathway.keggview.kegg.compounds;

import java.util.ArrayList;
import java.util.List;

import mayday.pathway.keggview.kegg.GenericKEGGDataItem;
import mayday.pathway.keggview.kegg.KEGGObject;
import mayday.pathway.keggview.kegg.ParsingStrategy;

/**
 * Strategy for parsing KEGG compound files. 
 * @author Stephan Symons
 *
 */
public class CompoundsParser implements ParsingStrategy
{
	
	public KEGGObject processItem(GenericKEGGDataItem item) 
	{
		Compound c=new Compound();
		//1.  entry:
		c.setEntry(item.get("ENTRY"));
		for(String s:item.get("NAME").split(";\\s"))
		{
			c.addName(s);
		}
		c.setFormula(item.get("FORMULA"));
		if(item.get("MASS")!=null)
			c.setMass(Double.parseDouble(item.get("MASS")));
		//			System.out.println(Arrays.toString(item.getLineArray("PATHWAY")));
		String[] pathways=item.getLineArray("PATHWAY");
		List<String> pathwaysList=new ArrayList<String>();
		
		if(pathways!=null)
		{
			for(String s:pathways)
			{
				try{
//					System.out.println(s.split("\\s")[1].substring(2));
					pathwaysList.add(s.split("\\s")[1].substring(2));
				}catch(RuntimeException e)
				{
					// stfu
				}
			}
		}
		return c;
	}
}
