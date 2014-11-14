package mayday.graphviewer.plugins.extend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.MIGroupSetting;
import mayday.graphviewer.core.DefaultNodeComponent;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.GraphModel;

public class AddMIGroup extends AbstractGraphViewerPlugin  
{
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		//extract probes
		List<Probe> probes=collectProbes(components);
		Set<DataSet> ds=new HashSet<DataSet>();
		for(Probe p:probes)
			ds.add(p.getMasterTable().getDataSet());
		if (ds.size()!=1)
		{
			throw new RuntimeException("Please select probes from a single dataset");
		}
		//we can do that since we have exactly one ds in ds. 
		DataSet dataSet=ds.iterator().next();		
		//call for mi group

		BooleanSetting collate=new BooleanSetting("Collate", null, true);

		MIGroupSetting setting=new MIGroupSetting("Meta Information", 
				"Select a Meta Information Group to be added to the graph",
				null, 
				dataSet.getMIManager(), false);

		HierarchicalSetting set=new HierarchicalSetting("MI Group Setting");
		set.addSetting(setting).addSetting(collate);

		SettingDialog dialog=new SettingDialog(canvas.getOutermostJWindow(), "Select MI Group", set);


		dialog.showAsInputDialog();
		if(!dialog.closedWithOK())
			return;
		//add mi group
		MIGroup grp=setting.getMIGroup();

		if(collate.getBooleanValue())
		{	
			Map<String, DefaultNodeComponent> comps=((SuperModel)model).addAndSummarizeMIGroup(grp, probes);
			for(CanvasComponent cc: components)
			{
				if(cc instanceof MultiProbeComponent)
				{
					for(Probe p: ((MultiProbeComponent) cc).getProbes())
					{
						MIType m=grp.getMIO(p);
						if(m!=null)
							model.connect(cc, comps.get(m.toString()));
					}
				}
			}		
			placeComponents(new ArrayList<CanvasComponent>(comps.values()), canvas, 20, 20);

		}else
		{
			Map<MIType, DefaultNodeComponent> comps=((SuperModel)model).addMIGroup(grp, probes);
			for(CanvasComponent cc: components)
			{
				if(cc instanceof MultiProbeComponent)
				{
					for(Probe p: ((MultiProbeComponent) cc).getProbes())
					{
						MIType m=grp.getMIO(p);
						if(m!=null)
							model.connect(cc, comps.get(m));
					}
				}
			}		
			placeComponents(new ArrayList<CanvasComponent>(comps.values()), canvas, 20, 20);

		}

		


		//connect nodes to their respective mio
	}


	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.AddMIGroup",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Add new nodes to the graph",
				"Add MIGroup"				
		);
		pli.setIcon("mayday/pathway/gvicons/migroup.png");
		pli.addCategory(EXTEND_CATEGORY);
		return pli;	
	}
}
