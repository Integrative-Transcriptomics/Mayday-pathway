package mayday.graphviewer.core;

import java.util.Collection;
import java.util.Collections;

import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.SortedExtendableConfigurableObjectListSetting.ElementBridge;

public class DataSetSettingBridge implements   ElementBridge<HierarchicalSetting>
{
	@Override
	public Collection<HierarchicalSetting> availableElementsForAddition(Collection<HierarchicalSetting> alreadyInList) 
	{
		return Collections.emptyList();
	}

	@Override
	public HierarchicalSetting createElementFromIdentifier(String identifier) 
	{
		return null;
	}

	@Override
	public String createIdentifierFromElement(HierarchicalSetting element) 
	{
		return element.getName();
	}

	@Override
	public void disposeElement(HierarchicalSetting element) 
	{
		//do nothing;
		
	}

	@Override
	public String getDisplayName(HierarchicalSetting element) 
	{
		return element.getName();
	}

	@Override
	public Setting getSettingForElement(HierarchicalSetting element) 
	{
		return element;
	}

	@Override
	public String getTooltip(HierarchicalSetting element) 
	{
		return element.getName();
	}
	
}
