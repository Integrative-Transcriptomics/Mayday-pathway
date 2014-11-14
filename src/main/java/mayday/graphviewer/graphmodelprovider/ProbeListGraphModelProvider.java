package mayday.graphviewer.graphmodelprovider;

import java.util.HashMap;
import java.util.Map;

import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.tasks.AbstractTask;
import mayday.graphviewer.core.SuperModel;
import mayday.vis3.graph.layout.CanvasLayouter;
import mayday.vis3.graph.layout.GridLayouter;
import mayday.vis3.graph.layout.SugiyamaLayout;

public class ProbeListGraphModelProvider extends AbstractGraphModelProvider
{
	private BooleanSetting askForProbes=new BooleanSetting("Select ProbeLists","Select the probe lists to be used when constructing the graph",false);
	private BooleanSetting connectProbeLists=new BooleanSetting("Connect Probe Lists","Connect the Probe Lists to resemble the hierarchy.",true);

	public ProbeListGraphModelProvider() 
	{
		basicSetting.addSetting(askForProbes).addSetting(connectProbeLists);		
	}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"mayday.graphModelProvider.probeslists",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Create a new graph from of all selected probelists",
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
		if(connectProbeLists.getBooleanValue())
			return new SugiyamaLayout();
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
		return "Probe Lists";
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
			super(ProbeListGraphModelProvider.this.getName());
		}

		@Override
		protected void initialize() 
		{						
		}		

		@Override
		protected void doWork() throws Exception 
		{
			Graph graph=new Graph();
			Map<ProbeList, Node> plMap=new HashMap<ProbeList, Node>();

			for(DataSet ds: probeLists.keySet())
			{
				for(ProbeList pl:probeLists.get(ds))
				{
					MultiProbeNode n=new MultiProbeNode(graph,pl);
					graph.addNode(n);
					plMap.put(pl, n);
				}
			}
			if(connectProbeLists.getBooleanValue())
			{
				for(ProbeList pl:plMap.keySet())
				{
					ProbeList parent=pl.getParent();
					if(parent!=null && plMap.containsKey(parent))
					{
						graph.connect(plMap.get(parent),plMap.get(pl));
					}
				}
			}
			model=new SuperModel(graph);
		}		
	}
}
