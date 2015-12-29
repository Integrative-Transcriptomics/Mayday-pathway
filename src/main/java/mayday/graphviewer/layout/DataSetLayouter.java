package mayday.graphviewer.layout;

import java.awt.Container;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.core.structures.graph.nodes.Nodes;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.util.components.DegreeComparator;
import mayday.graphviewer.util.components.NameComparator;
import mayday.graphviewer.util.components.ProbeCountComparator;
import mayday.graphviewer.util.components.WeightComparator;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.layout.CanvasLayouterPlugin;
import mayday.vis3.graph.model.GraphModel;

public class DataSetLayouter extends CanvasLayouterPlugin
{
	private IntSetting xoffset=new IntSetting("horizontal offset", null, 25);
	private IntSetting yoffset=new IntSetting("vertical offset", null, 50);
	@SuppressWarnings("unchecked")
	private ObjectSelectionSetting<Comparator<CanvasComponent>> comparator=new ObjectSelectionSetting<Comparator<CanvasComponent>>(
			"Component Order",null,0,new Comparator[]{new WeightComparator(), new DegreeComparator(), new NameComparator(), new ProbeCountComparator()});
	Comparator<CanvasComponent> presetComparator;
	
	public DataSetLayouter()
	{		
		initSetting();
	}
	
	public DataSetLayouter(Comparator<CanvasComponent> comparator) 
	{
		presetComparator=comparator;
		initSetting();
	}
		
	@Override
	public void layout(Container container, Rectangle bounds, GraphModel model) 
	{
		// partition the nodes by DataSet
		
		MultiHashMap<String, CanvasComponent> nodeDSMap=new MultiHashMap<String, CanvasComponent>();
		
		for(CanvasComponent comp:model.getComponents())
		{
			if(model.getNode(comp) instanceof DefaultNode)
			{
				String r=((DefaultNode)model.getNode(comp)).getPropertyValue(Nodes.Roles.DATASET_ROLE);
				r=r!=null?r:"null";
				nodeDSMap.put(r, comp);				
			}
		}
		
		int xspace=(bounds.width-(2*xoffset.getIntValue()) )/(nodeDSMap.keySet().size()+1);
		int xp=xoffset.getIntValue()+xspace/2;
		for(String s: nodeDSMap.keySet())
		{
			List<CanvasComponent> comps=new ArrayList<CanvasComponent>(nodeDSMap.get(s));
			Collections.sort(comps, presetComparator!=null?presetComparator:comparator.getObjectValue());
			int yp=yoffset.getIntValue();
			for(CanvasComponent comp:comps)
			{
				comp.setLocation(xp, yp);
				yp+=yoffset.getIntValue()+comp.getHeight();
			}
			xp+=xspace;
		}
	}

	public Comparator<CanvasComponent> getComparator() {
		return comparator.getObjectValue();
	}

	public void setComparator(Comparator<CanvasComponent> comparator) 
	{
		presetComparator=comparator;
	}
	
	@Override
	protected void initSetting() 
	{
		setting.addSetting(comparator).addSetting(xoffset).addSetting(yoffset);		
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Layout.DataSet",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Arranges Components grouped by dataset and orderd by edge connectivity",
				"Data Set"				
		);
		return pli;	
	}
	
	
	
}
