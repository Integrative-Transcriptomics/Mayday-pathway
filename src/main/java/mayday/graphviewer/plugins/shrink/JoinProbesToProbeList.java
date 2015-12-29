package mayday.graphviewer.plugins.shrink;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.action.AddProbeListAction;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.GraphModel;

public class JoinProbesToProbeList extends AbstractGraphViewerPlugin
{
	@Override
	public void run(final GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		MultiHashMap<DataSet, Probe> probeMap=new MultiHashMap<DataSet, Probe>();
		for(CanvasComponent comp:components)
		{
			if(! (comp instanceof MultiProbeComponent))
				continue;
			for(Probe p:((MultiProbeComponent)comp).getProbes())
			{
				probeMap.put(p.getMasterTable().getDataSet(), p);
			}
		}
		SuperModel sm=((SuperModel)model);
		List<ProbeList> probeList=new ArrayList<ProbeList>();
		System.out.println(probeMap);
		for(DataSet ds: probeMap.keySet())
		{
			ProbeList pl=new ProbeList(ds,false);
			pl.setName("Joined Probes ("+ds.getName()+")");
			
			for(Probe p: probeMap.get(ds)){
				if(!pl.contains(p)){
					pl.addProbe(p);
				}
			}
			probeList.add(pl);
			System.out.println(pl.getName()+"\t"+pl.getNumberOfProbes());
		}


		String[] options={"single node","node for each probe","Profile Plot","Heat Map","Box Plot"};
		RestrictedStringSetting method=new RestrictedStringSetting("Display as...",null,0,options);
		BooleanSetting keepSetting=new BooleanSetting("Keep original nodes", null, false);
		HierarchicalSetting setting=new HierarchicalSetting("Join Probes");
		setting.addSetting(method).addSetting(keepSetting);
		SettingDialog dialog=new SettingDialog(null, "Adding "+probeList.size()+" ProbeLists", setting);
		dialog.setModal(true);
		dialog.setVisible(true);
		if(!dialog.closedWithOK())
			return;

		AddProbeListAction.addProbeLists(canvas, probeList, method.getSelectedIndex());

		if(!keepSetting.getBooleanValue())
		{
			for(CanvasComponent comp:components)
			{
				sm.remove(comp);
			}
		}


	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.JoinProbesToProbeLists",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Join the Probes: Form a new node that conains all probes associated with the selected nodes.",
				"Replace by single Node"				
		);
		pli.addCategory(SHRINK_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/jointoprobelist.png");
		return pli;	

	}

}
