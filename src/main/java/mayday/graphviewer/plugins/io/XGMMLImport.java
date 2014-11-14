package mayday.graphviewer.plugins.io;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.typed.MappingSourceSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.io.GraphMLExport;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.graph.nodes.Nodes;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.datasources.xgmml.XGMMLParser;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class XGMMLImport  extends AbstractGraphImportPlugin
{
	private PathSetting file=new PathSetting("XGMML file", "The graph file to be imported", null, false, true, false);
	private MappingSourceSetting mapping;
	
	@Override
	protected String getFormat() 
	{
		return "XGMML";
	}
	
	@Override
	protected Graph importFile(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) throws Exception {
		XGMMLParser parser=new XGMMLParser();
		
		Map<String, Probe> probeMap=new HashMap<String,Probe>();
		for(Probe p:canvas.getModelHub().getViewModel().getDataSet().getMasterTable().getProbes().values())
		{
			probeMap.put(mapping.mappedName(p), p);
		}
		
		try 
		{
			List<Graph> graphs=parser.parse(file.getStringValue());
			ProbeList ap=canvas.getModelHub().getAddedProbes();
			for(Graph g:graphs)
			{
				for(Node n: g.getNodes())
				{
					if(probeMap.containsKey(n.getName()))
					{
						Probe p=probeMap.get(n.getName());
						((MultiProbeNode)n).addProbe(p);
						n.setRole(Nodes.Roles.PROBE_ROLE);
						if(!ap.contains(p))								
							ap.addProbe(p);							
					}else
					{
						DefaultNode dn=(DefaultNode)n;
						if(dn.hasProperty("descAnnot") && dn.getPropertyValue("descAnnot")!=null)
						{
							String[] tok=dn.getPropertyValue("descAnnot").split(" ");
							for(String t:tok)
							{
								if(probeMap.containsKey(t))
								{
									Probe p=probeMap.get(t);
									if(p==null)
										continue;
									((MultiProbeNode)n).addProbe(p);
									n.setRole(Nodes.Roles.PROBE_ROLE);
									if(!ap.contains(p))								
										ap.addProbe(p);							
								}
							}
						}
						
					}
				}	
			
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
				return g;	
			}	

			
		} catch (Exception e) 
		{
			throw new RuntimeException("Error reading file", e);
		}
		return null;
		
	}
		
	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.XGMMLImport",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Import a graph from a XGMML file",
				"XGMML"				
		);
		pli.addCategory(IMPORT_CATEGORY);
		return pli;	
	}
}
