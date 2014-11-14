package mayday.graphviewer.plugins.io;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Graph;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.datasources.graphml.GraphMLGraph;
import mayday.graphviewer.datasources.graphml.GraphMLIO;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;

public class LoadGraphML  extends AbstractGraphImportPlugin 
{

	@Override
	protected String getFormat()
	{
		return "GraphML";
	}
	
	@Override
	protected Graph importFile(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) throws Exception {
		List<GraphMLGraph> graphs=GraphMLIO.importGraphML2(new File(fileSetting.getStringValue()), 
				canvas.getModelHub().getViewModel().getDataSet().getMasterTable().getProbes().values());
		GraphMLGraph gr=graphs.get(0);
		Graph g=gr.graph;
		return g;
	}

	
//	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
//	{
//		PathSetting filePath=new PathSetting("File Name", null, "", false, true, false);
//		BooleanSetting clearGraph=new BooleanSetting("Clear Graph", "Remove any other components", true);
//		HierarchicalSetting setting=new HierarchicalSetting("GraphML Import");
//
//		setting.addSetting(filePath).addSetting(clearGraph);
//		Settings settings=new Settings(setting, PluginInfo.getPreferences("PAS.GraphViewer.LoadGraphML"));
//		SettingsDialog dialog=new SettingsDialog(null, "GraphML Import", settings);
//		dialog.setModal(true);
//		dialog.setVisible(true);
//		Pattern probesSplitPattern=Pattern.compile("^\\\"|\\\"$|\\\",\\\"");
//		if(dialog.closedWithOK())
//		{
//			try 
//			{
//				//				List<Graph> graphs=GraphMLImport.importGraphML(fileChooser.getSelectedFile(), canvas.getViewModel().getProbes());
//				//				Graph g=graphs.get(0);
//				SuperModel sm=(SuperModel)model;
//
//				//				canvas.setModel(new SuperModel(g));
//
//				List<GraphMLGraph> graphs=GraphMLIO.importGraphML2(new File(filePath.getStringValue()), 
//						canvas.getModelHub().getViewModel().getDataSet().getMasterTable().getProbes().values());
//				GraphMLGraph gr=graphs.get(0);
//				Graph g=gr.graph;
//				int y=25;
//				if(clearGraph.getBooleanValue())
//				{
//					model.clearAll();
//				}
//				else
//					y=canvas.getComponentMaxY()+25;
//
//				MultiHashMap<DataSet,Probe> foundProbes=new MultiHashMap<DataSet, Probe>();
//				
//				for(Node n:g.getNodes())
//				{
//					if(! (n instanceof DefaultNode))
//					{
//						continue;
//					}
//					DefaultNode dn=(DefaultNode)n;
//
//					if(((MultiProbeNode)dn).getProbes().isEmpty())
//					{
//						if(dn.hasProperty(GraphMLExport.PROBES_KEY))
//						{
//							String p=dn.getPropertyValue(GraphMLExport.PROBES_KEY);
//							String[] ps=probesSplitPattern.split(p);
//							for(int i=1; i!= ps.length; ++i)// skip first empty element!;
//							{
//								if(ps[i].contains("$"))
//								{
//									String[] dspl=ps[i].split("\\$");
//									for(DataSet ds: canvas.getModelHub().getAllAvailableDataSets())
//									{
//										if(ds.getName().equals(dspl[0]))
//										{
//											Probe pr=ds.getMasterTable().getProbe(dspl[1]);
//											if(pr!=null)
//											{
//												foundProbes.put(ds, pr);
//												((MultiProbeNode)dn).addProbe(pr);
//											}
//											
//										}									
//									}
//								}
//								
//							}
//							dn.getProperties().remove(GraphMLExport.PROBES_KEY);
//						}else
//						{
//							List<Probe> probes=new ArrayList<Probe>();
//							for(Probe probe: canvas.getModelHub().getViewModel().getDataSet().getMasterTable().getProbes().values())
//							{
//								if(probe.getDisplayName().equals(dn.getName()))
//								{
//									probes.add(probe);										
//								}
////								if(! probes.isEmpty())
////								{								
////									((MultiProbeNode)dn).setProbes(probes);
////									dn.setRole(Nodes.Roles.PROBES_ROLE);
////								}else
////								{
////									dn.setRole(Nodes.Roles.NODE_ROLE);
////								}
//							}
//						}
//
//					}else
//					{
//						for(Probe p: ((MultiProbeNode)dn).getProbes())
//						{
//							if(p instanceof SummaryProbe)
//								((SummaryProbe) p).updateSummary();
//						}
//					}
//					if(dn instanceof MultiProbeNode)
//					{
//						sm.addNode((MultiProbeNode)dn);
//					}else
//					{
//						sm.addNode(dn);
//					}
//
//					if(dn.hasProperty(GraphMLExport.GEOMETRY_KEY))
//					{
//						Rectangle r=GraphMLExport.parseRectangle(dn.getPropertyValue(GraphMLExport.GEOMETRY_KEY));
//						r.y+=y;
//						canvas.getModel().getComponent(n).setBounds(r);
//					}
//				}
//				for(DataSet ds:foundProbes.keySet())
//				{
//					canvas.getModelHub().addProbes(ds, foundProbes.get(ds));
//				}
//				
//				
//				for(Edge e: g.getEdges())
//				{
//					canvas.getModel().getGraph().connect(e);
//				}
//
//				AlignLayouter.getInstance().layout(canvas, canvas.getBounds(), canvas.getModel());
//
//				// add bags, if any:
//				for(String id: gr.bags.keySet())
//				{
//					Map<String, String> bagVal=gr.bags.get(id);
//					ComponentBag bag=new ComponentBag((SuperModel)canvas.getModel());
//					bag.setName(bagVal.get(GraphMLIO.NAME_KEY));
//					bag.setColor(new Color(Integer.parseInt(bagVal.get(GraphMLIO.COLOR_KEY))));
//					String[] tok=bagVal.get(GraphMLIO.NODES_KEY).split(",");
//					for(String s:tok)
//					{
//						for(Node n:canvas.getModel().getGraph().getNodes())
//						{
//							if(((DefaultNode)n).getPropertyValue("id").equals(s))
//							{
//								CanvasComponent comp=canvas.getModel().getComponent(n);
//								bag.addComponent(comp);	
//								break;
//							}
//						}
//					}
//					BagStyle style=BagStyle.valueOf(bagVal.get(GraphMLIO.STYLE_KEY));
//					bag.setStyle(style);
//					((SuperModel)canvas.getModel()).addBag(bag);
//					canvas.add(new BagComponent(bag));					
//				}
//				
//				// resize and reposition. 
//				canvas.revalidateEdges();
//				canvas.updateSize();
//				canvas.updatePlotNow();
//			} catch (Exception e1) 
//			{
//				e1.printStackTrace();
//				throw new RuntimeException(e1.getMessage());
//			}
//		}
//	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.LoadGraphML",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Load a graphml file ",
				"GraphML"				
		);
		pli.addCategory(IMPORT_CATEGORY);
		return pli;	

	}
}
