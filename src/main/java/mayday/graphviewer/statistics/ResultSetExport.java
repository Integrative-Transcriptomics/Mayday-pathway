package mayday.graphviewer.statistics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import mayday.core.Probe;
import mayday.core.pluma.AbstractPlugin;
import mayday.graphviewer.core.bag.ComponentBag;

public class ResultSetExport 
{
	public static final String RESULT_SET="resultSet";
	public static final String MULTI_DATASET_METHOD="MultiDataSetMethod";
	public static final String SUMMARY_OPTION="summary";
	public static final String PREFERRED_SCORE="preferredScope";
	public static final String TITLE="title";
	public static final String ROW_PROBES="rowProbes";
	public static final String ROW_GROUP_PROBES="rowGroupProbes";
	public static final String PROBE_RESULTS="probeResults";
	public static final String GROUP_RESULTS="groupResults";
	public static final String GROUP_PROBES="groupProbes";
	public static final String GROUP_NAME="groupName";

	public static void exportResultSets(ComponentBag bag, File f) throws IOException, Exception
	{
		XMLOutputFactory xof = XMLOutputFactory.newInstance();
		XMLStreamWriter writer =xof.createXMLStreamWriter(new FileWriter(f));
		writer.writeStartDocument("utf-8", "1.0");		
		writer.writeComment("Group statistics file created by Mayday");
		writer.writeStartElement("bagResults");
		for(ResultSet res: bag.getStatistics())
		{
			exportResultSet(res, writer);
		}
		writer.writeEndElement();
		writer.writeEndDocument();
		writer.flush();
		writer.close();
	}	
	
	public static void exportResultSets(ComponentBag bag, OutputStream s) throws IOException, Exception
	{
		XMLOutputFactory xof = XMLOutputFactory.newInstance();
		XMLStreamWriter writer =xof.createXMLStreamWriter(s);
		writer.writeStartDocument("utf-8", "1.0");		
		writer.writeComment("Group statistics file created by Mayday");
		writer.writeStartElement("bagResults");
		for(ResultSet res: bag.getStatistics())
		{
			exportResultSet(res, writer);
		}
		writer.writeEndElement();
		writer.writeEndDocument();
		writer.flush();
		writer.close();
	}	
	

	public static void exportResultSet(ResultSet res, XMLStreamWriter writer) throws IOException, Exception
	{

		writer.writeStartElement(RESULT_SET);
		writer.writeAttribute(TITLE, res.getTitle());
		writer.writeAttribute(PREFERRED_SCORE, res.getPreferredScope().toString());
		writer.writeAttribute(SUMMARY_OPTION, res.getSummary().toString());
		
		AbstractPlugin pl=(AbstractPlugin)(res.getMethod());
		writer.writeAttribute(MULTI_DATASET_METHOD,pl.getPluginInfo().getIdentifier() );

		boolean first=true;
		StringBuffer probeNames=new StringBuffer();
		if(res.getRowProbes()!=null)
		{
			writer.writeStartElement(ROW_PROBES);		
			for(Probe p:res.getRowProbes())
			{
				if(!first)
					probeNames.append(",");
				first=false;
				probeNames.append("\""+p.getMasterTable().getDataSet().getName()+"$"+p.getName()+"\"");
			}
			writer.writeCharacters(probeNames.toString());
			writer.writeEndElement();
		}


		if(res.getRowGroupProbes()!=null)
		{
			writer.writeStartElement(ROW_GROUP_PROBES);
			for(String s: res.getRowGroupProbes().keySet())
			{
				List<Probe> probes=res.getRowGroupProbes().get(s);
				writer.writeStartElement(GROUP_PROBES);
				writer.writeAttribute(GROUP_NAME, s);
				probeNames=new StringBuffer();

				first=true;
				for(Probe p:probes)
				{
					if(!first)
						probeNames.append(",");
					first=false;
					probeNames.append("\""+p.getMasterTable().getDataSet().getName()+"$"+p.getName()+"\"");
				}
				writer.writeCharacters(probeNames.toString());
				writer.writeEndElement();
			}
			writer.writeEndElement();
		}

		if(res.getProbeResults()!=null)
		{
			writer.writeStartElement(PROBE_RESULTS);
			writer.writeCharacters(res.getProbeResults().toString());
			writer.writeEndElement();
		}

		if(res.getGroupResults()!=null)
		{
			writer.writeStartElement(GROUP_RESULTS);
			writer.writeCharacters(res.getGroupResults().toString());
			writer.writeEndElement();
		}


		writer.writeEndElement();


	}



}
