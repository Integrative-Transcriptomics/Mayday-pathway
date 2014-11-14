package mayday.graphviewer.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.pluma.PluginManager;
import mayday.core.pluma.filemanager.FMFile;

public class URLHandler 
{
	private static Map<String, String> keyURLMap;

	public static List<String> findURLs(Map<String,String> map)
	{
		List<String> res=new ArrayList<String>();
		for(String s: getKeyTable().keySet())
		{
			if(map.containsKey(s))
			{
				String url=new String(getKeyTable().get(s));
				url=url.replaceFirst("@1", map.get(s));
				res.add(url);
			}
			
		}			
		return res;	
	}

	public static Map<String, String> getKeyTable()
	{
		if(keyURLMap==null)
		{
			try{
				FMFile rconn = PluginManager.getInstance().getFilemanager().getFile("mayday/graphviewer/lookup.txt");
				BufferedReader br = new BufferedReader(new InputStreamReader(rconn.getStream()));
				String l=br.readLine();
				keyURLMap=new HashMap<String, String>();
				while(l!=null)
				{
					String[] tok=l.split("\t");
					keyURLMap.put(tok[0], tok[1]);
					l=br.readLine();
				}
			}catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException("Error reading Database Mapping file");
			}
		}
		return keyURLMap;
	}
}
