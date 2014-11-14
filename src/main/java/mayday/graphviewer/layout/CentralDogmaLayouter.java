package mayday.graphviewer.layout;

import java.awt.Container;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.pathway.sbgn.processdiagram.ProcessDiagram;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.layout.CanvasLayouterPlugin;
import mayday.vis3.graph.model.GraphModel;

public class CentralDogmaLayouter  extends CanvasLayouterPlugin
{
	private int leftSpace=50;
	private int topSpace=50;
	private int betweenSpace=120;
	private int yStep=30;
	
	@Override
	public void layout(Container container, Rectangle bounds, GraphModel model) 
	{
		Graph g=model.getGraph();
		
		Set<Node> notPlaced=new HashSet<Node>(g.getNodes());
		
		int y=topSpace;
		
		for(Node rna: g.getNodes())
		{
			if(rna.getRole().equals(ProcessDiagram.NUCLEIC_ACID_FEATURE_ROLE))
			{
				int x=leftSpace;
				Node translation=g.getOutNeighbors(rna).iterator().next();
				Node protein=g.getOutNeighbors(translation).iterator().next();
				
				model.getComponent(rna).setLocation(x, y);
				x+=model.getComponent(rna).getWidth()+betweenSpace;
				
				int yf=(model.getComponent(rna).getWidth()/2)-model.getComponent(translation).getHeight();
				
				model.getComponent(translation).setLocation(x, y+yf);
				x+=model.getComponent(translation).getWidth()+betweenSpace;
				
				model.getComponent(protein).setLocation(x, y);
				
				y+=model.getComponent(protein).getHeight()+yStep;		
				
				notPlaced.remove(rna);
				notPlaced.remove(translation);
				notPlaced.remove(protein);				
			}			
		}
		y+=2*yStep;
		int x=leftSpace;
				
		for(Node n: notPlaced)
		{
			CanvasComponent comp=model.getComponent(n);
			comp.setLocation(x,y);
			x+=comp.getWidth()+20;
			if(x>bounds.getWidth())
			{
				x=20;
				y+=20+comp.getHeight();
			}
		}		
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Layout.CentralDogma",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Place nodes row-wise, each row with  transcript and protein of a specifc gene",
				"Central Dogma"				
		);
		return pli;	
	}
	
	@Override
	protected void initSetting() {}
}
