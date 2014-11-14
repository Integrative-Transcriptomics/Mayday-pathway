package mayday.graphviewer.core.edges;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.XMLStreamWriter;

import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.edges.Edges;
import mayday.core.structures.maps.DefaultValueMap;
import mayday.pathway.sbgn.processdiagram.ProcessDiagram;
import mayday.vis3.graph.edges.ArrowSetting;
import mayday.vis3.graph.edges.EdgeSetting;

public class EdgeDispatcher 
{
	private DefaultValueMap<String, EdgeSetting> roleMap;
	private HashMap<Edge, EdgeSetting> edgeMap;
	private EdgeSetting defaultEdge=new EdgeSetting();
	private EdgeSetting clearEdge=new EdgeSetting("Clear Edge");
	private EdgeSetting reverseEdge=new EdgeSetting("Reverse Edge");
	private EdgeSetting bidirectionalEdge=new EdgeSetting("Bidirectional Edge");
	
	public EdgeDispatcher(EdgeSetting defaultEdgeSetting)
	{
		Map<String, EdgeSetting> settingsMap=new HashMap<String, EdgeSetting>();
		roleMap=new DefaultValueMap<String, EdgeSetting>(settingsMap, defaultEdge);
		defaultEdge=defaultEdgeSetting;
		
		defaultEdge.setTargetRole(Edges.Roles.EDGE_ROLE);
		roleMap.put(Edges.Roles.EDGE_ROLE, defaultEdge);
		
		EdgeSetting stimulationEdge=defaultEdge.clone();
		stimulationEdge.setTargetRole(ProcessDiagram.STIMULATION_ROLE);
		roleMap.put(ProcessDiagram.STIMULATION_ROLE, stimulationEdge);
		
		clearEdge.setPaintTargetArrow(false);
		clearEdge.setPaintSourceArrow(false);
		
		EdgeSetting noArrow=clearEdge.clone();
		noArrow.setTargetRole(Edges.Roles.NO_ARROW_EDGE);		
		roleMap.put(Edges.Roles.NO_ARROW_EDGE, noArrow);
		
		EdgeSetting inters=clearEdge.clone();	
		inters.setTargetRole(Edges.Roles.INTERSECT_EDGE);	
		roleMap.put(Edges.Roles.INTERSECT_EDGE, inters);
		
		EdgeSetting cons=clearEdge.clone();
		cons.setTargetRole(ProcessDiagram.CONSUMPTION_ROLE);	
		roleMap.put(ProcessDiagram.CONSUMPTION_ROLE, cons);
		
		EdgeSetting eqiv=clearEdge.clone();
		eqiv.setTargetRole(ProcessDiagram.EQUIVALENCE_ROLE);	
		roleMap.put(ProcessDiagram.EQUIVALENCE_ROLE, eqiv);
		
		EdgeSetting log=clearEdge.clone();
		log.setTargetRole(ProcessDiagram.LOGIC_ROLE);	
		roleMap.put(ProcessDiagram.LOGIC_ROLE, log);
	
		
		
		reverseEdge.setPaintSourceArrow(true);
		reverseEdge.setPaintTargetArrow(false);
		reverseEdge.setTargetRole(Edges.Roles.REVERSE_EDGE);
		roleMap.put(Edges.Roles.REVERSE_EDGE, reverseEdge);
		
		bidirectionalEdge.setPaintSourceArrow(true);
		bidirectionalEdge.setTargetRole(Edges.Roles.BIDIRECTIONAL_EDGE);
		roleMap.put(Edges.Roles.BIDIRECTIONAL_EDGE, bidirectionalEdge);
		
		EdgeSetting catalysis=new EdgeSetting();
		catalysis.setTargetRole(ProcessDiagram.CATALYSIS_ROLE);
		catalysis.setTargetArrowStyle(ArrowSetting.CIRCLE);		
		roleMap.put(ProcessDiagram.CATALYSIS_ROLE,catalysis);
		
		EdgeSetting inhibition=new EdgeSetting();
		inhibition.setTargetArrowStyle(ArrowSetting.BAR);	
		inhibition.setTargetRole(ProcessDiagram.INHIBITION_ROLE);
		roleMap.put(ProcessDiagram.INHIBITION_ROLE,inhibition);
		
		EdgeSetting modulation=new EdgeSetting();
		modulation.setTargetArrowStyle(ArrowSetting.DIAMOND);	
		modulation.setTargetRole(ProcessDiagram.MODULATION_ROLE);
		roleMap.put(ProcessDiagram.MODULATION_ROLE,modulation);
		
		EdgeSetting production=new EdgeSetting();
		production.setTargetFillArrow(true);	
		production.setTargetRole(ProcessDiagram.PRODUCTION_ROLE);
		roleMap.put(ProcessDiagram.PRODUCTION_ROLE,production);
		
		EdgeSetting trigger=new EdgeSetting();
		trigger.setTargetArrowStyle(ArrowSetting.BAR_AND_ARROW);
		trigger.setTargetRole(ProcessDiagram.NECESSARY_STIMULATION_ROLE);
		roleMap.put(ProcessDiagram.NECESSARY_STIMULATION_ROLE,trigger);
		
		edgeMap=new HashMap<Edge, EdgeSetting>();
	}
	
	public void clear()
	{
		roleMap.clear();
		edgeMap.clear();
	}
	
	public EdgeSetting getSetting(Edge e)
	{
		if(edgeMap.containsKey(e))
			return edgeMap.get(e);
		return roleMap.get(e.getRole());
	}
	
	public boolean hasSpecificSetting(Edge e)
	{
		return edgeMap.containsKey(e);
	}
	
	public void putEdgeSetting(Edge e, EdgeSetting setting)
	{
		edgeMap.put(e, setting);
	}
	
	public void resetEdgeSetting(Edge e)
	{
		edgeMap.remove(e);
	}

	public EdgeSetting getSettingForRole(String identifier) 
	{
		EdgeSetting res=roleMap.get(identifier);
		return res==defaultEdge?null:res;
	}
	
	public void removeRole(String role)
	{
		roleMap.remove(role);
	}
	
	public String getRoleForSetting(EdgeSetting setting)
	{
		for(Entry<String, EdgeSetting> s:roleMap.entrySet())
		{
			if(s.getValue()==setting)
				return s.getKey();
		}
		return null;
	}
	
	public DefaultValueMap<String, EdgeSetting> getRoleMap() {
		return roleMap;
	}
	
	public void putRoleSetting(String role, EdgeSetting setting)
	{
		roleMap.put(role, setting);
	}
	
	public static final String EDGE_DISPATCHER="edges";
	public static final String DEFAULT_EDGE="defaultEdge";
	public static final String ROLE_EDGE="edgeRoleMap";
	public static final String SPECIAL_EDGE="specialEdgeMap";
	public static final String TARGET="target";
	public static final String CONFIG="edgeConfiguration";
	public static final String SETTING="edgeSetting";
	
	
	public void exportXML(XMLStreamWriter writer) throws Exception
	{
		writer.writeStartElement(EDGE_DISPATCHER);		
		
		// write default renderer
		writer.writeStartElement(DEFAULT_EDGE);
		exportEdgeSetting(writer, defaultEdge,defaultEdge.getTargetRole());
		writer.writeEndElement();

		
		// write role renderers;		
		writer.writeStartElement(ROLE_EDGE);
		for(String s: roleMap.keySet())
		{
			exportEdgeSetting(writer, roleMap.get(s), s);
		}
		writer.writeEndElement();	
		
		// individual renderers
		writer.writeStartElement(SPECIAL_EDGE);
		for(Edge e: edgeMap.keySet())				
		{
			exportEdgeSetting(writer, edgeMap.get(e), "edge"+e.hashCode());
		}
		writer.writeEndElement();
		
		writer.writeEndElement();
	}
	
	private void exportEdgeSetting(XMLStreamWriter writer, EdgeSetting setting, String target) throws Exception
	{
		writer.writeStartElement(CONFIG);		
		writer.writeAttribute(TARGET, target);		
		StringWriter w=new StringWriter();
		setting.toPrefNode().saveTo(new BufferedWriter(w));
		String prop=w.toString();
		prop=prop.replaceAll("\\n", "&#xa;");
		writer.writeAttribute(SETTING, w.toString());		
		writer.writeEndElement();
	}

	public EdgeSetting getDefaultEdge() {
		return defaultEdge;
	}
	
	public void setDefaultEdge(EdgeSetting defaultEdge) {
		this.defaultEdge = defaultEdge;
	}
	
	
	 
}
