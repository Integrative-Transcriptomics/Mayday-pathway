package mayday.pathway.keggview.pathways;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import mayday.core.pluma.PluginManager;
import mayday.core.pluma.filemanager.FMFile;
import mayday.pathway.core.FTPHelper;

public class AnnotationHelper 
{

	public static boolean isValidDataPath(String dataPath)
	{
		File file=new File(dataPath+"/ko");
		if(!file.exists() || !file.isFile() || !file.canRead()) return false;
		
		file=new File(dataPath+"/compound");
		if(!file.exists() || !file.isFile() || !file.canRead()) return false;

		file=new File(dataPath+"/map_title.tab");
		if(!file.exists() || !file.isFile() || !file.canRead()) return false;
		
		return true;
	}
	
	public static boolean catchupDirectory(String dataPath) throws Exception
	{
		FMFile lic = PluginManager.getInstance().getFilemanager().getFile("/mayday/pathway/keggFiles.txt");		
		BufferedReader r=new BufferedReader(new InputStreamReader(lic.getStream()));
		String line=r.readLine();
		Map<String,String> map=new HashMap<String, String>();
		while(line!=null)
		{
			String[] tok=line.split("\t");
			map.put(tok[0],tok[1]);
			line=r.readLine();
		}
		
		for(String s: map.keySet())
		{
			String requiredFile=dataPath+"/"+s;
			File file=new File(requiredFile);
			if(!file.exists() || !file.isFile() || !file.canRead()) 
			{
				boolean b=FTPHelper.getFTP(map.get(s),requiredFile);
				if(!b)
				{
					return false;
				}
			}
		}
		
		return true;
		
	}
	
	public static boolean catchupDirectoryVisual(String dataPath)
	{
		if(isValidDataPath(dataPath))
		{
			return true; // nothing to do.
		}
		
		int i=JOptionPane.showConfirmDialog(null, 
				"Some KEGG annotation files are missing.\nDownload the necessary files? This will take some time.",
				"Download KEGG Files?",
				JOptionPane.YES_NO_OPTION);
		if(i==JOptionPane.NO_OPTION)
		{
			return false;
		}
		try{
			catchupDirectory(dataPath);
		}catch(Exception e)
		{
			JOptionPane.showMessageDialog(null, 
					"Error downloading KEGG files.\\Please check your proxy settings\\ "+e.getMessage(),
					"Error",
					JOptionPane.ERROR_MESSAGE);
		}
		return true;
	}
}
