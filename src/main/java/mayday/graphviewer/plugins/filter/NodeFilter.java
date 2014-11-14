package mayday.graphviewer.plugins.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.SelectableHierarchicalSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.core.structures.graph.Graph;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.NodeComponent;
import mayday.vis3.graph.model.GraphModel;

public class NodeFilter extends FilterPlugin 
{

	@Override
	public void run(GraphViewerPlot canvas, GraphModel model,List<CanvasComponent> components) 
	{
		if(components.isEmpty())
			return;
		
		StringSetting name=new StringSetting("Name", "RegExp allowed", "");
		StringSetting role=new StringSetting("Role", "RegExp allowed", "");
		IntSetting degree=new IntSetting("Degree", null, 1);
		
		Setting[] options={name,role,degree};
		
		SelectableHierarchicalSetting targetSetting=new SelectableHierarchicalSetting("Filter by", null, 0, options);
		
		setting.addSetting(targetSetting);
		
		SettingDialog sd=new SettingDialog(canvas.getOutermostJWindow(), "Filter", setting);
		sd.showAsInputDialog();
		
		if(!sd.closedWithOK())
			return;
		
		int target=targetSetting.getSelectedIndex();
		
		List<CanvasComponent> matching=null;
		switch (target) {
		case 0:
			matching=filterRegExp(name.getStringValue(), components, true);			
			break;
		case 1:
			matching=filterRegExp(role.getStringValue(), components, false);			
			break;
		case 2:
			matching=filterDegree(degree.getIntValue(), components, model.getGraph());			
			break;	
			
		default:
			break;
		}
		
		Set<CanvasComponent> compsToHide=new HashSet<CanvasComponent>(components);
		if(invert.getBooleanValue())
			compsToHide.retainAll(matching);
		else
			compsToHide.removeAll(matching);
		
		applyFilter(canvas, model, compsToHide);

	}
	
	/**
	 * @param n
	 * @param input
	 * @param eatKitten
	 * @return a list of components in input whose name or role match the given string n.
	 */
	private List<CanvasComponent> filterRegExp(String n, List<CanvasComponent> input, boolean eatKitten)
	{
		
		
		List<CanvasComponent> res=new ArrayList<CanvasComponent>();
		for(CanvasComponent cc:input)
		{
			if(! (cc instanceof NodeComponent))
				continue;
			if(eatKitten)
			{
				if(cc.getLabel().toLowerCase().matches((".*"+n.toLowerCase()+".*") ) )
					res.add(cc);					
			}else
			{	
				if(((NodeComponent)cc).getNode().getRole().toLowerCase().matches(".*"+n.toLowerCase()+".*"))
					res.add(cc);	
			}
		}
		return res;
	}
	
	/**
	 * @param minDegree
	 * @param input
	 * @param g
	 * @return a list of components in input whose degree is equal or larger than minDegree
	 */
	private List<CanvasComponent> filterDegree(int minDegree, List<CanvasComponent> input, Graph g)
	{		
		List<CanvasComponent> res=new ArrayList<CanvasComponent>();
		for(CanvasComponent cc:input)
		{
			int d= g.getDegree( ((NodeComponent)cc).getNode());
			if( d >= minDegree)
				res.add(cc);					
			
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
				"PAS.GraphViewer.FilterNodes",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Remove nodes from the graph that match certain properties",
				"\0Filter Nodes"				
		);
		pli.addCategory(FILTER_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/filterNode.png");
		return pli;	

	}

}
