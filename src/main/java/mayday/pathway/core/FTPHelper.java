package mayday.pathway.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import mayday.pathway.keggview.pathways.FTPDownloadTask;


public class FTPHelper 
{
	public static boolean getFTPFile(String url, String targetFile) throws IOException
	{
		URL sourceURL=new URL(url);
		URLConnection con=sourceURL.openConnection();
		con.setDoInput(true);
		con.setUseCaches(false);

		DataInputStream stream=new DataInputStream(con.getInputStream());
		String s; 

		BufferedReader r=new BufferedReader(new InputStreamReader(stream));
		BufferedWriter w=new BufferedWriter(new FileWriter(targetFile));
		s=r.readLine();
		while (s != null) 
		{
			w.write(s+"\n");
			s=r.readLine();
		}
		r.close();
		stream.close(); 
		w.close();

		return true;
	}
	
	
	/**
	 * Retrieves a file from an ftp url.
	 * @param url
	 * @param targetFile
	 * @return true, if the file was retrieved without any exception; false otherwise. 
	 */
	public static boolean getFTP(String url, String targetFile)
	{
		try {
			return getFTP(new URL(url), targetFile);
		} catch (MalformedURLException e) 
		{
			return false;
		}
	}
	
	
	/**
	 * Retrieves a file from an ftp url.
	 * @param url
	 * @param targetFile
	 * @return true, if the file was retrieved without any exception; false otherwise. 
	 */	
	public static boolean getFTP(URL url, String targetFile)
	{
		try
		{
			FTPDownloadTask task=new FTPDownloadTask(url,targetFile);
			task.start();
			task.waitFor();
		}catch(Exception e)
		{
			return false;
		}
		return true;
	}

}
