package mayday.graphviewer.core;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.GraphModel;
import mayday.vis3.graph.model.ProbeGraphModel;
import mayday.vis3.graph.model.SelectionModel;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;

public class HubSelectionModel extends SelectionModel implements ViewModelListener, HubListener
{
	private boolean busy=false;
	private ModelHub hub;
	
	public HubSelectionModel(GraphModel model) 
	{
		super(model);		
	}
	
	public HubSelectionModel(GraphModel model, ModelHub hub)
	{
		super(model);
		this.hub=hub;
		for(DataSet ds: hub.getDataSets())
		{
			hub.getViewModel(ds).addViewModelListener(this);
		}		
	}
	
	@Override
	public void componentSelectionChanged(CanvasComponent comp) 
	{
		super.componentSelectionChanged(comp);
		busy=true;
		if(! (comp instanceof MultiProbeComponent) )
			return;
		
		Set<Probe> selectedProbes=new HashSet<Probe>();
		for(CanvasComponent c:getSelectedComponents())
		{
			if(c instanceof MultiProbeComponent)
			{
				selectedProbes.addAll(((MultiProbeComponent) c).getProbes());
			}
		}
				
				
		if(comp.isSelected())
		{
			selectedProbes.addAll(((MultiProbeComponent)comp).getProbes());
		}else
		{
			selectedProbes.removeAll(((MultiProbeComponent)comp).getProbes());
		}
		fireProbeSelection(selectedProbes);
		busy=false;
	}
	
	public synchronized void clearSelection()
	{
		busy=true;
		fireProbeSelection(Collections.<Probe>emptySet());
		
		busy=false;
		super.clearSelection();
	}
	
	/**
	 * @param selectedComponents the selectedComponents to set
	 */
	public void setSelectedComponents(List<CanvasComponent> selectedComponents) 
	{
		super.setSelectedComponents(selectedComponents);
		Set<Probe> probes =new HashSet<Probe>();
		for(CanvasComponent comp:selectedComponents)
		{
				probes.addAll(((MultiProbeComponent) comp).getProbes());
				comp.setSelected(true);
		}
		fireProbeSelection(probes);
		fireSelectionChanged();
	}
	
	/**
	 * Select the component
	 * @param comp
	 */
	public void select(CanvasComponent comp)
	{
		super.select(comp); 		
		busy=true;
		if(! (comp instanceof MultiProbeComponent))
			return;		
		Set<Probe> selectedProbes=new HashSet<Probe>();
		for(CanvasComponent c:getSelectedComponents())
		{
			selectedProbes.addAll(((MultiProbeComponent) c).getProbes());
		}
		selectedProbes.addAll(((MultiProbeComponent)comp).getProbes());		
		fireProbeSelection(selectedProbes);
		busy=false;
	}
	
	/**
	 * Unselect the component
	 * @param comp
	 */
	public void unselect(CanvasComponent comp)
	{
		super.unselect(comp);
		busy=true;
		Set<Probe> selectedProbes=new HashSet<Probe>();
		for(CanvasComponent c:getSelectedComponents())
		{
			if(c instanceof MultiProbeComponent)
			{
				selectedProbes.addAll(((MultiProbeComponent) c).getProbes());
			}
		}
		selectedProbes.removeAll(((MultiProbeComponent)comp).getProbes());		
		fireProbeSelection(selectedProbes);
		busy=false;

	}
	
	public synchronized void selectAll()
	{
		super.selectAll();	
		busy=true;
		Set<Probe> selectedProbes=new HashSet<Probe>();
		for(CanvasComponent comp:getSelectedComponents())
		{
			if(comp instanceof MultiProbeComponent)
				selectedProbes.addAll(((MultiProbeComponent) comp).getProbes());
		}
		fireProbeSelection(selectedProbes);
		busy=false;
	}
	
	private void fireProbeSelection(Set<Probe> selectedProbes)
	{
		for(DataSet ds: hub.getDataSets())
		{
			hub.getViewModel(ds).setProbeSelection(selectedProbes);
		}
	}

	@Override
	public void viewModelChanged(ViewModelEvent vme) 
	{
		if(busy) return;
		if(vme.getChange()==ViewModelEvent.PROBE_SELECTION_CHANGED)
		{	
			for(CanvasComponent c:getSelectedComponents())
			{
				if(c==null) continue;
				c.setSelected(false);
			}
			getSelectedComponents().clear();		
			
			if(model instanceof ProbeGraphModel)
			{				
				for(Probe p: ((ViewModel)vme.getSource()).getSelectedProbes())
				{
					if(((ProbeGraphModel)model).getComponents(p)!=null)
					{
						for(CanvasComponent cc:((ProbeGraphModel)model).getComponents(p) )
							super.select(cc);
					}
				}
			}else
			{
				for(Probe p:((ViewModel)vme.getSource()).getSelectedProbes())
				{
					for(CanvasComponent comp:model.getComponents())
					{
						if(!(comp instanceof MultiProbeComponent)) 
							continue;
						if( ((MultiProbeComponent)comp).getProbes().contains(p))
						{
							super.select(comp);
						}
					}
				}
			}
		}
	}
	
	@Override
	public void hubStateChanged(HubEvent event) 
	{
		for(DataSet ds: hub.getDataSets())
		{
			hub.getViewModel(ds).removeViewModelListener(this);
		}
		for(DataSet ds: hub.getDataSets())
		{
			hub.getViewModel(ds).addViewModelListener(this);
		}
	}
}
