package mayday.graphviewer.core.bag;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import mayday.core.Probe;
import mayday.core.structures.graph.Edge;
import mayday.graphviewer.action.GraphBagModel;
import mayday.graphviewer.statistics.ResultSet;
import mayday.vis3.graph.GraphCanvas;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.components.NodeComponent;

public class ComponentBag implements Iterable<CanvasComponent>
{
	private Set<CanvasComponent> components;
	private Color color;
	private String name;
	private BagStyle style;
	private GraphBagModel model;
	private List<ResultSet> statistics; 

	
	public ComponentBag(GraphBagModel model, BagStyle style)
	{
		components=new HashSet<CanvasComponent>();
		color=new Color(150,150,255);
		this.style=style;
		this.model=model;
		statistics=new ArrayList<ResultSet>();
	}
	
	public ComponentBag(GraphBagModel model)
	{
		this(model,BagStyle.PLAIN);
	}
	
	public void addComponent(CanvasComponent comp)
	{
		components.add(comp);
		statistics.clear();
	}
	
	public Rectangle getBoundingRect()
	{
		int minX= Integer.MAX_VALUE;
		int	minY= Integer.MAX_VALUE;
		int maxX= Integer.MIN_VALUE;
		int	maxY= Integer.MIN_VALUE;
		
		for(CanvasComponent comp:components)
		{
			minX=Math.min(minX, comp.getX());
			maxX=Math.max(maxX, comp.getX()+comp.getWidth());			
			minY=Math.min(minY, comp.getY());
			maxY=Math.max(maxY, comp.getY()+comp.getHeight());
		}
		
		return new Rectangle(minX-20, minY-10, maxX-minX+30, maxY-minY+20);
	}
	
	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	public void removeComponent(CanvasComponent comp)
	{
		components.remove(comp);
		if(components.isEmpty())
			model.remove(this);
		statistics.clear();
	}

	public Iterator<CanvasComponent> iterator()
	{
		return components.iterator();
	}
	
	public void close()
	{
		for(CanvasComponent c:components)
		{
			c.setVisible(false);
			if(c.getParent() instanceof GraphCanvas)
			{
				((GraphCanvas)c.getParent()).remove(c);
			}
		}
		getModel().remove(this);
	}
		
	public void compress()
	{
		int r=(int)Math.ceil(Math.sqrt(components.size()));
		
		
		int i=0;
		int initX=getBoundingRect().x+15;
		int xspace=initX;
		int yspace=getBoundingRect().y;
		int xmax=0;
		for(CanvasComponent comp:components)
		{
			if(i % r==0)
			{
				xspace=initX;
				yspace+=xmax+15;
				xmax=0;
			}
			comp.setLocation(xspace, yspace);
			xspace+=comp.getWidth()+15;
			if(comp.getHeight()>xmax) 
				xmax=comp.getHeight();
			++i;
		}
	}
	
	public void minimizeAll()
	{
		for(CanvasComponent comp:components)
		{
			comp.setSize(5, 5);
		}
	}
	
	public void restoreAll()
	{
		for(CanvasComponent comp:components)
		{
			comp.setSize(comp.getRenderer().getSuggestedSize(((NodeComponent)comp).getNode(),null));
		}
	}
	
	public void hide()
	{
		for(CanvasComponent comp:components)
		{
			comp.setVisible(false);
		}
	}
	
	public void setComponentsVisible(boolean b)
	{
		for(CanvasComponent comp:components)
		{
			comp.setVisible(b);
		}
	}
	

	public Set<CanvasComponent> getComponents() {
		return components;
	}
	
	public List<Edge> getEdges()
	{
		List<Edge> res=new ArrayList<Edge>();
		for(Edge e:model.getEdges())
		{
			if(components.contains(model.getComponent(e.getSource())) || 
			   components.contains(model.getComponent(e.getTarget()))  )
			{
				res.add(e);
			}
		}
		return res;
	}

	public boolean isEmpty() 
	{
		return components.isEmpty();
	}
	
	public int size() 
	{
		return components.size();
	}

	public GraphBagModel getModel() {
		return model;
	}

	public void setModel(GraphBagModel model) {
		this.model = model;
	}
	
	public void addStatistic(ResultSet res)
	{
		statistics.add(res);
	}

	public List<ResultSet> getStatistics() {
		return statistics;
	}
	
	public BagStyle getStyle() {
		return style;
	}
	
	public void setStyle(BagStyle style) 
	{
		this.style = style;
	}
		
	public List<Probe> getProbes()
	{
		List<Probe> res=new ArrayList<Probe>();
		for(CanvasComponent cc: components)
		{
			if(cc instanceof MultiProbeComponent)
			{
				res.addAll(((MultiProbeComponent) cc).getProbes());
			}
		}
		return res;
	}
	
	public boolean contains(CanvasComponent cc)
	{
		return components.contains(cc);
	}
			
	public static enum BagStyle
	{
		PLAIN,
		CONVEX,
		COMPARTMENT,
		ELLIPSE,
		SUBMAP
	}
		
}
