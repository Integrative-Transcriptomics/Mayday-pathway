package mayday.graphviewer.core.bag.tools;

import java.util.Collection;

import javax.swing.JPanel;

import mayday.core.Probe;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.core.bag.BagComponent;
import mayday.graphviewer.core.bag.ComponentBag;
import mayday.vis3.graph.components.MultiProbeComponent;

@SuppressWarnings("serial")
public abstract class BagCentralComponent extends JPanel
{
	protected ComponentBag bag;
	protected BagComponent comp;
	
	public BagCentralComponent(ComponentBag bag, BagComponent comp) 
	{
		this.bag = bag;
		this.comp = comp;
	}	
	
	public ComponentBag getBag() 
	{
		return bag;
	}
	
	public void setBag(ComponentBag bag) 
	{
		this.bag = bag;
	}
	
	public BagComponent getComp() 
	{
		return comp;
	}
	
	public void setComp(BagComponent comp) 
	{
		this.comp = comp;
	}
	
	protected void selectProbes(Collection<Probe> probes)
	{
		if(comp.getParent() instanceof GraphViewerPlot)
		{
			GraphViewerPlot viewer=((GraphViewerPlot)comp.getParent());
			SuperModel model=(SuperModel)viewer.getModel();
			
			viewer.getSelectionModel().clearSelection();
			
			for(Probe p:probes)
			{
				for(MultiProbeComponent comp: model.getComponents(p))
				{
					viewer.getSelectionModel().select(comp);
				}								
			}			
		}
		
		
	}
	
}
