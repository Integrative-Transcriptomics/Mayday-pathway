package mayday.pathway.keggview.plugins;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JDialog;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.settings.typed.MappingSourceSetting;
import mayday.pathway.core.PathwayDefaults;
import mayday.pathway.keggview.kegg.ko.KOEntry;
import mayday.pathway.keggview.kegg.pathway.Pathway;
import mayday.pathway.keggview.pathways.AnnotationManager;
import mayday.pathway.keggview.pathways.PathwayLink;
import mayday.pathway.keggview.pathways.PathwayManager;
import mayday.pathway.keggview.pathways.PathwayOverviewTableModel;
import mayday.pathway.keggview.pathways.graph.GeneNode;
import mayday.pathway.keggview.pathways.graph.PathwayGraph;
import mayday.pathway.keggview.pathways.gui.PathwayOverviewFrame;

public class PathwayOverviewPlugin extends AbstractPlugin implements ProbelistPlugin
{
	public void init() 
	{		
	}

	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli= new PluginInfo(
				this.getClass(),
				"pas.mayday.pathway.PathwayOverview",
				new String[0], 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"KEGG Pathway Overview",
		"KEGG Pathway Overview");
		pli.addCategory(PathwayDefaults.CATEGORY_PATHWAYS);
		return pli;
	}

	public List<ProbeList> run(List<ProbeList> probeLists,MasterTable masterTable)
	{
		// get Mapping and other information;
		KEGGSettings settings=new KEGGSettings(masterTable.getDataSet(),null);
		JDialog dialog=settings.getDialog();
		dialog.setModal(true);
		dialog.setVisible(true);
		
		
		AnnotationManager annotationManager=new AnnotationManager();
		try {
			annotationManager.init(settings.getKeggDataDirectory().getStringValue(), settings.getTaxonSetting().getStringValue());
		} catch (IOException e1) 
		{			
			e1.printStackTrace();
		}
		
		List<PathwayOverviewTableModel> models=new ArrayList<PathwayOverviewTableModel>();
		
		for(ProbeList pl:probeLists)
		{
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
						
			List<PathwayGraph> pathways=readPathways(settings.getKeggDataDirectory().getStringValue(), 
					settings.getTaxonSetting().getStringValue(),annotationManager);
			List<PathwayGraph> resultGraphs=new ArrayList<PathwayGraph>();
			List<Integer> resultNum=new ArrayList<Integer>();
			for(PathwayGraph p:pathways)
			{
				Set<Probe> found=new HashSet<Probe>();
				for(GeneNode gn:p.getGeneNodes())
				{
					for(KOEntry e: gn.getGenes())
					{
						if(e==null) continue;
						for(String s:e.getPossibleNames())
						{
							if(s==null)
								continue;
							Probe pr= probeMapping.get(s.toLowerCase());
							if(pr!=null) 
							{
								gn.addProbe(pr);
								found.add(pr);
							}
						}			
					}					
				}
				if(found.size() >0 )
				{
					resultGraphs.add(p);
					resultNum.add(found.size());
				}
			}
			if(resultGraphs.size()!=0)
			{
				models.add(new PathwayOverviewTableModel(resultNum,resultGraphs,pl));
			}
			
		}
		
		PathwayOverviewFrame frame=new PathwayOverviewFrame(models);
		frame.setVisible(true);
		
		return null;
	}
	
	List<PathwayGraph> readPathways(String path, String taxon, AnnotationManager annotationManager)
	{
		List<PathwayGraph> res=new ArrayList<PathwayGraph>();
		try
		{
			PathwayManager pathwayManager=new PathwayManager();
			pathwayManager.setDataDirectory(path);
			pathwayManager.setTaxon(taxon);
			pathwayManager.readDirectory();
			
			for(PathwayLink l:pathwayManager.getPathways())
			{
				if(!l.isAvailable()) continue;
				Pathway p=pathwayManager.loadPathway(l);
				PathwayGraph graph=new PathwayGraph(p);
				graph.setGenes(annotationManager.getNameEntryMap());			
				res.add(graph);
			}			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return res;
	}
}
