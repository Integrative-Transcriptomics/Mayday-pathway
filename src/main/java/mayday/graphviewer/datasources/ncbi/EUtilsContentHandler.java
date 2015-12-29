package mayday.graphviewer.datasources.ncbi;

import java.util.List;
import java.util.Map;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;

public interface EUtilsContentHandler extends ContentHandler
{
	public List<Map<String,String>> getValues();
	
	public void parse(InputSource input) throws Exception;
}
