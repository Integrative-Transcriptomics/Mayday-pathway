package mayday.pathway.sbgn.processdiagram;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import mayday.core.structures.graph.edges.Edges;
import mayday.vis3.graph.arrows.ArrowSettings;
import mayday.vis3.graph.arrows.ArrowStyle;

public class ProcessDiagram 
{
	public static final String SOURCE_ROLE="Source";
	public static final String SINK_ROLE="Sink";
	public static final String TAG_ROLE="Tag";	
	
	@Deprecated
	public static final String OBSERVALBE_ROLE="Observable";
	//new as per sbgn pd 1.1
	public static final String PHENOTYPE_ROLE="Phenotype";
	@Deprecated
	public static final String PERTUBATION_ROLE="Pertubation";
	public static final String PERTUBING_AGENT_ROLE="Pertubing agent";
	public static final String COMPLEX_ROLE="Complex";
	public static final String MULTIMER_COMPLEX_ROLE="Multimer Complex";

	public static final String NUCLEIC_ACID_FEATURE_ROLE="Nucleic Acid Feature";
	public static final String MULTIMER_NUCLEIC_ACID_FEATURE_ROLE="Multimer Nucleic Acid Feature";

	public static final String MACROMOLECULE_ROLE="Macromolecule";
	public static final String MULTIMER_MACROMOLECULE_ROLE="Multimer Macromolecule";

	public static final String SIMPLE_CHEMICAL_ROLE="Simple Chemical"; 
	public static final String MULTIMER_SIMPLE_CHEMICAL_ROLE="Multimer Simple Chemical";

	public static final String LOGIC_AND_ROLE="Logic AND";
	public static final String LOGIC_NOT_ROLE="Logic NOT";
	public static final String LOGIC_OR_ROLE="Logic OR";

	public static final String ASSOCIATION_ROLE="Association";
	public static final String UNCERTAIN_PROCESS_ROLE="Uncertain Process";
	//new as per sbgn pd 1.1
	public static final String PROCESS_ROLE="Process";
	@Deprecated
	public static final String TRANSITION_ROLE="Transition";
	
	public static final String OMITTED_PROCESS_ROLE="Omitted Process";
	public static final String DISSOCIATION_ROLE="Dissociation";

	public static final String[] ROLES={SOURCE_ROLE,SINK_ROLE,TAG_ROLE,PHENOTYPE_ROLE,PERTUBING_AGENT_ROLE,COMPLEX_ROLE,MULTIMER_COMPLEX_ROLE,
		NUCLEIC_ACID_FEATURE_ROLE,MULTIMER_NUCLEIC_ACID_FEATURE_ROLE,MACROMOLECULE_ROLE,MULTIMER_MACROMOLECULE_ROLE,SIMPLE_CHEMICAL_ROLE,
		MULTIMER_SIMPLE_CHEMICAL_ROLE,LOGIC_AND_ROLE,LOGIC_NOT_ROLE,LOGIC_OR_ROLE,ASSOCIATION_ROLE,UNCERTAIN_PROCESS_ROLE,PROCESS_ROLE,
		OMITTED_PROCESS_ROLE,DISSOCIATION_ROLE};


	public static final String UNIT_OF_INFORMATION_KEY="UofI";
	public static final String STATE_INFORMATION_KEY="State Variable";
	public static final String CLONE_MARKER_KEY="Clone Marker";

	public static final String CLONE_MARKER_PRESENT_VALUE="true";
	public static final String CLONE_MARKER_ABSENT_VALUE="false";

	public  static final String CATALYSIS_ROLE="Catalysis";
	public  static final String CONSUMPTION_ROLE="Consumption";
	public  static final String EQUIVALENCE_ROLE="Equivalence";
	public  static final String INHIBITION_ROLE="Inhibition";
	public  static final String LOGIC_ROLE="Logic";
	public  static final String MODULATION_ROLE="Modulation";
	public  static final String PRODUCTION_ROLE="Production";
	public  static final String STIMULATION_ROLE="Stimulation";
	// new as in sbgn pd 1.1
	public  static final String NECESSARY_STIMULATION_ROLE="Necessary Stimulation";
	public  static final String TRIGGER_ROLE="Trigger";
	
	public static final String[] EDGE_ROLES={CATALYSIS_ROLE,CONSUMPTION_ROLE,EQUIVALENCE_ROLE,INHIBITION_ROLE,
		LOGIC_ROLE,MODULATION_ROLE,PRODUCTION_ROLE,STIMULATION_ROLE,NECESSARY_STIMULATION_ROLE};

	public static boolean isSBGNEdgeRole(String role)
	{
		for(int i=0; i!=  EDGE_ROLES.length; ++i)
		{
			if(EDGE_ROLES[i].equals(role)) return true;
		}
		return false;
	}

	public static boolean isEntityPoolNode(String role)
	{
		if(role.equals(SOURCE_ROLE) ||
				role.equals(SINK_ROLE) ||	
				role.equals(TAG_ROLE)	 ||
				role.equals(PHENOTYPE_ROLE) ||
				role.equals(PERTUBING_AGENT_ROLE) ||
				role.equals(COMPLEX_ROLE) ||
				role.equals(MULTIMER_COMPLEX_ROLE) ||
				role.equals(NUCLEIC_ACID_FEATURE_ROLE) ||
				role.equals(MULTIMER_NUCLEIC_ACID_FEATURE_ROLE) ||
				role.equals(MACROMOLECULE_ROLE) ||
				role.equals(MULTIMER_MACROMOLECULE_ROLE) ||
				role.equals(SIMPLE_CHEMICAL_ROLE) ||
				role.equals(MULTIMER_SIMPLE_CHEMICAL_ROLE)	)
		{
			return true;
		}
		return false;
	}
	
	public static boolean isLogic(String role)
	{
		if(role.equals(LOGIC_AND_ROLE) ||
				role.equals(LOGIC_OR_ROLE) ||	
				role.equals(LOGIC_NOT_ROLE)	)
		{
			return true;
		}
		return false;
	}
	
	public static boolean isTransition(String role)
	{
		if(role.equals(ASSOCIATION_ROLE) ||
				role.equals(UNCERTAIN_PROCESS_ROLE) ||	
				role.equals(PROCESS_ROLE) ||	
				role.equals(OMITTED_PROCESS_ROLE) ||	
				role.equals(DISSOCIATION_ROLE))
		{
			return true;
		}
		return false;
	}
	

	public static ArrowSettings getArrowSettings(String role)
	{
		ArrowSettings s=new ArrowSettings();
		if(role.equals(CATALYSIS_ROLE))
		{
			s.setRenderTarget(true);
			s.setFillTarget(false);
			s.setTargetAngle(Math.toRadians(45));
			s.setTargetStyle(ArrowStyle.ARROW_CIRCLE);
			return s;
		}
		if(role.equals(CONSUMPTION_ROLE) || role.equals(EQUIVALENCE_ROLE)|| role.equals(LOGIC_ROLE))
		{
			s.setRenderTarget(false);
			return s;
		}
		if(role.equals(INHIBITION_ROLE))
		{
			s.setRenderTarget(true);
			s.setFillTarget(false);
			s.setTargetAngle(Math.toRadians(45));
			s.setTargetStyle(ArrowStyle.ARROW_BAR);
			return s;
		}
		if(role.equals(MODULATION_ROLE))
		{
			s.setRenderTarget(true);
			s.setFillTarget(false);
			s.setTargetAngle(Math.toRadians(45));
			s.setTargetStyle(ArrowStyle.ARROW_DIAMOND);
		}
		if(role.equals(PRODUCTION_ROLE))
		{
			s.setRenderTarget(true);
			s.setFillTarget(true);
			s.setTargetAngle(Math.toRadians(45));
			s.setTargetStyle(ArrowStyle.ARROW_TRIANGLE);
			s.setFillColor(Color.black);
			return s;
		}
		if(role.equals(STIMULATION_ROLE))
		{
			s.setRenderTarget(true);
			s.setFillTarget(false);
			s.setTargetAngle(Math.toRadians(45));
			s.setTargetStyle(ArrowStyle.ARROW_TRIANGLE);
			return s;
		}
		if(role.equals(NECESSARY_STIMULATION_ROLE))
		{
			s.setRenderTarget(true);
			s.setFillTarget(false);
			s.setTargetAngle(Math.toRadians(45));
			s.setTargetStyle(ArrowStyle.ARROW_BAR_AND_TRIANGLE);
			return s;
		}
		return s;
	}


	public static boolean isSBGNRole(String role)
	{
		for(int i=0; i!=  ROLES.length; ++i)
		{
			if(ROLES[i].equals(role)) return true;
		}
		return false;
	}

	public static Shape getGlyph(String role)
	{
		if(role.equals(SOURCE_ROLE) || role.equals(SINK_ROLE))
		{
			Path2D res=new Path2D.Float();
			res.append(new Ellipse2D.Float(1,1,50,50),false);
			res.append(new Line2D.Float(50,1,1,50), false);
			return res;				
		}
		if(role.equals(TAG_ROLE))
		{
			Path2D res=new Path2D.Float();
			res.append(new Line2D.Float(1,1,60,1), true);
			res.append(new Line2D.Float(60,1,80,25), true);
			res.append(new Line2D.Float(80,25,60,50), true);
			res.append(new Line2D.Float(60,50,1,50), true);
			res.append(new Line2D.Float(1,50,1,1), true);
			return res;
		}
		if(role.equals(PHENOTYPE_ROLE))
		{
			Path2D res=new Path2D.Float();
			res.append(new Line2D.Float(1,30,20,1), true);
			res.append(new Line2D.Float(20,1,60,1), true);
			res.append(new Line2D.Float(60,1,80,30), true);
			res.append(new Line2D.Float(80,30,60,60), true);
			res.append(new Line2D.Float(60,60,20,60), true);
			res.append(new Line2D.Float(20,60,1,30), true);
			return res;
		}
		if(role.equals(PERTUBATION_ROLE) || role.equals(PERTUBING_AGENT_ROLE))
		{
			Path2D res=new Path2D.Float();
			res.append(new Line2D.Float(1,1,80,1), true);
			res.append(new Line2D.Float(80,1,60,25), true);
			res.append(new Line2D.Float(60,25,80,50), true);
			res.append(new Line2D.Float(80,50,1,50), true);
			res.append(new Line2D.Float(1,50,20,25), true);
			res.append(new Line2D.Float(20,25,1,1), true);
			return res;
		}
		if(role.equals(NUCLEIC_ACID_FEATURE_ROLE) || role.equals(MULTIMER_NUCLEIC_ACID_FEATURE_ROLE))
		{
			Path2D path=new Path2D.Float();
			path.moveTo(1, 1);
			path.lineTo(80, 1);
			path.lineTo(80, 45);
			path.quadTo(80, 55, 70, 55);
			path.lineTo(10, 55);
			path.quadTo(1, 55, 1, 45);
			path.lineTo(1, 1);
			return path;
		}
		if(role.equals(COMPLEX_ROLE)|| role.equals(MULTIMER_COMPLEX_ROLE))
		{
			Path2D path=new Path2D.Float();
			path.moveTo(20, 1);
			path.lineTo(60, 1);
			path.lineTo(80, 20);
			path.lineTo(80, 40);
			path.lineTo(60, 60);
			path.lineTo(20, 60);
			path.lineTo(1, 40);
			path.lineTo(1, 20);
			path.closePath();
			return path;
		}
		if(role.equals(MACROMOLECULE_ROLE) || role.equals(MULTIMER_MACROMOLECULE_ROLE))
		{
			RoundRectangle2D glyph=new RoundRectangle2D.Float(0,0,80,55,15,15);
			return glyph;
		}
		if(role.equals(SIMPLE_CHEMICAL_ROLE) || role.equals(MULTIMER_SIMPLE_CHEMICAL_ROLE))
		{
			Ellipse2D glyph=new Ellipse2D.Float(0,0,50,50);			
			return glyph;
		}
		if(role.equals(LOGIC_AND_ROLE) || role.equals(LOGIC_NOT_ROLE) || role.equals(LOGIC_OR_ROLE))
		{
			Path2D res=new Path2D.Float();
			res.append(new Ellipse2D.Float(25,0,50,50),false);
			res.append(new Line2D.Float(0,10,30,10),false);
			res.append(new Line2D.Float(0,40,30,40),false);
			res.append(new Line2D.Float(75,25,100,25),false);		
			return res;
		}

		if(role.equals(ASSOCIATION_ROLE))
		{
			Path2D res=new Path2D.Float();
			res.append(new Ellipse2D.Float(1.0f, 1.0f,15.0f, 15.0f), false);
			return res;
		}
		if(role.equals(UNCERTAIN_PROCESS_ROLE))
		{
			Path2D res=new Path2D.Float();
			res.append(new Rectangle2D.Float(1.0f, 1.0f, 15.0f, 15.0f),false);
			res.append(new QuadCurve2D.Float(4.5f, 2.5f, 7.0f, 0.0f, 9.5f, 2.5f), false);
			res.append(new QuadCurve2D.Float(9.5f, 2.5f, 7.0f,5.0f, 7.0f, 7.5f),true);
			res.append(new Ellipse2D.Float(7.0f, 10.5f, 1.0f,1.0f),false);
			return res;
		}
		if(role.equals(PROCESS_ROLE))
		{
			Path2D res=new Path2D.Float();
			res.append(new Rectangle2D.Float(0f, 0f, 15.0f, 15.0f),false);
			return res;
		}
		if(role.equals(OMITTED_PROCESS_ROLE))
		{
			Path2D res=new Path2D.Float();
			res.append(new Rectangle2D.Float(1.0f, 1.0f, 15.0f, 15.0f),false);
			res.append(new Line2D.Float(4.0f, 12.0f, 8.0f, 2.0f), false);
			res.append(new Line2D.Float(8.0f, 12.0f, 12.0f, 2.0f), false);
			return res;
		}
		if(role.equals(DISSOCIATION_ROLE))
		{
			Path2D res=new Path2D.Float();
			res.append(new Ellipse2D.Float(1.0f, 1.0f,15.0f, 15.0f), false);
			res.append(new Ellipse2D.Float(4f, 4f,9.0f, 9.0f), false);
			return res;
		}
		return null;
	}

	public static Area defaultCloneMarker(Shape glyph)
	{
		Area cloneMarkerGlyph=new Area(glyph);
		Rectangle r=glyph.getBounds();
		Area b=new Area(new Rectangle2D.Float(r.x,(int)(0.80*r.height),r.width,(int)(0.20*r.height)));
		cloneMarkerGlyph.intersect(b);		
		return cloneMarkerGlyph;
	}
	
	public static String guessEdgeRole(String role1, String role2)
	{
		if(role1.equals(SIMPLE_CHEMICAL_ROLE))
		{
			if(role2.equals(PROCESS_ROLE))
				return CONSUMPTION_ROLE;			
		}
		if(role1.equals(SOURCE_ROLE))
		{
			if(role2.equals(PROCESS_ROLE))
				return CONSUMPTION_ROLE;			
		}
		if(role1.equals(PROCESS_ROLE))
		{
			if(role2.equals(SIMPLE_CHEMICAL_ROLE))
				return PRODUCTION_ROLE;
			if(role2.equals(MACROMOLECULE_ROLE))
				return PRODUCTION_ROLE;
			if(role2.equals(SINK_ROLE))
				return PRODUCTION_ROLE;
		}
		if(role1.equals(MACROMOLECULE_ROLE))
		{	
			if(role2.equals(PROCESS_ROLE))
				return CATALYSIS_ROLE;
			
		}
		return Edges.Roles.EDGE_ROLE;
		
		
	}


}
