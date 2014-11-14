package mayday.pathway.keggview.plugins;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.StringListMIO;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.settings.Settings;
import mayday.core.settings.SettingsDialog;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.MappingSourceSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.core.structures.maps.MultiHashMap;
import mayday.pathway.core.PathwayDefaults;
import mayday.pathway.keggview.kegg.KEGGHandler;
import mayday.pathway.keggview.kegg.pathway.Entry;
import mayday.pathway.keggview.kegg.pathway.Pathway;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class KEGGToProbeList  extends AbstractPlugin implements ProbelistPlugin
{
	public void init() 
	{		
	}

	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli= new PluginInfo(
				this.getClass(),
				"pas.mayday.pathway.KEGGToProbeList",
				new String[0], 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Creates ProbeLists from KEGG Pathways",
		"KGML to ProbeList");
		pli.addCategory(PathwayDefaults.CATEGORY_PATHWAYS);
		return pli;
	}

	public List<ProbeList> run(List<ProbeList> probeLists,			MasterTable masterTable) 
	{
		ProbeList pl=ProbeList.createUniqueProbeList(probeLists);
		// acquire settings: 
		// files containing KEGG pathways in KGML
		// mapping source
		// MIOs? -> name, -> id ??
		PathSetting keggPath=new PathSetting("Path to KEGG files", null, null, true, true, false);
		MappingSourceSetting keggMappingSourceSetting=new MappingSourceSetting(pl.getDataSet());
		BooleanHierarchicalSetting addOrganismId=new BooleanHierarchicalSetting("Add Organism Id", null, true);
		StringSetting orgId=new StringSetting("Organism Id", null, "sce");
		
		BooleanSetting createIdGroup=new BooleanSetting("Save IDs as MI Group", null, true);
		BooleanSetting createNameGroup=new BooleanSetting("Save Pathway Names as MI Group", null, true);
		
		HierarchicalSetting setting=new HierarchicalSetting("KEGG Pathways");
		setting.addSetting(keggPath).addSetting(keggMappingSourceSetting).addSetting(createIdGroup).addSetting(createNameGroup);
		
		Settings settings=new Settings(setting, PluginInfo.getPreferences("pas.mayday.pathway.KEGGToProbeList"));
		SettingsDialog dialog=new SettingsDialog(null, "KEGG Pathways", settings);
		dialog.showAsInputDialog();
		if(!dialog.closedWithOK())
			return null;
		
		// crawl over all XML files;
		File dir=new File(keggPath.getStringValue());
		String[] files=dir.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) 
			{
				return name.endsWith(".xml");
			}
		});
		
		try {
			XMLReader parser;
			parser = XMLReaderFactory.createXMLReader();
			parser.setEntityResolver(new EntityResolver() {
				public InputSource resolveEntity(String publicID, String systemID)
				throws SAXException {
					return new InputSource(new StringReader(""));
				}
			}
			);
			KEGGHandler handler=new KEGGHandler();
			parser.setContentHandler(handler);
			
			MultiHashMap<Pathway, String> pathwayToGenes=new MultiHashMap<Pathway, String>();
			MultiHashMap<String, Pathway> geneToPathways=new MultiHashMap<String, Pathway>();
			List<Pathway> pathways=new ArrayList<Pathway>();
			
			for(String currentFile: files)
			{
				String cf=dir.getPath()+"/"+currentFile;
				System.out.println(cf);
				Pathway p=null;
				try{
				parser.parse(new InputSource(new FileReader(cf)));
				p=handler.getPathway();
				}catch(Exception e){
					System.out.println("Error reading file: "+ cf + ": "+e.getMessage());
					continue;
				}
				pathways.add(p);
				for(Entry e: p.getEntries())
				{
					if(e.getType().equals("gene"))
					{
						String[] gIds=e.getName().split(" ");
//						System.out.println(Arrays.toString(gIds));
						for(String gid:gIds){
							pathwayToGenes.put(p, gid);
							geneToPathways.put(gid, p);
						}
					}					
				}				
			}
			// create MIGroups if necessary:
			MIGroup idGroup=null;
			MIGroup nameGroup=null;
			if(createIdGroup.getBooleanValue())
				idGroup=pl.getDataSet().getMIManager().newGroup("PAS.MIO.StringList", "KEGG Pathway IDs");
			if(createNameGroup.getBooleanValue())
				nameGroup=pl.getDataSet().getMIManager().newGroup("PAS.MIO.StringList", "KEGG Pathway Names");
				
				
			
			// create reverse mapping of probes;
			Map<String, Probe> probeMap=new HashMap<String, Probe>();
			String prefix=addOrganismId.getBooleanValue()?orgId.getStringValue()+":":"";
			for(Probe p: pl)
			{				
				String searchName=prefix+keggMappingSourceSetting.mappedName(p);
				probeMap.put(searchName, p);
				if(idGroup!=null){
					if(geneToPathways.containsKey(searchName)){
						List<String> mip=new ArrayList<String>();
						for(Pathway pw: geneToPathways.get(searchName)){
							mip.add(pw.getName());
						}
						if(!mip.isEmpty())	
							idGroup.add(p, new StringListMIO(mip));
					}
				}
				if(nameGroup!=null){
					if(geneToPathways.containsKey(searchName)){
						List<String> mip=new ArrayList<String>();
						for(Pathway pw: geneToPathways.get(searchName)){
							mip.add(pw.getTitle());
						}
						if(!mip.isEmpty())	
							nameGroup.add(p, new StringListMIO(mip));
					}
				}				
			}	
			List<ProbeList> res=new ArrayList<ProbeList>();
			// iterate over pathways and add probe lists to result
			for(Pathway p: pathwayToGenes.keySet()){
				ProbeList pwpl=new ProbeList(pl.getDataSet(), true);
				pwpl.setName(p.getTitle());
				
				for(String s: pathwayToGenes.get(p)){
					if(probeMap.containsKey(s)){
						Probe pr=probeMap.get(s);
						if(!pwpl.contains(pr))
							pwpl.addProbe(pr);
					}						
				}
				
				if(pwpl.getNumberOfProbes()!=0)
					res.add(pwpl);				
			}				
			return res;
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return null; 
	}
}
