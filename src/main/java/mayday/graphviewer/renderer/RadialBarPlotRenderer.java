package mayday.graphviewer.renderer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Arc2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import mayday.core.Probe;
import mayday.core.gui.GUIUtilities;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Node;
import mayday.vis3.graph.renderer.primary.DefaultComponentRenderer;
import mayday.vis3.graph.vis3.SuperColorProvider;

public class RadialBarPlotRenderer  extends DefaultComponentRenderer
{
	private SuperColorProvider colorProvider;
	
	public RadialBarPlotRenderer() 
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
		// calculate max
		double min=Double.MAX_VALUE;
		double max=Double.MIN_VALUE;

		for(int i=0; i!= value.length; ++i)
		{
			if(value[i] < min)
				min=value[i];
			if(value[i] > max)
				max=value[i];
		}		
		drawPetals(g,Arrays.asList(GUIUtilities.rainbow(value.length, 1)), bounds, selected, value, min, max);	
		drawBounds(g, bounds, selected);
	}


	private final static void drawPetals(Graphics2D g, List<Color> colors, Rectangle bounds, boolean selected, double[] values, double min, double max)
	{
		final int wh=Math.min(bounds.width, bounds.height)-2;
		final double range=max-min;	
		double aStep=(360.0) / (values.length*1.0);
		double aSofar= 0;//- Math.PI/2;
		
		double il=wh/2.0;	
		g.fillOval((int) il, (int)(il), 3, 3);
		
		for(int i=0; i!= values.length; ++i)
		{
			// find radius of point
			double yp= (( values[i]-min)/range)*il;
			
			Arc2D arc=new Arc2D.Double(
					il - yp,
					il - yp,					
					2*yp,
					2*yp,
					aSofar,
					aStep,
					Arc2D.PIE);

			g.setColor(colors.get(i));
			g.fill(arc);
			if(selected)
				g.setColor(Color.red);
			else				
				g.setColor(Color.black);
			g.draw(arc);
			aSofar+=aStep;
		}		
	}


	/* (non-Javadoc)
	 * @see mayday.vis3.graph.renderer.DefaultComponentRenderer#drawProbe(java.awt.Graphics2D, java.awt.Rectangle, java.lang.String, boolean, mayday.core.Probe)
	 */
	@Override
	public void drawProbe(Graphics2D g, Rectangle bounds, String label,	boolean selected, Probe value) 
	{		
		double min=value.getMinValue();
		double max=value.getMaxValue();
		drawPetals(g, colorProvider.getColors(value), bounds, selected, value.getValues(), min, max);		
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

		List<Double> v= colorProvider.getProbesMeanValue(value);
		double[] vals=new double[v.size()];
		int i=0; 
		for(double d: v)
		{
			vals[i]=d;
			++i;
		}
		drawPetals(g,colorProvider.getMeanColors(value) , bounds, selected, vals, min, max);		
		
		drawBounds(g, bounds, selected);
	}
	
	private void drawBounds(Graphics2D g, Rectangle bounds, boolean selected)
	{
		final int wh=Math.min(bounds.width, bounds.height)-3;
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
				"PAS.GraphViewer.Renderer.RadialBarPlot",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Radial bar Plot",
				"Radial Bar Plot"				
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
