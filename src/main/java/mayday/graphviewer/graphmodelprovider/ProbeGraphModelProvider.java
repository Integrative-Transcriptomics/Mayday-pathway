package mayday.graphviewer.graphmodelprovider;

import java.util.HashMap;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.tasks.AbstractTask;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.core.bag.ComponentBag;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.layout.CanvasLayouter;
import mayday.vis3.graph.layout.GridLayouter;

public class ProbeGraphModelProvider extends AbstractGraphModelProvider implements GraphModelProvider
{
	private BooleanSetting askForProbes=new BooleanSetting("Select ProbeLists","Select the probe lists to be used when constructing the graph",false);
	private BooleanSetting groupByProbeList;
	
	public ProbeGraphModelProvider() 
	{
		groupByProbeList=new BooleanSetting("Group Probes by ProbeList", null, true);
		basicSetting.addSetting(askForProbes).addSetting(groupByProbeList);		
	}
		
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"mayday.graphModelProvider.probes",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Create a new graph from of all selected probes",
				getName()				
		);		
		return pli;			
	}
	
		
	@Override
	public boolean isAskForProbeLists() 
	{
		return askForProbes.getBooleanValue();
	}
	
	
	@Override
	public CanvasLayouter defaultLayouter() 
	{
		return new GridLayouter();
	}
	
	@Override
	public Setting getInformedSetting() 
	{
		return null;
	}
		
	@Override
	public String getName() 
	{
		return "Probes";
	}
	
	@Override
	public String getDescription() 
	{
		return "Build a simple graph with no connections from the probes ";
	}
	
	@Override
	public AbstractTask buildGraph() 
	{
		return new BuildGraphTask();
	}
		

	private class BuildGraphTask extends AbstractTask
	{
		public BuildGraphTask() 
		{
			super(ProbeGraphModelProvider.this.getName());
		}
		
		@Override
		protected void initialize() 
		{						
		}		
		
		@Override
		protected void doWork() throws Exception 
		{
			Graph graph=new Graph();
			for(DataSet ds: probeLists.keySet())
			{
				for(ProbeList pl: probeLists.get(ds))
				{
					for(Probe p: pl)
					{
						MultiProbeNode n=new MultiProbeNode(graph,p);
						graph.addNode(n);
						
					}
				}
			}
			model=new SuperModel(graph);
			for(DataSet ds: probeLists.keySet())
			{
				for(ProbeList pl: probeLists.get(ds))
				{
					ComponentBag bag=new ComponentBag(model);
					bag.setColor(pl.getColor());
					bag.setName(pl.getName());
					for(Probe p: pl)
					{
						for(MultiProbeComponent cc:model.getComponents(p))
						{
							bag.addComponent(cc);
						}
						
					}
					model.addBag(bag);
				}
			}			
		}		
	}

		
}
