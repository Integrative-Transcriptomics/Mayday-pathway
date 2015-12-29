package mayday.graphviewer.plugins.extend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.Probe;
import mayday.core.meta.types.StringMIO;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.typed.MappingSourceSetting;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.graph.nodes.Nodes;
import mayday.graphviewer.core.DefaultNodeComponent;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.datasources.uniprot.UniProtConnector;
import mayday.graphviewer.datasources.uniprot.UniProtParser;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.GraphModel;

public class AddUniProt extends AbstractGraphViewerPlugin{

	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		if(components.isEmpty())
			return; 

		// build setting
		MappingSourceSetting mappingSetting=new MappingSourceSetting(canvas.getModelHub().getViewModel().getDataSet());
		SettingDialog dialog=new SettingDialog(null, "UniProt Import Settings",mappingSetting);
		dialog=new SettingDialog(null, "Identifier Mapping",mappingSetting);
		dialog.setModal(true);
		dialog.setVisible(true);

		if(!dialog.closedWithOK())
			return;

		List<String> ids=new ArrayList<String>();
		Map<String, CanvasComponent> reverseMap=new HashMap<String, CanvasComponent>();
		for(CanvasComponent comp:components)
		{
			if(comp instanceof MultiProbeComponent)
				for(Probe p: ((MultiProbeComponent) comp).getProbes())
				{
					switch(mappingSetting.getMappingSource())
					{
					case MappingSourceSetting.PROBE_NAMES: 
						ids.add(p.getName());
						reverseMap.put(p.getName(), comp);
						break;
					case MappingSourceSetting.PROBE_DISPLAY_NAMES: 
						ids.add(p.getDisplayName());
						reverseMap.put(p.getDisplayName(), comp);
						break;	
					case MappingSourceSetting.MIO: 
						if(mappingSetting.getMappingGroup().contains(p))
						{
							ids.add(((StringMIO)mappingSetting.getMappingGroup().getMIO(p)).getValue());
							reverseMap.put(((StringMIO)mappingSetting.getMappingGroup().getMIO(p)).getValue(), comp);
						}
						break;		
					}
				}
		}		

		// query uniprot
		UniProtConnector uc=new UniProtConnector();
		UniProtParser parser=null;
		try{
			parser=uc.uniProtFetch(ids);
		}catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		
		SuperModel sm=(SuperModel)model;
		List<CanvasComponent> addedComponents=new ArrayList<CanvasComponent>();
		for(String id:reverseMap.keySet())
		{
			int idx=-1;
			if(parser.getIdMaps().containsKey(id))
				idx=parser.getIdMaps().get(id);
			else
				continue;
			
			// add protein
			Map<String, String> current=parser.getProtein().get(idx);
			addedComponents.addAll(addMap(current, reverseMap.get(id), sm));
			//gene
			current=parser.getGene().get(idx);
			addedComponents.addAll(addMap(current, reverseMap.get(id), sm));
			//comments
			current=parser.getComments().get(idx);
			addedComponents.addAll(addMap(current, reverseMap.get(id), sm));
			//references
			List<Map<String,String>> refs=parser.getReferences().get(idx);
			for(Map<String,String> ref:refs)
			{
				if(!ref.containsKey("title"))
					continue;
				MultiProbeNode node=new MultiProbeNode(model.getGraph());
				node.setName(ref.get("title"));
				node.setProperties(ref);
				node.setRole(Nodes.Roles.NOTE_ROLE);
				DefaultNodeComponent cc=sm.addNode(node);
				model.connect(reverseMap.get(id), cc);
				addedComponents.add(cc);
				
				System.out.println(node.getName());
				System.out.println(node.getPropertyValue(Nodes.NOTE_TEXT));
			}
			//interactors
			List<String> interactors=parser.getInteractors().get(idx);
			for(String s:interactors)
			{
				Probe p=StringImport.getMappedProbe(mappingSetting, canvas.getModelHub().getViewModel().getDataSet().getMasterTable(), s);
				if(sm.getProbes().contains(p))
				{
					for(CanvasComponent cc:sm.getComponents(p))
					{
						model.connect(reverseMap.get(id), cc);
					}
				}else
				{
					CanvasComponent cc=sm.addProbe(p);
					model.connect(reverseMap.get(id), cc);
					addedComponents.add(cc);
				}
			}
		}
		placeComponents(addedComponents, canvas, 10, 20);
	}
	
	private List<CanvasComponent> addMap(Map<String, String> current, CanvasComponent center, SuperModel model)
	{
		List<CanvasComponent> res=new ArrayList<CanvasComponent>();
		for(String k:current.keySet())
		{
			if(k==null || current.get(k)==null)
				continue;
			MultiProbeNode node=new MultiProbeNode(model.getGraph());
			node.setName(k);
			node.setRole(Nodes.Roles.NOTE_ROLE);
			node.setProperty(Nodes.NOTE_TEXT, current.get(k));
			DefaultNodeComponent cc=model.addNode(node);
			model.connect(center, cc);
		}
		return res;
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.UniProtImport",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Add information from UniProt about the selected nodes ",
				"UniProt"				
		);
		pli.addCategory(EXTEND_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/uniprot.png");
		return pli;	
	}
	
}
