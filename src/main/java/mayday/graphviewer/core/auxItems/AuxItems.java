package mayday.graphviewer.core.auxItems;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;


public enum AuxItems 
{	
	STAR(Keys.STAR,new StarAuxItemRenderer(), Color.black,Color.yellow,""),
	CIRCLE(Keys.CIRCLE,new CircleAuxItemRenderer(), Color.black,Color.green,""),
	BOX(Keys.BOX,new BoxAuxItemRenderer(), Color.black,Color.blue,""),
	TRIANGLE(Keys.TRIANGLE,new PolygonAuxItemRenderer(3), Color.black,Color.blue,""),
	WARNING(Keys.WARNING,new PolygonAuxItemRenderer(3), Color.black,Color.orange,"!"),
	QUESTION(Keys.QUESTION,new CircleAuxItemRenderer(), Color.red,Color.lightGray,"?"),
	NOTE(Keys.NOTE,new BoxAuxItemRenderer()),
	IMPORTANT(Keys.IMPORTANT,new BoxAuxItemRenderer(), Color.white,Color.red,"!"),
	UNIT_OF_INFORMATION(Keys.UNIT_OF_INFORMATION,new BoxAuxItemRenderer(), Color.black,Color.white,""),
	STATE_VARIABLE(Keys.STATE_VARIABLE,new CircleAuxItemRenderer(), Color.black,Color.white,"");
	
	private AuxItemRenderer renderer;
	private Color lineColor=Color.black;
	private Color fillColor=Color.white;
	private String defaultText="";
	private String key;
	
	public static final Map<String, AuxItems> keyMap=getKeyMap();
	
	private AuxItems(String key, AuxItemRenderer renderer, Color lineColor,	Color fillColor, String defaultText) 
	{
		this.renderer = renderer;
		this.lineColor = lineColor;
		this.fillColor = fillColor;
		this.defaultText = defaultText;
	}

	private AuxItems(String key,AuxItemRenderer renderer) 
	{
		this.renderer = renderer;		
	}
	
	public static class Keys
	{
		public static final String STAR="!star";
		public static final String CIRCLE="!circle";
		public static final String BOX="!box";
		public static final String TRIANGLE="!triangle";
		public static final String WARNING="!warning";
		public static final String QUESTION="!question";
		public static final String NOTE="!note";
		public static final String IMPORTANT="!important";
		public static final String UNIT_OF_INFORMATION="!UofI";
		public static final String STATE_VARIABLE="!state";
		
		public static final String[] KEYS=new String[]{STAR, CIRCLE, BOX, TRIANGLE, WARNING, QUESTION, NOTE, IMPORTANT, UNIT_OF_INFORMATION, STATE_VARIABLE};
	}

	public AuxItemRenderer getRenderer() 
	{
		return renderer;
	}

	public Color getLineColor() 
	{
		return lineColor;
	}

	public Color getFillColor() 
	{
		return fillColor;
	}

	public String getDefaultText() 
	{
		return defaultText;
	}

	public String getKey() {
		return key;
	}
	
	private static Map<String, AuxItems> getKeyMap()
	{
		Map<String, AuxItems> res=new HashMap<String, AuxItems>();		
		res.put(Keys.STAR,STAR);
		res.put(Keys.CIRCLE,CIRCLE);
		res.put(Keys.BOX,BOX);
		res.put(Keys.TRIANGLE,TRIANGLE);
		res.put(Keys.WARNING,WARNING);
		res.put(Keys.QUESTION,QUESTION);
		res.put(Keys.NOTE,NOTE);
		res.put(Keys.IMPORTANT,IMPORTANT);
		res.put(Keys.UNIT_OF_INFORMATION,UNIT_OF_INFORMATION);
		res.put(Keys.STATE_VARIABLE,STATE_VARIABLE);
		return res;
	}
	
	
	
	
}
