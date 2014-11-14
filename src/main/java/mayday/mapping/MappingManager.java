package mayday.mapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.meta.types.StringMIO;

public class MappingManager 
{
	private Map<String, Probe> mapping;
	private Map<String,String> helper;
	private Map<String, List<String> > groups;
	
	public MappingManager()
	{
		mapping=new HashMap<String, Probe>();
		helper=new HashMap<String, String>();
		groups=new HashMap<String, List<String>>();
	}
	
	public void addMapping(ProbeList probeList)
	{
		List<String> grp=new ArrayList<String>();
		for(Probe p:probeList)
		{
			mapping.put(p.getDisplayName(), p);
			grp.add(p.getDisplayName());
		}
		groups.put(probeList.getName(), grp);
		
	}
	
	public void addMapping(List<ProbeList> probeLists)
	{
		for(ProbeList p:probeLists)
			addMapping(p);
	}
	
	public void addMapping(MIGroup m, ProbeList probeList)
	{
		List<String> grp=new ArrayList<String>();
		for(Probe p:probeList)
		{
			if(m.getMIO(p)==null) continue;
			String s=((StringMIO)m.getMIO(p)).getValue();
			mapping.put(s, p);
			grp.add(s);
		}
		groups.put(m.getName(), grp);
	}
	
	public void addMapping(MIGroupSelection<MIType> selection, ProbeList probeList)
	{
		for(MIGroup m:selection)
		{
			addMapping(m, probeList);				
		}
	}
	
	public void addMapping(HashMap<String, String> mapping)
	{
		addMapping(mapping, mapping.hashCode()+"");
	}
	
	public void addMapping(Map<String, String> mapping, String name)
	{
		List<String> grp=new ArrayList<String>();
		grp.addAll(mapping.keySet());
		groups.put(name, grp);
		helper.putAll(mapping);
		processHelper();		
	}
	
	public void addMapping(String name, Probe probe)
	{
		mapping.put(name, probe);		
	}
	
	public int size()
	{
		return mapping.size();
	}
	
	public Probe getProbeForIdenitfier(String ident)
	{
		return mapping.get(ident);
	}
	
	public void processHelper()
	{
		for(String s: helper.keySet())
		{
			// 1. key matches in mapping: map target -> probe!
			if(mapping.containsKey(s))
				mapping.put(helper.get(s), mapping.get(s));
			// 2. target matches in mapping: map key -> probe!#
			if(mapping.containsKey(helper.get(s)))
				mapping.put(s, mapping.get(helper.get(s)));
		}
	}
	
	public void showMapping()
	{
		for(String s: mapping.keySet())
		{
			System.out.println(s+"\t"+mapping.get(s).getDisplayName());
		}
	}
	
	public int groupCount()
	{
		return groups.size();
	}

	/**
	 * @return the groups
	 */
	public Map<String, List<String>> getGroups() {
		return groups;
	}

	/**
	 * @param groups the groups to set
	 */
	public void setGroups(Map<String, List<String>> groups) {
		this.groups = groups;
	}
	
}
