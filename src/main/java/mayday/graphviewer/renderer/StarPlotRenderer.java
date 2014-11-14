package mayday.graphviewer.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mayday.core.Probe;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.structures.graph.Node;
import mayday.vis3.graph.renderer.RendererTools;
import mayday.vis3.graph.renderer.primary.DefaultComponentRenderer;
import mayday.vis3.graph.vis3.SuperColorProvider;

public class StarPlotRenderer extends DefaultComponentRenderer
{
	private SuperColorProvider colorProvider;

	private BooleanSetting drawAxesSetting=new BooleanSetting("Draw Axes", null, true);
	public StarPlotRenderer() 
	{

	}

	/* (non-Javadoc)
	 * @see mayday.vis3.graph.renderer.DefaultComponentRenderer#drawDouble(java.awt.Graphics2D, java.awt.Rectangle, java.lang.String, boolean, double[])
	 */
	@Override
	public void drawDouble(Graphics2D g, Rectangle bounds, String label, boolean selected, double... value)
	{	
		if(value.length ==1)
		{
			DefaultComponentRenderer.getDefaultRenderer().drawDouble(g, bounds, label, selected, value);
			return;
		}

		drawAxes(g, bounds, value.length, drawAxesSetting.getBooleanValue());

		// calculate max
		double min=Double.MAX_VALUE;
		double max=Double.MIN_VALUE;

		List<Double> vals=new ArrayList<Double>();
		for(int i=0; i!= value.length; ++i)
		{
			if(value[i] < min)
				min=value[i];
			if(value[i] > max)
				max=value[i];

			vals.add(value[i]);
		}		
		drawStar(g, Color.black, bounds, selected, value, min, max);		
		drawBounds(g, bounds, selected);
	}

	private void drawAxes(Graphics2D g,  Rectangle bounds, int numAxes, boolean drawAll)
	{
		final int wh=Math.min(bounds.width, bounds.height)-2;
		double aStep=(2*Math.PI) / (numAxes*1.0);
		double aSofar= 0;//- Math.PI/2;
		Point2D center=new Point2D.Double(wh/2.0, wh/2.0);
		Point2D pc=new Point2D.Double(1,1); // don't care about this value; 

		g.setColor(Color.black);
		Stroke s=g.getStroke();
		g.setStroke(new BasicStroke(2.0f));
	
		for(int i=0; i!= numAxes; ++i)
		{
			pc= RendererTools.rotate((wh/2)-1,0, aSofar, center );			
			Line2D l=new Line2D.Double(center, pc);
			g.draw(l);				
			if(i==0)
			{
				g.setStroke(new BasicStroke(0.5f));
			}			
			aSofar+=aStep;
		}
		g.setStroke(s);
	}

	private final static void drawStar(Graphics2D g, Color c, Rectangle bounds, boolean Selected, double[] values, double min, double max)
	{
		g.setColor(c);
		final int wh=Math.min(bounds.width, bounds.height)-2;
		final double range=max-min;	
		double aStep=(2*Math.PI) / (values.length*1.0);
		double aSofar= 0;//- Math.PI/2;

		double il=wh/2.0;		
		Point2D center=new Point2D.Double(wh/2.0, wh/2.0);

		double yf= (( values[0]-min)/range)*il;
		Point2D pf= RendererTools.rotate(yf,0, aSofar, center );
		Point2D pl=pf;
		Point2D pc=new Point2D.Double(pl.getX(),pl.getY()); // don't care about this value; 
		aSofar+=aStep;
		for(int i=1; i!= values.length; ++i)
		{
			// find radius of point
			double yp= (( values[i]-min)/range)*il;
			pc= RendererTools.rotate(yp,0, aSofar, center );				
			g.drawLine((int)pl.getX(), (int)pl.getY(), (int)pc.getX(), (int)pc.getY());

			pl=pc;
			aSofar+=aStep;
		}
		g.drawLine((int)pl.getX(), (int)pl.getY(), (int)pf.getX(), (int)pf.getY());
	}


	/* (non-Javadoc)
	 * @see mayday.vis3.graph.renderer.DefaultComponentRenderer#drawProbe(java.awt.Graphics2D, java.awt.Rectangle, java.lang.String, boolean, mayday.core.Probe)
	 */
	@Override
	public void drawProbe(Graphics2D g, Rectangle bounds, String label,	boolean selected, Probe value) 
	{		
		double min=value.getMinValue();
		double max=value.getMaxValue();

		drawAxes(g, bounds, value.getNumberOfExperiments(), drawAxesSetting.getBooleanValue());
		drawStar(g,colorProvider.getColor(value) , bounds, selected, value.getValues(), min, max);	
		
		drawBounds(g, bounds, selected);
	}

	/* (non-Javadoc)
	 * @see mayday.vis3.graph.renderer.DefaultComponentRenderer#drawProbes(java.awt.Graphics2D, java.awt.Rectangle, java.lang.String, boolean, java.lang.Iterable)
	 */
	@Override
	public void drawProbes(Graphics2D g, Rectangle bounds, String label,boolean selected, Iterable<Probe> value) 
	{
		double min=colorProvider.minimum();
		double max=colorProvider.maximum();

		int numExp=value.iterator().next().getNumberOfExperiments();
		drawAxes(g, bounds, numExp, drawAxesSetting.getBooleanValue());
		for(Probe p: value)
		{
			drawStar(g,colorProvider.getColor(p) , bounds, selected, p.getValues(), min, max);		
		}
		drawBounds(g, bounds, selected);
	}
	
	private void drawBounds(Graphics2D g, Rectangle bounds, boolean selected)
	{
		final int wh=Math.min(bounds.width, bounds.height)-2;
		if(selected)
			g.setColor(Color.red);
		else
			g.setColor(Color.black);
		g.setStroke(new BasicStroke(2.0f));		
		g.drawOval(1, 1,wh-1,wh-1);
	}

	public Dimension getSuggestedSize() 
	{
		return new Dimension(60,60);
	}
	
	@Override
	public Dimension getSuggestedSize(Node node, Object value) 
	{
		return new Dimension(60,60);
	}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Renderer.StarPlot",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Star Plot",
				"Star Plot"				
		);
		pli.addCategory(GROUP_PRIMARY);
		return pli;	
	}

	public SuperColorProvider getColorProvider() {
		return colorProvider;
	}

	public void setColorProvider(SuperColorProvider colorProvider) {
		this.colorProvider = colorProvider;
	}

	@Override
	public String getRendererStatus() 
	{
		return "color: "+colorProvider.getSourceName();
	}
}
