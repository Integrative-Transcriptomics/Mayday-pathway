package mayday.pathway.keggview.pathways.gui.canvas;

import java.awt.Image;
import java.util.Set;
import java.util.TreeSet;

import mayday.core.Probe;
import mayday.core.pluma.PluginInfo;
import mayday.pathway.keggview.kegg.compounds.Compound;
import mayday.pathway.keggview.pathways.graph.CompoundNode;
import mayday.pathway.keggview.pathways.graph.PathwayGraph;

@SuppressWarnings("serial")
public class CompoundComponent extends PathwayComponent 
{
	private static final Image compoundIcon=PluginInfo.getIcon("mayday/image/compound.png").getImage();
	
	public CompoundComponent(CompoundNode node, PathwayGraph graph) 
	{
		super(node, graph);
		setSize(80, 40);
		setToolTipText(makeToolTipText());
		setLabel(prepareLabel());
	}
	
	private String prepareLabel()
	{
		String label=getNode().getName();
		if(((CompoundNode)getNode()).getCompound()!=null)
		{
			label=((CompoundNode)getNode()).getCompound().getName();
		}
		return label;
	}
	
	private String makeToolTipText()
	{
		StringBuffer sb=new StringBuffer("<html><body>");
		for(Compound cmp:((CompoundNode)getNode()).getCompounds())
		{
			if(cmp==null) continue;
			sb.append("<h2>"+cmp.getName()+"</h2> " +
				"Formula "+cmp.getFormula()+"<br>" +
				"Mass " +cmp.getMass()+"<br>");
		}
		return sb.toString();
	}	

	public Image getImage()
	{
		return compoundIcon;
	}
	
	@Override
	public void resetLabel() 
	{
		setLabel(prepareLabel());		
	}

	@Override
	public void setProbeNamesAsLabel() 
	{
		if(getProbes().isEmpty()) return;
		StringBuffer sb=new StringBuffer("");
		Set<Probe> probes=new TreeSet<Probe>(getProbes());
		for(Probe p:probes)
		{
			sb.append(p.getDisplayName()+", ");
		}
		setLabel(sb.toString());		
	}
}
