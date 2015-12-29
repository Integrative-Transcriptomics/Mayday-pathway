package mayday.graphviewer.plugins.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.SelectableHierarchicalSetting;
import mayday.core.settings.generic.SelectableHierarchicalSetting.LayoutStyle;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.core.auxItems.AuxItems;
import mayday.graphviewer.gui.InformationSetting;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.group.BagSelected;
import mayday.graphviewer.plugins.shrink.MergeNodes;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.NodeComponent;
import mayday.vis3.graph.model.GraphModel;

public abstract class FilterPlugin  extends AbstractGraphViewerPlugin
{
	protected static final String[] methods={"Minimize","Hide","Delete"};
	//	protected RestrictedStringSetting method=new RestrictedStringSetting("Method", "What to do with nodes filtered out", 0, methods);
	protected BooleanSetting invert=new BooleanSetting("Invert", "If checked, keep the nodes not matching the criterion and discard the othets", false);

	protected HierarchicalSetting setting;
	protected SelectableHierarchicalSetting methodSetting;

	protected BooleanSetting keepOriginals; 
	protected StringSetting targetRole;
	protected StringSetting targetNodeName;
	protected StringSetting targetGroupName;
	protected RestrictedStringSetting targetAux;

	public FilterPlugin() 
	{
		setting=new HierarchicalSetting("Filter");


		HierarchicalSetting minimizeSetting=new HierarchicalSetting("Minimize Nodes");
		minimizeSetting.addSetting(new InformationSetting("info", "Minimize the size of the nodes\n which do not match the filter"));

		HierarchicalSetting hideSetting=new HierarchicalSetting("Hide Nodes");
		hideSetting.addSetting(new InformationSetting("info", "Hide the nodes that do not match the filter\n (reversible)"));

		HierarchicalSetting deleteSetting=new HierarchicalSetting("Delete Nodes");
		deleteSetting.addSetting(new InformationSetting("info", "Delete the nodes that do not match the filter\n (permanent)"));

		HierarchicalSetting mergeSetting=new HierarchicalSetting("Merge Nodes");
		keepOriginals=new BooleanSetting("Keep original nodes", null, false);
		targetNodeName=new StringSetting("Target Node Name", null, "Filtered Nodes");
		mergeSetting.addSetting(new InformationSetting("info", "Merge nodes _not_ matching the filter together"));
		mergeSetting.addSetting(keepOriginals).addSetting(targetNodeName);

		HierarchicalSetting groupSetting=new HierarchicalSetting("Group Nodes");
		targetGroupName=new StringSetting("Target Group Name", null, "Filtered Nodes");
		groupSetting.addSetting(new InformationSetting("info", "Grpup nodes _not_ matching the filter together"));
		groupSetting.addSetting(targetGroupName);
		
		HierarchicalSetting roleSetting=new HierarchicalSetting("Assign Role");
		targetRole=new StringSetting("Roll to assign:", null, "Filtered Nodes");
		roleSetting.addSetting(new InformationSetting("info", "Group nodes _not_ matching the filter together"));
		roleSetting.addSetting(targetRole);

		HierarchicalSetting auxSetting=new HierarchicalSetting("Assign Auxiliary");
		targetAux=new RestrictedStringSetting("Auxiliary Item to assign", null, 0, AuxItems.Keys.KEYS);
		auxSetting.addSetting(new InformationSetting("info", "Mark nodes _not_ matching the filter together"));
		auxSetting.addSetting(targetAux);

		HierarchicalSetting[] settings=new HierarchicalSetting[]{
				minimizeSetting, 
				hideSetting, 
				deleteSetting, 
				mergeSetting, 
				groupSetting, 
				roleSetting, 
				auxSetting};

		methodSetting=new SelectableHierarchicalSetting("Method","What to do with nodes filtered out", 0,  settings);
		methodSetting.setLayoutStyle(LayoutStyle.COMBOBOX);
		setting.addSetting(methodSetting).addSetting(invert);
	}

	protected void applyFilter(GraphViewerPlot canvas, GraphModel model, Collection<CanvasComponent> comps)
	{
		if(methodSetting.getSelectedIndex()== 3 )	
		{
			List<CanvasComponent> components=new ArrayList<CanvasComponent>(comps);
			MergeNodes merger=new MergeNodes();
			merger.run(canvas, model, components);
			((NodeComponent)merger.getLastResult()).getNode().setName(targetNodeName.getStringValue());
			merger.getLastResult().setLabel(targetNodeName.getStringValue());
		}
		if(methodSetting.getSelectedIndex()== 4)
		{
			List<CanvasComponent> components =new ArrayList<CanvasComponent>(comps);
			BagSelected merger=new BagSelected();
			merger.run(canvas, model, components);
			merger.getLastBag().setName(targetGroupName.getStringValue());
			
			((SuperModel)model).getComponent(merger.getLastBag()).setName(targetGroupName.getStringValue());
		}

		for(CanvasComponent cc:comps)
		{
			switch (methodSetting.getSelectedIndex()) 
			{
			case 0:
				cc.minimize();
				break;
			case 1:
				cc.setVisible(false);				
				break;
			case 2:
				model.remove(cc);
				break;	
			case 3:
				model.remove(cc);
				break;	
			case 5:
				((NodeComponent)cc).getNode().setRole(targetRole.getStringValue());				
				break;	
			case 6:
				((DefaultNode)((NodeComponent)cc).getNode()).setProperty(targetAux.getStringValue(), "TRUE");	
				break;	
			default:
				break;
			}
		}
		canvas.updatePlot();
	}


}
