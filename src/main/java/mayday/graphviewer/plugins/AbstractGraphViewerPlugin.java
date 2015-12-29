package mayday.graphviewer.plugins;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import mayday.core.Probe;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.GraphModel;

public abstract class AbstractGraphViewerPlugin extends AbstractPlugin implements GraphViewerPlugin 
{

	@Override
	public abstract void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components);

	public Rectangle getBoundingRect(Collection<CanvasComponent> comps)
	{
		Rectangle r=null;
		boolean first=true;
		for(CanvasComponent c:comps)
		{
			if(first)
			{
				first=false;
				r=new Rectangle(c.getLocation(), new Dimension(c.getWidth(), c.getHeight()));
				continue;
			}
			r.add(c.getBounds());			
		}		
		return r;
	}

	public List<Probe> collectProbes(List<CanvasComponent> comps)
	{
		List<Probe> res=new ArrayList<Probe>();
		for(CanvasComponent cc: comps)
		{
			if(cc instanceof MultiProbeComponent)
			{
				res.addAll(((MultiProbeComponent) cc).getProbes());
			}
		}
		return res;
	}

	public void placeComponents(List<CanvasComponent> components, GraphViewerPlot viewer, int xspace, int yspace)
	{
		int x=xspace;
		int y=viewer.getComponentMaxY()+yspace;
		int maxW=viewer.getWidth();
		int h=0;
		for(CanvasComponent cc:components)
		{
			cc.setLocation(x, y);
			x+=cc.getWidth()+xspace;
			h=Math.max(h, cc.getHeight());
			if(x>maxW)
			{
				x=xspace;
				y+=yspace+h;
				h=0;
			}			
		}
		viewer.updateSize();
	}

	/**
	 * Place the components in <code>components</code> in <code>viewer</code>. Components are mapped to already laid out components via <code> originals</code>
	 * unmapped components are then placed via placeComponents(List, GraphViewerPlot, int, int);
	 * @param components
	 * @param originals
	 * @param viewer
	 * @param xspace
	 * @param yspace
	 */
	public void placeComponents(List<CanvasComponent> components, Map<CanvasComponent, CanvasComponent> originals, GraphViewerPlot viewer, int xspace, int yspace)
	{
		int y=viewer.getComponentMaxY()+yspace;
		List<CanvasComponent> orphans=new ArrayList<CanvasComponent>();
		for(CanvasComponent cc:components)
		{
			if(originals.containsKey(cc))
			{
				Point p=new Point(originals.get(cc).getLocation());
				p.y+=y;
				p.x+=xspace;
				cc.setLocation(p);	
			}
			else
				orphans.add(cc);
		}
		if(!orphans.isEmpty())
			placeComponents(orphans, viewer, xspace, yspace);
		viewer.updateSize();
	}

	public static MultiHashMap<String, Probe> groupProbesByDataSet(List<CanvasComponent> components)
	{
		MultiHashMap<String, Probe> res=new MultiHashMap<String, Probe>();
		for(CanvasComponent cc:components)
		{
			if(cc instanceof MultiProbeComponent)
			{
				for(Probe p:((MultiProbeComponent) cc).getProbes())
				{
					res.put(p.getMasterTable().getDataSet().getName(), p);
				}
			}
		}
		return res;
	}

	public static MultiHashMap<String, Probe> groupProbesByNode(List<CanvasComponent> components)
	{
		MultiHashMap<String, Probe> res=new MultiHashMap<String, Probe>();
		for(CanvasComponent cc:components)
		{
			if(cc instanceof MultiProbeComponent)
			{
				for(Probe p:((MultiProbeComponent) cc).getProbes())
				{
					res.put(cc.getLabel(), p);
				}
			}
		}
		return res;
	}

	public static MultiHashMap<String, Probe> groupProbesByNodeRole(List<CanvasComponent> components)
	{
		MultiHashMap<String, Probe> res=new MultiHashMap<String, Probe>();
		for(CanvasComponent cc:components)
		{
			if(cc instanceof MultiProbeComponent)
			{
				for(Probe p:((MultiProbeComponent) cc).getProbes())
				{
					res.put(((MultiProbeComponent) cc).getNode().getRole(), p);
				}
			}
		}
		return res;
	}

	public static MultiHashMap<String, Probe> groupProbesByName(List<CanvasComponent> components)
	{
		MultiHashMap<String, Probe> res=new MultiHashMap<String, Probe>();
		for(CanvasComponent cc:components)
		{
			if(cc instanceof MultiProbeComponent)
			{
				for(Probe p:((MultiProbeComponent) cc).getProbes())
				{
					res.put(p.getName(), p);
				}
			}
		}
		return res;
	}

	public static MultiHashMap<String, Probe> groupProbesByDisplayName(List<CanvasComponent> components)
	{
		MultiHashMap<String, Probe> res=new MultiHashMap<String, Probe>();
		for(CanvasComponent cc:components)
		{
			if(cc instanceof MultiProbeComponent)
			{
				for(Probe p:((MultiProbeComponent) cc).getProbes())
				{
					res.put(p.getDisplayName(), p);
				}
			}
		}
		return res;
	}


}
