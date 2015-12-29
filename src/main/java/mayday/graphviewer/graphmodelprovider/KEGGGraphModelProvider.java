package mayday.graphviewer.graphmodelprovider;

import java.awt.Component;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import mayday.core.DataSet;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.MappingSourceSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.io.GraphMLExport;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.core.structures.graph.nodes.Nodes;
import mayday.core.tasks.AbstractTask;
import mayday.graphviewer.core.KEGGPathwayGraph;
import mayday.graphviewer.core.KEGGRoles;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.core.Utilities;
import mayday.graphviewer.layout.CopyLayout;
import mayday.pathway.keggview.kegg.KEGGHandler;
import mayday.pathway.keggview.kegg.KEGGObject;
import mayday.pathway.keggview.kegg.KEGGParser;
import mayday.pathway.keggview.kegg.compounds.Compound;
import mayday.pathway.keggview.kegg.compounds.CompoundsParser;
import mayday.pathway.keggview.kegg.pathway.Pathway;
import mayday.vis3.graph.layout.CanvasLayouter;
import mayday.vis3.graph.layout.FruchtermanReingoldLayout;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class KEGGGraphModelProvider  extends AbstractGraphModelProvider
{
	private PathSetting pathSetting;
	private Map<DataSet, MappingSourceSetting> mappings;
	private static final String LAST_KEGG_FILE="AnacondaLastKEGGDir";
	private static final String LAST_COMPOUND_FILE="AnacondaLastCompoundDir";
	private PathSetting compoundsFile;
	private DoubleSetting magnify=new DoubleSetting("Magnification factor", "The factor used to scale the KEGG nodes", 2.5d, 0.1, 5.0, true, true);
	private BooleanSetting sameSize=new BooleanSetting("Uniform size", "Draw enzymes and metabolites in the same size: 100x50", true);
	private Map<String, KEGGObject> compounds;
	private CopyLayout layouter;


	private KEGGHandler handler;

	public KEGGGraphModelProvider() 
	{
		String d= Utilities.prefs.get(LAST_KEGG_FILE, System.getProperty("user.home"));
		String e=Utilities.prefs.get(LAST_COMPOUND_FILE, System.getProperty("user.home"));
		pathSetting=new PathSetting("Pathway File", null, d, false, true, false);
		compoundsFile=new PathSetting("Compounds file","The KEGG annotation file on compounds",e,false,true,true );
		basicSetting.addSetting(pathSetting).addSetting(compoundsFile).addSetting(magnify).addSetting(sameSize);		
	}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"mayday.graphModelProvider.keggpathway",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Create a new graph from a KEGG pathway",
				getName()				
		);		
		return pli;			
	}

	@Override
	public String getName() 
	{
		return "KEGG Pathway";
	}

	@Override
	public String getDescription() 
	{
		return "Build a graph from information stored in a biopax file.";
	}

	@Override
	public CanvasLayouter defaultLayouter() 
	{
		if(layouter!=null)
			return layouter;
		else
			return new FruchtermanReingoldLayout();
	}

	@Override
	public AbstractTask buildGraph() 
	{
		return new GraphTask();
	}

	@Override
	public AbstractTask parseFile() 
	{
		Utilities.prefs.put(LAST_KEGG_FILE, pathSetting.getStringValue());
		Utilities.prefs.put(LAST_COMPOUND_FILE, compoundsFile.getStringValue());
		return new ParseTask();
	}

	@Override
	public boolean isAskForProbeLists() 
	{
		return true;
	}

	@Override
	public boolean isAskForFileSetting() 
	{
		return false;
	}

	@Override
	public Component getAdditionalComponent() 
	{
		return null;
	}

	@Override
	public Setting getInformedSetting() 
	{
		informedSetting=new HierarchicalSetting(getName());
		mappings=new TreeMap<DataSet, MappingSourceSetting>();
		for(DataSet ds:probeLists.keySet())
		{
			MappingSourceSetting mapping=new MappingSourceSetting(ds);
			mappings.put(ds, mapping);
			HierarchicalSetting dsSetting=new HierarchicalSetting(ds.getName());
			dsSetting.addSetting(mapping);
			informedSetting.addSetting(dsSetting);
		}
		return informedSetting;
	}

	private class ParseTask extends AbstractTask
	{
		public ParseTask() 
		{
			super("Parsing KEGG file");
		}

		@Override
		protected void doWork() throws Exception 
		{
			XMLReader parser;
			parser = XMLReaderFactory.createXMLReader();
			parser.setEntityResolver(new EntityResolver() {
				public InputSource resolveEntity(String publicID, String systemID)
				throws SAXException {
					return new InputSource(new StringReader(""));
				}
			}
			);
			handler=new KEGGHandler();
			parser.setContentHandler(handler);
			parser.parse(new InputSource(new FileReader(pathSetting.getStringValue())));



			File f=new File(compoundsFile.getStringValue());
			if(f.exists())
			{
				KEGGParser compoundsParser=new KEGGParser(new CompoundsParser());
				compounds=compoundsParser.parseData(compoundsFile.getStringValue());
			}


		}
		@Override
		protected void initialize() {}
	}

	private class GraphTask extends AbstractTask
	{
		public GraphTask() 
		{
			super("Building Graph (BioPax Pathway)");
		}

		@Override
		protected void doWork() throws Exception 
		{
			Pathway p=handler.getPathway();
			// create graph
			KEGGPathwayGraph g=new KEGGPathwayGraph(p);
			g.removeOrphans();



			if(compounds!=null)
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
				}
			}

			Pattern probesSplitPattern=Pattern.compile("^\\\"|\\\"$|\\\",\\\"");	

				

			for(Node n:g.getNodes())
			{
				if(! (n instanceof DefaultNode))
				{
					continue;
				}
				if(n.getRole().equals(KEGGRoles.GENE_ROLE) || n.getRole().equals(KEGGRoles.COMPOUND_ROLE))
					n.setRole(Nodes.Roles.PROBE_ROLE);
				if(n.getRole().equals(KEGGRoles.MAP_ROLE) )
					n.setRole(Nodes.Roles.NODE_ROLE);
				
				DefaultNode dn=(DefaultNode)n;
				String pr=dn.getPropertyValue(GraphMLExport.PROBES_KEY);
				if(pr!=null){
					String[] ps=probesSplitPattern.split(pr);
					int begin=ps.length>1?1:0;
					StringBuffer sb=new StringBuffer();
					for(int i=begin; i!= ps.length; ++i)// skip first empty element!;
					{						
						if(i!=begin)
							sb.append(",");
						sb.append(ps[i]);
					}	
					dn.setProperty(GraphMLExport.PROBES_KEY, sb.toString());
				}
			}
			annotateNodes(getProbeLists(), g, mappings);
			model=new SuperModel(g);
				
			for(Node n:g.getNodes())
			{
				if(! (n instanceof DefaultNode))
				{
					continue;
				}
				DefaultNode dn=(DefaultNode)n;
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
					model.getComponent(dn).setBounds(rect);					
				}
			}
			layouter=new CopyLayout(model.getComponents());

			
		}



		@Override
		protected void initialize() {}
	}




}
