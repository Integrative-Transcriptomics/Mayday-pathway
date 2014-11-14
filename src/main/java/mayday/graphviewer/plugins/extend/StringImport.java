package mayday.graphviewer.plugins.extend;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import mayday.core.MasterTable;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.meta.types.AnnotationMIO;
import mayday.core.meta.types.StringMIO;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.MappingSourceSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.graph.nodes.Nodes;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.datasources.string.StringConnector;
import mayday.graphviewer.datasources.string.StringConnector.StringInteraction;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.GraphModel;

public class StringImport  extends AbstractGraphViewerPlugin  
{
	private RestrictedStringSetting database=new RestrictedStringSetting("Database", 
			"String: contains protein-protein interactions\n" +
			"Stitch: also contains protein-metabolite interactions", 0, new String[]{StringConnector.DATABASE_STRING, StringConnector.DATABASE_STITCH});

	private BooleanHierarchicalSetting interactionsSetting=new BooleanHierarchicalSetting("Retrieve Interaction Network", 
			"if selected, fetch the network of closely related interactors," +
			"else, get only direct interactors", true);

	private IntSetting scoreSetting=new IntSetting("Minimum Score", "0: no minimum score", 0,0,1000,true,false);
	private IntSetting maxSetting=new IntSetting("Maximum Interactors", "0: no maximum", 0);

	private HierarchicalSetting setting;


	public StringImport() 
	{
		setting=new HierarchicalSetting("String Import Settings");
		interactionsSetting.addSetting(scoreSetting);
		setting.addSetting(database).addSetting(interactionsSetting).addSetting(maxSetting);
	}

	@Override
	public Setting getSetting() 
	{
		return setting;
	}

	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		if(components.isEmpty())
			return; 

		// build setting
		SettingDialog dialog=new SettingDialog(null, "String Import Settings",setting);
		dialog.setModal(true);
		dialog.setVisible(true);

		if(!dialog.closedWithOK())
			return;

		// inspect selected probes

		MappingSourceSetting mappingSetting=new MappingSourceSetting(canvas.getModelHub().getViewModel().getDataSet());
		dialog=new SettingDialog(null, "Identifier Mapping",mappingSetting);
		dialog.setModal(true);
		dialog.setVisible(true);

		if(!dialog.closedWithOK())
			return;



		List<String> ids=new ArrayList<String>();
		for(CanvasComponent comp:components)
		{
			if(comp instanceof MultiProbeComponent)
				for(Probe p: ((MultiProbeComponent) comp).getProbes())
				{
					switch(mappingSetting.getMappingSource())
					{
					case MappingSourceSetting.PROBE_NAMES: 
						ids.add(p.getName());
						break;
					case MappingSourceSetting.PROBE_DISPLAY_NAMES: 
						ids.add(p.getDisplayName());
						break;	
					case MappingSourceSetting.MIO: 
						if(mappingSetting.getMappingGroup().contains(p))
							ids.add(((StringMIO)mappingSetting.getMappingGroup().getMIO(p)).getValue());
						break;		
					}
				}
		}		

		// query string

		StringConnector sc=new StringConnector(database.getStringValue());		
		List<StringInteraction> interactions=null;

		try{
			if(interactionsSetting.getBooleanValue())
			{
				interactions=sc.getInteractions(ids, maxSetting.getIntValue(), scoreSetting.getIntValue());
			}else
			{
				interactions=sc.getInteractors(ids, maxSetting.getIntValue(), scoreSetting.getIntValue());
			}
		}catch(Exception e)
		{
			throw new RuntimeException(e);
		}

		// extend graph by adding new components
		Graph g=model.getGraph();
		MasterTable mt=canvas.getModelHub().getViewModel().getDataSet().getMasterTable();
		ProbeList pl=new ProbeList(canvas.getModelHub().getViewModel().getDataSet(), false);
		
		int x=20;
		int y=canvas.getComponentMaxY()+20;
		
		
		for(StringInteraction i:interactions)
		{
			i.trimIDs();
			Node leftNode=g.findNode(i.left);
			if(leftNode==null)
			{
				leftNode=new MultiProbeNode(g);
				leftNode.setName(i.left);
				Probe p=getMappedProbe(mappingSetting, mt, i.left);
				if(p==null && mappingSetting.getMappingSource()==MappingSourceSetting.MIO) 
				{					
					if(mt.getProbe(i.left)!=null)
						p=mt.getProbe(i.left);
				}
				if(p==null && mappingSetting.getMappingSource()==MappingSourceSetting.MIO) 
				{					
					for(Probe pr: mt.getProbes().values())
					{
						if(pr.getDisplayName().equals(i.left))
							p=pr;	
					}
				}				
				if(p!=null)
				{
					((MultiProbeNode)leftNode).addProbe(p);
					leftNode.setRole(Nodes.Roles.PROBE_ROLE);
					if(!pl.contains(p))
						pl.addProbe(p);
				}
				CanvasComponent comp=((SuperModel)model).addNode((MultiProbeNode)leftNode);
				comp.setLocation(x,y);
				x+=comp.getWidth()+20;
				if(x>canvas.getWidth())
				{
					x=20;
					y+=20+comp.getHeight();
				}
			}
			Node rightNode=g.findNode(i.right);
			if(rightNode==null)
			{
				rightNode=new MultiProbeNode(g);
				rightNode.setName(i.right);
				
				Probe p=getMappedProbe(mappingSetting, mt, i.right);
				if(p==null && mappingSetting.getMappingSource()==MappingSourceSetting.MIO) 
				{					
					if(mt.getProbe(i.right)!=null)
						p=mt.getProbe(i.right);
				}
				if(p==null && mappingSetting.getMappingSource()==MappingSourceSetting.MIO) 
				{					
					for(Probe pr: mt.getProbes().values())
					{
						if(pr.getDisplayName().equals(i.right))
							p=pr;	
					}
				}
				
				if(p!=null)				
				{
					((MultiProbeNode)rightNode).addProbe(p);
					rightNode.setRole(Nodes.Roles.PROBE_ROLE);
					if(!pl.contains(p))
						pl.addProbe(p);
					
				}
				CanvasComponent comp=((SuperModel)model).addNode((MultiProbeNode)rightNode);
				comp.setLocation(x,y);
				x+=comp.getWidth()+20;
				if(x>canvas.getWidth())
				{
					x=20;
					y+=20+comp.getHeight();
				}
			}
			if(leftNode!=rightNode)
			{
				model.getGraph().connect(leftNode, rightNode);
			}			
		}
		
		pl.setName("String Interaction Partners ("+SimpleDateFormat.getDateTimeInstance().format(new Date())+")");		
		pl.setAnnotation(new AnnotationMIO("Anchor components: "+ids, "Data Base: "+database.getStringValue()+" Min Score: "+scoreSetting.getIntValue()));
		pl.setParent(canvas.getModelHub().getAddedProbes().getParent());
		canvas.getModelHub().getViewModel().getDataSet().getProbeListManager().addObject(pl);
		canvas.getModelHub().getViewModel().addProbeListToSelection(pl);

		canvas.updateSize();

	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.StringImport",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Add and /or connect interaction partners of the selected genes as defined in the String Database",
				"String Interaction Partners"				
		);
		pli.addCategory(EXTEND_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/string.png");
		return pli;	
	}

	public static Probe getMappedProbe(MappingSourceSetting mapping, MasterTable mt, String query)
	{
		if(mapping.getMappingSource()==MappingSourceSetting.PROBE_NAMES)
		{
			return mt.getProbe(query);
		}

		for(Probe p: mt.getProbes().values())
		{
			switch(mapping.getMappingSource())
			{

			case MappingSourceSetting.PROBE_DISPLAY_NAMES: 
				if(p.getDisplayName().equals(query))
					return p;
				
				break;	
			case MappingSourceSetting.MIO: 
				if(mapping.getMappingGroup().contains(p) && ((StringMIO)mapping.getMappingGroup().getMIO(p)).getValue().equals(query))
					return p;				
				break;		
			}			
		}
		return null;
	}
}
