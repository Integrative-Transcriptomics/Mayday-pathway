package mayday.graphviewer.core.bag.tools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mayday.core.Probe;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.ModelHub;
import mayday.graphviewer.core.bag.BagComponent;
import mayday.graphviewer.core.bag.ComponentBag;
import mayday.vis3.graph.renderer.RendererTools;
import mayday.vis3.graph.vis3.SuperColorProvider;

@SuppressWarnings("serial")
public class BagHeatmap extends BagCentralComponent implements MouseListener
{
	private List<Probe> probes;
	
	@SuppressWarnings("unchecked")
	public BagHeatmap(ComponentBag bag, BagComponent comp) 
	{
		super(bag, comp);
		probes=bag.getProbes();
		Collections.sort(probes);
		addMouseListener(this);
	}
	
	@Override
	public void paint(Graphics g) 
	{
		List<Color> colors;
		g.setColor(Color.white);
		g.fillRect(0, 0,getWidth(), getHeight());
		ModelHub hub=((GraphViewerPlot)comp.getParent()).getModelHub();
		SuperColorProvider coloring=null;
		
		int dy=(int) ( (getHeight()) / probes.size());
		
		List<String> names=new ArrayList<String>();
		List<String> dss=new ArrayList<String>();
		int j=0;
		for(Probe p: probes)
		{
			coloring=hub.getColorProvider(p.getMasterTable().getDataSet());
			colors = coloring.getColors(p);
			Rectangle r=new Rectangle(80, (j*dy),getWidth()-160,dy);
			RendererTools.drawColorLine((Graphics2D)g, colors, r);
			++j;
			names.add(p.getDisplayName());
			dss.add(p.getMasterTable().getDataSet().getName());
		}
		g.setColor(Color.black);
		RendererTools.drawNameColumn((Graphics2D)g, names, new Rectangle(0,5,80, getHeight()));
		RendererTools.drawNameColumn((Graphics2D)g, dss, new Rectangle(getWidth()-80,5,80, getHeight()));
	}
	
	private int pointToRow(Point p)
	{
		List<Probe> probes=bag.getProbes();
		int dy=(int) ( (getHeight()) / probes.size());
		
		int i=(int)p.getY()/dy;
		return i;		
	}

	@Override
	public void mouseClicked(MouseEvent e) 
	{
		int r=pointToRow(e.getPoint());
		Probe p=probes.get(r);
		List<Probe> probes=new ArrayList<Probe>();
		probes.add(p);
		selectProbes(probes);
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}
	
	@Override
	public void mousePressed(MouseEvent event) 	{}
	

	
}
