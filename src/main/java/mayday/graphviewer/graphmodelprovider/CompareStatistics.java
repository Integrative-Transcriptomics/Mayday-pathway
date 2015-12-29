package mayday.graphviewer.graphmodelprovider;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.MIGroupSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.edges.Edges;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.graph.nodes.Nodes;
import mayday.core.tasks.AbstractTask;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.layout.OrderedGroupsLayouter;
import mayday.graphviewer.statistics.Correlations;
import mayday.vis3.graph.layout.CanvasLayouter;
import mayday.vis3.plots.chromogram.probelistsort.ProbeMIOComparator;

public class CompareStatistics extends AbstractGraphModelProvider
{
	private static final String[] numberModes={"linear", "log"};
	private static final String[] edgeWeightModes={"Intersect", "Pairwise complete correlation"};

	private RestrictedStringSetting edgeWeightSetting=new RestrictedStringSetting("Edge weight", "How the edge weights are to be calculated", 0, edgeWeightModes);
	private RestrictedStringSetting binSetting=new RestrictedStringSetting("Bin mode", null, 0, numberModes);
	private IntSetting binWidthSetting=new IntSetting("Bin Size (for linear bins)",null,250,10,10000,true,true);
	private MIGroupSetting leftMiGroup;
	private MIGroupSetting rightMiGroup;

	
	public static final String LEFT="0";
	public static final String RIGHT="1";
	public static final String GROUP="group";
	public static final String ORDER="order";
	
	public CompareStatistics() 
	{
				
	}
		
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"mayday.graphModelProvider.comparestatistics",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				getDescription(),
				getName()				
		);		
		return pli;			
	}
	
		
	@Override
	public boolean isAskForProbeLists() 
	{
		return true;
	}
	
	
	@Override
	public CanvasLayouter defaultLayouter() 
	{
		return new OrderedGroupsLayouter();
//		return new GridLayouter();
	}
	
	@Override
	public Setting getInformedSetting() 
	{
	
		informedSetting=new HierarchicalSetting(getName());
		DataSet ds=probeLists.keySet().iterator().next();
		leftMiGroup=new MIGroupSetting("Left Value", null, null, ds.getMIManager(), false);
		leftMiGroup.setAcceptableClass(DoubleMIO.class);
		rightMiGroup=new MIGroupSetting("Right Value", null, null, ds.getMIManager(), false );
		rightMiGroup.setAcceptableClass(DoubleMIO.class);
		
		informedSetting.addSetting(leftMiGroup);
		informedSetting.addSetting(rightMiGroup);
		informedSetting.addSetting(edgeWeightSetting).addSetting(binSetting).addSetting(binWidthSetting);	
		
		return informedSetting;		
	}
	
	@Override
	public Setting getBasicSetting() 
	{
		return null;
	}
		
	@Override
	public String getName() 
	{
		return "Compare Statistics";
	}
	
	@Override
	public String getDescription() 
	{
		return "Compare a set of statitical measurements";
	}
	
	@Override
	public AbstractTask buildGraph() 
	{
		return new BuildGraphTask();
	}
	
	
	private double eval(Set<Probe> p, MIGroup left, MIGroup right, boolean cor)
	{
		if(!cor)
		{
			return p.size();
		}
		double[] x=new double[p.size()];
		double[] y=new double[p.size()];
		int i=0; 
		for(Probe pr:p)
		{
			x[i]=((DoubleMIO)left.getMIO(pr)).getValue();
			y[i]=((DoubleMIO)right.getMIO(pr)).getValue();
		}
		return Correlations.cor(x, y);
	}	

	private class BuildGraphTask extends AbstractTask
	{
		public BuildGraphTask() 
		{
			super(CompareStatistics.this.getName());
		}
		
		@Override
		protected void initialize() 
		{						
		}		
		
		@Override
		protected void doWork() throws Exception 
		{
			boolean log=binSetting.getSelectedIndex()==1;

			MIGroup left=leftMiGroup.getMIGroup();
			MIGroup right=rightMiGroup.getMIGroup();
			
			
			
			Graph g=new Graph();

			int binWidth=1;
			if(!log)
				binWidth=binWidthSetting.getIntValue();

			List<Probe> leftProbes=new ArrayList<Probe>();
			for(Object o:left.getObjects())
				leftProbes.add((Probe)o);


			
			Collections.sort(leftProbes, Collections.reverseOrder(new ProbeMIOComparator(left)));
			
			List<Probe> rightProbes=new ArrayList<Probe>();
			for(Object o:right.getObjects())
				rightProbes.add((Probe)o);


			Collections.sort(rightProbes, Collections.reverseOrder(new ProbeMIOComparator(right)));

			int i=0;
			int overall=1;
			MultiProbeNode node=new MultiProbeNode(g);
			node.setName(left.getName()+ ""+ overall + "..." + (overall+binWidth-1) );
			node.setProperty(GROUP, LEFT);
			node.setProperty(ORDER, ""+overall);
			node.setRole(Nodes.Roles.PROBES_ROLE);
			g.addNode(node);
			List<MultiProbeNode> leftNodes=new ArrayList<MultiProbeNode>();
			leftNodes.add(node);
			for(Probe p: leftProbes)
			{
				node.addProbe(p);
				++i;
				++overall;
				if(i == binWidth)
				{
					if(log)
						binWidth*=10;
					node=new MultiProbeNode(g);
					node.setName(left.getName()+ ""+ overall + "..." + (overall+binWidth-1) );
					leftNodes.add(node);
					node.setProperty(GROUP, LEFT);
					node.setProperty(ORDER, ""+overall);
					node.setRole(Nodes.Roles.PROBES_ROLE);
					g.addNode(node);
					i=0;				
				}
			}

			
			if(log)
				binWidth=1;
			List<MultiProbeNode> rightNodes=new ArrayList<MultiProbeNode>();
			

			i=0;
			overall=1;
			node=new MultiProbeNode(g);
			node.setName(right.getName()+ ""+ overall + "..." + (overall+binWidth-1) );
			
			node.setProperty(GROUP, RIGHT);
			node.setProperty(ORDER, ""+overall);
			node.setRole(Nodes.Roles.PROBES_ROLE);
			g.addNode(node);
			rightNodes.add(node);
			
			for(Probe p: rightProbes)
			{
				node.addProbe(p);
				++i;
				++overall;
				if(i == binWidth)
				{
					if(log)
						binWidth*=10;
					node=new MultiProbeNode(g);
					node.setName(right.getName()+ ""+ overall + "..." + (overall+binWidth-1) );
					rightNodes.add(node);				
					node.setProperty(GROUP, RIGHT);
					node.setProperty(ORDER, ""+overall);
					node.setRole(Nodes.Roles.PROBES_ROLE);
					g.addNode(node);
					i=0;				
				}
			}

			boolean cor=edgeWeightSetting.getSelectedIndex()==1;
			for(MultiProbeNode ln:leftNodes)
			{
				for(MultiProbeNode rn:rightNodes)
				{
					Set<Probe> l=new HashSet<Probe>(ln.getProbes());
					l.retainAll(rn.getProbes());
					if(!l.isEmpty())
					{
						Edge e=new Edge(ln,rn);
						double v=eval(l,left,right,cor);
						e.setWeight(v);
						e.setName(NumberFormat.getNumberInstance().format(v));
						e.setRole(Edges.Roles.INTERSECT_EDGE);
						g.connect(e);
					}
				}
			}			
			model=new SuperModel(g);
		}		
	}
}
