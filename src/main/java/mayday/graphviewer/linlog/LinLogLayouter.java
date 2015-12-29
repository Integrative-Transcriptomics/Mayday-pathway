package mayday.graphviewer.linlog;

import java.awt.Container;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.pathway.viewer.canvas.AlignLayouter;
import mayday.vis3.graph.layout.CanvasLayouterPlugin;
import mayday.vis3.graph.model.GraphModel;

public class LinLogLayouter extends CanvasLayouterPlugin 
{

	private static final String[] minimizers=new String[]{"Barnes-Hut","Classic"};
	
	private IntSetting iterations=new IntSetting("Iterations", null, 100);
	private RestrictedStringSetting minimizer=new RestrictedStringSetting("Minimizer", 
			"The minimization strategy to be used.\n" +
			"\"Energy Models for Graph Clustering\",  Journal of Graph Algorithms\n" +
			"and Applications, Vol. 11, no. 2, pp. 453-480, 2007.  http://jgaa.info/", 0, minimizers);
	
	
	@Override
	protected void initSetting() 
	{
		setting.addSetting(iterations).addSetting(minimizer);
	}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Layout.LinLog",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"LinLog Layout by Noack et al.",
				"LinLog Layout"				
		);
		return pli;	
	}

	@Override
	public void layout(Container container, Rectangle bounds, GraphModel model) 
	{
		Graph g=model.getGraph();
		if(g.edgeCount()==0)
		{
			JOptionPane.showMessageDialog(container, "LinLog layout only works on connected graphs.", "LinLog layout",JOptionPane.ERROR_MESSAGE);
			return;
		}
		// produce a LinLogLayout graph (i.e. a map of maps)
		Map<Node,String> names=LinLogWrapper.buildNames(g.getNodes());
		Map<String,Map<String,Double>> graph=LinLogWrapper.buildLLGraph(g, names);
		
		// LinLogLayout way of building the layout:
		Map<String, double[]> pos=LinLogWrapper.position(graph,minimizer.getSelectedIndex(),iterations.getIntValue());
		// place nodes according to calculated positions and bounds
		
		double minX=Double.MAX_VALUE;
		double maxX=Double.MIN_VALUE;
		
		double minY=Double.MAX_VALUE;
		double maxY=Double.MIN_VALUE;
		
		for(double[] np: pos.values())
		{
			if(np[0] > maxX)
				maxX=np[0];
			if(np[0] < minX)
				minX=np[0];
			
			if(np[1] > maxY)
				maxY=np[1];
			if(np[1] < minY)
				minY=np[1];
		}
			
		
		for(Node n: g.getNodes())
		{
			double[] np=pos.get(names.get(n));
			model.getComponent(n).setLocation((int)((np[0]/maxX)*bounds.width) , (int)((np[1]/maxY)*bounds.height));			
		}
		
		new AlignLayouter(10).layout(container, bounds, model);
		
	}
	

	
	

}
