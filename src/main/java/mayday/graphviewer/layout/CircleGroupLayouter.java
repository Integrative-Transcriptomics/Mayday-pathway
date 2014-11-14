package mayday.graphviewer.layout;

import java.awt.Container;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.pathway.viewer.canvas.AlignLayouter;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.layout.FruchtermanReingoldLayout;
import mayday.vis3.graph.layout.LayoutUtilities;
import mayday.vis3.graph.layout.SimpleCircularLayout;
import mayday.vis3.graph.model.DefaultGraphModel;
import mayday.vis3.graph.model.GraphModel;

public class CircleGroupLayouter extends GroupAndSortLayout {

	private String[] styles={"Grid", "Smart Cricles", "Cocentric Circles", "Circle of Circles", "Force Based Circles"};	
	private RestrictedStringSetting styleSetting=new RestrictedStringSetting("Style", null, 0, styles);
	private IntSetting minimumRadius=new IntSetting("Default Circle Radius",null,300);

	private static final int SPACER=40;
	private static final int WITHIN_SPACER=3;

	public CircleGroupLayouter() 
	{
		initSetting();
	}

	@Override
	protected void placeGroups(List<List<CanvasComponent>> components,Container container, Rectangle bounds, GraphModel model) 
	{
		if(styleSetting.getSelectedIndex() == 0)
		{
			doGridLayout(components,container,bounds,model);
		}
		if(styleSetting.getSelectedIndex() == 1)
		{
			doSmartLayout(components,container,bounds,model);
		}
		if(styleSetting.getSelectedIndex() == 2)
		{
			doConcentricLayout(components,container,bounds,model);
		}

		if(styleSetting.getSelectedIndex() == 3)
		{
			doCircleCircleLayout(components,container,bounds,model);
		}	
		if(styleSetting.getSelectedIndex() == 4)
		{
			doForceBasedLayout(components, container, bounds, model);
		}

		new AlignLayouter().layout(container, bounds, model);

	}

	private void doSmartLayout(List<List<CanvasComponent>> components,Container container, Rectangle bounds, GraphModel model)
	{
		int y=SPACER; 
		int x=SPACER/2; 

		double rmax=0;
		for(List<CanvasComponent> cmp:components)
		{	
			double rad=0;
			if(cmp.size() ==1)
			{
				rad=cmp.get(0).getWidth()*2;
				cmp.get(0).setLocation((int) (x+rad/2), (int)(y+rad/2));				
			}else
			{
				int m=0;
				for(CanvasComponent cc:cmp)
				{
					m=Math.max(m, cc.getWidth());
					m=Math.max(m, cc.getHeight());
				}

				rad= (cmp.size()*(m+WITHIN_SPACER))/(2*Math.PI);
				rad+=m; 


				Point2D center=new Point2D.Double( x + (int)rad, y+(int)rad);
				LayoutUtilities.buildCircle(cmp, (int)rad, center);
				//				x+= 2*rad + SPACER;	

			}
			rmax=Math.max(rmax, rad);

			x+=2*rad+3*SPACER;
			System.out.println(x + "\t" + SPACER + "\t"+ bounds.width);
			if( x > (bounds.width-SPACER) )
			{
				x=SPACER/2;
				y+=2*rmax + 2*SPACER;
				rmax=0;
			}			
		}
	}

	private void doGridLayout(List<List<CanvasComponent>> components,Container container, Rectangle bounds, GraphModel model)
	{
		int y=SPACER; 
		int x=SPACER/2; 

		for(List<CanvasComponent> cmp:components)
		{			
			Point2D center=new Point2D.Double( x + minimumRadius.getIntValue(), y+minimumRadius.getIntValue());
			LayoutUtilities.buildCircle(cmp, minimumRadius.getIntValue(), center);
			x+= 2*minimumRadius.getIntValue() + SPACER;			
			if( x > (bounds.width-SPACER) )
			{
				x=SPACER/2;
				y+=2*minimumRadius.getIntValue() + SPACER;
			}			
		}
	}

	private void doConcentricLayout(List<List<CanvasComponent>> components,Container container, Rectangle bounds, GraphModel model)
	{
		int maxRad=minimumRadius.getIntValue() + components.size()*(minimumRadius.getIntValue());

		Point2D center=new Point2D.Double( maxRad+SPACER, maxRad+SPACER);
		double r=minimumRadius.getIntValue();

		for(List<CanvasComponent> cmp:components)
		{
			LayoutUtilities.buildCircle(cmp, r, center);
			r+=minimumRadius.getIntValue();
		}
	}

	private void doCircleCircleLayout(List<List<CanvasComponent>> components,Container container, Rectangle bounds, GraphModel model)
	{
		double majorRad= (2.0*(minimumRadius.getIntValue()+SPACER)*components.size())/(Math.PI*2.0);		
		double aStep=LayoutUtilities.TWO_PI/(components.size());		
		Point2D center=new Point2D.Double( majorRad+SPACER+ minimumRadius.getIntValue(), majorRad+SPACER+ minimumRadius.getIntValue());



		double angle=0;
		for(List<CanvasComponent> cmp:components)
		{			
			double xp= 0.0*Math.cos(angle) - (-1.0*majorRad)*Math.sin(angle);
			double yp= 0.0*Math.sin(angle) + (-1.0*majorRad)*Math.cos(angle);
			xp+=center.getX();
			yp+=center.getY();

			Point2D currentCenter=new Point2D.Double(xp, yp);

			LayoutUtilities.buildCircle(cmp, minimumRadius.getIntValue(), currentCenter);


			angle+=aStep;
		}
	}

	private void doForceBasedLayout(List<List<CanvasComponent>> components,Container container, Rectangle bounds, GraphModel model)
	{
		// build group intersection graph
		Graph iGraph=new Graph();
		Map<List<CanvasComponent>, Node> grpToNode=new HashMap<List<CanvasComponent>, Node>();
		for(List<CanvasComponent> grp: components)
		{
			Node n=new Node(iGraph);
			iGraph.addNode(n);
			grpToNode.put(grp,n);
		}
		// connect groups
		for(int i=0; i!= components.size(); ++i)
		{
			for(int j=i+1; j< components.size(); ++j)
			{
				List<CanvasComponent> grp1=components.get(i);
				List<CanvasComponent> grp2=components.get(j);

				boolean found=false;
				for(CanvasComponent cc1: grp1)
				{
					for(CanvasComponent cc2:grp2)
					{
						Node n1=model.getNode(cc1);
						Node n2=model.getNode(cc2);
						if(model.getGraph().isConnected(n1, n2))
						{
							found=true;
							iGraph.connect(grpToNode.get(grp1), grpToNode.get(grp2));
							break;
						}
					}
					if(found)
						break;
				}				
			}
		}
		// layout it to container respecting the bounds
		DefaultGraphModel subModel=new DefaultGraphModel(iGraph);
		new FruchtermanReingoldLayout().layout(container, bounds, subModel);

		for(List<CanvasComponent> cmp: components)
		{
			CanvasComponent placementComp= subModel.getComponent(grpToNode.get(cmp));
			Point2D center=new Point2D.Double(
					placementComp.getBounds().getBounds2D().getCenterX(),
					placementComp.getBounds().getBounds2D().getCenterY());

			LayoutUtilities.buildCircle(cmp, minimumRadius.getIntValue(), center);

		}
	}

	@Override
	protected void simpleLayout(Container container, Rectangle bounds, GraphModel model) 
	{
		new SimpleCircularLayout().layout(container, bounds, model);
	}

	@Override
	protected void initSetting() 
	{
		setting.addSetting(styleSetting).addSetting(minimumRadius);
	}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Layout.CircleGroup",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Group and sort nodes by varous criteria before placing them in circles.",
				"Group and Sort: Cricles"				
		);
		return pli;	
	}

}
