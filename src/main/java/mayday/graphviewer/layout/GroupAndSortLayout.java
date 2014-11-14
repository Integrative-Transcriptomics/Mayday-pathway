package mayday.graphviewer.layout;

import java.awt.Color;
import java.awt.Container;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.gui.GUIUtilities;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting.LayoutStyle;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.action.GraphBagModel;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.core.bag.ComponentBag;
import mayday.graphviewer.util.GroupingSetting;
import mayday.graphviewer.util.components.ComponentSortSetting;
import mayday.graphviewer.util.grouping.GroupStrategy;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.layout.CanvasLayouterPlugin;
import mayday.vis3.graph.model.GraphModel;
import mayday.vis3.plots.termpyramid.CollectionSizeComparator;


public abstract class GroupAndSortLayout extends CanvasLayouterPlugin
{
	@Override
	public void layout(Container container, Rectangle bounds, GraphModel model) 
	{
		if(! (container instanceof GraphViewerPlot))
		{
			simpleLayout(container, bounds, model);
			return;
		}
		
		GraphViewerPlot canvas=(GraphViewerPlot)container;
		
		
		HierarchicalSetting masterSetting=new HierarchicalSetting("Group and Sort");
		masterSetting.setLayoutStyle(LayoutStyle.TABBED);
		
		GroupingSetting groupingSetting=new GroupingSetting(canvas.getModelHub());
		masterSetting.addSetting(groupingSetting);
		ComponentSortSetting sortSetting=new ComponentSortSetting(canvas.getModelHub());
		masterSetting.addSetting(sortSetting);
		
		HierarchicalSetting miscSetting=new HierarchicalSetting("Misc");
		
		RestrictedStringSetting groupOrdering=new RestrictedStringSetting("Order Groups", null, 0, new String[]{"Group Name", "Group Size"});
		miscSetting.addSetting(groupOrdering);
		
		BooleanSetting buildGroups=new BooleanSetting("Build Groups", null, false);
		miscSetting.addSetting(buildGroups);
		
		masterSetting.addSetting(miscSetting);
		SettingDialog dialog=new SettingDialog(canvas.getOutermostJWindow(), "Group and Sort Settings", masterSetting);
		dialog.showAsInputDialog();
		
		if(dialog.canceled())
			return;
		
		
		
		GroupStrategy strategy=groupingSetting.getStrategy();

		List<DefaultNode> nodes=new ArrayList<DefaultNode>();
		for(Node n: model.getNodeMap().values())
		{
			if(n instanceof DefaultNode)
				nodes.add((DefaultNode) n);
		}
		// group
		MultiHashMap<String, DefaultNode> groups=strategy.groupNodes(nodes);
		// turn groups into sortable lists of canvasComponents
		Map<List<CanvasComponent>, String> nameToComp=new HashMap<List<CanvasComponent>, String>();
		
		Comparator<CanvasComponent> cmp=sortSetting.getComparator();
		List<List<CanvasComponent>>  sortedAndOrderedGroups=new ArrayList<List<CanvasComponent>>();
		for(String gn: groups.keySet())
		{
			List<CanvasComponent> group=new ArrayList<CanvasComponent>();
			for(Node n: groups.get(gn))
			{
				group.add(model.getComponent(n));
			}
			// sort the components
			Collections.sort(group, cmp);			
			nameToComp.put(group, gn);	
			
			sortedAndOrderedGroups.add(group);
			
		}
		// sort groups
		if(groupOrdering.getSelectedIndex()==0)
			Collections.sort(sortedAndOrderedGroups, new GroupNameComparator(nameToComp));
		else
			Collections.sort(sortedAndOrderedGroups, Collections.reverseOrder(new CollectionSizeComparator()) );
		
		// have the layouter place the sorted group in 
		placeGroups(sortedAndOrderedGroups, container, bounds, model);
		//allow the nodes to be grouped
		if(buildGroups.getBooleanValue())
			bagGroups(nameToComp, canvas);
	}
	
	private void bagGroups(Map<List<CanvasComponent>, String> nameToComp, GraphViewerPlot canvas) 
	{
		Color[] cols=GUIUtilities.rainbow(nameToComp.size(), 1);
		int i=0;
		for(List<CanvasComponent> grp: nameToComp.keySet())
		{
			ComponentBag bag=new ComponentBag((SuperModel)canvas.getModel());
			bag.setName(nameToComp.get(grp));
			bag.setColor(cols[i]);
			for(CanvasComponent c:grp)
			{
				bag.addComponent(c);				
			}
			((GraphBagModel)canvas.getModel()).addBag(bag);			
		}		
	}

	protected abstract void simpleLayout(Container container, Rectangle bounds, GraphModel model);
	
	protected abstract void placeGroups(List<List<CanvasComponent>> components,Container container, Rectangle bounds, GraphModel model);
	
	
	@SuppressWarnings("unchecked")
	private class GroupNameComparator implements Comparator<List>
	{
		Map<List<CanvasComponent>, String> nameMap;
			
		public GroupNameComparator(Map<List<CanvasComponent>, String> nameMap) 
		{
			this.nameMap = nameMap;
		}

		@Override
		public int compare(List o1, List o2) 
		{
			return nameMap.get(o1).compareTo(nameMap.get(o2));
		}
		
		@Override
		public String toString() 
		{
			return "Name";
		}
	}
}
