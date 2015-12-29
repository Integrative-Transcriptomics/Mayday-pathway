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
import mayday.graphviewer.core.SBGNRoles;
import mayday.graphviewer.core.Utilities;
import mayday.vis3.graph.layout.CanvasLayouterPlugin;
import mayday.vis3.graph.model.GraphModel;

public class TransProtMetaLayouter extends CanvasLayouterPlugin
{
	private int leftSpace=50;
	private int topSpace=50;
	private int tlSpace=40; // space for translation
	private int prSpace=100; // space between protein and reaction
	
	private int rHeight=200;
//	private int rcomp=75;
	private int rWidth=300;
	private int rspace=20;
	
	private int yStep=30;
	
	@Override
	public void layout(Container container, Rectangle bounds, GraphModel model) 
	{
		Graph g=model.getGraph();
		Set<Node> notPlaced=new HashSet<Node>(g.getNodes());
		int y=topSpace;
		
		for(Node rna: g.getNodes())
		{
			
			if(rna.getRole().equals(SBGNRoles.NUCLEIC_ACID_FEATURE_ROLE))
			{
				int baseline=y;
				
				Node translation=g.getOutNeighbors(rna).iterator().next();
				Node protein=g.getOutNeighbors(translation).iterator().next();
				
				int reas=g.getOutNeighbors(protein).size();
				int shift=(reas* rHeight) + ((reas-1)*rspace);
				baseline+= shift/2;
				
				int x=leftSpace;
				
				// place rna, translation, protein on baseline. 
				model.getComponent(rna).setLocation(x, baseline);
				x+=model.getComponent(rna).getWidth()+tlSpace;
				// adjust for size of translation
				int yf=(model.getComponent(rna).getWidth()/2)-model.getComponent(translation).getHeight();
				
				model.getComponent(translation).setLocation(x, baseline+yf);
				x+=model.getComponent(translation).getWidth()+tlSpace;
				
				model.getComponent(protein).setLocation(x, baseline);
				
				notPlaced.remove(rna);
				notPlaced.remove(translation);
				notPlaced.remove(protein);		
				
				x+=model.getComponent(protein).getWidth()+prSpace;
				
				// place reactions
				int i=0; 
				for(Node rea: g.getOutNeighbors(protein))
				{
					int ry=(int)((i+0.5)*rHeight)+(model.getComponent(protein).getHeight()/2)-(model.getComponent(rea).getHeight()/2) ;
					ry+=y;
					
					model.getComponent(rea).setLocation(x, ry);
					notPlaced.remove(rea);
					int rx=x-(rWidth/2);
					int drx=rWidth/ (g.getInNeighbors(rea).size()-1);					
					for(Node in: g.getInNeighbors(rea))
					{
						if(in==protein || in.getRole().equals(SBGNRoles.MACROMOLECULE_ROLE))
							continue;
						//model.getComponent(in).setLocation(rx, ry-rcomp);
						rx+=drx;
						//notPlaced.remove(in);
					}
					
					rx=x+rWidth;
					int pry=(ry-rHeight/2);
					int dry=rHeight/ (g.getInNeighbors(rea).size()-1);
					//count in-neighbors:
					int numIn=0;
					for(Node in: g.getInNeighbors(rea))
					{
						if(in==protein || (!notPlaced.contains(in)) )
							continue;
						numIn++;
					}
					double[] angles=Utilities.seriesOver(numIn, Math.PI/2.0);
					int c=0;
					for(Node in: g.getInNeighbors(rea))
					{
						if(in==protein || (!notPlaced.contains(in)) )
							continue;
						double ang=Math.PI/4.0+ angles[c];
						double xp=80*Math.cos(ang);
						double yp=80*Math.sin(ang);
						
						model.getComponent(in).setLocation(
								(int) ( (x+model.getComponent(rea).getWidth()/2) + xp -model.getComponent(in).getWidth()/2 ), 
								(int)( (model.getComponent(rea).getY()+model.getComponent(rea).getHeight()/2) - yp-model.getComponent(in).getHeight()/2));
						pry+=dry;
						notPlaced.remove(in);
						c++;
					}
					
					
					rx=x-(rWidth/2);
					
					//count in-neighbors:
					int numOut=0;
					for(Node out: g.getOutNeighbors(rea))
					{
						if(out==protein || (!notPlaced.contains(out)) )
							continue;
						numOut++;
					}
					
					drx=rWidth/ (g.getOutNeighbors(rea).size());
					angles=Utilities.seriesOver(numOut, Math.PI/2.0);
					c=0;
					for(Node out: g.getOutNeighbors(rea))
					{
						if(out==protein)
							continue;
//						model.getComponent(out).setLocation(rx, ry+rcomp);
						double ang=1.25*Math.PI+ angles[c];
						double xp=80*Math.cos(ang);
						double yp=80*Math.sin(ang);
						
						model.getComponent(out).setLocation(
								(int) ( (x+model.getComponent(rea).getWidth()/2) + xp-model.getComponent(out).getWidth()/2 ),
								(int)( (model.getComponent(rea).getY()+model.getComponent(rea).getHeight()/2) - yp-model.getComponent(out).getHeight()/2));
						rx+=drx;
						notPlaced.remove(out);
						c++;
					}
					++i;
				}
				
				if(shift < rHeight)
					shift=rHeight;
				
				y+=shift+yStep;		
				
		
			}			
		}
			
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Layout.TransProtMeta",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Place nodes row-wise, each row with  transcript and protein of a specifc gene and reactions influenced by the protein",
				"Transcript-Protein-Reaction"				
		);
		return pli;	
	}
	
	@Override
	protected void initSetting() {}
}
