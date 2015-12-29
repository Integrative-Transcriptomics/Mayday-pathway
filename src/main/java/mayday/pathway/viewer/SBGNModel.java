package mayday.pathway.viewer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.StringMIO;
import mayday.core.settings.typed.MappingSourceSetting;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.maps.MultiTreeMap;
import mayday.pathway.sbgn.graph.AbstractSBGNPathwayGraph;
import mayday.pathway.sbgn.graph.SBGNNode;
import mayday.pathway.sbgn.processdiagram.process.Transition;
import mayday.pathway.viewer.canvas.SBGNComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.DefaultGraphModel;
import mayday.vis3.graph.model.GraphWithProbeModel;
import mayday.vis3.graph.model.SummaryProbe;
import mayday.vis3.graph.model.SummaryProbeSetting;

public class SBGNModel extends DefaultGraphModel implements GraphWithProbeModel
{
	private MultiTreeMap<Probe, MultiProbeComponent> probeToComponent;
	
	public SBGNModel(Graph graph) 
	{
		super(graph);
		probeToComponent=new MultiTreeMap<Probe, MultiProbeComponent>();
	}

	protected void init()
	{
		clear();
		for(Node n:getGraph().getNodes())
		{
			if(n instanceof SBGNNode)
			{
				SBGNComponent comp=new SBGNComponent((SBGNNode)n);
				addComponent(comp);	
				getNodeMap().put(comp, n);
				getComponentMap().put(n, comp);		
			}
		}
		Collections.sort(getComponents());
	}
	
	public void setAnnotation(Iterable<Probe> probes, MappingSourceSetting mappingSource)
	{
		switch (mappingSource.getMappingSource()) 
		{
		case MappingSourceSetting.PROBE_NAMES:
			setMappingByName(probes);
			break;
		case MappingSourceSetting.PROBE_DISPLAY_NAMES:
			setMappingByDisplayName(probes);
			break;	
		case MappingSourceSetting.MIO:
			setMappingByMIO(probes, mappingSource.getMappingGroup());			
			break;	
		default:
			break;
		}
	}
	
	public void setSummaryNodes(MasterTable masterTable, SummaryProbeSetting setting)
	{
		removeSummaryNodes(setting);
		for(Node n:getGraph().getNodes())
		{
			if(n instanceof Transition)
			{
				SummaryProbe p=new SummaryProbe(masterTable,getGraph(),n,setting.getSummaryMode());
				if(p.isEmpty()) 
					continue;
				p.setWeightMode(setting.getWeightMode());
				setting.addChangeListener(p);
				p.setName(n.getName()+" (Summary)");
				((SBGNComponent)getComponent(n)).addProbe(p);
				p.updateSummary();
			}
		}
	}
	
	public void removeSummaryNodes(SummaryProbeSetting setting)
	{
		for(Node n:getGraph().getNodes())
		{
			if(n instanceof Transition)
			{
				Iterator<Probe> iter=((SBGNComponent)getComponent(n)).getProbes().iterator();
				while(iter.hasNext())
				{
					Probe p=iter.next();
					if(p instanceof SummaryProbe)
					{
						setting.removeChangeListener((SummaryProbe)p);
						iter.remove();
					}
				}
			}
		}
	}
	
	public void updateSummaryNodes()
	{
		for(Node n:getGraph().getNodes())
		{
			if(n instanceof Transition)
			{
				for(Probe p: ((MultiProbeNode)n).getProbes())
				{
					if(p instanceof SummaryProbe)
						((SummaryProbe) p).updateSummary();
				}
				
			}
		}
	}
	
	private Map<String,Node> buildNameMap()
	{
		Map<String,Node> nameMap=new HashMap<String, Node>();
		
		for(Node n:getGraph().getNodes())
		{
			if(n instanceof SBGNNode)
			{
				List<String> names=((SBGNNode)n).getPossibleNames();
				for(String name:names)
				{
					nameMap.put(name.toLowerCase(), n);
				}
			}
		}		
		return nameMap;
	}
	
	private void setMappingByName(Iterable<Probe>  probes)
	{
		Map<String,Node> nameMap=buildNameMap();
		for(Probe p:probes)
		{
			if(nameMap.containsKey(p.getName().toLowerCase()))
			{
				if( nameMap.get(p) instanceof MultiProbeNode)
				{
					((MultiProbeNode)nameMap.get(p)).addProbe(p);
				}
				((SBGNComponent)getComponent(nameMap.get(p.getName().toLowerCase()))).addProbe(p);
				probeToComponent.put(p,((SBGNComponent)getComponent(nameMap.get(p.getName()))));
			}
		}
	}
	
	private void setMappingByDisplayName(Iterable<Probe>  probes)
	{
		Map<String,Node> nameMap=buildNameMap();
		for(Probe p:probes)
		{
			if(nameMap.containsKey(p.getDisplayName().toLowerCase()))
			{
				if( nameMap.get(p.getDisplayName().toLowerCase()) instanceof MultiProbeNode)
				{
					((MultiProbeNode)nameMap.get(p)).addProbe(p);
				}
				((SBGNComponent)getComponent(nameMap.get(p.getDisplayName().toLowerCase()))).addProbe(p);
				probeToComponent.put(p,((SBGNComponent)getComponent(nameMap.get(p.getName()))));
			}
		}
	}

	private void setMappingByMIO(Iterable<Probe>  probes, MIGroup mapping)
	{
		Map<String,Node> nameMap=buildNameMap();
		for(Probe p:probes)
		{
//			System.out.println(p.getName());
			if(mapping.contains(p))
			{
				String n= ((StringMIO)mapping.getMIO(p)).getValue().toLowerCase();
				if(nameMap.containsKey(n))
				{
					if( nameMap.get(n) instanceof MultiProbeNode)
					{
						((MultiProbeNode)nameMap.get(n)).addProbe(p);
					}
					((SBGNComponent)getComponent(nameMap.get(n))).addProbe(p);
					probeToComponent.put(p,(getComponent(nameMap.get(n))));
				}
			}
		}
	}
	
	public Graph getReactionGraph()
	{
		if(getGraph() instanceof AbstractSBGNPathwayGraph)
			return ((AbstractSBGNPathwayGraph)getGraph()).getReactionGraph();
		else
			return null;
	}
	
	public List<MultiProbeComponent> getComponents(Probe probe) 
	{
		return probeToComponent.get(probe);
	}
	
}
