package mayday.graphviewer.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.structures.graph.Node;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.gradient.ColorGradientSetting;
import mayday.vis3.graph.renderer.RendererTools;
import mayday.vis3.graph.renderer.primary.CircleRenderer;
import mayday.vis3.graph.vis3.SuperColorProvider;

public class CentralityRenderer extends CircleRenderer
{
	private ColorGradient gradient;
	private ColorGradientSetting gradientSetting;
	private HierarchicalSetting setting; 
	private DoubleSetting maxValue;
	@Override
	public Setting getSetting() 
	{
		if(setting==null)
		{
		
			gradient= ColorGradient.createDefaultGradient(0,1);
			gradientSetting=new ColorGradientSetting("Color Gradient", null, gradient);
			setting=new HierarchicalSetting("Centrality Renderer");
			maxValue=new DoubleSetting("Maximum Value (for rendering)", null, 1.0,0.0,1.0,true,true);			
			
			setting.addSetting(gradientSetting).addSetting(maxValue);
			setting.addChangeListener(new SettingChangeListener() {
				
				@Override
				public void stateChanged(SettingChangeEvent e) {
					gradient= gradientSetting.getColorGradient();	
					gradient.setMax(maxValue.getDoubleValue());
					gradient.setMin(0);
				}
			});
		}
		return setting;
	}


	public void draw(Graphics2D g, Node node, Rectangle bounds, Object value,String label, boolean selected) 
	{
		int r=Math.min(bounds.width, bounds.height)-1;
		int d=node.getDegree();
		int n= node.getGraph().nodeCount(); 
		double cd= ((double)d)/((double)(n-1));
		
		Ellipse2D p=new Ellipse2D.Double((bounds.width-r)/2, 0,r, r);	
		if(n==0)
		{
			g.setColor(selected?Color.red:Color.blue);
			g.draw(p);
		}else
		{	if(cd > maxValue.getDoubleValue())
				cd=maxValue.getDoubleValue();
			Color c=gradient.mapValueToColor(cd);
			g.setColor(c);	
			g.fill(p);
			RendererTools.drawEllipse(g, bounds, selected);
			
			if(selected)
			{
				c=RendererTools.invertColor(c);
				g.draw(p);
			}
		}
	}
	
	@Override
	public void setColorProvider(SuperColorProvider colorProvider) 
	{
		getSetting();
	}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Renderer.NodeCentrality",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Render nodes as circle, color by degree centrality",
				"Degree Centrality Renderer"				
		);
		pli.addCategory(GROUP_PRIMARY);
		return pli;	
	}
}
