package mayday.graphviewer.datasources.ncbi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xml.sax.InputSource;

public class EUtilsQuery 
{
	public static final String BASE_URL="http://eutils.ncbi.nlm.nih.gov/entrez/eutils/";

	public static final String DATABASE_PUBMED="pubmed";
	public static final String DATABASE_GENE="gene";
	public static final String DATABASE_PROTEIN="protein";

	public static final String FORMAT_TEXT="text";
	public static final String FORMAT_HTML="html";
	public static final String FORMAT_XML="xml";
	public static final String FORMAT_ASN1="asn.1";

	public static final String PARAMTER_RETTYPE_ABSTRACT="abstract";
	public static final String PARAMTER_RETTYPE_FULL="";

	public static final String PARAMETER_DB="db";
	public static final String PARAMETER_TERM="term";
	public static final String PARAMETER_WEBENV="WebEnv";
	public static final String PARAMETER_QUERY_KEY="query_key";
	public static final String PARAMETER_RETMAX="retmax";
	public static final String PARAMETER_RETTYPE="rettype";
	public static final String PARAMETER_RETMODE="retmode";
	public static final String PARAMETER_ID="id";
	public static final String PARAMETER_USEHISTORY="usehistory";
	public static final String VALUE_USEHISTORY="y";

	public static final String TOOL_ESEARCH="esearch.fcgi";
	public static final String TOOL_EFETCH="efetch.fcgi";

	private String database;


	private int retMax=20;
	private String retType;

	private String webenv;
	private int queryKey;
	private int count;

	private boolean ready=false;

	public EUtilsQuery(String database) 
	{
		super();
		this.database = database;
	}

	public List<String> eSearch(String term) throws Exception
	{
		String query=BASE_URL+TOOL_ESEARCH+"?"+
		PARAMETER_DB+"="+database+"&"+
		PARAMETER_TERM+"="+term+"&"+
		PARAMETER_USEHISTORY+"="+VALUE_USEHISTORY;

//		System.out.println(query);

		URL url=new URL(query);
		URLConnection con = url.openConnection();

		ESearchParser parser=new ESearchParser();
		parser.parse(new InputSource(con.getInputStream()));

		count=parser.getCount();
		webenv=parser.getWebEnv();
		queryKey=parser.getQueryKey();
		ready=true;

		return parser.getIds();

	}



	public String eFetch() throws Exception
	{
		if(!ready)
			throw new IllegalStateException("Can not query: No previous query available");

		String query=BASE_URL+TOOL_EFETCH+"?"+
		PARAMETER_DB+"="+database+"&"+
		PARAMETER_QUERY_KEY+"="+queryKey+"&"+
		PARAMETER_WEBENV+"="+webenv+"&"+
		PARAMETER_RETMAX+"="+retMax+"&"+
		PARAMETER_RETTYPE+"="+PARAMTER_RETTYPE_ABSTRACT+"&"+
		PARAMETER_RETMODE+"="+FORMAT_XML;
//		System.out.println(query);

		URL url=new URL(query);
		URLConnection con = url.openConnection();

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;

		StringBuilder res=new StringBuilder();
		while ((inputLine = in.readLine()) != null) 
		{
			res.append(inputLine);
		}
		in.close();
		return res.toString();	
	}

	public List<Map<String,String>>eFetch(List<String>  ids,EUtilsContentHandler parser) throws Exception
	{
		if(ids.isEmpty())
			return new ArrayList<Map<String,String>>();
		StringBuffer term=new StringBuffer();
		boolean first=true;
		for(String i:ids)
		{
			if(first)
			{
				term.append(i);
				first=false;
				continue;
			}
			term.append(",").append(i);			
		}

		String query=BASE_URL+TOOL_EFETCH+"?"+
		PARAMETER_DB+"="+database+"&"+
		PARAMETER_ID+"="+term.toString()+"&"+		
		PARAMETER_RETMAX+"="+retMax+"&"+
		PARAMETER_RETTYPE+"="+PARAMTER_RETTYPE_ABSTRACT+"&"+
		PARAMETER_RETMODE+"="+FORMAT_XML;

		URL url=new URL(query);
		URLConnection con = url.openConnection();

		parser.parse(new InputSource(con.getInputStream()));
		return parser.getValues();
	}

	public List<Map<String,String>> eFetch(EUtilsContentHandler parser) throws Exception
	{
		if(!ready)
			throw new IllegalStateException("Can not query: No previous query available");

		String query=BASE_URL+TOOL_EFETCH+"?"+
		PARAMETER_DB+"="+database+"&"+
		PARAMETER_QUERY_KEY+"="+queryKey+"&"+
		PARAMETER_WEBENV+"="+webenv+"&"+
		PARAMETER_RETMAX+"="+retMax+"&"+
		PARAMETER_RETTYPE+"="+PARAMTER_RETTYPE_ABSTRACT+"&"+
		PARAMETER_RETMODE+"="+FORMAT_XML;
//		System.out.println(query);

		URL url=new URL(query);
		URLConnection con = url.openConnection();

		parser.parse(new InputSource(con.getInputStream()));
		return parser.getValues();

	}


	public int getRetMax() {
		return retMax;
	}

	public void setRetMax(int retMax) {
		this.retMax = retMax;
	}

	public String getRetType() {
		return retType;
	}

	public void setRetType(String retType) {
		this.retType = retType;
	}

	public String getDatabase() {
		return database;
	}

	public boolean isReady() {
		return ready;
	}

	public int getCount() {
		return count;
	}




	//	
	//	eFetch()
	//	eFetch(String id)
	//	eFetch(List<String>  ids)




}
