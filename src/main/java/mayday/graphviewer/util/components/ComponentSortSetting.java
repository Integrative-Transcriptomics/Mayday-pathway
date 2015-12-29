package mayday.graphviewer.util.components;

import java.util.Comparator;

import mayday.core.DataSet;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.graphviewer.core.ModelHub;
import mayday.vis3.ValueProviderSetting;
import mayday.vis3.graph.components.CanvasComponent;

public class ComponentSortSetting extends HierarchicalSetting
{
	private ObjectSelectionSetting<Comparator<CanvasComponent>> comparator;
	private StringSetting propertySetting=new StringSetting("Property", null, "");	
	private ModelHub hub; 
	
	@SuppressWarnings("unchecked")
	public ComponentSortSetting(ModelHub hub) 
	{
		super("Sort Components");
		this.hub=hub;
		
		Comparator[] predefComparators={
				new NameComparator(),				
				new DegreeComparator(DegreeComparator.OVERALL_DEGREE),
				new DegreeComparator(DegreeComparator.IN_DEGREE),
				new DegreeComparator(DegreeComparator.OUT_DEGREE),
				new WeightComparator(),
				new ProbeCountComparator(),
				new PropertyComparator(),
				new ValueProviderComparator(),
				new ComponentProbesPropertyComparator(ComponentProbesPropertyComparator.MEAN),
				new ComponentProbesPropertyComparator(ComponentProbesPropertyComparator.VAR),
				new ComponentProbesPropertyComparator(ComponentProbesPropertyComparator.SD),
				new ComponentProbesPropertyComparator(ComponentProbesPropertyComparator.MIN),				
				new ComponentProbesPropertyComparator(ComponentProbesPropertyComparator.MAX)};
		comparator=new ObjectSelectionSetting<Comparator<CanvasComponent>>("Sort by",null, 0, predefComparators);		
		addSetting(comparator);		
		addSetting(propertySetting);
				
		for(DataSet ds: hub.getDataSets())
		{
			ValueProviderSetting vps=new ValueProviderSetting(ds.getName(),null, hub.getValueProvider(ds), hub.getViewModel(ds));			
			addSetting(vps);
		}
				
	}
	
	public Comparator<CanvasComponent> getComparator() 
	{
		Comparator<CanvasComponent> cmp=comparator.getObjectValue();
		if(cmp instanceof ValueProviderComparator)
		{
			((ValueProviderComparator) cmp).setHub(hub);
		}
		if(cmp instanceof PropertyComparator)
		{
			((PropertyComparator) cmp).setProperty(propertySetting.getValueString());
		}		
		return cmp;
	}
	
	@Override
	public ComponentSortSetting clone() 
	{
		ComponentSortSetting res=new ComponentSortSetting(hub);
		res.fromPrefNode(toPrefNode());
		// TODO Auto-generated method stub
		return res;
	}
}
