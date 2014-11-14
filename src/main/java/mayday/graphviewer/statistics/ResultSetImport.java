package mayday.graphviewer.statistics;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import mayday.core.Probe;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginManager;
import mayday.core.structures.graph.io.GraphFactory;
import mayday.core.structures.linalg.matrix.DoubleMatrix;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.core.SummaryOption;
import mayday.graphviewer.core.bag.ComponentBag;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ResultSetImport 
{
	private DocumentBuilder builder;



	public ResultSetImport() throws Exception
	{
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		builder=dbf.newDocumentBuilder();
	}

	private ResultSet buildResultSet(Node rNode, ComponentBag bag) throws Exception
	{
		ResultSet res=new ResultSet();
		Element e=(Element)rNode;

		res.setTitle(e.getAttribute(ResultSetExport.TITLE));
		ResultScope scope=ResultScope.valueOf(e.getAttribute(ResultSetExport.PREFERRED_SCORE));
		res.setPreferredScope(scope);
		SummaryOption summary=SummaryOption.valueOf(e.getAttribute(ResultSetExport.SUMMARY_OPTION));
		res.setSummary(summary);

		try
		{
			AbstractPlugin pl=PluginManager.getInstance().getInstance(e.getAttribute(ResultSetExport.MULTI_DATASET_METHOD));
			res.setMethod((MultiDataSetMethod)pl);
		}catch(Throwable ex)
		{
			//			ex.printStackTrace();
		}
		NodeList children=e.getChildNodes();
		for(int i=0; i!= children.getLength(); ++i)
		{
			Element ce=(Element)children.item(i);

			if(ce.getNodeName().equals(ResultSetExport.ROW_PROBES))
			{
				res.setRowProbes(extractProbes(ce.getTextContent(), bag));
			}
			
			if(ce.getNodeName().equals(ResultSetExport.ROW_GROUP_PROBES))
			{
				MultiHashMap<String, Probe> rgps=new MultiHashMap<String, Probe>();
				NodeList  groups=ce.getChildNodes();
				for(int j=0; j!= groups.getLength(); ++i)
				{
					Element ge=(Element)groups.item(i);
					String grp=ge.getAttribute(ResultSetExport.GROUP_NAME);					
					rgps.put(grp, extractProbes(ge.getTextContent(), bag));
				}				
				res.setRowGroupProbes(rgps);
			}
			if(ce.getNodeName().equals(ResultSetExport.PROBE_RESULTS))
			{
				res.setProbeResults(parseMatrix(new StringReader(ce.getTextContent())));
			}
			if(ce.getNodeName().equals(ResultSetExport.GROUP_RESULTS))
			{
				res.setGroupResults(parseMatrix(new StringReader(ce.getTextContent())));
			}
			
		}
		return res;
	}

	public DoubleMatrix parseMatrix(Reader reader) throws Exception
	{
		BufferedReader r=new BufferedReader(reader);
		String headLine=r.readLine();
		
		String[] splt=headLine.split(" ");
		int ncol=splt.length-1;
		
		int nrow=0;
		List<double[]> data=new ArrayList<double[]>();
		String line=r.readLine();
		List<String> rnames=new ArrayList<String>();
		while(!line.startsWith("}}"))
		{
			nrow++;
			String[] ldat=line.split(" ");
			double[] l=new double[ncol];
			rnames.add(ldat[0]);
			for(int i=1; i!= ldat.length; ++i)
			{
				l[i-1]=Double.parseDouble(ldat[i]);				
			}		
			data.add(l);
			line=r.readLine();
		}
		DoubleMatrix mat=new DoubleMatrix(nrow, ncol);
		
		for(int j=0; j!= ncol; ++j)
		{
			mat.setColumnName(j, splt[j+1]);
		}
		
		for(int i=0; i!= nrow; ++i)
		{
			
			mat.setRowName(i, rnames.get(i));
			for(int j=0; j!= ncol; ++j)
			{
				mat.setValue(i, j, data.get(i)[j]);
			}
		}		
		return mat;
	}
	
	public List<Probe> extractProbes(String p, ComponentBag bag)
	{
		List<Probe> res=new ArrayList<Probe>();
		String[] ps=GraphFactory.probesSplitPattern.split(p);


		for(int i=1; i!= ps.length; ++i)// skip first empty element!;
		{
			if(ps[i].contains("$"))
			{
				String[] dspl=ps[i].split("\\$");
				for(Probe sp:bag.getProbes())
				{
					if(sp.getName().equals(dspl[1]))
					{
						res.add(sp);
						break;
					}
				}
			}		
		}
		return res;
	}
	
	
	public List<ResultSet> parse(InputStream is, ComponentBag bag) throws Exception
	{
		Document document = builder.parse(is);
		List<ResultSet> results=new ArrayList<ResultSet>();
		NodeList components=document.getElementsByTagName(ResultSetExport.RESULT_SET);
		for(int i=0; i!=components.getLength(); ++i)
		{
			Node rNode=components.item(i);
			ResultSet res=buildResultSet(rNode,bag);
			results.add(res);
		}
		return results;
	}
	
	public List<ResultSet> parse(String fileName, ComponentBag bag) throws Exception
	{
		Document document = builder.parse(fileName);
		List<ResultSet> results=new ArrayList<ResultSet>();
		NodeList components=document.getElementsByTagName(ResultSetExport.RESULT_SET);
		for(int i=0; i!=components.getLength(); ++i)
		{
			Node rNode=components.item(i);
			ResultSet res=buildResultSet(rNode,bag);
			results.add(res);
		}
		return results;
	}	



}
