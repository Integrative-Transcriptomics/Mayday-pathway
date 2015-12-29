package mayday.pathway.sbgn.processdiagram.container;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import mayday.core.structures.graph.Graph;
import mayday.pathway.sbgn.graph.SBGNNode;
import mayday.pathway.sbgn.processdiagram.entitypool.EntityPoolNode;
import mayday.pathway.sbgn.processdiagram.entitypool.StatefulEntityPoolNode;

public class Complex extends StatefulEntityPoolNode
{
	/** Components that form the complex */
	protected List<EntityPoolNode> subunits;
	
	public Complex(Graph graph, String name) 
	{
		super(graph, name);
		subunits=new ArrayList<EntityPoolNode>();
		setRole("Complex");		
	}
	
	public void addComponent(EntityPoolNode comp)
	{
		subunits.add(comp);
	}
	
	public int componentCount()
	{
		return subunits.size();
	}

	@Override
	public Shape getGlyph() 
	{
		int d = (int) Math.ceil(Math.sqrt(componentCount()));
		int n=0;
		int wMax=10; 
		int h=23;
		for(int i=0; i!= d; ++i)
		{
			if(n >=componentCount())
				break;
			int hMax=0;
			int wL=10;
			for(int j=0; j!=d; ++j)
			{
				if(n >=componentCount())
					break;
				SBGNNode su=getSubunits().get(n);
				Path2D p=new Path2D.Double(su.getGlyph());
				wL+=p.getBounds().getWidth()+5;
				if(p.getBounds().height>hMax) hMax=p.getBounds().height;
				n++;
			}
			if(wL > wMax) wMax=wL;
			h+=hMax;
		}
		return new RoundRectangle2D.Double(0d,0d,wMax,h,2,2);
	}

	/**
	 * @return the subunits
	 */
	public List<EntityPoolNode> getSubunits() {
		return subunits;
	}

	/**
	 * @param subunits the subunits to set
	 */
	public void setSubunits(List<EntityPoolNode> subunits) {
		this.subunits = subunits;
	}
	
	public List<String> getPossibleNames() 
	{
		List<String> res=super.getPossibleNames();
		
		for(EntityPoolNode n:subunits)
			res.addAll(n.getPossibleNames());
		
		return res;
	}
	
	
	
}
