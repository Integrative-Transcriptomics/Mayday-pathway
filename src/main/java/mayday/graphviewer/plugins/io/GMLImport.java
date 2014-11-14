package mayday.graphviewer.plugins.io;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.io.GraphMLExport;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.graph.nodes.Nodes;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.datasources.gml.GMLParser;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class GMLImport extends AbstractGraphImportPlugin
{
	@Override
	protected String getFormat() {
		return "GML";
	}

	@Override
	protected Graph importFile(GraphViewerPlot canvas, GraphModel model,List<CanvasComponent> components) throws Exception {
		GMLParser parser=new GMLParser();

		List<Graph> graphs=parser.parse(fileSetting.getStringValue());
		ProbeList ap=canvas.getModelHub().getAddedProbes();
		for(Graph g:graphs)
		{
			for(Node n: g.getNodes())
			{
				for(Probe p:canvas.getModelHub().getViewModel().getDataSet().getMasterTable().getProbes().values())
				{
					String m=mapping.mappedName(p);
					if(n==null)
						continue;
					if(n.getName().equals(m))
					{
						((MultiProbeNode)n).addProbe(p);
						n.setRole(Nodes.Roles.PROBE_ROLE);
						if(!ap.contains(p))								
							ap.addProbe(p);
					}
				}
			}				
			SuperModel sm=new SuperModel(g);
			canvas.setModel(sm);

			hasLayout=false;
			for(Node n:g.getNodes())
			{
				if(! (n instanceof DefaultNode))
				{
					continue;
				}
				DefaultNode dn=(DefaultNode)n;
				if(dn.hasProperty(GraphMLExport.GEOMETRY_KEY))
				{
					hasLayout=true;
					Rectangle r=GraphMLExport.parseRectangle(dn.getPropertyValue(GraphMLExport.GEOMETRY_KEY));
					canvas.getModel().getComponent(n).setBounds(r);
				}
			}
			if(!hasLayout)
			{
				canvas.updateLayout();
			}

			// import one graph only. 	
			return g;
		}
		return null;		
	}


//	@Override
//	public void run(final GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
//	{
//		HierarchicalSetting setting=new HierarchicalSetting("GML Import");
//		mapping=new MappingSourceSetting(canvas.getModelHub().getViewModel().getDataSet());
//		setting.addSetting(gmlFile).addSetting(mapping);
//
//		SettingsDialog settingDialog=new SettingsDialog(null, "GML Import", new Settings(setting, getPluginInfo().getPreferences()));
//		settingDialog.setModal(true);
//		settingDialog.setVisible(true);
//
//		if(!settingDialog.closedWithOK())
//			return;
//
//		GMLParser parser=new GMLParser();
//
//		try 
//		{
//			List<Graph> graphs=parser.parse(gmlFile.getStringValue());
//			ProbeList ap=canvas.getModelHub().getAddedProbes();
//			for(Graph g:graphs)
//			{
//				for(Node n: g.getNodes())
//				{
//					for(Probe p:canvas.getModelHub().getViewModel().getDataSet().getMasterTable().getProbes().values())
//					{
//						String m=mapping.mappedName(p);
//						if(n==null)
//							continue;
//						if(n.getName().equals(m))
//						{
//							((MultiProbeNode)n).addProbe(p);
//							n.setRole(Nodes.Roles.PROBE_ROLE);
//							if(!ap.contains(p))								
//								ap.addProbe(p);
//						}
//					}
//				}				
//				SuperModel sm=new SuperModel(g);
//				canvas.setModel(sm);
//
//				boolean hasLayout=false;
//				for(Node n:g.getNodes())
//				{
//					if(! (n instanceof DefaultNode))
//					{
//						continue;
//					}
//					DefaultNode dn=(DefaultNode)n;
//					if(dn.hasProperty(GraphMLExport.GEOMETRY_KEY))
//					{
//						hasLayout=true;
//						Rectangle r=GraphMLExport.parseRectangle(dn.getPropertyValue(GraphMLExport.GEOMETRY_KEY));
//						canvas.getModel().getComponent(n).setBounds(r);
//					}
//				}
//				if(!hasLayout)
//				{
//					canvas.updateLayout();
//				}
//
//				break; // import one graph only. 				
//			}	
//
//
//		} catch (Exception e) 
//		{
//			throw new RuntimeException("Error reading file", e);
//		}
//
//	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.GMLImport",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Import a graph from a GML file",
				"GML"				
		);
		pli.addCategory(IMPORT_CATEGORY);
		return pli;	
	}
}
