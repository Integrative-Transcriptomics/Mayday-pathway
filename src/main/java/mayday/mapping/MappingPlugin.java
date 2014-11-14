package mayday.mapping;

import java.util.HashMap;
import java.util.List;

import mayday.core.MasterTable;
import mayday.core.ProbeList;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.pathway.core.PathwayDefaults;



public class MappingPlugin  extends AbstractPlugin implements ProbelistPlugin
{
	public void init() 
	{		
	}

	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli= new PluginInfo(
				this.getClass(),
				"pas.mayday.pathway.Mapping",
				new String[0], 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Mapping of Annotations",
		"Annotation Mapping");
		pli.addCategory(PathwayDefaults.CATEGORY_PATHWAYS);
		return pli;
	}



	public List<ProbeList> run(List<ProbeList> probeLists,MasterTable masterTable) 
	{
		MappingManager mappingManager=new MappingManager();
		mappingManager.addMapping(probeLists);
		ProbeList pl=ProbeList.createUniqueProbeList(probeLists);
		MappingDialog dialog=new MappingDialog(mappingManager,pl);
		dialog.setVisible(true);
		
		return null;
	}


}
