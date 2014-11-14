package mayday.pathway.keggview.pathways.gui.canvas;

import java.awt.Image;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import mayday.core.Probe;
import mayday.core.pluma.PluginInfo;
import mayday.pathway.keggview.kegg.ko.KOEntry;
import mayday.pathway.keggview.pathways.graph.GeneNode;
import mayday.pathway.keggview.pathways.graph.PathwayGraph;

@SuppressWarnings("serial")
public class GeneComponent extends PathwayComponent
{
	private static final Image enzymeIcon=PluginInfo.getIcon("mayday/image/enzyme2.png").getImage();
	
	public GeneComponent(GeneNode node,PathwayGraph graph)
	{
		super(node,graph);
		setSize(80, 40);
		setToolTipText(makeToolTipText());
		setLabel(prepareLabel());
	}
	
	private String prepareLabel()
	{
		String label="";
		Iterator<KOEntry> iter=((GeneNode)getNode()).getGenes().iterator();
		label=((GeneNode)getNode()).getName();
		if(!iter.hasNext()) label=((GeneNode)getNode()).getName();
		else
		{
			KOEntry first= iter.next();
			if(first==null ) return "";
			if(first.getName()==null) return "";
			label=first.getName();
			if(label.length() > 4 && label.charAt(3)==':') label=label.substring(4);
			if(label.startsWith("TITLE:")) label=label.substring(6);
			if(label.indexOf(",")>0)
			{
				label=label.substring(0,label.indexOf(","));
			}
		}
		return label;
	}
	
	private String makeToolTipText()
	{
		StringBuffer sb=new StringBuffer("<html><body><ul>");
		for(KOEntry e:((GeneNode)getNode()).getGenes())
		{
			if(e==null) continue;
			sb.append("<li>"+e.getName()+": "+e.getDefinition()+"</li>");
		}
		for(Probe p:((GeneNode)getNode()).getProbes())
		{
			sb.append("<li>"+p.getDisplayName()+"</li>");
		}
		return sb.toString();
	}	
	
	public Image getImage()
	{
		return enzymeIcon;
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
