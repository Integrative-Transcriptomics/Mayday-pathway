package mayday.pathway.sbgn.processdiagram.operators;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

import mayday.core.structures.graph.Graph;
import mayday.pathway.sbgn.graph.SBGNNode;
import mayday.pathway.sbgn.processdiagram.entitypool.EntityPoolNode;
import mayday.pathway.sbgn.processdiagram.process.Transition;

public abstract class LogicGateNode extends SBGNNode 
{
	public static final int AND_GATE=0;
	public static final int OR_GATE=1;
	public static final int NOT_GATE=2;
	
	protected int gateType;
	
	protected EntityPoolNode modulating1;
	protected EntityPoolNode modulating2;
	protected Transition modulated;
	
	public LogicGateNode(Graph graph, String name) 
	{
		super(graph, name);		
	}
	

	/* (non-Javadoc)
	 * @see sbgn.graph.SBGNNode#getGlyph()
	 */
	public Shape getGlyph() 
	{
		Path2D res=new Path2D.Float();
		res.append(new Ellipse2D.Float(25,0,50,50),false);
		res.append(new Line2D.Float(0,10,30,10),false);
		res.append(new Line2D.Float(0,40,30,40),false);
		res.append(new Line2D.Float(75,25,100,25),false);		
		return res;

	}
	

	/* (non-Javadoc)
	 * @see sbgn.graph.SBGNNode#getLabelBounds()
	 */
	public Rectangle getLabelBounds()
	{
		return new Ellipse2D.Float(25,0,50,50).getBounds();
	}
}
