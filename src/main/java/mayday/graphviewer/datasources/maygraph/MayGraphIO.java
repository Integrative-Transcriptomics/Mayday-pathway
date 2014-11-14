package mayday.graphviewer.datasources.maygraph;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.io.GraphFactory;
import mayday.core.structures.graph.io.GraphMLExport;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.core.bag.ComponentBag;
import mayday.graphviewer.core.bag.ComponentBag.BagStyle;
import mayday.graphviewer.datasources.graphml.GraphMLGraph;
import mayday.graphviewer.datasources.graphml.GraphMLIO;
import mayday.graphviewer.statistics.ResultSet;
import mayday.graphviewer.statistics.ResultSetExport;
import mayday.graphviewer.statistics.ResultSetImport;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.SummaryProbe;
import mayday.vis3.graph.renderer.RendererDecorator;
import mayday.vis3.graph.renderer.dispatcher.AssignedRendererSetting;

public class MayGraphIO extends GraphMLIO
{

	private static final String VIEWER_SETTINGS_TARGET="viewerSettings";
	
	public static void exportCompleteGraph(GraphViewerPlot canvas, SuperModel model, File file) throws Exception
	{
		ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(file));
		ZipEntry graphEntry = new ZipEntry(GRAPH_TARGET);
		zipOut.putNextEntry(graphEntry);

		XMLOutputFactory xof = XMLOutputFactory.newInstance();
		// this is all necessary because some operating systems named
		// like fierce predators don't give a damn about standards. 
		XMLStreamWriter writer =xof.createXMLStreamWriter(zipOut, "utf-8");
		
		writeHeader(writer);
		writeGraph(writer, model);

		// export bags
		Map<ComponentBag, String> bagIdMap=new HashMap<ComponentBag, String>(); 
		for(ComponentBag bag:model.getBags())
		{
			String s=exportBag(writer,bag,model);
			bagIdMap.put(bag, s);
		}

		writer.writeEndDocument();
		writer.flush();
		writer.close();

		for(ComponentBag bag:model.getBags())
		{
			if(bag.getStatistics().isEmpty())
				continue;

			String s=bagIdMap.get(bag);
			ZipEntry bagEntry = new ZipEntry(s);
			zipOut.putNextEntry(bagEntry);
			ResultSetExport.exportResultSets(bag,zipOut);
		}	
		// export renderers:
		ZipEntry rendererEntry = new ZipEntry(RENDERER_KEY);
		zipOut.putNextEntry(rendererEntry);
		XMLStreamWriter writer2 =xof.createXMLStreamWriter(zipOut,"utf8");
		writer2.writeStartDocument("utf-8", "1.0");
		canvas.getRendererDispatcher().exportXML(writer2);
		writer2.writeEndDocument();
		writer2.flush();
		writer2.close();
		
		//export edges:
		ZipEntry edgesEntry = new ZipEntry(EDGES_KEY);
		zipOut.putNextEntry(edgesEntry);
		writer2 =xof.createXMLStreamWriter(zipOut,"utf8");
		writer2.writeStartDocument("utf-8", "1.0");
		canvas.getEdgeDispatcher().exportXML(writer2);
		writer2.writeEndDocument();
		writer2.flush();
		writer2.close();
		
		// export graph settings:
		ZipEntry viewerEntry = new ZipEntry(VIEWER_SETTINGS_TARGET);
		zipOut.putNextEntry(viewerEntry);		
		XMLStreamWriter writer3 =xof.createXMLStreamWriter(zipOut,"utf-8");
		writer3.writeStartDocument("utf-8", "1.0");
		canvas.exportBasicSettings(writer3);
		writer3.writeEndDocument();
		writer3.flush();
		writer3.close();
		
		zipOut.close();	
	}

	@SuppressWarnings("unchecked")
	public static void loadGraph(File file, SuperModel model, GraphViewerPlot canvas) throws Exception
	{
		ZipFile zipFile=new ZipFile(file);
		ZipEntry graphEntry=zipFile.getEntry(GRAPH_TARGET);
		GraphMLGraph gr=loadGraph(zipFile.getInputStream(graphEntry), model, canvas);

		Map<String, ComponentBag> bags=processBags(gr, model, canvas);

		ResultSetImport resultSetImport=new ResultSetImport();
		Enumeration ent=zipFile.entries();
		while(ent.hasMoreElements())
		{
			ZipEntry entry=(ZipEntry)ent.nextElement();
			if(entry.getName().equals(RENDERER_KEY))
			{				
				processRenderers(zipFile.getInputStream(entry), gr, model, canvas);
			}
			if(entry.getName().equals(EDGES_KEY))
			{				
				processEdges(zipFile.getInputStream(entry), gr, model, canvas);
			}			
			if(entry.getName().equals(VIEWER_SETTINGS_TARGET))
			{		
				ViewerSettingsFileParser gsfp = new ViewerSettingsFileParser();
				gsfp.parse(zipFile.getInputStream(entry));
				
				canvas.updateSettingsFromPreferences(gsfp);
			}

			if(bags.containsKey(entry.getName()))
			{				
				ComponentBag bag=bags.get(entry.getName());

				List<ResultSet> rr=resultSetImport.parse(zipFile.getInputStream(entry), bag);
				for(ResultSet r:rr)
					bag.addStatistic(r);	
			}

		}
		zipFile.close();	
		canvas.updatePlotNow();
	}



	public static final GraphMLGraph loadGraph(InputStream in, SuperModel model, GraphViewerPlot canvas) throws Exception
	{
		List<GraphMLGraph> graphs=GraphMLIO.importGraphML2(in,
				canvas.getModelHub().getViewModel().getDataSet().getMasterTable().getProbes().values());
		GraphMLGraph gr=graphs.get(0);
		Graph g=gr.graph;
		// always override the stuff already present. 
		int y=25;
		model.clearAll();

		MultiHashMap<DataSet,Probe> foundProbes=new MultiHashMap<DataSet, Probe>();

		for(Node n:g.getNodes())
		{
			if(! (n instanceof DefaultNode))
			{
				continue;
			}
			DefaultNode dn=(DefaultNode)n;

			if(((MultiProbeNode)dn).getProbes().isEmpty())
			{
				if(dn.hasProperty(GraphMLExport.PROBES_KEY))
				{
					String p=dn.getPropertyValue(GraphMLExport.PROBES_KEY);
					String[] ps=GraphFactory.probesSplitPattern.split(p);
					for(int i=1; i!= ps.length; ++i)// skip first empty element!;
					{
						if(ps[i].contains("$"))
						{
							String[] dspl=ps[i].split("\\$");
							for(DataSet ds: canvas.getModelHub().getAllAvailableDataSets())
							{
								if(ds.getName().equals(dspl[0]))
								{
									Probe pr=ds.getMasterTable().getProbe(dspl[1]);
									if(pr!=null)
									{
										foundProbes.put(ds, pr);
										((MultiProbeNode)dn).addProbe(pr);
									}

								}									
							}
						}

					}
					dn.getProperties().remove(GraphMLExport.PROBES_KEY);
				}else
				{
					List<Probe> probes=new ArrayList<Probe>();
					for(Probe probe: canvas.getModelHub().getViewModel().getDataSet().getMasterTable().getProbes().values())
					{
						if(probe.getDisplayName().equals(dn.getName()))
						{
							probes.add(probe);										
						}
						//						if(! probes.isEmpty())
						//						{								
						//							((MultiProbeNode)dn).setProbes(probes);
						//							dn.setRole(Nodes.Roles.PROBES_ROLE);
						//						}else
						//						{
						//							dn.setRole(Nodes.Roles.NODE_ROLE);
						//						}
					}
				}

			}else
			{
				for(Probe p: ((MultiProbeNode)dn).getProbes())
				{
					foundProbes.put(p.getMasterTable().getDataSet(), p);
					if(p instanceof SummaryProbe)
						((SummaryProbe) p).updateSummary();
				}
			}
			if(dn instanceof MultiProbeNode)
			{
				model.addNode((MultiProbeNode)dn);
			}else
			{
				model.addNode(dn);
			}

			if(dn.hasProperty(GraphMLExport.GEOMETRY_KEY))
			{
				Rectangle r=GraphMLExport.parseRectangle(dn.getPropertyValue(GraphMLExport.GEOMETRY_KEY));
				r.y+=y;
				canvas.getModel().getComponent(n).setBounds(r);
			}
		}
		for(DataSet ds:foundProbes.keySet())
		{
			canvas.getModelHub().addProbes(ds, foundProbes.get(ds));
		}		

		for(Edge e: g.getEdges())
		{
			canvas.getModel().getGraph().connect(e);
		}
		return gr;
	}
	
	private static void processEdges(InputStream in,GraphMLGraph gr, SuperModel model, GraphViewerPlot canvas) throws Exception
	{
		EdgeFileParser parser=new EdgeFileParser();
		parser.parse(in);
		
		canvas.getEdgeDispatcher().clear();
		canvas.getEdgeDispatcher().setDefaultEdge(parser.getDefaultEdge());
		canvas.getEdgeSetting().fromPrefNode(parser.getDefaultEdge().toPrefNode());
		for(String s: parser.getEdgeRoleMap().keySet())
		{
			canvas.getEdgeDispatcher().putRoleSetting(s, parser.getEdgeRoleMap().get(s));
		}
		
		if(!parser.getEdgeSpecialMap().isEmpty())
		{
			for(Edge e: gr.graph.getEdges())
			{
				String id=e.getProperties().get(GraphFactory.ID_KEY);
				if(parser.getEdgeSpecialMap().containsKey(id))
				{
					canvas.getEdgeDispatcher().putEdgeSetting(e, parser.getEdgeSpecialMap().get(id));
				}
			}
			
		}
	}

	private static void processRenderers(InputStream in,GraphMLGraph gr, SuperModel model, GraphViewerPlot canvas) throws Exception
	{
		RendererFileParser parser=new RendererFileParser(canvas.getModelHub().getViewModel().getDataSet(), canvas.getModelHub().getColorProvider());
		parser.parse(in);

		canvas.getRendererDispatcher().updateDefaultRenderer(parser.getDefaultRenderer());
		
		canvas.getRendererDispatcher().getOverallDecorators().clear();
		for(RendererDecorator dec:  parser.getDecorators())
		{
			canvas.getRendererDispatcher().getOverallDecorators().add(dec);
		}
		
		
		canvas.getRendererDispatcher().clearRoles();			
		List<AssignedRendererSetting> ret=new ArrayList<AssignedRendererSetting>();
		for(String s: parser.getRoleRenderers().keySet())
		{
			ret.add( parser.getRoleRenderers().get(s));					
		}		
		canvas.getRendererDispatcher().getRoleRenderersSetting().update(ret);


		canvas.getRendererDispatcher().clearIndividualRenderers();
		for(String s: parser.getIndividualRenderers().keySet())
		{
			CanvasComponent cc=null;
			for(Node n: gr.graph.getNodes())
			{
				if( ((DefaultNode)n).getPropertyValue("id").equals(s))
				{
					cc=model.getComponent(n);
					break;
				}
			}
			if(cc!=null)
			{
				AssignedRendererSetting extraeinladung =  parser.getIndividualRenderers().get(s);
				canvas.getRendererDispatcher().addIndividualRenderer(cc, extraeinladung);
			}
		}
	}

	private static Map<String, ComponentBag> processBags(GraphMLGraph gr, SuperModel model, GraphViewerPlot canvas)
	{
		Map<String, ComponentBag> bagMap=new HashMap<String, ComponentBag>();
		// add bags, if any:
		for(String id: gr.bags.keySet())
		{
			Map<String, String> bagVal=gr.bags.get(id);
			ComponentBag bag=new ComponentBag((SuperModel)canvas.getModel());
			bag.setName(bagVal.get(GraphMLIO.NAME_KEY));
			bag.setColor(new Color(Integer.parseInt(bagVal.get(GraphMLIO.COLOR_KEY))));
			String[] tok=bagVal.get(GraphMLIO.NODES_KEY).split(",");
			for(String s:tok)
			{
				for(Node n:canvas.getModel().getGraph().getNodes())
				{
					if(((DefaultNode)n).getPropertyValue("id").equals(s))
					{
						CanvasComponent comp=canvas.getModel().getComponent(n);
						bag.addComponent(comp);	
						break;
					}
				}
			}
			BagStyle style=BagStyle.valueOf(bagVal.get(GraphMLIO.STYLE_KEY));
			bag.setStyle(style);
			if(!bag.isEmpty()){
				((SuperModel)canvas.getModel()).addBag(bag);
//				canvas.add(new BagComponent(bag));		
				bagMap.put(id, bag);
			}
		}
		return bagMap;
	}

}
