package mayday.graphviewer.renderer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import mayday.core.Probe;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.MIGroupSetting;
import mayday.core.structures.graph.Node;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.gradient.ColorGradientSetting;
import mayday.vis3.graph.renderer.RendererTools;
import mayday.vis3.graph.renderer.primary.DefaultComponentRenderer;
import mayday.vis3.graph.vis3.SuperColorProvider;
import mayday.vis3.plots.tagcloud.AbstractTagCloudFactory;
import mayday.vis3.plots.tagcloud.Tag;

public class TagCloudRenderer extends DefaultComponentRenderer
{

	private Font font=new Font(Font.SANS_SERIF,Font.PLAIN,10);

	private MIGroupSetting miGroupSetting;
	private ColorGradientSetting fcGradient=new ColorGradientSetting("Abundance Gradient", null, ColorGradient.createDefaultGradient(0, 1 ));

	private boolean miGroupSet=false;

	public void setColorProvider(SuperColorProvider colorProvider) 
	{
		if(colorProvider==null)
			return; 
		if(!miGroupSet)
		{
			miGroupSetting=new MIGroupSetting("Tag Source", null, null,
					colorProvider.getViewModel().getDataSet().getMIManager(), false);
			miGroupSet=true;
		}
	}

	@Override
	public void drawProbe(Graphics2D g, Rectangle bounds, String label,
			boolean selected, Probe value) {
		List<Probe> probes=new ArrayList<Probe>();
		probes.add(value);
		drawProbes(g, bounds, label, selected, probes);
	}

	@Override
	public void drawProbes(Graphics2D g, Rectangle bounds, String label,
			boolean selected, Iterable<Probe> value) 
	{
		g.setBackground(Color.white);
		g.clearRect(0, 0, bounds.width, bounds.height);
		Font b=g.getFont();
		g.setFont(font);
		if(miGroupSet){
			List<Tag> tags=new ArrayList<Tag>();
			if(miGroupSetting.getMIGroup().getMIOType().equals("PAS.MIO.StringList"))
				tags= AbstractTagCloudFactory.getLinearInstance().StringByStringListMIO(value, miGroupSetting.getMIGroup());
			else
				tags= AbstractTagCloudFactory.getLinearInstance().MIOByFrequency(value, miGroupSetting.getMIGroup());
			Collections.sort(tags);
			Collections.reverse(tags);
//			System.out.println(tags);
			
			int t=1;
			for(Tag tag: tags)
			{
				g.setColor(fcGradient.getColorGradient().mapValueToColor(tag.getFrequency()));
				g.drawString(t+" "+tag.getTag().toString(), bounds.x+3, bounds.y+(t*12) );
				++t;
				if( t*12 > bounds.height )
					break;
			}
		}else
		{
			g.drawString("No Tag Source", bounds.x+5, bounds.y+20);
		}

		g.setFont(b);
		RendererTools.drawBox(g, bounds, selected);


	}

	@Override
	public Setting getSetting() 
	{
		HierarchicalSetting setting=new HierarchicalSetting("Tag Cloud Renderer Setting");
		if(miGroupSetting!=null  )
			setting.addSetting(miGroupSetting);
		setting.addSetting(fcGradient);
		return setting;
		//		return null;
	}

	@Override
	public String getRendererStatus() 
	{
		return "Tag Source: "+miGroupSetting.getStringValue();
	}
	
	@Override
	public Dimension getSuggestedSize(Node node, Object value) {
		return getSuggestedSize();
	}
	
	@Override
	public Dimension getSuggestedSize() {
		return new Dimension(300,120);
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Renderer.TagList",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Render information about probes as list of most important tags",
				"Tag List Renderer"				
		);
		pli.addCategory(GROUP_PRIMARY);
		return pli;	
	}


}
