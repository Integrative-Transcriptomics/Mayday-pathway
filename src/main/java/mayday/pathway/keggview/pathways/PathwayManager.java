package mayday.pathway.keggview.pathways;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import mayday.core.pluma.PluginManager;
import mayday.core.pluma.filemanager.FMFile;
import mayday.pathway.core.FTPHelper;
import mayday.pathway.keggview.kegg.KEGGHandler;
import mayday.pathway.keggview.kegg.pathway.Pathway;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * PathwayManager handles requests for KEGG Pathways. It encapsulates file system and http connections
 * and retrieves pathways identified by name, PathwayLinks or indices in the list of pathway links. 
 * Pathways that are not locally available are retrieved automatically from the KEGG ftp site. 
 * @author Stephan Symons
 *
 */
public class PathwayManager 
{
	private List<PathwayLink> pathways;
	private String dataDirectory;
	private String taxon;
	
	public PathwayManager() 
	{
		pathways=new ArrayList<PathwayLink>();
	}
	
	public PathwayManager(String directory, String taxon)
	{
		dataDirectory=directory;
		this.taxon=taxon;
		pathways=new ArrayList<PathwayLink>();
		
	}
	
	public void readDirectory() throws IOException
	{
		if(taxon==null || dataDirectory == null) return;
		BufferedReader r=new BufferedReader(new FileReader(dataDirectory+"/map_title.tab"));
		String line=r.readLine();
		while(line!=null)
		{
			PathwayLink pwf=new PathwayLink();
			pwf.setNumber(line.substring(0,5));
			pwf.setName(line.substring(6));			
			if(new File(dataDirectory+"/"+taxon+pwf.getNumber()+".xml").exists())
			{
				pwf.setAvailable(true);
			}
			pathways.add(pwf);
			line=r.readLine();
		}
	}
	
	/**
	 * Loads KEGG pathways from the KEGG FTP site. 
	 * @param link
	 * @throws Exception
	 */
	private void fetchPathway(PathwayLink link) throws Exception
	{
		FMFile lic = PluginManager.getInstance().getFilemanager().getFile("/mayday/pathway/keggURL.txt");
		String keggurl=new BufferedReader(new InputStreamReader(lic.getStream())).readLine();
		URL url=new URL(keggurl+taxon+"/"+taxon+link.getNumber()+".xml");
		String targetFile=dataDirectory+"/"+taxon+link.getNumber()+".xml";
		boolean res=FTPHelper.getFTP(url, targetFile);
//		URLConnection con=url.openConnection();
//		con.setDoInput(true);
//		con.setUseCaches(false);
//		
//		DataInputStream stream=new DataInputStream(con.getInputStream());
//	    String s; 
//	    
//	    BufferedReader r=new BufferedReader(new InputStreamReader(stream));
//	    BufferedWriter w=new BufferedWriter(new FileWriter(dataDirectory+"/"+taxon+link.getNumber()+".xml"));
//	    s=r.readLine();
//	    while (s != null) 
//	    {
//	        w.write(s+"\n");
//	        s=r.readLine();
//	    }
//	    r.close();
//	    stream.close(); 
//	    w.close();
	    link.setAvailable(res);
	}
	

	
	/**
	 * Retrieve pathway object pathway reference.
	 * @param f
	 * @return
	 */
	public Pathway loadPathway(PathwayLink f) throws Exception
	{
		if(!f.isAvailable())
		{
			try
			{
				fetchPathway(f);
			}catch(Exception e)
			{
				// this pathway is not available for the organism
				return null;
			}
			
		}
		XMLReader parser;
		parser = XMLReaderFactory.createXMLReader();
		parser.setEntityResolver(new EntityResolver() {
			public InputSource resolveEntity(String publicID, String systemID)
			throws SAXException {
				return new InputSource(new StringReader(""));
			}
		}
		);
		KEGGHandler handler=new KEGGHandler();
		parser.setContentHandler(handler);
		parser.parse(new InputSource(new FileReader(dataDirectory+"/"+taxon+f.getNumber()+".xml")));
		//parser.parse(dataDirectory+"/"+taxon+f.getNumber()+".xml");
		return handler.getPathway();				
	}
	
	public Pathway loadPathway(File file) throws Exception
	{
		XMLReader parser;
		parser = XMLReaderFactory.createXMLReader();
		parser.setEntityResolver(new EntityResolver() {
			public InputSource resolveEntity(String publicID, String systemID)
			throws SAXException {
				return new InputSource(new StringReader(""));
			}
		}
		);
		KEGGHandler handler=new KEGGHandler();
		parser.setContentHandler(handler);
		parser.parse(new InputSource(new FileReader(file)));
		//parser.parse(dataDirectory+"/"+taxon+f.getNumber()+".xml");
		return handler.getPathway();
	}
		
	public Pathway loadPathway(int i) throws Exception
	{
		if(i >= pathways.size()) return null;
		return loadPathway(pathways.get(i));			
	}
	
	/**
	 * @param n The Pathway ID to load
	 * @return The pathway, or null if the pathway cannot be found
	 * @throws Exception
	 */
	public Pathway loadPathway(String n) throws Exception
	{
		for(PathwayLink l:pathways)
		{
			if(l.getNumber().equals(n))
				return loadPathway(l);
		}
		return null;					
	}
	
	public int numPathways()
	{
		return pathways.size();
	}

	/**
	 * @return the pathways
	 */
	public List<PathwayLink> getPathways() {
		return pathways;
	}



	/**
	 * @return the dataDirectory
	 */
	public String getDataDirectory() {
		return dataDirectory;
	}

	/**
	 * @param dataDirectory the dataDirectory to set
	 */
	public void setDataDirectory(String dataDirectory) {
		this.dataDirectory = dataDirectory;
	}

	/**
	 * @return the taxon
	 */
	public String getTaxon() {
		return taxon;
	}

	/**
	 * @param taxon the taxon to set
	 */
	public void setTaxon(String taxon) {
		this.taxon = taxon;
	}
	
	public boolean isAvailable(int i)
	{
		return pathways.get(i).isAvailable();
	}
	
	
	
	
	
}
