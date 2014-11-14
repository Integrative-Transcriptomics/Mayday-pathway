package mayday.graphviewer.layout;

import java.awt.Container;
import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.graphviewer.core.Utilities;
import mayday.pathway.viewer.canvas.AlignLayouter;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.layout.CanvasLayouterPlugin;
import mayday.vis3.graph.model.GraphModel;
import att.grappa.Element;
import att.grappa.GraphIterator;
import att.grappa.GrappaConstants;
import att.grappa.Node;
import att.grappa.Parser;

public class DotLayout  extends CanvasLayouterPlugin 
{
	private static final String[] METHODS={"dot", "neato", "twopi", "circo", "fdp", "sfdp"};
	private RestrictedStringSetting layoterSetting;
	private PathSetting pathSetting; 

	private static final String LAST_DOT_PATH="AnacondaLastDotDir";
	
	
	public DotLayout() {
		initSetting();
	}

	@Override
	protected void initSetting() {
		layoterSetting=new RestrictedStringSetting("Layout Method", null, 0, METHODS);
		
		String d= Utilities.prefs.get(LAST_DOT_PATH, "/usr/bin");
		pathSetting=new PathSetting("GraphViz path", null, d, true, true, false);
		
		setting=new HierarchicalSetting("GraphVit Settings");
		setting.addSetting(layoterSetting).addSetting(pathSetting);
	}

	@Override
	public void layout(Container container, Rectangle bounds, GraphModel model) 
	{
		Graph g=model.getGraph();
		
		Utilities.prefs.put(LAST_DOT_PATH, pathSetting.getStringValue());
		
		try{
			File out= File.createTempFile("mgvlayout", "dot");
			String basename=out.getAbsolutePath(); 
			Map<Integer,mayday.core.structures.graph.Node> hashCodeMap=new HashMap<Integer, mayday.core.structures.graph.Node>();
			for(mayday.core.structures.graph.Node n: g.getNodes())
				hashCodeMap.put(n.hashCode(), n);
			
			BufferedWriter w=new BufferedWriter(new FileWriter(out));
			
			w.write("digraph g{\n");
			for(Edge e:g.getEdges())
			{
				w.write("\""+e.getSource().hashCode()+"\" -> \""+e.getTarget().hashCode()+"\";\n");
			}
			w.write("}\n");
			w.close();
			
			
			String cmd=""+pathSetting.getStringValue()+"/"+layoterSetting.getStringValue()+" "+basename;
			Process r=Runtime.getRuntime().exec(cmd);
			
			
			Parser parser;
			parser = new Parser(new InputStreamReader(r.getInputStream()));
			parser.parse();	
			
			att.grappa.Graph graph=parser.getGraph();


			GraphIterator iter=graph.elements(GrappaConstants.NODE);
			while(iter.hasNext())
			{
				Element e= iter.nextGraphElement();
				Node node=(Node)e;
				CanvasComponent cc=model.getComponent(hashCodeMap.get(Integer.parseInt(node.getName())));
				cc.setLocation((int)node.getCenterPoint().x, (int)node.getCenterPoint().y);
			}

			new AlignLayouter(20).layout(container, bounds, model);

		} catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Layout.Dot",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"",
				"GraphViz"				
		);
		return pli;	
	}
}
