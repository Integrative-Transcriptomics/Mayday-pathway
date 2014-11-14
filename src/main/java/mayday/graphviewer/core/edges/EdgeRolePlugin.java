package mayday.graphviewer.core.edges;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.SortedExtendableConfigurableObjectListSetting;
import mayday.core.settings.generic.SortedExtendableConfigurableObjectListSetting.ElementBridge;
import mayday.core.settings.typed.StringSetting;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.edges.EdgeSetting;
import mayday.vis3.graph.model.GraphModel;

public class EdgeRolePlugin extends AbstractGraphViewerPlugin  
{
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		EdgeDispatcher dispatcher=canvas.getEdgeDispatcher();

		SortedExtendableConfigurableObjectListSetting<EdgeSetting> list=
			new SortedExtendableConfigurableObjectListSetting<EdgeSetting>("Edge Role Settings",null,new EdgeSettingBride(dispatcher));
		List<EdgeSetting> values=new ArrayList<EdgeSetting>(dispatcher.getRoleMap().values());
		Collections.sort(values, new EdgeSettingRoleComparator());
		list.setElements(values);
		
		SettingDialog diag=new SettingDialog(canvas.getOutermostJWindow(), "Configure Edges", list);
		diag.showAsInputDialog();
		if(diag.closedWithOK())
		{
			Set<EdgeSetting> res=new HashSet<EdgeSetting>(list.getElements());
			Set<EdgeSetting> orig=new HashSet<EdgeSetting>(dispatcher.getRoleMap().values());
			Set<EdgeSetting> orig2=new HashSet<EdgeSetting>(dispatcher.getRoleMap().values()); // unschön
			// remove all that are in orig, but not in res
			orig.removeAll(res);
			for(EdgeSetting s:orig)
			{
				dispatcher.removeRole(s.getTargetRole());
			}
			// add all that are in res, but not in orig.
			res.removeAll(orig2);
			for(EdgeSetting s:res)
			{
				dispatcher.putRoleSetting(s.getTargetRole(), s);
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
				"PAS.GraphViewer.core.EdgeRole",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Set the rendering rules for edges",
				"Edge Rendering"				
		);
		pli.setIcon("mayday/pathway/gvicons/edges.png");
		return pli;	
	}
	
	private class EdgeSettingBride implements ElementBridge<EdgeSetting>
	{
		private EdgeDispatcher dispatcher;
		
		EdgeSettingBride(EdgeDispatcher dispatcher) {
			this.dispatcher = dispatcher;
		}

		@Override
		public Collection<EdgeSetting> availableElementsForAddition(
				Collection<EdgeSetting> alreadyInList) 
		{
			StringSetting nameSetting=new StringSetting("Name", null, "new role");
			SettingDialog dialog=new SettingDialog(null, "New Role", nameSetting);
			dialog.showAsInputDialog();
			if(!dialog.closedWithOK())
				return Collections.emptyList();
			else
			{
				List<EdgeSetting> settings=new ArrayList<EdgeSetting>();
				EdgeSetting r1Setting=new EdgeSetting();
				r1Setting.setTargetRole(nameSetting.getStringValue());
				settings.add(r1Setting);
				return settings;
			}
			
			
		}

		@Override
		public EdgeSetting createElementFromIdentifier(String identifier) 
		{
			return dispatcher.getSettingForRole(identifier);
		}

		@Override
		public String createIdentifierFromElement(EdgeSetting element) 
		{
			return element.getTargetRole();
		}

		@Override
		public void disposeElement(EdgeSetting element) 
		{
			System.out.println("dispose");
			
		}

		@Override
		public String getDisplayName(EdgeSetting element) 
		{
			return element.getTargetRole();
		}

		@Override
		public Setting getSettingForElement(EdgeSetting element) 
		{
			return element;
		}

		@Override
		public String getTooltip(EdgeSetting element) 
		{
			return element.getTargetRole();
		}
		
	}
	
	private static class EdgeSettingRoleComparator implements Comparator<EdgeSetting>
	{
		@Override
		public int compare(EdgeSetting o1, EdgeSetting o2) 
		{
			return o1.getTargetRole().compareTo(o2.getTargetRole());
		}
	}
}
