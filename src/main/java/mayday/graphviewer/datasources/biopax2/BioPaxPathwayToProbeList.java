package mayday.graphviewer.datasources.biopax2;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.FilesSetting;
import mayday.core.settings.typed.MappingSourceSetting;
import mayday.core.tasks.AbstractTask;
import mayday.graphviewer.core.Utilities;
import mayday.pathway.core.PathwayDefaults;

public class BioPaxPathwayToProbeList  extends AbstractPlugin implements ProbelistPlugin
{
	private static final String LAST_BIOPAX_FILE="AnacondaLastBioPaxDir";

	public void init() 
	{		
	}

	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli= new PluginInfo(
				this.getClass(),
				"it.mayday.pathway.BioPaxPathwayProbeList",
				new String[0], 
				Constants.MC_PROBELIST,
				(HashMap<String,Object>)null,
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Creates ProbeLists from BioPax Pathways",
		"BioPax Pathways to ProbeList");
		pli.addCategory(PathwayDefaults.CATEGORY_PATHWAYS);
		return pli;
	}

	public List<ProbeList> run(List<ProbeList> probeLists,
			MasterTable masterTable) 
			{
		ProbeList pl=ProbeList.createUniqueProbeList(probeLists);		
		// bixpax file, mapping setting
//		String d = Utilities.prefs.get(LAST_BIOPAX_FILE, System.getProperty("user.home"));
//		PathSetting pathSetting = new PathSetting("Pathway File", null, d, false, true, false);
		FilesSetting pathwayFilesSetting = new FilesSetting("Biopax Pathway Files", "Add all Biopax Pathway Files, that should be analyzed.", null);
		MappingSourceSetting mappingSource = new MappingSourceSetting(pl.getDataSet());
		HierarchicalSetting setting=new HierarchicalSetting("BioPax Pathways");
		setting.addSetting(pathwayFilesSetting);
//		setting.addSetting(pathSetting);
		setting.addSetting(mappingSource);
		// ask for settings:
		SettingDialog dialog=new SettingDialog(null, "BioPax Pathways", setting);
		dialog.showAsInputDialog();
		if(!dialog.closedWithOK())
			return null;		
		// biopax squeezer,
		BioPaxAnnotationTask task = new BioPaxAnnotationTask("BioPax Annotations", pl, mappingSource, pathwayFilesSetting);
		task.start();
		task.waitFor();
		
		return task.getResult();
//		
//		List<ProbeList> res;
//		try {
//			BioPaxSqueezer2 squeezer=BioPaxSqueezer2.getSharedInstance(pathSetting.getStringValue());
//			Utilities.prefs.put(LAST_BIOPAX_FILE, pathSetting.getStringValue());
//			// go over all probes in pl and look for pathways
//			MultiHashMap<String, Probe> map=new MultiHashMap<String, Probe>();
//			MIGroup grp= pl.getDataSet().getMIManager().newGroup("PAS.MIO.StringList", "BioPax Pathways");
//			for(Probe p: pl)
//			{
//				String n=mappingSource.mappedName(p);
//				String s=squeezer.getProteinForXref(n);
//				List<String> pathways= squeezer.getPathwaysForProtein(s);
//				StringListMIO mio=new StringListMIO(pathways);
//				grp.add(p, mio);
//				for(String pw: pathways)
//					map.put(pw, p);			
//			}
//			res = new ArrayList<ProbeList>();
//			for(String pw:  map.keySet())
//			{
//				ProbeList plist=new ProbeList(pl.getDataSet(), true);
//				List<String> lnames=squeezer.getObject(pw, BioPaxSqueezer2.NAME);
//				plist.setName(lnames.get(0));
//
//				for(Probe p: map.get(pw))
//					plist.addProbe(p);
//				res.add(plist);
//			}
//			return res;
//		}catch (Exception e) 
//		{
//			e.printStackTrace();
//		}
//		return null;

			}

	private class BioPaxAnnotationTask extends AbstractTask {
		private ProbeList pl;
		private MappingSourceSetting mappingSource;
		private FilesSetting pathwayFilesSetting;
		private List<ProbeList> result;

		public BioPaxAnnotationTask(String title, ProbeList pl,
				MappingSourceSetting mappingSource, FilesSetting pathwayFilesSetting) {
			super(title);
			this.pl = pl;
			this.mappingSource = mappingSource;
			this.pathwayFilesSetting = pathwayFilesSetting;
		}

		@Override
		protected void doWork() throws Exception 	{
			setProgress(0);
			
			result = new ArrayList<ProbeList>();
			List<String> filePaths = pathwayFilesSetting.getFileNames();
			
			for(int i = 0; i < filePaths.size(); i++) {
				String filePath = filePaths.get(i);
				
				System.out.println(filePath);
				
				File f = new File(filePath);
				
				writeLog("Parsing BioPax file: " + f.getName() + "\n");
				Long t = System.currentTimeMillis();
				BioPaxSqueezer2 squeezer = BioPaxSqueezer2.getSharedInstance(filePath);
				Utilities.prefs.put(LAST_BIOPAX_FILE, filePath);
				
				writeLog("Done reading pathway file.\n\tElapsed time = "+ (System.currentTimeMillis() - t) + " ms\n");
				
				// go over all probes in pl and look for pathways
				List<String> pws = squeezer.getObjectsOfType(BioPaxSqueezer2.PATHWAY);
				
				Map<String, Probe> reverseMapping=new HashMap<String, Probe>();
				
				for(Probe p : pl) {
					//allow multiple name mappings for a single probe
					List<String> mappedNames = mappingSource.mappedNames(p);
					
					for(String s : mappedNames) {
						reverseMapping.put(s.trim(), p);
					}
				}
				
				writeLog(pws.size() + " pathways have been found in the provided pathway-file\n");
				
				for(String pw: pws) {
					t = System.currentTimeMillis();
					List<String> pc=squeezer.getParticipantXRefForPathway(pw);
					ProbeList plist=new ProbeList(pl.getDataSet(), true);
					List<String> lnames=squeezer.getObject(pw, BioPaxSqueezer2.NAME);
					plist.setName(lnames.get(0));
					for(String s: pc) {
						if(reverseMapping.containsKey(s)) {
							Probe p=reverseMapping.get(s);
								if(!plist.contains(p))
									plist.addProbe(p);
						}
					}
					
					writeLog("The resulting probe list contains " + plist.getNumberOfProbes() + " probes\n");
					writeLog("\tElapsed time = "+ (System.currentTimeMillis() - t) + " ms\n");
					writeLog("Done\n");
					
					result.add(plist);
				}
				
//				MIGroup grp= pl.getDataSet().getMIManager().newGroup("PAS.MIO.StringList", "BioPax Pathways");
//				int i=0;
//				int l=pl.getNumberOfProbes();
//				for(Probe p: pl)
//				{
//					
//					String n=mappingSource.mappedName(p);
//					String s=squeezer.getProteinForXref(n);
////					List<String> pathways= squeezer.getPathwaysForProtein(s);
////					StringListMIO mio=new StringListMIO(pathways);
////					grp.add(p, mio);
////					for(String pw: pathways)
////						map.put(pw, p);	
//					++i;
//					setProgress((int) (10000*(1.0*i)/(1.0*l))  );
//					
//				}
//				result = new ArrayList<ProbeList>();
////				for(String pw:  map.keySet())
////				{
////					ProbeList plist=new ProbeList(pl.getDataSet(), true);
////					List<String> lnames=squeezer.getObject(pw, BioPaxSqueezer2.NAME);
////					plist.setName(lnames.get(0));
	////
////					for(Probe p: map.get(pw))
////						plist.addProbe(p);
////					result.add(plist);
////				}
//				writeLog("t= "+ (System.currentTimeMillis() - t));
			}
		}

		@Override
		protected void initialize() {}

		public List<ProbeList> getResult() {
			return result;
		}
	}
}
