package mayday.graphviewer.illumina;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mayday.core.Probe;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.genetics.basic.Strand;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.graph.renderer.RendererTools;
import mayday.vis3.graph.renderer.primary.DefaultComponentRenderer;
import mayday.vis3.graph.renderer.primary.PieChartRenderer;
import mayday.vis3.graph.vis3.SuperColorProvider;

public class ExonRenderer extends DefaultComponentRenderer 
{
	private HierarchicalSetting setting=new HierarchicalSetting("Exon Renderer");

	private static final String[] TEXT_OPTIONS={"Number of gene models","Names of gene Models","Length","Begin - End", "none"};

	private BooleanHierarchicalSetting useGradient=new BooleanHierarchicalSetting("Use Gradient to indicate direction", null, false);
	private RestrictedStringSetting textSetting=new RestrictedStringSetting("Show Text", null, 0, TEXT_OPTIONS);

	private BooleanSetting usePieChart=new BooleanSetting("Show Pie Chart" , null, false);
	private BooleanSetting useHeatMap=new BooleanSetting("Show HeatMap" , null, false);


	private ColorSetting fivePrimeColor=new ColorSetting("5' color", null, Color.LIGHT_GRAY);
	private ColorSetting threePrimeColor=new ColorSetting("3' color", null, Color.WHITE);

	private Font font;
	private SuperColorProvider colorProvider;
	private PieChartRenderer pieChartRenderer=new PieChartRenderer();

	private ColorGradient exonGradient; 

	public ExonRenderer(SuperColorProvider colorProvider) 
	{
		this();
		this.colorProvider=colorProvider;
	}

	public ExonRenderer() 
	{
		font=new Font(Font.SANS_SERIF,Font.PLAIN,10);
		useGradient.addSetting(fivePrimeColor).addSetting(threePrimeColor);		
		setting.addSetting(textSetting).addSetting(useGradient);	
		setting.addSetting(usePieChart).addSetting(useHeatMap);
	}

	public ExonRenderer(boolean b) 
	{
		font=new Font(Font.SANS_SERIF,Font.PLAIN,10);
		useGradient.setBooleanValue(b);
		useGradient.addSetting(fivePrimeColor).addSetting(threePrimeColor);		
		setting.addSetting(textSetting).addSetting(useGradient);		
	}

	@Override
	public void draw(Graphics2D g, Node node, Rectangle bounds, Object value, String label, boolean selected) 
	{
		MultiProbeNode dn=(MultiProbeNode)node;		
		// find the text
		String text="";
		switch(textSetting.getSelectedIndex())
		{
		case 0:
			if(dn.hasProperty(GeneticNode.MODELS))
				text=""+dn.getPropertyValue(GeneticNode.MODELS).split(",").length;
			break;
		case 1:	text=""+dn.getPropertyValue(GeneticNode.MODELS);
		break;
		case 2:	text=""+(Long.parseLong(dn.getPropertyValue(GeneticNode.TO)) - Long.parseLong(dn.getPropertyValue(GeneticNode.FROM)));
		break;
		case 3:	text=dn.getName();
		break;
		}

		Paint p=g.getPaint();
		if(dn.hasProperty(GeneModelRoles.END_PROPERTY))
		{

			if(dn.getPropertyValue(GeneticNode.STRAND).equals(Strand.PLUS.toString()))
			{
				drawPointyBoxPlus(g, bounds, selected);
			}else
			{
				if(dn.getPropertyValue(GeneticNode.STRAND).equals(Strand.MINUS.toString()))
				{
					drawPointyBoxMinus(g, bounds, selected);

				}else
				{
					// something funny: 
					RendererTools.fill(g, bounds, Color.white);
					RendererTools.drawBox(g, bounds, selected);
				}
			}
		}else
		{
			//			
			if(useGradient.getBooleanValue())
			{
				if(dn.getPropertyValue(GeneticNode.STRAND).equals(Strand.PLUS.toString()))
				{
					g.setPaint(new GradientPaint(
							bounds.x, bounds.y+bounds.height/2,
							fivePrimeColor.getColorValue(),
							bounds.x+bounds.width, bounds.y+bounds.height/2,
							threePrimeColor.getColorValue()));
				}
				if(dn.getPropertyValue(GeneticNode.STRAND).equals(Strand.MINUS.toString()))
				{
					g.setPaint(new GradientPaint(
							bounds.x+bounds.width, bounds.y+bounds.height/2,
							fivePrimeColor.getColorValue(),
							bounds.x, bounds.y+bounds.height/2,
							threePrimeColor.getColorValue()));
				}
				g.fill(bounds);

			}
			else
			{
				RendererTools.fill(g, bounds, Color.white);
			}


			RendererTools.drawBox(g, bounds, selected);
		}
		g.setPaint(p);
		if(text!=null && !text.isEmpty())
		{
			if(bounds.width > 10)
				RendererTools.drawBreakingString(g, font, text, bounds.width-10, 5, 5);
		}
		if(usePieChart.getBooleanValue())
		{
			double mc=dn.getPropertyValue(GeneticNode.MODELS).split(",").length;
			double mt=Integer.parseInt(dn.getPropertyValue(GeneticNode.TOTAL_MODELS));			
			pieChartRenderer.drawDouble(g, bounds, "", selected, mc/mt);
		}
		if(useHeatMap.getBooleanValue() && colorProvider!=null)
		{
			List<Probe> geneModelProbes=new ArrayList<Probe>(); 
			GeneticNode gn=(GeneticNode) dn;
			for(Probe pr: dn.getProbes())
			{
				{
					geneModelProbes.add(pr);
				}
			}
			drawColorBoxes(geneModelProbes, new Rectangle(0,0,bounds.width, (bounds.height/2)-1), g);
			List<Color> cols=new ArrayList<Color>();
			if(gn.getExonValues()!=null )
				for(double d:gn.getExonValues())
				{
					cols.add(exonGradient.mapValueToColor(d));
				}
			RendererTools.drawColorLine(g, cols , new Rectangle(0,(bounds.height/2)+1,bounds.width, (bounds.height/2)-1));
		}
	}

	private void drawColorBoxes(List<Probe> probes, Rectangle bounds, Graphics2D g)
	{
		List<Color> colors;		
		try {
			int pc=0;
			for(@SuppressWarnings("unused") Probe p:probes)
				pc++;

			colors = colorProvider.getColors(probes.iterator().next());

			//				int dx=(int) (bounds.getWidth() / colors.size());
			int dy=(int) (bounds.getHeight() / pc);

			int j=0;
			for(Probe p:probes)
			{					
				colors = colorProvider.getColors(p);
				Rectangle r=new Rectangle(bounds.x, bounds.y+j*dy,bounds.width,dy);
				RendererTools.drawColorLine(g, colors, r);
				j++;
			}			
		} catch (Exception e) 
		{
			g.setColor(Color.white);
			g.fillRect(0, 0,(int)bounds.getWidth(), (int)bounds.getHeight());
		}

	}

	private void drawPointyBoxPlus(Graphics2D g, Rectangle bounds, boolean selected)
	{
		Polygon p=new Polygon();

		int h=(int) ((bounds.getHeight()/2.0)*Math.sqrt(3));
		p.addPoint(bounds.x, bounds.y);
		p.addPoint(bounds.x+bounds.width-h, bounds.y);
		p.addPoint(bounds.x+bounds.width, bounds.y+ bounds.height/2);
		p.addPoint(bounds.x+bounds.width-h, bounds.y+ bounds.height-1);
		p.addPoint(bounds.x, bounds.y+ bounds.height-1);
		p.addPoint(bounds.x, bounds.y);


		if(useGradient.getBooleanValue())
		{
			g.setPaint(new GradientPaint(
					bounds.x, bounds.y+bounds.height/2,
					fivePrimeColor.getColorValue(),
					bounds.x+bounds.width, bounds.y+bounds.height/2,
					threePrimeColor.getColorValue()));
		}
		else
		{
			g.setColor(Color.white);
		}		
		g.fill(p);
		g.setColor(Color.black);
		if(selected)
			g.setColor(Color.red);
		g.draw(p);
	}

	private void drawPointyBoxMinus(Graphics2D g, Rectangle bounds, boolean selected)
	{
		Polygon p=new Polygon();

		int h=(int) ((bounds.getHeight()/2.0)*Math.sqrt(3));

		p.addPoint(bounds.x, bounds.y+ bounds.height/2);
		p.addPoint(bounds.x+h, bounds.y);
		p.addPoint(bounds.x+bounds.width-1, bounds.y);
		p.addPoint(bounds.x+bounds.width-1, bounds.y+bounds.height-1);
		p.addPoint(bounds.x+h, bounds.y+bounds.height-1);
		p.addPoint(bounds.x, bounds.y+ bounds.height/2);

		if(useGradient.getBooleanValue())
		{
			g.setPaint(new GradientPaint(
					bounds.x+bounds.width, bounds.y+bounds.height/2,
					fivePrimeColor.getColorValue(),
					bounds.x, bounds.y+bounds.height/2,
					threePrimeColor.getColorValue()));
		}
		else
		{
			g.setColor(Color.white);
		}
		g.fill(p);
		g.setColor(Color.black);
		if(selected)
			g.setColor(Color.red);
		g.draw(p);
	}


	@Override
	public Dimension getSuggestedSize() 
	{
		return new Dimension(80,40);
	}

	@Override
	public Setting getSetting() 
	{
		return setting;
	}

	public SuperColorProvider getColorProvider() 
	{
		return colorProvider;
	}

	public void setColorProvider(SuperColorProvider colorProvider) 
	{
		this.colorProvider = colorProvider;
		exonGradient=ColorGradient.createDefaultGradient(0, 1);
	}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Renderer.Exon",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Renderer for exons",
				"Exon renderer"				
		);
		pli.addCategory(GROUP_PRIMARY);
		return pli;	
	}
}
