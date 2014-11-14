package mayday.pathway.keggview.pathways;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import mayday.core.tasks.AbstractTask;

public class FTPDownloadTask extends AbstractTask
{
	URL source;
	String target;
	
	public FTPDownloadTask(URL sourceURL, String targetFileName) 
	{
		super("FTP Download");
		source=sourceURL;
		target=targetFileName;
	}

	@Override
	protected void doWork() throws Exception 
	{
		try {
			URLConnection con=source.openConnection();
			con.setDoInput(true);
			con.setUseCaches(false);
			
			int size=con.getContentLength();
			if( size <0)
			{
				fireIndeterminateChanged(true);
			}
			DataInputStream stream=new DataInputStream(con.getInputStream());
			String s; 
			
			writeLog("Downloading "+source.toString()+"\n");
			writeLog(size >0?"Estimated size:"+size+"\n":"Unknown size"+"\n");
			fireIndeterminateChanged(true);
			BufferedReader r=new BufferedReader(new InputStreamReader(stream));
			BufferedWriter w=new BufferedWriter(new FileWriter(target));
			s=r.readLine();
			int read=0;
			int n=0;
			while (s != null) 
			{
			    w.write(s+"\n");
			    s=r.readLine();
			    if(s!=null)
			    {
			        read+=s.length();
			        if(size >0 && n % 100 ==0)
			        {
			        	setProgress(10000*(size/read) );
			        }
			        if(n % 10000 ==0)
			        {
			        	writeLog(n+" lines read ("+read+" bytes)"+"\n");
			        	fireIndeterminateChanged(true);			        	
			        }
			        
			    }
			    n++;
			    if(hasBeenCancelled())
			    {
			    	throw new Exception("Download cancelled by user.");
			    }
			    	
			}
			writeLog(n+" lines read ("+read+" bytes)"+"\n");
        	fireIndeterminateChanged(true);
        	
			r.close();
			stream.close(); 
			w.close();
			writeLog("successfully downloaded file."+"\n");
		} catch (Exception e) 
		{
			writeLog("An error occured while downloading"+"\n");
			writeLog("Maybe this error message is helpful:"+"\n");
			writeLog(e.getMessage());
			throw e;
		}
	}

	@Override
	protected void initialize() 
	{		
	}

}
