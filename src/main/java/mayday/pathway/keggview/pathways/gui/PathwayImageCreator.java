package mayday.pathway.keggview.pathways.gui;
//
//import java.awt.BasicStroke;
//import java.awt.Color;
//import java.awt.Component;
//import java.awt.Dimension;
//import java.awt.Font;
//import java.awt.Graphics2D;
//import java.awt.geom.AffineTransform;
//import java.awt.geom.Rectangle2D;
//import java.awt.image.BufferedImage;
//import java.util.Map;
//
//import javax.swing.JComponent;
//
//import mayday.core.Probe;
//import mayday.graph.Edge;
//import mayday.graph.Node;
//import mayday.graph.layout.GraphLayouter;
//import mayday.pathway.pathways.graph.CompoundNode;
//import mayday.pathway.pathways.graph.GeneNode;
//import mayday.pathway.pathways.graph.MapNode;
//import mayday.pathway.pathways.graph.PathwayGraph;
//import mayday.pathway.pathways.graph.PathwayNode;
//import mayday.pathway.pathways.graph.ReactionEdge;
//import mayday.pathway.pathways.graph.RelationEdge;
//import mayday.pathway.pathways.gui.util.SuperColorProvider;
//import mayday.pathway.vis.PathwaySettings;
//import mayday.vis3.model.ViewModel;
//
///**
// * This class works as a converter from a PathwayGraph to a BufferedImage, 
// * which can be saved or used for other purposes. The pathway is layouted using
// * the supplied GraphLayouter. 
// * The image can be customized using {@link mayday.pathway.vis.PathwaySettings}.
// * A zoomed instance can be created by altering the zoomFactor. 
// * @author Stephan Symons
// *
// */
//@SuppressWarnings("serial")
//public class PathwayImageCreator extends JComponent
//{
//
//	private PathwaySettings settings;
//	private GraphLayouter layouter;
//	private double zoomFactor;
//	
//	public PathwayImageCreator(PathwaySettings settings, GraphLayouter layouter)
//	{
//		this.settings=settings;
//		this.layouter=layouter;
//		zoomFactor=1.0;
//		setBackground(Color.white);
//	}
//	
//	public BufferedImage produceImage(PathwayGraph graph, Map<String,Probe> probeMapping, ViewModel viewModel)
//	{
//		// layout graph;
//		removeAll();
//		layouter.layoutGraph(graph, new Rectangle2D.Double());
//		SuperColorProvider coloring=new  SuperColorProvider(viewModel);
//		int maxX=0;
//		int maxY=0;
//		
//		for(Node node:graph.getNodes())
//		{
//			PathwayNode n=(PathwayNode)node;
//			if(n.getBounds().getMaxX() > maxX) maxX=(int)n.getBounds().getMaxX();
//			if(n.getBounds().getMaxY() > maxY) maxY=(int)n.getBounds().getMaxY();
//			if(graph.getDegree(n)==0) continue;
//			if(n instanceof MapNode && settings.isDisplayNeighborPathway())
//			{
//				MapComponent mc=new MapComponent((MapNode)n,null);
//				mc.setText( ((MapNode)n).getName());
//				add(mc);
//				continue;
//			}			
//			if(n instanceof CompoundNode)
//			{
//				CompoundComponent cc=new CompoundComponent((CompoundNode)n,null);
//				add(cc);
//				continue;
//			}
//			if(n instanceof GeneNode)
//			{			
//				GeneComponent gc=new GeneComponent((GeneNode)n,null);
//				gc.setRenderer(settings.getDefaultRenderer(coloring));
//				add(gc);
//				continue;
//			}
//		}
//		
//		for(int i=0; i!= getComponentCount(); ++i)
//		{
//			Component c=getComponent(i);
//			if(c instanceof GeneComponent)
//			{				
//				((GeneComponent)c).setPlotData(probeMapping);				
//			}
//		}
//		maxX+=20;
//		maxY+=20;
//		setSize(new Dimension(maxX,maxY));
//        setPreferredSize(new Dimension(maxX,maxY));
//		
//        BufferedImage res=new BufferedImage(maxX,maxY,BufferedImage.TYPE_INT_RGB);
//        
//        paintPlot(res.createGraphics(), graph);        
//        return res;
//	}
//	
//	public void paintPlot(Graphics2D g, PathwayGraph graph) 
//	{
//		
//		// apply zoom
//		g.transform(AffineTransform.getScaleInstance(zoomFactor, zoomFactor));
//
//		g.setColor(Color.white);
//		g.fillRect(0, 0, getWidth(), getHeight());
//		g.setColor(Color.lightGray);
//		g.fillRect(0, 0, getWidth(), 27);
//		Font f=g.getFont();
//		g.setFont(new Font("Arial",Font.BOLD,20));
//		g.setColor(Color.black);
//		g.drawString(graph.getTitle(), (getWidth()-g.getFontMetrics().stringWidth(graph.getTitle()))/2, 25);
//		//setup color and stroke;
//		g.setFont(f);
//		g.setColor(settings.getReactionColor());
//		g.setStroke(settings.getReactionStroke());
//        for(Edge e:graph.getEdges())
//        {
//        	if(e instanceof ReactionEdge)
//        	{
//        		g.draw(e.getShape());
//        	}
//        } 
//        if(settings.isDisplayMapLinks() && settings.isDisplayNeighborPathway())
//        {
//			g.setColor(settings.getRelationColor());
//			g.setStroke(settings.getRelationStroke());
//	        for(Edge e:graph.getEdges())
//	        {
//	        	if(e instanceof RelationEdge)
//	        	{
//	        		g.draw(e.getShape());
//	        	}
//	        } 
//        }        
//        g.setStroke(new BasicStroke(1.0f));
//		paintChildren(g);		
//	}
//}
