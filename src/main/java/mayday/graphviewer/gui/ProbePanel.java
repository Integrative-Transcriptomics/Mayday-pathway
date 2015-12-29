package mayday.graphviewer.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.structures.maps.MultiHashMap;
import mayday.tiala.pairwise.data.viewmodel.EnrichedViewModel;
import mayday.tiala.pairwise.data.viewmodel.NonClosingVisualizer;
import mayday.tiala.pairwise.gui.views.ProfileView;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.GraphModelSelectionListener;
import mayday.vis3.graph.model.SelectionModel;
import mayday.vis3.model.Visualizer;
import mayday.vis3.model.VolatileProbeList;

@SuppressWarnings("serial")
public class ProbePanel extends JPanel implements GraphModelSelectionListener
{
	private Map<DataSet,Visualizer> visualizers;
	private Map<DataSet, ProfileView> views;
	
	private SelectionModel selectionModel;	
	private JTabbedPane tabbedPane;	
	MultiHashMap<DataSet, Probe> probes=new MultiHashMap<DataSet, Probe>();
	
	
	public ProbePanel(SelectionModel model) 
	{
		this.selectionModel=model;
		model.addSelectionListener(this);
		
		tabbedPane=new JTabbedPane();		
		visualizers=new HashMap<DataSet, Visualizer>();
		views=new HashMap<DataSet, ProfileView>();
		setProbes();
		setLayout(new BorderLayout());
		add(new JScrollPane(tabbedPane),BorderLayout.CENTER);
		
		setPreferredSize(new Dimension(500,300));
	}
	
	private void setProbes()
	{
		probes.clear();
		for(CanvasComponent comp:selectionModel.getSelectedComponents())
		{
			if(comp instanceof MultiProbeComponent)
			{
				for( Probe p: ((MultiProbeComponent) comp).getProbes())
				{
					probes.put(p.getMasterTable().getDataSet(), p);
				}
			}
		}		
		
		for(DataSet ds: probes.keySet())
		{
//			if(visualizers.containsKey(ds) && visualizers.get(ds).getViewModel()==null)
//				visualizers.remove(ds);
			if(!visualizers.containsKey(ds))
			{				
				NonClosingVisualizer viz=new NonClosingVisualizer();
				viz.setViewModel(new EnrichedViewModel(viz, ds));
				
				visualizers.put(ds, viz);				
			}			
			VolatileProbeList pl=new VolatileProbeList(ds);
			pl.setProbes(probes.get(ds));
			if(!visualizers.get(ds).getViewModel().getProbeLists(false).isEmpty())
				visualizers.get(ds).getViewModel().removeProbeListFromSelection(visualizers.get(ds).getViewModel().getProbeLists(false).get(0));
			visualizers.get(ds).getViewModel().addProbeListToSelection(pl);
			
			if(!views.containsKey(ds))
			{
				ProfileView pv=new ProfileView(Color.red, visualizers.get(ds));
				pv.setName(ds.getName());
				views.put(ds, pv);
				pv.setPreferredSize(new Dimension(500,300));
				tabbedPane.add(pv);
			}		
		}
		
		Set<DataSet> retiredDS=new HashSet<DataSet>();
		retiredDS.addAll(views.keySet());
		retiredDS.removeAll(probes.keySet());
		for(DataSet ds: retiredDS)
		{
			tabbedPane.remove(views.get(ds));
			views.remove(ds);
			visualizers.remove(ds);
		}		
	}
	
	
	@Override
	public void removeNotify() 
	{
		super.removeNotify();
		selectionModel.removeSelectionListener(this);
	}
	
	@Override
	public void selectionChanged() 
	{
		if(isVisible())
		{
			setProbes();
		}
		
	}
	

	
	
	
}
