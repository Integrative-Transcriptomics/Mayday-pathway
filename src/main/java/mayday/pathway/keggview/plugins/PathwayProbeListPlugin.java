package mayday.pathway.keggview.plugins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JDialog;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.StringMIO;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.settings.typed.MappingSourceSetting;
import mayday.core.structures.graph.Node;
import mayday.pathway.core.PathwayDefaults;
import mayday.pathway.keggview.kegg.ko.KOEntry;
import mayday.pathway.keggview.kegg.pathway.Pathway;
import mayday.pathway.keggview.pathways.AnnotationManager;
import mayday.pathway.keggview.pathways.PathwayLink;
import mayday.pathway.keggview.pathways.PathwayManager;
import mayday.pathway.keggview.pathways.graph.GeneNode;
import mayday.pathway.keggview.pathways.graph.PathwayGraph;

public class PathwayProbeListPlugin extends AbstractPlugin implements ProbelistPlugin
{
	public void init() 
	{		
	}

	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli= new PluginInfo(
				this.getClass(),
				"pas.mayday.pathway.PathwayProbeList",
				new String[0], 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Creates ProbeLists from KEGG Pathways",
		"KEGG Pathways to ProbeList");
		pli.addCategory(PathwayDefaults.CATEGORY_PATHWAYS);
		return pli;
	}

	public List<ProbeList> run(List<ProbeList> probeLists,
			MasterTable masterTable) 
	{
		ProbeList pl=probeLists.get(0);
		
		KEGGSettings settings=new KEGGSettings(masterTable.getDataSet(),null);
		JDialog dialog=settings.getDialog();
		dialog.setModal(true);
		dialog.setVisible(true);
		
		String pathwayPath=settings.getKeggDataDirectory().getStringValue();
		Map<String, Probe> probeMapping=null;
		
		
		
		switch (settings.getMappingSource().getMappingSource()) 
		{
		case MappingSourceSetting.PROBE_NAMES:
			probeMapping=AnnotationManager.createMappingByName(pl.getAllProbes());				
			break;
		case MappingSourceSetting.PROBE_DISPLAY_NAMES:
			probeMapping=AnnotationManager.createMappingByDisplayName(pl.getAllProbes());
			break;	
		case MappingSourceSetting.MIO:
			probeMapping=AnnotationManager.createMappingByMIO(settings.getMappingSource().getMappingGroup(),pl.getAllProbes());
			break;	
		default:
			break;
		}
		
		AnnotationManager annotationManager=new AnnotationManager();
		try {
			annotationManager.init(settings.getKeggDataDirectory().getStringValue(), settings.getTaxonSetting().getStringValue());
		} catch (IOException e1) 
		{			
			e1.printStackTrace();
		}

		List<ProbeList>res=new ArrayList<ProbeList>();
		try
		{
			PathwayManager pathwayManager=new PathwayManager();
			pathwayManager.setDataDirectory(pathwayPath);
			pathwayManager.setTaxon(settings.getTaxonSetting().getStringValue());
			pathwayManager.readDirectory();
			
			// main loop:
			
			MIGroup group=masterTable.getDataSet().getMIManager().newGroup("PAS.MIO.String", "Pathway Source");
			for(PathwayLink l:pathwayManager.getPathways())
			{
				if(!l.isAvailable()) continue;
				Pathway p=pathwayManager.loadPathway(l);
				PathwayGraph graph=new PathwayGraph(p);
				graph.setCompounds(annotationManager.getCompounds());			
				graph.setGenes(annotationManager.getNameEntryMap());
				
				ProbeList newPl=new ProbeList(masterTable.getDataSet(),true);
				
				StringMIO mio=new StringMIO(p.getNumber());
				group.add(newPl,mio);
				for(Node n:graph.getNodes())
				{
					if(!(n instanceof GeneNode)) continue;
					for(KOEntry e: ((GeneNode)n).getGenes())
					{
						if(e==null) continue;
						for(String s:e.getPossibleNames())
						{
							if(s==null)
								continue;
							Probe pr= probeMapping.get(s.toLowerCase());
							if(pr!=null && !newPl.contains(pr)) 
							{								
								newPl.addProbe(pr);
							}
						}			
					}					
				}	
				newPl.setName(pl.getName()+":"+graph.getTitle());
				res.add(newPl);
			}
			
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return res;
	}
	
}
