package mayday.graphviewer.plugins.io;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.datasetmanager.DataSetManager;
import mayday.core.gui.PreferencePane;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.PluginManager.IGNORE_PLUGIN;
import mayday.core.settings.Setting;
import mayday.core.settings.Settings;
import mayday.core.settings.SettingsDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.io.GraphMLExport;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.KEGGPathwayGraph;
import mayday.graphviewer.core.KEGGRoles;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.pathway.keggview.kegg.KEGGHandler;
import mayday.pathway.keggview.kegg.KEGGObject;
import mayday.pathway.keggview.kegg.KEGGParser;
import mayday.pathway.keggview.kegg.compounds.Compound;
import mayday.pathway.keggview.kegg.compounds.CompoundsParser;
import mayday.pathway.keggview.kegg.pathway.Pathway;
import mayday.pathway.viewer.canvas.AlignLayouter;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.GraphModel;
import mayday.vis3.graph.renderer.dispatcher.AssignedRendererSetting;
import mayday.vis3.graph.vis3.SuperColorProvider;
import mayday.vis3.model.ViewModel;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

@IGNORE_PLUGIN
public class KEGGImport extends AbstractGraphViewerPlugin
{
	private PathSetting keggFile=new PathSetting("KGML file", "The pathway file to be imported", null, false, true, false);
	private PathSetting compoundsFile=new PathSetting("Compounds file","The KEGG annotation file on compounds",null,false,true,true );
	private DoubleSetting magnify=new DoubleSetting("Magnification factor", "The factor used to scale the KEGG nodes", 2.5d, 0.1, 5.0, true, true);
	private BooleanSetting sameSize=new BooleanSetting("Uniform size", "Draw enzymes and metabolites in the same size: 100x50", true);

	private HierarchicalSetting setting=new HierarchicalSetting("KEGG Import Settings"); 

	//	private ObjectSelectionSetting<DataSet> enzymeDataSetSetting;
	private ObjectSelectionSetting<DataSet> metaboliteDataSetSetting;

	private Settings settings;

	public KEGGImport() 
	{
		setting.addSetting(keggFile).addSetting(compoundsFile).addSetting(magnify).addSetting(sameSize);

		if(DataSetManager.singleInstance.size()!=0)
		{
			DataSet[] ds=DataSetManager.singleInstance.getDataSets().toArray(new DataSet[1]);		
			//		enzymeDataSetSetting=new ObjectSelectionSetting<DataSet>("Enzyme DataSet",null, 0, ds);		
			metaboliteDataSetSetting=new ObjectSelectionSetting<DataSet>("Metabolites DataSet",null, 0, ds);		
			//		setting.addSetting(enzymeDataSetSetting).
			setting.addSetting(metaboliteDataSetSetting);		
			settings=new Settings(setting, PluginInfo.getPreferences("PAS.GraphViewer.KEGGImport"));
		}
	}

	@Override
	public void run(final GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		// update settings.
		//		enzymeDataSetSetting.setObjectValue(canvas.getViewModel().getDataSet());
		metaboliteDataSetSetting.setObjectValue(canvas.getModelHub().getViewModel().getDataSet());

		SettingsDialog settingsDialog=new SettingsDialog(null, "KEGG Import Settings", settings);		
		settingsDialog.setModal(true);
		settingsDialog.setVisible(true);		
		if(settingsDialog.canceled())
			return;

		try 
		{
			// open KGML file 
			XMLReader parser;
			parser = XMLReaderFactory.createXMLReader();
			parser.setEntityResolver(new EntityResolver() {
				public InputSource resolveEntity(String publicID, String systemID)
				throws SAXException {
					return new InputSource(new StringReader(""));
				}
			}
			);
			KEGGHandler handler=new KEGGHandler();
			parser.setContentHandler(handler);
			parser.parse(new InputSource(new FileReader(keggFile.getStringValue())));
			//parser.parse(dataDirectory+"/"+taxon+f.getNumber()+".xml");
			Pathway p=handler.getPathway();
			// create graph
			KEGGPathwayGraph g=new KEGGPathwayGraph(p);
			g.removeOrphans();
			
			DataSet metaboliteDS=metaboliteDataSetSetting.getObjectValue();

			// open compounds, if necessary:
			File f=new File(compoundsFile.getStringValue());
			Pattern probesSplitPattern=Pattern.compile("^\\\"|\\\"$|\\\",\\\"");

			ProbeList pl=new ProbeList(metaboliteDS, false);
			if(f.exists())
			{
				KEGGParser compoundsParser=new KEGGParser(new CompoundsParser());
				Map<String, KEGGObject> compounds=compoundsParser.parseData(compoundsFile.getStringValue());
				for(Node n:g.getNodes())
				{
					if(n.getRole()!=KEGGRoles.COMPOUND_ROLE)
						continue;
					DefaultNode dn=(DefaultNode)n;
					Compound o=(Compound) compounds.get(dn.getName());
					dn.setProperty(GraphMLExport.PROBES_KEY,o.getName());
					dn.setName(o.getName());
					
					String pr=dn.getPropertyValue(GraphMLExport.PROBES_KEY);
					String[] ps=probesSplitPattern.split(pr);
					int begin=ps.length>1?1:0;
					for(int i=begin; i!= ps.length; ++i)// skip first empty element!;
					{
						for(Probe probe:metaboliteDS.getMasterTable().getProbes().values())
						{
							if(probe.getName().equalsIgnoreCase(ps[i]))
							{								
								((MultiProbeNode)dn).addProbe(probe);
								if(!pl.contains(probe))
									pl.addProbe(probe);
							}									
							else
							{
								if(probe.getDisplayName().equalsIgnoreCase(ps[i]))
								{
									((MultiProbeNode)dn).addProbe(probe);
									if(!pl.contains(probe))
										pl.addProbe(probe);
								}
							}
						}
					}
				}
			}
			
			ViewModel metabolitVM=canvas.getModelHub().getViewModel();
			SuperColorProvider metabolitCP=canvas.getModelHub().getColorProvider();
			
			// adjust metabolites dataset
			
			if(metaboliteDS!=canvas.getModelHub().getViewModel().getDataSet())
			{
				List<ProbeList> pls=new ArrayList<ProbeList>();
				canvas.getModelHub().addProbeLists(metaboliteDS, pls);	
			}
			
			// place the graph in the model
			canvas.setModel(new SuperModel(g));
			ProbeList pl2=canvas.getModelHub().getAddedProbes();
			for(Node n:g.getNodes())
			{
				if(! (n instanceof DefaultNode))
				{
					continue;
				}
				DefaultNode dn=(DefaultNode)n;

				if(dn.hasProperty(GraphMLExport.PROBES_KEY) && dn.getRole().equals(KEGGRoles.GENE_ROLE))
				{
					String pr=dn.getPropertyValue(GraphMLExport.PROBES_KEY);
					String[] ps=probesSplitPattern.split(pr);
					for(int i=1; i!= ps.length; ++i)// skip first empty element!;
					{
						for(Probe probe:canvas.getModelHub().getViewModel().getProbes())
						{
							if(probe.getName().equals(ps[i]))
							{
								((MultiProbeNode)dn).addProbe(probe);
								if(!pl2.contains(probe))
									pl2.addProbe(probe);
							}									
							else
							{
								if(probe.getDisplayName().equals(ps[i]))
								{
									((MultiProbeNode)dn).addProbe(probe);	
									if(!pl2.contains(probe))
										pl2.addProbe(probe);
								}// else test selected mi group
							}
						}
					}
				}
				if(dn.hasProperty(GraphMLExport.GEOMETRY_KEY))
				{
					Rectangle rect=GraphMLExport.parseRectangle(dn.getPropertyValue(GraphMLExport.GEOMETRY_KEY));
					rect.x=(int) (rect.x*magnify.getDoubleValue());
					rect.y=(int) (rect.y*magnify.getDoubleValue());
					rect.width=(int) (rect.width*magnify.getDoubleValue());
					rect.height=(int) (rect.height*magnify.getDoubleValue());
					
					
					if(sameSize.getBooleanValue() && (dn.getRole().equals(KEGGRoles.GENE_ROLE) || dn.getRole().equals(KEGGRoles.COMPOUND_ROLE)) )
					{
						rect.width=100;
						rect.height=50;
					}
					canvas.getModel().getComponent(dn).setBounds(rect);					
//					canvas.getModel().getComponent(n).setBounds(GraphMLExport.parseRectangle(dn.getPropertyValue(GraphMLExport.GEOMETRY_KEY)));
				}
			}
			
			
			
			

			AssignedRendererSetting probesRenderer=new AssignedRendererSetting(
					KEGGRoles.GENE_ROLE,
					canvas.getModelHub().getViewModel().getDataSet(), 
					canvas.getModelHub().getColorProvider());
			
			probesRenderer.setPrimaryRenderer("PAS.GraphViewer.Renderer.Heatmap");
			canvas.getRendererDispatcher().addRoleRenderer(KEGGRoles.GENE_ROLE, probesRenderer);
			

			AssignedRendererSetting profileRenderer=new AssignedRendererSetting(
					KEGGRoles.COMPOUND_ROLE, 
					metabolitVM.getDataSet(),
					metabolitCP );
			profileRenderer.setPrimaryRenderer("PAS.GraphViewer.Renderer.Profile");
			canvas.getRendererDispatcher().addRoleRenderer(KEGGRoles.COMPOUND_ROLE, profileRenderer);
			

			AlignLayouter.getInstance().layout(canvas, canvas.getBounds(), canvas.getModel());
			canvas.revalidateEdges();
			canvas.updateSize();
			canvas.updatePlotNow();
		}catch (Exception e1) 
		{
			e1.printStackTrace();
			throw new RuntimeException(e1.getMessage());
		}
		//		}


		ProbeList pl=new ProbeList(canvas.getModelHub().getViewModel().getDataSet().getMasterTable().getDataSet(),false);
		SuperModel sm=((SuperModel)model);
		for(CanvasComponent comp:components)
		{
			if(! (comp instanceof MultiProbeComponent))
				continue;
			for(Probe p:((MultiProbeComponent)comp).getProbes())
			{
				pl.addProbe(p);
			}
		}
		if(pl.getNumberOfProbes()==0) return;
		pl.setName("Joined Probes");
		// steps necessary to add node: 
		canvas.addComponent(sm.addProbeListNode(pl));
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.KEGGImport",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Import a pathway from a KGML file",
				"KEGG Import"				
		);
		pli.addCategory(IMPORT_CATEGORY);
		return pli;	

	}

	@Override
	public Setting getSetting() 
	{
		return setting;
	}
	
	@Override
	public PreferencePane getPreferencesPanel() 
	{
		return null;
	}
}