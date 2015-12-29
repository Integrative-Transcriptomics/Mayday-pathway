package mayday.graphviewer.TimeSeriesBitmap;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mayday.core.Probe;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.structures.graph.Node;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.gradient.ColorGradientSetting;
import mayday.vis3.graph.renderer.RendererTools;
import mayday.vis3.graph.renderer.primary.DefaultComponentRenderer;
import mayday.vis3.graph.vis3.SuperColorProvider;



public class TimeSeriesBitmapRenderer extends DefaultComponentRenderer
{
	private SaxEncoding encoding=new SaxEncoding(4);
	private IntSetting windowLength=new IntSetting("Window Length", "Use 0 for whole probe", 10);
	private IntSetting symbolsPerWindow=new IntSetting("Symbols per Window", "Use 0 for window length", 2);
	private IntSetting substringLengthSetting=new IntSetting("Substring Length", null, 2);

	private ColorGradient gradient=ColorGradient.createDefaultGradient(0, 1);
	private ColorGradientSetting gradientSetting=new ColorGradientSetting("Color Gradient",null, gradient);
	
	private SuperColorProvider coloring; 
	
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

		List<Double> values=new ArrayList<Double>(value.length);
		for(double d: value)
		{
			values.add(d);
		}		
		draw(g,selected,bounds,values);
	}
	

	/* (non-Javadoc)
	 * @see mayday.vis3.graph.renderer.DefaultComponentRenderer#drawProbe(java.awt.Graphics2D, java.awt.Rectangle, java.lang.String, boolean, mayday.core.Probe)
	 */
	@Override
	public void drawProbe(Graphics2D g, Rectangle bounds, String label,	boolean selected, Probe value) 
	{		
		drawDouble(g, bounds, label, selected, value.getValues());
	}

	/* (non-Javadoc)
	 * @see mayday.vis3.graph.renderer.DefaultComponentRenderer#drawProbes(java.awt.Graphics2D, java.awt.Rectangle, java.lang.String, boolean, java.lang.Iterable)
	 */
	@Override
	public void drawProbes(Graphics2D g, Rectangle bounds, String label,boolean selected, Iterable<Probe> value) 
	{		
		List<Double> v= coloring.getProbesMeanValue(value);		
		draw(g,selected,  bounds, v);
	}
	
	private void draw(Graphics2D g, boolean selected, Rectangle bounds,List<Double> values)
	{
		int N=windowLength.getIntValue();
		if( N==0)
			N=values.size();
		int n= symbolsPerWindow.getIntValue();
		if( n==0)
		{
			n=N;
		}
		encoding.encodeTimeSeries(values, N, n);
		List<Character> alphabet=encoding.getAlphabet();
		SubstringMap map=new SubstringMap(alphabet, substringLengthSetting.getIntValue());
		map.addSaxEncodedString(encoding);
		int c=map.mapSize();
		int rowH=bounds.height/(c);
		// bring map to visibility:
		for(int i=0; i!= c; ++i){
			List<Color> colors=new ArrayList<Color>();
			for(int j=0; j!= c; ++j){
				colors.add(gradient.mapValueToColor(map.getScaledCount(i, j)));
			}
			RendererTools.drawColorLine(g, colors, new Rectangle(bounds.x, bounds.y+(i*rowH), bounds.width, rowH));
		}
		
		RendererTools.drawBox(g,bounds,selected);
		
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
				"PAS.GraphViewer.Renderer.TSBitmapRenderer",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Time Series Bitmap",
				"Time Series Bitmap"				
		);
		pli.addCategory(GROUP_PRIMARY);
		return pli;	
	}

	@Override
	public void setColorProvider(SuperColorProvider coloring) 
	{
		this.coloring=coloring;
	}

	@Override
	public Setting getSetting() 
	{
		HierarchicalSetting setting=new HierarchicalSetting("Time Series Bitmap Renderer");
		setting.addSetting(windowLength).addSetting(symbolsPerWindow).addSetting(substringLengthSetting).addSetting(gradientSetting);
		return setting;
	}
	
	
}
