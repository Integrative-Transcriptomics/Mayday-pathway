package mayday.graphviewer.datasources.string;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class StringConnector 
{
	private String stringDBBaseUrl="http://string-db.org/api/";

	public static final String FORMAT_ONLY_IDS="only-ids";
	public static final String FORMAT_FULL="full";

	public static final String FILE_FORMAT_TSV_NO_HEADER="tsv-no-header";
	public static final String FILE_FORMAT_PSI_MI_TAB="psi-mi-tab";


	public static final String IDENTIFIER="identifier";
	public static final String IDENTIFIERS="identifiers";



	public static final String RESOLVE="resolve";
	public static final String RESOLVE_LIST="resolveList";	
	public static final String INTERACTORS="interactors";
	public static final String INTERACTORS_LIST="interactorsList";
	public static final String ACTIONS="actions";	
	public static final String ACTIONS_LIST="actionsList";
	public static final String INTERACTIONS="interactions";
	public static final String INTERACTIONS_LIST="interactionsList";

	public static final String DATABASE_STRING="string-db.org/";
	public static final String DATABASE_STITCH="stitch.embl.de/";

	public StringConnector(String db) 
	{
		stringDBBaseUrl="http://"+db+"api/";
	}


	public List<String> getStringIDs(List<String> ids) throws Exception
	{
		String query=buildQueryForIDs(FILE_FORMAT_TSV_NO_HEADER,RESOLVE_LIST, IDENTIFIERS, ids, 0, FORMAT_ONLY_IDS,0);

		URL url=new URL(query);

		URLConnection con = url.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;

		List<String> res=new ArrayList<String>();
		while ((inputLine = in.readLine()) != null) 
		{
			res.add(inputLine.trim());            
		}
		in.close();

		return res;		
	}
	
	public List<String> getAbstractIds(List<String> ids, int limit) throws Exception
	{
//		http://string-db.org/api/tsv-no-header/abstractsList?identifiers=4932.YML115C%0D4932.YJR075W%0D4932.YEL036C
		String query=buildQueryForIDs(FILE_FORMAT_TSV_NO_HEADER,"abstractsList", IDENTIFIERS, ids,limit,null,-1);
		
		URL url=new URL(query);

		URLConnection con = url.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;

		List<String> res=new ArrayList<String>();
		while ((inputLine = in.readLine()) != null) 
		{
			res.add(inputLine.trim());            
		}
		in.close();

		return res;	
	}

	public List<StringInteraction> getInteractions(List<String> ids, int limit, int score) throws Exception
	{
		String query=buildQueryForIDs(FILE_FORMAT_PSI_MI_TAB,INTERACTIONS_LIST, IDENTIFIERS, ids,limit,null,score);

		URL url=new URL(query);

		URLConnection con = url.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;

		List<StringInteraction> res=new ArrayList<StringInteraction>();
		while ((inputLine = in.readLine()) != null) 
		{
			if(inputLine.trim().isEmpty())
				continue;
			String[] tok=inputLine.split("\t");
			StringInteraction i=new StringInteraction();
			i.left=tok[0];
			i.right=tok[1];
			i.score=Double.parseDouble(tok[14].substring(6,tok[14].indexOf("|")));
			res.add(i);			      
		}
		in.close();

		return res;		
	}

	public List<String> getInteractors(String ids, int limit, int score) throws Exception
	{
		String query=buildQuery(FILE_FORMAT_TSV_NO_HEADER,INTERACTORS_LIST, IDENTIFIERS, ids,limit,null,score);

		URL url=new URL(query);

		URLConnection con = url.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;

		List<String> res=new ArrayList<String>();
		while ((inputLine = in.readLine()) != null) 
		{
			res.add(inputLine.trim());            
		}
		in.close();

		return res;		
	}

	public List<StringInteraction> getInteractors(List<String> ids, int limit, int score) throws Exception
	{
		List<StringInteraction> res=new ArrayList<StringInteraction>();
		for(String s:ids)
		{
			String query=buildQuery(FILE_FORMAT_TSV_NO_HEADER,INTERACTORS_LIST, IDENTIFIERS, s,limit,null,score);

			URL url=new URL(query);

			URLConnection con = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null) 
			{
				StringInteraction i=new StringInteraction();
				i.left=s;
				i.right=inputLine.trim();
				res.add(i);            
			}
			in.close();

		}
		return res;		
	}


	private String buildQuery(String fileFormat, String db, String idParameterName, String idParameterValue, int limit, String format, int score )
	{
		StringBuffer sb=new StringBuffer(stringDBBaseUrl+fileFormat+"/");
		sb.append(db+"?");
		sb.append(idParameterName+"=");
		sb.append(idParameterValue);

		if(limit > 0)
		{
			sb.append("&limit="+limit);
		}
		if(format!=null)
		{
			sb.append("&format="+format);
		}
		if(score > 0)
		{
			sb.append("&required_score="+score);
		}
		return sb.toString();
	}

	private String buildQueryForIDs(String fileFormat, String db, String idParameterName, List<String> idParameterValues, int limit, String format, int score )
	{
		StringBuffer ids=new StringBuffer();
		boolean first=true;
		for(String s: idParameterValues)
		{
			if(first)
			{
				ids.append(s);
				first=false;
				continue;
			}
			ids.append("%0D"+s);			
		}
		return buildQuery(fileFormat,db, idParameterName, ids.toString(), limit, format,score);
	}

	public class StringInteraction
	{
		public String left;
		public String right;
		public double score=-1; 
		
		public void trimIDs()
		{
			if(left.contains("."))
				left=left.substring(left.indexOf('.')+1);
			if(right.contains("."))
				right=right.substring(right.indexOf('.')+1);
		}
		
		@Override
		public String toString()
		{
			return left+" - "+right;
		}
	}
}
