package mayday.graphviewer.datasources.maygraph;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import mayday.core.Preferences;
import mayday.graphviewer.core.edges.EdgeDispatcher;
import mayday.vis3.graph.edges.EdgeSetting;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class EdgeFileParser 
{
	private EdgeSetting defaultEdge;
	private Map<String,EdgeSetting> edgeRoleMap;
	private Map<String,EdgeSetting> edgeSpecialMap;	
	private DocumentBuilder builder;

	public EdgeFileParser() throws Exception
	{
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		builder=dbf.newDocumentBuilder();
	}

	public void parse(InputStream in) throws Exception
	{
		Document document = builder.parse(in);

		// parse default renderer
		Element defaultEdgeE = (Element) document.getElementsByTagName(EdgeDispatcher.DEFAULT_EDGE).item(0);
		Element defaultEdgeConfigE= (Element) defaultEdgeE.getChildNodes().item(0);
		defaultEdge=parseEdgeConfiguration(defaultEdgeConfigE);
		
		Element roleEdgeE = (Element) document.getElementsByTagName(EdgeDispatcher.ROLE_EDGE).item(0);
		NodeList roleList= roleEdgeE.getChildNodes();
		edgeRoleMap=new HashMap<String, EdgeSetting>();
		for(int i=0; i!=roleList.getLength(); ++i)
		{
			Element roleE=(Element)roleList.item(i);
			EdgeSetting s=parseEdgeConfiguration(roleE);
			edgeRoleMap.put(s.getTargetRole(), s);
		}
		
		Element specialEdgeE = (Element) document.getElementsByTagName(EdgeDispatcher.SPECIAL_EDGE).item(0);
		NodeList specialList= specialEdgeE.getChildNodes();
		edgeSpecialMap=new HashMap<String, EdgeSetting>();
		for(int i=0; i!=specialList.getLength(); ++i)
		{
			Element roleE=(Element)specialList.item(i);
			EdgeSetting s=parseEdgeConfiguration(roleE);
			edgeSpecialMap.put(s.getTargetRole(), s);
		}
	}

	private EdgeSetting parseEdgeConfiguration(Element e)
	{
		String target="";
		if(e.hasAttribute(EdgeDispatcher.TARGET))
		{
			target=e.getAttribute(EdgeDispatcher.TARGET);			
		}		
		String cfg="";
		if(e.hasAttribute(EdgeDispatcher.SETTING))
		{
			cfg=e.getAttribute(EdgeDispatcher.SETTING);			
		}
		System.out.println(cfg);
		cfg=cfg.replace("&#xa;","\n");
		Preferences pref=Preferences.createUnconnectedPrefTree("key", "value");
		EdgeSetting setting=new EdgeSetting(target);
		try 
		{
			pref.loadFrom(new BufferedReader(new StringReader(cfg)));
			setting.fromPrefNode(pref);
		} catch (Exception e1) 
		{
			e1.printStackTrace();
		}
		setting.setTargetRole(target);
		return setting;
	}
	
	public EdgeSetting getDefaultEdge() {
		return defaultEdge;
	}
	
	public Map<String, EdgeSetting> getEdgeRoleMap() {
		return edgeRoleMap;
	}
	
	public Map<String, EdgeSetting> getEdgeSpecialMap() {
		return edgeSpecialMap;
	}
	

}

