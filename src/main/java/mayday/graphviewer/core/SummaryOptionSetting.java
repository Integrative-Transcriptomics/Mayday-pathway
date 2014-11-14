package mayday.graphviewer.core;

import mayday.core.settings.generic.ObjectSelectionSetting;

public class SummaryOptionSetting extends ObjectSelectionSetting<SummaryOption>
{
	public SummaryOptionSetting(String name) 
	{
		super(name,null,0,new SummaryOption[]{SummaryOption.MEAN,SummaryOption.MEDIAN});
	}
	
	@Override
	public SummaryOptionSetting clone() 
	{
		SummaryOptionSetting res=new SummaryOptionSetting(getName());
		res.setSelectedIndex(getSelectedIndex());
		return res;
	}
}
