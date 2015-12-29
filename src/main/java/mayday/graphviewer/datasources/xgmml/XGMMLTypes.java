package mayday.graphviewer.datasources.xgmml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class XGMMLTypes 
{
	public static abstract class XType
	{
		public String id, name, label,labelanchor;
		public List<XAtt> atts=new ArrayList<XAtt>();
		public Map<String,String> attributes=new HashMap<String, String>();		
	}
	
	public  static class XGraph extends XType
	{
		public List<XNode> nodes=new ArrayList<XNode>();
		public List<XEdge> edges=new ArrayList<XEdge>();		
	}
	
	public static  class XNode extends XType
	{
		public String edgeAnchor, weight;
		public XGraphics graphics;
	}

	public static  class XEdge extends XType
	{
		public String source, target, weight;
		public XGraphics graphics;
	}	
	
	public  static class XGraphics 
	{
		public XLine line;
		public XCenter center;
		public Map<String,String> attributes=new HashMap<String, String>();		
	}
	
	public  static class XAtt
	{
		public String name, value, label;
		public List<XAtt> atts=new ArrayList<XAtt>();		
	}
	
	public  static class XPoint
	{
		String x,y,z;
	}
	
	public  static class XCenter
	{
		String x,y,z;
	}
	
	public  static class XLine
	{
		List<XPoint> points=new ArrayList<XPoint>();
	}
	
}
