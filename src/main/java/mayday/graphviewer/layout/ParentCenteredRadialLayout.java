package mayday.graphviewer.layout;

import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.graphviewer.util.components.DegreeComparator;
import mayday.pathway.viewer.canvas.AlignLayouter;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.layout.CanvasLayouterPlugin;
import mayday.vis3.graph.model.GraphModel;

public class ParentCenteredRadialLayout extends CanvasLayouterPlugin
{
	private Node root;
	
	//settings:
	private IntSetting radius=new IntSetting("Intitial Radius",null,400);
	private DoubleSetting foldAngle=new DoubleSetting("Child Angle",null,Math.PI/1.5d,Math.PI/10.0d,Math.PI*2.0d,true,false);
	private DoubleSetting shrinkFactor= new DoubleSetting("Shrink Factor", null, 1.0, 0.5, 1.5,true,true);
	
	public ParentCenteredRadialLayout() 
	{
		
	}
	
	public ParentCenteredRadialLayout(Node r) 
	{
		root=r;
	}
	
	@Override
	public void layout(Container container, Rectangle bounds, GraphModel model) 
	{
		Graph g=model.getGraph();
		if(!g.contains(root))
		{
			root=null;
		}
		
		if(root==null)
		{
			Comparator<CanvasComponent> cmp=new DegreeComparator(DegreeComparator.OUT_DEGREE);
			CanvasComponent c=model.getComponents().get(0);
			
			for(CanvasComponent comp:model.getComponents())
			{
				if( cmp.compare(comp,c) >0)
				{
					c=comp;
				}
			}
			root=model.getNode(c);
		}
		
		Graph t=new BreadthFirstSpanningTree().getSpanningTree(g, root);		
		Map<Node, Point2D> parentCenterdCoordinates=new HashMap<Node, Point2D>();		
		placeTheRoot(t, root, parentCenterdCoordinates);
		

		for(Node n:t.getNodes())
		{
			Point2D p=parentCenterdCoordinates.get(n);
			double r=p.getY();
			double phi=p.getX();
			Point pc=new Point(
					(int) (r*Math.cos(phi)),
					(int) (r*Math.sin(phi)) );
			model.getComponent(n).setLocation(pc);
			
		}
		
		translate(t,root,model);
		new AlignLayouter(50).layout(container, bounds, model);
	}
	
	private void translate(Graph t, Node parent, GraphModel model)
	{
		Point pp=model.getComponent(parent).getLocation();
		
		for(Node n: t.getOutNeighbors(parent))
		{
			Point pc=model.getComponent(n).getLocation();
			pc.x=pc.x+pp.x;
			pc.y=pc.y+pp.y;
			model.getComponent(n).setLocation(pc);
			
			translate(t,n,model);
		}
	}
	
	private void placeTheRoot(Graph t, Node node, Map<Node, Point2D> points)
	{
		points.put(node, new Point2D.Double(0,0));
		int m=t.getOutDegree(node);
		double angle=2*Math.PI/(1.0*m);
		int i=0;
		
		for(Node v:t.getOutNeighbors(node))
		{
			place(t,v,(1.0*i*angle),radius.getIntValue(),angle,points);
			++i;
		}
	}
	
	private void place(Graph t, Node node, double angle, double r, double parentStep,Map<Node, Point2D> points)
	{
		points.put(node, new Point2D.Double(angle,r));
		int m=t.getOutDegree(node);
		double a=foldAngle.getDoubleValue();
		double min=0;
		double step=0;
		if(m==1)
		{
			min=angle;
			step=0;
		}else
		{ 
			min=angle-a/2;
			step=a/(m-1);
		}
		double rnew=shrinkFactor.getDoubleValue()*(2.0*Math.PI*r)* ((parentStep/2.0)/ (2.0*Math.PI) );
		int i=0;
		for(Node v:t.getOutNeighbors(node))
		{
			place(t,v,min+i*step,rnew,step,points);
			++i;
		}
	}
	

	
	@Override
	protected void initSetting() 
	{
		setting.addSetting(radius).addSetting(foldAngle).addSetting(shrinkFactor);		
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Layout.ParentCenteredRadial",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Parent Centered Radial Layout",
				"Parent Centered Radial Layout"				
		);
		return pli;	
	}
	
	public void setRadius(int r)
	{
		radius.setIntValue(r);
	}
	
	public void setChildAngle(double d)
	{
		foldAngle.setDoubleValue(d);
	}
	
	public void setShrinkFactor(double d)
	{
		shrinkFactor.setDoubleValue(d);
	}
	
	public void setRoot(Node r)
	{
		this.root=r;
	}
	
}
