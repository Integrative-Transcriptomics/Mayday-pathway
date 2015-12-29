package mayday.graphviewer.crossViz3.plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.MappingSourceSetting;
import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.core.bag.BagComponent;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.components.NodeComponent;
import mayday.vis3.graph.model.GraphModel;

public class CopyComponents  extends AbstractGraphViewerPlugin
{
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		if(components.isEmpty())  return; // nothing to do. 
		// query the user for dataset to be used:

		List<DataSet> ds=canvas.getModelHub().getAllAvailableDataSets();
		DataSet[] dsa= (DataSet[]) ds.toArray(new DataSet[ds.size()]);
		MappingSourceSetting currentMapping=new MappingSourceSetting(canvas.getModelHub().getViewModel().getDataSet());
		final ObjectSelectionSetting<DataSet> dataSetSetting=new ObjectSelectionSetting<DataSet>("Dataset",null,0,dsa);
		final MappingSourceSetting mapping=new MappingSourceSetting(dsa[0]);
		dataSetSetting.addChangeListener(new SettingChangeListener() {

			@Override
			public void stateChanged(SettingChangeEvent e) 
			{				
				mapping.setDataSet(dataSetSetting.getObjectValue());
			}
		});
		HierarchicalSetting setting=new HierarchicalSetting("Select source Dataset");
		setting.addSetting(currentMapping);
		setting.addSetting(dataSetSetting).addSetting(mapping);
		SettingDialog sd=new SettingDialog(canvas.getOutermostJWindow(), "Source Dataset", setting);
		sd.setModal(true);
		sd.setVisible(true);

		if(!sd.closedWithOK())
			return; 


		DataSet sourceDs=dataSetSetting.getObjectValue();
		Map<String,Probe> sourceProbes=new HashMap<String, Probe>();
		for(Probe p:sourceDs.getMasterTable().getProbes().values())
		{
			sourceProbes.put(mapping.mappedName(p), p);
		}

		List<CanvasComponent> res=new ArrayList<CanvasComponent>();
		List<Probe> addedProbes=new ArrayList<Probe>();
		Map<CanvasComponent, CanvasComponent> originalToCopy=new HashMap<CanvasComponent, CanvasComponent>();
		Map<CanvasComponent, CanvasComponent> copyToOriginal=new HashMap<CanvasComponent, CanvasComponent>();
		for(CanvasComponent cc: components)
		{
			if( cc instanceof BagComponent)
				continue;
			if(cc instanceof MultiProbeComponent)
			{
				MultiProbeNode copyNode=new MultiProbeNode(model.getGraph());
				copyNode.setName(cc.getLabel());
				for(Probe p: ((MultiProbeComponent) cc).getProbes())
				{
					String lname=currentMapping.mappedName(p);
					if(sourceProbes.containsKey(lname))
					{
						Probe ap=sourceProbes.get(lname);
						copyNode.addProbe(sourceProbes.get(lname));
						addedProbes.add(ap);
					}						
				}
				copyNode.setRole(((MultiProbeComponent) cc).getNode().getRole());
				CanvasComponent ccc= ((SuperModel)model).addNode(copyNode);
				res.add(ccc);

				originalToCopy.put(cc, ccc);
				copyToOriginal.put(ccc,cc);
				// add connections here: 

			}else
			{				
				DefaultNode copyNode=new DefaultNode(model.getGraph());
				copyNode.setName( ((NodeComponent)cc).getNode().getName());
				copyNode.setRole( ((NodeComponent)cc).getNode().getRole());

				for(Entry<String, String> e: ( (DefaultNode) ((NodeComponent)cc).getNode()).getProperties().entrySet() )
				{
					copyNode.setProperty(e.getKey(), e.getValue());
				}
				CanvasComponent ccc= ((SuperModel)model).addNode(copyNode);
				originalToCopy.put(cc, ccc);
				copyToOriginal.put(ccc,cc);
			}		
		}

		Graph g=model.getGraph();
		for(CanvasComponent cc: res)
		{
			// get the original:
			CanvasComponent ccOrg=copyToOriginal.get(cc);
			for(Edge edge: g.getOutEdges(((NodeComponent)ccOrg).getNode()))
			{
				Node target=edge.getTarget();
				if( originalToCopy.containsKey(model.getComponent(target)))
				{
					// get target component:
					CanvasComponent cctarget= originalToCopy.get(model.getComponent(target));
					((SuperModel)model).connect(cc, cctarget, edge);					
				}
			}
		}

		canvas.getModelHub().addProbes(sourceDs, addedProbes);
		placeComponents(res, copyToOriginal, canvas,50, 20);
		canvas.updateSize();
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.CopyComponents",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH_CROSS_DATASET,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Import probes from other datasets that match the selected probes",
				"Copy components"				
		);
		return pli;	
	}
}
