package mayday.graphviewer.util.grouping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.core.ModelHub;
import mayday.vis3.ValueProvider;
import mayday.vis3.ValueProviderSetting;

public class ValueGrouping extends HierarchicalSetting implements GroupStrategy
{
	private ModelHub hub;	
	private Map<DataSet, ValueProviderSetting> valueProviders=new HashMap<DataSet, ValueProviderSetting>();
	private IntSetting numBins=new IntSetting("Number of Bins",null,3,2,20,true,true);	
	private BooleanSetting useDataSet=new BooleanSetting("Subgroup by Dataset", null, false);
	
	private static final String GROUP="Group ";
	
	public ValueGrouping() 
	{
		super("Group by Value");
		addSetting(numBins).addSetting(useDataSet);
		
	}

	@Override
	public MultiHashMap<String, DefaultNode> groupNodes(List<DefaultNode> nodes) 
	{
		// partition things by dataset:
		MultiHashMap<String, DefaultNode> res=new MultiHashMap<String, DefaultNode>();
		MultiHashMap<DataSet, DefaultNode> dsGrp=new MultiHashMap<DataSet, DefaultNode>();
		
		
		for(DefaultNode n: nodes)
		{
			if(n instanceof MultiProbeNode)
			{
				List<DataSet> dsNames=new ArrayList<DataSet>();
				for(Probe p: ((MultiProbeNode) n).getProbes())
				{
					dsNames.add(p.getMasterTable().getDataSet());
				}
				dsGrp.put(DataSetGrouping.highestFrequencyItem(dsNames), n);					
			}else
			{
				res.put(EMPTY_GROUP, n);
			}
		}
		
		// for each ds, calculate bins and put data into it
		
		for(DataSet ds: dsGrp.keySet())
		{
			// sort nodes by value
			List<MultiProbeNode> oc=new ArrayList<MultiProbeNode>();
			for(DefaultNode n: dsGrp.get(ds))
			{
				oc.add((MultiProbeNode)n);
			}				
			Collections.sort(oc, new ValueProviderNodeComparator(hub.getValueProvider(ds), ds));
			// use avarage if several.
			
			double binWidth=Math.ceil((1.0*oc.size())/(1.0*numBins.getIntValue()) );
			int i=0;
			int bc=1;
			
			for(MultiProbeNode cc: oc)
			{
				res.put(GROUP+bc, cc);
				++i;
				if(i == binWidth)
				{					
					bc++;
					i=0;
				}
			}			
		}
			
		
		return res;
		
	}

	@Override
	public void setHub(ModelHub hub) 
	{
		this.hub=hub;
		valueProviders=new HashMap<DataSet, ValueProviderSetting>();
		for(DataSet ds: hub.getDataSets())
		{
			ValueProviderSetting vps=new ValueProviderSetting(ds.getName(),null, hub.getValueProvider(ds), hub.getViewModel(ds));			
			valueProviders.put(ds, vps);
			addSetting(vps);
		}
	}
	
	@Override
	public ValueGrouping clone() 
	{
		ValueGrouping res=new ValueGrouping();
		res.fromPrefNode(toPrefNode());
		res.hub=hub;
		
		return res;
		
	}
	
	public static  class ValueProviderNodeComparator implements Comparator<MultiProbeNode>
	{
		private ValueProvider valueProvider;
		private DataSet dataSet;
		
		public ValueProviderNodeComparator(ValueProvider valueProvider, DataSet ds) 
		{
			this.valueProvider = valueProvider;
			this.dataSet=ds;
		}

		@Override
		public int compare(MultiProbeNode o1, MultiProbeNode o2) 
		{
			
			if(o1.getProbes().isEmpty() && o2.getProbes().isEmpty()) return 0;			
			if(o1.getProbes().isEmpty()) return -1;
			if(o2.getProbes().isEmpty()) return 1;
			
			double v1=0;
			double v2=0;
			
			int n=0;
			for(Probe p: o1.getProbes())
			{
				if(p.getMasterTable().getDataSet().equals(dataSet))
				{
					v1+=valueProvider.getValue(p);
					++n;
				}
			}
			v1=v1/(1.0*n);
			
			n=0;
			for(Probe p: o2.getProbes())
			{
				if(p.getMasterTable().getDataSet().equals(dataSet))
				{
					v2+=valueProvider.getValue(p);
					++n;
				}
			}	
			v2=v2/(1.0*n);
			return Double.compare(v1, v2);
		}
	}
	
}
