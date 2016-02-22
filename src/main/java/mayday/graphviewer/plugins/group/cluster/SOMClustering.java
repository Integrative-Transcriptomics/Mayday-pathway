package mayday.graphviewer.plugins.group.cluster;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import mayday.clustering.som.BatchSOMPlugin;
import mayday.clustering.som.SOMSettings;
import mayday.core.MaydayDefaults;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingsDialog;
import mayday.graphviewer.core.DefaultNodeComponent;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.core.bag.ComponentBag;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.GraphModel;

public class SOMClustering  extends AbstractGraphViewerPlugin 
{
	@Override
	public void run(final GraphViewerPlot canvas, final GraphModel model, final List<CanvasComponent> components) 
	{
		final ProbeList pl=new ProbeList(canvas.getModelHub().getViewModel().getDataSet().getMasterTable().getDataSet(),false);
		final SuperModel superModel=((SuperModel)model);
		for(CanvasComponent comp:components)
		{
			if(! (comp instanceof MultiProbeComponent))
				continue;
			for(Probe p:((MultiProbeComponent)comp).getProbes())
			{
				if(!pl.contains(p))
					pl.addProbe(p);
			}
		}
		if(pl.getNumberOfProbes()==0) return;

		List<ProbeList> probeLists=new ArrayList<ProbeList>();
		probeLists.add(pl);
		try 
		{  
			//List<ProbeList> res=som.run(probeLists, pl.getDataSet().getMasterTable());
			BatchSOMPlugin som=new BatchSOMPlugin();

			SOMSettings setting=new SOMSettings();
			SettingsDialog dialog=new SettingsDialog(null, "SOM Settings", setting);
			dialog.showAsInputDialog();
			if(!dialog.closedWithOK())
				return;

			List<ProbeList> res=som.cluster(probeLists, pl.getDataSet().getMasterTable(), setting);

			ComponentBag bag=new ComponentBag((SuperModel)model);
			bag.setName("SOM clustered probes");
			bag.setColor(Color.LIGHT_GRAY);
			Rectangle rect=getBoundingRect(components);

			int x=rect.x;
			int y=rect.y;

			int space=30; 

			int cc=0; 

			int ccMax=setting.getMapRows();
			Collections.reverse(res);
			for(ProbeList plist: res)
			{
				DefaultNodeComponent comp=superModel.addProbeListNode(plist);
				canvas.addComponent(comp);
				bag.addComponent(comp);	
				comp.setLocation(x, y);
				x+=space+comp.getWidth();
				cc++;
				if(cc>= ccMax )
				{
					cc=0;
					x=rect.x;
					y+=comp.getHeight()+space;
				}
			}
			superModel.addBag(bag);
		}
		catch ( Exception exception ) 
		{
			exception.printStackTrace();			    	           
			JOptionPane.showMessageDialog( null,
					exception.toString(),
					MaydayDefaults.Messages.ERROR_TITLE,
					JOptionPane.ERROR_MESSAGE );
		}


	}


	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.ClusterSOM",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Cluster the Probes using SOM  ",
				"SOM"				
		);
		pli.setIcon("mayday/pathway/gvicons/som.png");
		pli.addCategory(GROUP_CATEGORY+"/"+MaydayDefaults.Plugins.CATEGORY_CLUSTERING);
		return pli;	

	}
}
