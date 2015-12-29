package mayday.graphviewer.plugins.group.cluster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import mayday.core.MaydayDefaults;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.core.bag.BagComponent;
import mayday.graphviewer.core.bag.ComponentBag;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.GraphModel;

public class KMeansClustering  extends AbstractGraphViewerPlugin 
{
	@Override
	public void run(final GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		final ProbeList pl=new ProbeList(canvas.getModelHub().getViewModel().getDataSet().getMasterTable().getDataSet(),false);
		final SuperModel superModel=((SuperModel)model);		
		final Map<Probe,CanvasComponent> probeToComp=new HashMap<Probe, CanvasComponent>();
		
		for(CanvasComponent comp:components)
		{
			if(! (comp instanceof MultiProbeComponent))
				continue;
			for(Probe p:((MultiProbeComponent)comp).getProbes())
			{
				if(!pl.contains(p))
					pl.addProbe(p);
				probeToComp.put(p, comp);
			}
		}
		if(pl.getNumberOfProbes()==0) return;
		
		Thread RunPluginThread = new Thread("kMeansClustering")
		{
			public void run() {

				System.runFinalization();
				System.gc();

				ProbelistPlugin km=(ProbelistPlugin) PluginManager.getInstance().getPluginFromID("PAS.clustering.kmeans").getInstance();
				
				List<ProbeList> probeLists=new ArrayList<ProbeList>();
				probeLists.add(pl);
				try 
				{  
					List<ProbeList> res=km.run(probeLists, pl.getDataSet().getMasterTable());
					for(ProbeList plist: res)
					{
						ComponentBag bag=new ComponentBag(superModel);
						bag.setName(pl.getName());
						bag.setColor(plist.getColor());
						
						for(Probe p:plist)
						{
							bag.addComponent(probeToComp.get(p));							
						}
						superModel.addBag(bag);
						canvas.add(new BagComponent(bag));
					}
				}
				catch ( Exception exception ) {
					exception.printStackTrace();			    	           
					JOptionPane.showMessageDialog( null,
							exception.getMessage(),
							MaydayDefaults.Messages.ERROR_TITLE,
							JOptionPane.ERROR_MESSAGE );
				}
			}
			
		};
		RunPluginThread.start();
	}	

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Cluster",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Cluster the Probes using kMeans ",
				"kMeans"				
		);
		pli.setIcon("mayday/pathway/gvicons/kmeans.png");
		pli.addCategory(GROUP_CATEGORY+"/"+MaydayDefaults.Plugins.CATEGORY_CLUSTERING);
		return pli;	
	}
}
