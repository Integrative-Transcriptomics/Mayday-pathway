//package mayday.graphviewer.illumina;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import mayday.core.DataSet;
//import mayday.core.MasterTable;
//import mayday.core.MaydayDefaults;
//import mayday.core.Probe;
//import mayday.core.ProbeList;
//import mayday.core.math.Statistics;
//import mayday.core.math.distance.DistanceMeasureManager;
//import mayday.core.math.distance.DistanceMeasurePlugin;
//import mayday.core.math.distance.measures.EuclideanDistance;
//import mayday.core.pluma.AbstractPlugin;
//import mayday.core.pluma.Constants;
//import mayday.core.pluma.PluginInfo;
//import mayday.core.pluma.PluginManagerException;
//import mayday.core.pluma.prototypes.ProbelistPlugin;
//import mayday.core.settings.SettingDialog;
//import mayday.core.settings.generic.HierarchicalSetting;
//import mayday.core.structures.maps.MultiHashMap;
//import mayday.genetics.basic.Strand;
//import mayday.genetics.basic.chromosome.Chromosome;
//import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
//import mayday.genetics.locusmap.LocusMap;
//import mayday.genetics.locusmap.LocusMapSetting;
//
//public class InterestingLoci extends AbstractPlugin implements ProbelistPlugin 
//{
//	
//
//	@	Override
//	public PluginInfo register() throws PluginManagerException 
//	{
//		PluginInfo pli= new PluginInfo(
//				this.getClass(),
//				"PAS.Illumina.InterestingLoci",
//				new String[0],
//				Constants.MC_PROBELIST,
//				(HashMap<String,Object>)null,
//				"Stephan Symons",
//				"a@b.de",
//				"MIOTest",
//		"Interesting Loci");
//		pli.addCategory(MaydayDefaults.Plugins.CATEGORY_TEST);
//		return pli;
//	}
//
//	@Override
//	public void init() {
//		
//
//	}
//
//	@Override
//	public List<ProbeList> run(List<ProbeList> probeLists,	MasterTable masterTable) 
//	{
//		HierarchicalSetting setting= new HierarchicalSetting("Gene Models");
//		LocusMapSetting lms=new LocusMapSetting();
//		setting.addSetting(lms);
//		
//		SettingDialog dialog=new SettingDialog(null, "Gene Models", setting);
//		dialog.showAsInputDialog();
//		
//		if(!dialog.closedWithOK())
//			return null;
//		
//		LocusMap map=lms.getLocusMap();
//		ProbeList pl=ProbeList.createUniqueProbeList(probeLists);
//		
//		MultiHashMap<Chromosome, Probe> chromosomeMaps=new MultiHashMap<Chromosome, Probe>();
//		for(Probe p:pl)
//		{
//			AbstractGeneticCoordinate acg=map.get(p.getName());
//			chromosomeMaps.put(acg.getChromosome(), p);
//		}
//		EuclideanDistance ed=(EuclideanDistance)DistanceMeasureManager.get("Euclidean");
//		
//		List<ProbeList> results=new ArrayList<ProbeList>();
//		for(Chromosome c: chromosomeMaps.keySet())
//		{
//			List<AbstractGeneticCoordinate> plusCoords=new ArrayList<AbstractGeneticCoordinate>();
//			List<AbstractGeneticCoordinate> minusCoords=new ArrayList<AbstractGeneticCoordinate>();
//			Map<AbstractGeneticCoordinate, Probe> coordMap=new HashMap<AbstractGeneticCoordinate, Probe>();
//			for(Probe p: chromosomeMaps.get(c))
//			{
//				AbstractGeneticCoordinate acg=map.get(p.getName());
//				System.out.println(acg);
//				if(acg.getStrand()==Strand.PLUS)
//				{
//					plusCoords.add(acg);
//					coordMap.put(acg, p);
//				}
//				if(acg.getStrand()==Strand.MINUS)
//				{
//					minusCoords.add(acg);
//					coordMap.put(acg, p);
//				}
//			}
//			// ignore undefined / both		
//			List<List<AbstractGeneticCoordinate>> plusGroups=findOverlappingGroups(plusCoords);
//			for(List<AbstractGeneticCoordinate> group: plusGroups)
//			{
//				if(group.size() <= 1) // booooring
//					continue;
//				// get probes of groups
//				List<Probe> probes=new ArrayList<Probe>();
//				for(AbstractGeneticCoordinate agc: group)
//				{
//					probes.add(coordMap.get(agc));
//				}
//				ProbeList resPl=createProbeList(probes, ed, probes.get(0).getMasterTable().getDataSet());
//				if(resPl!=null)
//				{
//					resPl.setName(c.getId()+" (+): "+group.get(0).getFrom());
//					results.add(resPl);
//				}					
//			}
//			List<List<AbstractGeneticCoordinate>> minusGroups=findOverlappingGroups(minusCoords);
//			for(List<AbstractGeneticCoordinate> group: minusGroups)
//			{
//				if(group.size() <= 1) // booooring
//					continue;
//				// get probes of groups
//				List<Probe> probes=new ArrayList<Probe>();
//				for(AbstractGeneticCoordinate agc: group)
//				{
//					probes.add(coordMap.get(agc));
//				}
//				ProbeList resPl=createProbeList(probes, ed, probes.get(0).getMasterTable().getDataSet());
//				if(resPl!=null)
//				{
//					resPl.setName(c.getId()+" (-): "+group.get(0).getFrom());
//					results.add(resPl);
//				}					
//			}						
//		}
//		return results;
//	}
//	
//	private ProbeList createProbeList(List<Probe> probes, DistanceMeasurePlugin dp, DataSet  ds)
//	{
//		List<Double> dist=new ArrayList<Double>();
//		for(int i=0; i!= probes.size(); ++i)
//		{
//			for(int j=i+1; j< probes.size(); ++j)
//			{
//				double d=dp.getDistance(probes.get(i).getValues(), probes.get(j).getValues());
//				dist.add(d);
//			}
//		}
//		double mDist=Statistics.mean(dist);
//		double sdDist=Statistics.sd(dist);
//		// find deviation
//		boolean found=false;
//		for(double d: dist)
//		{
//			if(d > mDist+(sdDist*1.5) || d < mDist-(sdDist*1.5) )
//			{
//				found=true;
//			}
//		}
//		if(found)
//		{
//			ProbeList resPl=new ProbeList(ds, false);
//			for(Probe p:probes)
//			{
//				if(resPl.contains(p))
//					continue;
//				resPl.addProbe(p);
//			}
//			return resPl;
//			
//		}
//		return null;
//	}
//	
////	private boolean isInteresting(List<Probe> probes, double cutoff)
////	{
////		int numExp=probes.get(0).getNumberOfExperiments();
////		for(int i=0; i!=numExp; ++i)
////		{
////			for(Probe p: probes)
////			{
////				
////			}
////		}
////	}
//	
//	private List<List<AbstractGeneticCoordinate>>  findOverlappingGroups(List<AbstractGeneticCoordinate> coords)
//	{
//		Collections.sort(coords, new AbstractGeneticCoordinateComparator());
//		int i=0;
//		List<List<AbstractGeneticCoordinate>> result =new ArrayList<List<AbstractGeneticCoordinate>>();
//		while(i < coords.size())
//		{
//			AbstractGeneticCoordinate last=coords.get(i);
//			List<AbstractGeneticCoordinate> overlappingGroup=new ArrayList<AbstractGeneticCoordinate>();
//			overlappingGroup.add(last);
//			i=i+1;
//			while(i < coords.size())
//			{
//				AbstractGeneticCoordinate current=coords.get(i);
//				if(current.getFrom() < last.getTo())
//				{
//					overlappingGroup.add(current);
//					last=current;
//					++i;
//				}else
//				{
//					break;
//				}				
//			}
//			result.add(overlappingGroup);
//		}		
//		return result;
//	}
//
//	private static class AbstractGeneticCoordinateComparator implements Comparator<AbstractGeneticCoordinate>
//	{
//		@Override
//		public int compare(AbstractGeneticCoordinate o1,AbstractGeneticCoordinate o2) {
//			return o1.compareTo(o2);
//		}
//	}
//}
