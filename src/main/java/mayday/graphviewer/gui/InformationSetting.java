package mayday.graphviewer.gui;

import javax.swing.JComponent;
import javax.swing.JLabel;

import mayday.core.settings.Setting;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.typed.StringSetting;

public class InformationSetting extends StringSetting 
{
	public InformationSetting(String name, String text) 
	{
		super(name, null, text);
	}
	
	@Override
	public SettingComponent getGUIElement() 
	{
		return new SettingComponent() {
			
			@Override
			public boolean updateSettingFromEditor(boolean failSilently) 
			{
				return true;
			}
			
			@Override
			public JComponent getEditorComponent() 
			{
				return new JLabel(getStringValue());
			}
			
			@Override
			public Setting getCorrespondingSetting() 
			{
				return InformationSetting.this;
			}
		};
	}
	
	@Override
	public InformationSetting clone() 
	{
		InformationSetting res=new InformationSetting(name, getStringValue());
		return res;
	}
	
	
}
