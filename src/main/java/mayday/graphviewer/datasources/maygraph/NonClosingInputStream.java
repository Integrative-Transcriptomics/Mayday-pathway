package mayday.graphviewer.datasources.maygraph;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipInputStream;

public class NonClosingInputStream extends ZipInputStream 
{
	
	public NonClosingInputStream(InputStream in) 
	{
		super(in);
	}
	
	@Override
	public void close() throws IOException {
		// nö. mach ich nicht. 
	}
	
	
}
