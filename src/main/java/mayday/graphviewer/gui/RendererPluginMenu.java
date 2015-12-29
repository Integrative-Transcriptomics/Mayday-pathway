package mayday.graphviewer.gui;

import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.PluginInstanceSetting;
import mayday.graphviewer.core.DefaultNodeComponent;
import mayday.vis3.graph.menus.RendererMenu;
import mayday.vis3.graph.renderer.RendererAcceptor;
import mayday.vis3.graph.renderer.dispatcher.DecoratorListSetting;
import mayday.vis3.graph.renderer.dispatcher.RendererInstanceSetting;
import mayday.vis3.graph.renderer.dispatcher.RendererPluginSetting;
import mayday.vis3.graph.vis3.SuperColorProvider;

@SuppressWarnings("serial")
public class RendererPluginMenu extends RendererMenu implements SettingChangeListener
{
	private RendererPluginSetting setting;
	
	public RendererPluginMenu(RendererPluginSetting renderer, SuperColorProvider coloring, RendererAcceptor acceptor) 
	{
		super(coloring,acceptor);
		removeAll();
		setting=renderer;
//		setting=renderer.clone();
		add(setting.getMenuItem(null));
		setting.addChangeListener(this);		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void stateChanged(SettingChangeEvent e) 
	{
		if(e.getSource() instanceof PluginInstanceSetting)
		{
			setting.setPrimaryRenderer((RendererInstanceSetting)e.getSource());
		}
		if(e.getSource() instanceof DecoratorListSetting)
		{
			setting.setDecorators((DecoratorListSetting) e.getSource());
		}
		
		if(acceptor instanceof DefaultNodeComponent)
		{
			((DefaultNodeComponent) acceptor).setRendererSetting(setting);
			return;
		}
		acceptor.setRenderer(setting.getRenderer());
		
	}
	
	@Override
	public void removeNotify() 
	{
//		setting.removeChangeListener(this);
	}
}
