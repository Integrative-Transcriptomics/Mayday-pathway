package mayday.graphviewer.datasources.biopax2;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;

@SuppressWarnings("serial")
public class PathwayListModel extends DefaultListModel
{
		
	public PathwayListModel(List<String> pathwayIDs, BioPaxSqueezer2 squeezer)  
	{
		super();
		List<PathwayItem> items=new ArrayList<PathwayItem>();
		try {
			for(String id: pathwayIDs)
			{
				PathwayItem item=new PathwayItem();
				item.id=id;
				item.name=squeezer.getObject(id,BioPaxSqueezer2.NAME).get(0);
				List<String> t=squeezer.getObject(id, BioPaxSqueezer2.SHORT_NAME);
				if(!t.isEmpty())
				{
					item.shortName=t.get(0);
				}
				
				t=squeezer.getObject(id, BioPaxSqueezer2.COMMENT);
				if(!t.isEmpty())
				{
					item.comment=t.get(0);
				}
				for(String syn: squeezer.getObject(id, BioPaxSqueezer2.SYNONYMS))
				{
					item.synonyms.add(syn);
				}
				for(String rea: squeezer.getReactionsForPathway(id))
				{
					item.reactions.add(squeezer.getObject(rea, BioPaxSqueezer2.NAME).get(0));
				}
				Collections.sort(item.reactions);
				items.add(item);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}			
		Collections.sort(items);
		for(PathwayItem item:items)
			addElement(item);
	}
	
	static class PathwayItem implements Comparable<PathwayItem>
	{
		String id;
		String name;
		String shortName;
		String comment;
		List<String> synonyms=new ArrayList<String>();
		List<String> reactions=new ArrayList<String>();	
		
		@Override
		public String toString() 
		{
			return name;
		}
		
		@Override
		public int compareTo(PathwayItem o) 
		{
			return toString().compareTo(o.toString());
		}
		
		@Override
		public boolean equals(Object obj) 
		{
			return toString().equals(toString());
		}
	}


	
}
