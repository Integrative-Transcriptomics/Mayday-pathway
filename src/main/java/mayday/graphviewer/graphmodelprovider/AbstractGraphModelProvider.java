package mayday.graphviewer.graphmodelprovider;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.MappingSourceSetting;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.maps.MultiHashMap;
import mayday.core.tasks.AbstractTask;
import mayday.graphviewer.core.SuperModel;


public abstract class AbstractGraphModelProvider extends AbstractPlugin  implements GraphModelProvider
{
	protected HierarchicalSetting basicSetting;
	protected HierarchicalSetting informedSetting;
	protected SuperModel model;
	protected MultiHashMap<DataSet, ProbeList> probeLists;
	
	public AbstractGraphModelProvider()
	{		
		basicSetting=new HierarchicalSetting(getName());
		informedSetting=new HierarchicalSetting(getName());		
	}
	
	
	@Override
	public void setProbeLists(MultiHashMap<DataSet, ProbeList> probeLists) 
	{		
		this.probeLists=probeLists;
	}
	
	public MultiHashMap<DataSet, ProbeList> getProbeLists() 
	{
		return probeLists;
	}
	
	@Override
	public SuperModel getGraphModel() 
	{
		return model;
	}
	
	@Override
	public boolean isAskForFileSetting() 
	{		
		return false;
	}
	
	@Override
	public boolean isAskForProbeLists() 
	{
		return false;
	}
	
	@Override
	public Setting getBasicSetting() 
	{
		return basicSetting;
	}
	
	@Override
	public Setting getInformedSetting() 
	{
		return informedSetting;
	}
		
	@Override
	public AbstractTask parseFile() 
	{	
		return null;
	}
	
	@Override
	public Component getAdditionalComponent()
	{
		return null;
	}
	
	@Override
	public void updateFileSettings() 
	{
		//do nothing;		
	}
	
	@Override
	public String toString() 
	{
		return getName();		
	}
	
	public static void annotateNodes(MultiHashMap<DataSet, ProbeList> probeLists, Graph graph, Map<DataSet, MappingSourceSetting> mappings )
	{
		// build map of mappings
		Map<String, Probe> nameProbeMap=new HashMap<String, Probe>();
		for(DataSet ds: probeLists.keySet())
		{
			MappingSourceSetting ms=mappings.get(ds);				
			for(ProbeList pl: probeLists.get(ds))
			{					
				for(Probe p:pl)
				{
					String s= ms.mappedName(p);
					if(s!=null)
						nameProbeMap.put(ms.mappedName(p),p);
				}
			}
		}

		for(Node n: graph.getNodes())
		{
			DefaultNode dn=(DefaultNode) n;
			for(String s: dn.getProperties().values())
			{
				if(s==null)
					continue;
				if(s.startsWith("[") && s.endsWith("]"))
				{
					// we have a serialized List<String>						
					String[] tok= s.substring(1,s.length()-1).split(",");						
					for(String st:tok)
					{
						if(nameProbeMap.containsKey(st.trim()))
						{
							((MultiProbeNode)dn).addProbe(nameProbeMap.get(st.trim()));								
						}								
					}						
				}else
				{
					if(nameProbeMap.containsKey(s))
					{
						((MultiProbeNode)dn).addProbe(nameProbeMap.get(s));
					}					
				}					
			}
		}
	}
	
	protected static void annotateNodesIgnoreCase(MultiHashMap<DataSet, ProbeList> probeLists, Graph graph, Map<DataSet, MappingSourceSetting> mappings )
	{
		// build map of mappings
		Map<String, Probe> nameProbeMap=new HashMap<String, Probe>();
		for(DataSet ds: probeLists.keySet())
		{
			MappingSourceSetting ms=mappings.get(ds);				
			for(ProbeList pl: probeLists.get(ds))
			{					
				for(Probe p:pl)
				{
					String s= ms.mappedName(p);
					if(s!=null)
						nameProbeMap.put(ms.mappedName(p).toLowerCase(),p);
				}
			}
		}

		for(Node n: graph.getNodes())
		{
			DefaultNode dn=(DefaultNode) n;
			for(String s: dn.getProperties().values())
			{
				if(s.startsWith("[") && s.endsWith("]"))
				{
					// we have a serialized List<String>						
					String[] tok= s.substring(1,s.length()-1).split(",");						
					for(String st:tok)
					{
						if(nameProbeMap.containsKey(st.trim()))
						{
							((MultiProbeNode)dn).addProbe(nameProbeMap.get(st.trim().toLowerCase()));								
						}								
					}						
				}else
				{
					if(nameProbeMap.containsKey(s.toLowerCase()))
					{
						((MultiProbeNode)dn).addProbe(nameProbeMap.get(s.toLowerCase()));
					}					
				}				
			}
		}
	}
		
	@Override
	public void init() {}
}
