package mayday.graphviewer.crossViz3.probes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.settings.typed.MappingSourceSetting;
import mayday.core.structures.maps.MultiHashMap;

/**
 * Maps probes from different DataSets to each other, creating a so-called unit
 * Each unit may contain 0 or 1 probes from 1 or more datasets. 
 * <pre>
 * unit	
 * 1	ds1.1	ds2.a	
 * 2	ds1.2		ds3.43
 * 3		ds2.b
 * 4	ds1.3	ds2.b	ds3.43534
 * @author Stephan Symons
 *
 */
public class ProbeMapping implements IProbeMapping, Iterable<IProbeUnit>
{
	private Map<String,IProbeUnit> units;
	private List<DataSet> dataSets;
	
	
	public ProbeMapping(List<DataSet> dataSets) 
	{
		units=new HashMap<String, IProbeUnit>();
		this.dataSets=dataSets;
	}
		
	@Override
	public int getNumberOfUnits() 
	{
		return units.size();
	}
	
	@Override
	public IProbeUnit getUnit(String unitName) 
	{
		return units.get(unitName);
	}
	
	@Override
	public Collection<IProbeUnit> getUnits()
	{
		return units.values();
	}
	
	@Override
	public List<IProbeUnit> getUnits(Probe p) 
	{
		List<IProbeUnit> res=new ArrayList<IProbeUnit>();
		for(IProbeUnit u:this)
		{
			for(Probe probe:u.getProbes())
			{
				if(probe==p)
					res.add(u);
			}
		}
		return res;
	}
	
	
	
	
	public void addUnit(IProbeUnit unit)
	{
		units.put(unit.getName(),unit);
	}
	
	public void removeUnit(IProbeUnit unit)
	{
		units.remove(unit.getName());
	}

	
	@Override
	public Iterator<IProbeUnit> iterator() {
		return units.values().iterator();
	}
	
	@Override
	public int getNumberOfDataSets() 
	{
		return dataSets.size();
	}
	
	@Override
	public List<DataSet> getDataSets() 
	{
		return dataSets;
	}
	
	public static ProbeMapping createMappingByName(List<DataSet> dataSets)
	{
		ProbeMapping mapping=new ProbeMapping(dataSets);
		Set<String> names=new TreeSet<String>();
		for(DataSet ds: dataSets)
		{
			names.addAll(ds.getMasterTable().getProbes().keySet());			
		}
		for(String n:names)
		{
			List<Probe> probes=new ArrayList<Probe>();
			int i=0;
			for(DataSet ds: dataSets)
			{
				probes.add(ds.getMasterTable().getProbe(n));
				++i;
			}
			ProbeUnit probe=new ProbeUnit(probes,n);
			mapping.addUnit(probe);			
		}
		return mapping;
	}
	
	public static ProbeMapping createMappingByName(List<DataSet> dataSets, MultiHashMap<DataSet, ProbeList> probeLists)
	{
		ProbeMapping mapping=new ProbeMapping(dataSets);
		Set<String> names=new TreeSet<String>();
		for(DataSet ds: dataSets)
		{
			for(ProbeList pl: probeLists.get(ds))
				for(Probe p:pl)
					names.add(p.getName());			
		}
		for(String n:names)
		{
			List<Probe> probes=new ArrayList<Probe>();
			
			for(DataSet ds: dataSets)
			{
				probes.add(ds.getMasterTable().getProbe(n));		
			}
			ProbeUnit probe=new ProbeUnit(probes,n);
			mapping.addUnit(probe);			
		}
		return mapping;
	}
	
	public static ProbeMapping createMappingByName(List<DataSet> dataSets, MultiHashMap<DataSet, Probe> probes, boolean clarify)
	{
		ProbeMapping mapping=new ProbeMapping(dataSets);
		Set<String> names=new TreeSet<String>();
		for(DataSet ds: dataSets)
		{
			for(Probe p: probes.get(ds))				
				names.add(p.getName());			
		}
		for(String n:names)
		{
			List<Probe> unitProbes=new ArrayList<Probe>();
			
			for(DataSet ds: dataSets)
			{
				unitProbes.add(ds.getMasterTable().getProbe(n));		
			}
			ProbeUnit probe=new ProbeUnit(unitProbes,n);
			mapping.addUnit(probe);			
		}
		return mapping;
	}
	
	public static ProbeMapping createMapping(List<DataSet> dataSets, Map<DataSet,MappingSourceSetting> mappings, MultiHashMap<DataSet, Probe> probes)
	{
		ProbeMapping mapping=new ProbeMapping(dataSets);
		HashMap<String, List<Probe>> mappedObjects=new HashMap<String, List<Probe>>();
//		MultiHashMap<String, Probe> mappedObjects=new MultiHashMap<String, Probe>();
		for(DataSet ds: dataSets)
		{
			for(Probe p: probes.get(ds))
			{		
				if(!mappedObjects.containsKey(mappings.get(ds).mappedName(p)))
				{
					LinkedList<Probe> pl=new LinkedList<Probe>();
					pl.add(p);
					mappedObjects.put(mappings.get(ds).mappedName(p),pl);
				}else
					mappedObjects.get(mappings.get(ds).mappedName(p)).add(p);
			
			}
			
		}
		for(String n:mappedObjects.keySet())
		{
			List<Probe> unitProbes=mappedObjects.get(n);
			ProbeUnit probe=new ProbeUnit(unitProbes,n);
			mapping.addUnit(probe);			
		}
		return mapping;
	}
	
	public static ProbeMapping createMappingBySetting(ProbeMappingSetting mappingSetting, MultiHashMap<DataSet, Probe> probes)
	{
		Set<DataSet> dataSets=mappingSetting.getMappings().keySet();
		return createMapping(new ArrayList<DataSet>(dataSets), mappingSetting.getMappings(),probes);
	}
	
	
}
