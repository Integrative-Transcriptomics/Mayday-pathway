package mayday.graphviewer.datasources.maygraph;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import mayday.core.Preferences;
import mayday.graphviewer.core.GraphViewerPlot;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ViewerSettingsFileParser 
{
	private DocumentBuilder builder;

	private Preferences edgePreferences;

	private Preferences labelPreferences;
	private boolean showLabels;

	private String title;
	private boolean showTitle;

	private int selectedAux;

	private Preferences defaultColoring;
	private Map<String,Preferences> colorings;
	private String dataManipulation;

	private boolean hq;
	private boolean simplify;

	public static final String VIS_3_SETTINGS="vis3Settings";
	public static final String DATA_MANIPULATION="dataManipulation";
	public static final String DEFAULT_COLORING="defaultColoring";
	public static final String DATASET_COLORINGS="datasetColoring";
	public static final String CONFIG="config";

	public ViewerSettingsFileParser() throws Exception
	{
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		builder=dbf.newDocumentBuilder();
	}

	public void parse(InputStream in) throws Exception
	{
		Document document = builder.parse(in);

		Element el = (Element)document.getElementsByTagName(GraphViewerPlot.SettingsTarget.EDGES.toString()).item(0);
		String s=el.getAttribute(GraphViewerPlot.SETTINGS);
		edgePreferences=Preferences.createUnconnectedPrefTree("key", "value");
		edgePreferences.loadFrom(new BufferedReader(new StringReader(s)));

		el = (Element)document.getElementsByTagName(GraphViewerPlot.SettingsTarget.LABELS.toString()).item(0);
		s=el.getAttribute(GraphViewerPlot.SETTINGS);
		labelPreferences=Preferences.createUnconnectedPrefTree("key", "value");
		labelPreferences.loadFrom(new BufferedReader(new StringReader(s)));
		showLabels=Boolean.parseBoolean(el.getAttribute("active"));

		el = (Element)document.getElementsByTagName(GraphViewerPlot.SettingsTarget.AUX.toString()).item(0);
		selectedAux=Integer.parseInt(el.getAttribute("selected"));

		el = (Element)document.getElementsByTagName(GraphViewerPlot.SettingsTarget.TITLE.toString()).item(0);
		title=el.getAttribute("title");
		showTitle=Boolean.parseBoolean(el.getAttribute("active"));

		try{
			el = (Element)document.getElementsByTagName(GraphViewerPlot.SettingsTarget.RENDERING.toString()).item(0);
			hq=Boolean.parseBoolean(el.getAttribute("hqRendering"));
			simplify=Boolean.parseBoolean(el.getAttribute("simplifyNodes"));
		}catch(Exception e){} 
		
		//parse vis3 settins: 
		el = (Element)document.getElementsByTagName(VIS_3_SETTINGS).item(0);
		try {
			parseVis3Settings(el);
		} catch (Exception e) {
			//skip the viewer settings if there are none or they lead to exceptions.
		}
	}

	private void parseVis3Settings(Element element)
	{
		Element defaultCE=(Element)element.getElementsByTagName(DEFAULT_COLORING).item(0);
		if(defaultCE.hasAttribute(CONFIG))
		{
			String cfg=defaultCE.getAttribute(CONFIG);		
			defaultColoring=Preferences.createUnconnectedPrefTree("key", "value");
			try {
				defaultColoring.loadFrom(new BufferedReader(new StringReader(cfg)));
			} catch (Exception e) {
				defaultColoring=null;
			}
		}
		// ds colorings;
		NodeList dsColorings=element.getElementsByTagName(DATASET_COLORINGS);
		colorings=new HashMap<String, Preferences>();
		for(int i=0; i!=dsColorings.getLength(); ++i)
		{
			Element dsCE=(Element)dsColorings.item(i);
			String target=dsCE.getAttribute("target");
			if(dsCE.hasAttribute(CONFIG))
			{
				String cfg=dsCE.getAttribute(CONFIG);		
				Preferences pref=Preferences.createUnconnectedPrefTree("key", "value");
				try {
					pref.loadFrom(new BufferedReader(new StringReader(cfg)));
					colorings.put(target, pref);
				} catch (Exception e){}
			}
		}
		Element manipE=(Element)element.getElementsByTagName(DATA_MANIPULATION).item(0);
		dataManipulation=manipE.getAttribute(CONFIG);			
	}

	public Preferences getEdgePreferences() {
		return edgePreferences;
	}

	public Preferences getLabelPreferences() {
		return labelPreferences;
	}

	public boolean isShowLabels() {
		return showLabels;
	}

	public String getTitle() {
		return title;
	}

	public boolean isShowTitle() {
		return showTitle;
	}

	public int getSelectedAux() {
		return selectedAux;
	}

	public String getDataManipulation() {
		return dataManipulation;
	}

	public Preferences getDefaultColoring() {
		return defaultColoring;
	}

	public Map<String, Preferences> getColorings() {
		return colorings;
	}

	public boolean isHq() {
		return hq;
	}
	
	public boolean isSimplify() {
		return simplify;
	}


}
