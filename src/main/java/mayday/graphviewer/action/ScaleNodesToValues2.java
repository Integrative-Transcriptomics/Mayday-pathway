package mayday.graphviewer.action;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.typed.BooleanSetting;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.vis3.ValueProvider;
import mayday.vis3.ValueProvider.ExperimentProvider;
import mayday.vis3.ValueProvider.Provider;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;

public class ScaleNodesToValues2 implements ChangeListener, SettingChangeListener {

	private BooleanSetting activeSetting;
	private GraphViewerPlot viewer;

	private static final int minW=20;
	private static final int maxW=150;

	public ScaleNodesToValues2(BooleanSetting activeSetting,GraphViewerPlot viewer) 
	{
		this.activeSetting = activeSetting;
		this.viewer = viewer;
	}

	@Override
	public void stateChanged(ChangeEvent e) 
	{

		if(activeSetting.getBooleanValue())
		{
			ValueProvider provider=(ValueProvider)e.getSource();
			SuperModel sm=(SuperModel)viewer.getModel();
			double min=provider.getMinimum();
			double max=provider.getMaximum();

			for(Probe p:provider.getValuesMap().keySet())
			{
				for(CanvasComponent comp: sm.getComponents(p))
				{
					double v=provider.getValue(p);
					double w=((v-min)/(max-min))*(maxW-minW)+minW;
					comp.setSize((int)w, (int)w);
					comp.setLocked(true);
				}
			}

			for(CanvasComponent comp:sm.getComponents())
			{
				if((comp instanceof MultiProbeComponent))
				{
					double v=0;
					int numP=0;
					for(Probe p:((MultiProbeComponent)comp).getProbes())
					{
						v+=provider.getValue(p);
						numP++;
					}
					v=v/(1.0*numP);

					double w=((v-min)/(max-min))*(maxW-minW)+minW;
					comp.setSize((int)w, (int)w);
					comp.setLocked(true);
				}
			}
			viewer.updatePlot();
		}
	}

	@Override
	public void stateChanged(SettingChangeEvent e) 
	{
		if(activeSetting.getBooleanValue())
			activate();
		else
		{
			deActivate();
		}
	}

	public void deActivate()
	{
		for(CanvasComponent cc:viewer.getModel().getComponents())
		{
			if(cc instanceof MultiProbeComponent)
			{
				cc.setSize(viewer.getRendererDispatcher().getRenderer(
						cc, ((MultiProbeComponent) cc).getNode()).
						getSuggestedSize(((MultiProbeComponent) cc).getNode(), ((MultiProbeComponent) cc).getProbes()));
				cc.setLocked(false);
			}
		}
	}


	public void activate()
	{
		for(DataSet ds: viewer.getModelHub().getDataSets())
		{
			ValueProvider vp=viewer.getModelHub().getValueProvider(ds);
			vp.setProvider(vp.getProvider());			
		}		
	}

	public void nextExperiment()
	{
		for(DataSet ds: viewer.getModelHub().getDataSets())
		{
			ValueProvider vp=viewer.getModelHub().getValueProvider(ds);
			Provider p=vp.getProvider();
			if(p instanceof ExperimentProvider)
			{
				((ExperimentProvider) p).setExperiment(((ExperimentProvider) p).getExperiment()+1);
				if(((ExperimentProvider) p).getExperiment() >= ds.getMasterTable().getNumberOfExperiments())
					((ExperimentProvider) p).setExperiment(0);
			}
			//			vp.setProvider(vp.getProvider());			
		}		
	}

}
