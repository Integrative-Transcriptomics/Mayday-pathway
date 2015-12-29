package mayday.graphviewer.plugins.extend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.meta.types.StringMIO;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.MappingSourceSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.graph.nodes.Nodes;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.datasources.ncbi.EUtilsQuery;
import mayday.graphviewer.datasources.ncbi.PubmedArticleParser;
import mayday.graphviewer.datasources.string.StringConnector;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.GraphModel;

public class QueryAbstracts extends AbstractGraphViewerPlugin  
{

	private BooleanSetting useStringSetting=new BooleanSetting("Use StringDB to query","Otherwise pubmed is queried directly",true);
	private BooleanHierarchicalSetting useOrganism=new BooleanHierarchicalSetting("Limit by organism",null,false);
	private StringSetting organism=new StringSetting("Organism",null,"",true);

	private IntSetting maxSetting=new IntSetting("Maximum Abstracts", "0: no maximum", 20);
	private MappingSourceSetting mappingSetting;

	public QueryAbstracts() 
	{
		useOrganism.addSetting(organism);
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.AbstractImport",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Add notes containing publication abstracts about the selected node",
				"Abstracts"				
		);
		pli.addCategory(EXTEND_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/abstract.png");
		return pli;	
	}

	@Override
	public void run(GraphViewerPlot canvas, GraphModel model,List<CanvasComponent> components) 
	{
		if(components.size()!=1)
			throw new RuntimeException("Please select exactly one component");

		if(!(components.get(0) instanceof MultiProbeComponent) || ((MultiProbeComponent)components.get(0)).getProbes().isEmpty() )
			return;
		DataSet ds=((MultiProbeComponent)components.get(0)).getProbes().get(0).getMasterTable().getDataSet();
	
		
		mappingSetting=new MappingSourceSetting(ds);
		HierarchicalSetting setting=new HierarchicalSetting("Abstract Import Settings");
		setting.addSetting(useStringSetting).addSetting(useOrganism).addSetting(maxSetting).addSetting(mappingSetting);
		SettingDialog dialog=new SettingDialog(null, "Abstract Import Settings",setting);
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
						ids.add(((StringMIO)mappingSetting.getMappingGroup().getMIO(p)).getValue());
						break;		
					}
				}
		}
		List<Map<String,String>> res=null;
		try{
			if(useStringSetting.getBooleanValue())
			{
				StringConnector sc=new StringConnector(StringConnector.DATABASE_STRING);
				List<String> pmids=sc.getAbstractIds(ids, maxSetting.getIntValue());
				List<String> cleanIds=new ArrayList<String>();
				for(String s:pmids)
				{
					cleanIds.add(s.substring(4));
				}
				EUtilsQuery query=new EUtilsQuery(EUtilsQuery.DATABASE_PUBMED);
				res= query.eFetch(cleanIds, new PubmedArticleParser());


			}else
			{
				EUtilsQuery query=new EUtilsQuery(EUtilsQuery.DATABASE_PUBMED);
				String term=ids.get(0);
				if(useOrganism.getBooleanValue())
				{
					String org=organism.getStringValue();
					org.replaceAll("\\s", "+");
					term=term+"+AND+"+org+"[organism]";
				}
				query.eSearch(term);
				if(!query.isReady() || query.getCount()==0)
				{
					return;
				}
				res=query.eFetch(new PubmedArticleParser());
				
				
				
			}
		}catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		int x=20;
		int y=canvas.getComponentMaxY()+20;
		for(Map<String,String> abstr:res)
		{
			MultiProbeNode node=new MultiProbeNode(canvas.getModel().getGraph());
			node.setProperties(abstr);
			node.setName(abstr.get("Title"));
			node.setProperty(Nodes.NOTE_TEXT, abstr.get("Abstract"));
			node.setRole(Nodes.Roles.NOTE_ROLE);

			
			CanvasComponent comp=((SuperModel)model).addNode(node);
			((SuperModel)model).connect(components.get(0), comp);
			
			comp.setLocation(x,y);
			x+=comp.getWidth()+20;
			if(x>canvas.getWidth())
			{
				x=20;
				y+=20+comp.getHeight();
			}
		}

	}


}
