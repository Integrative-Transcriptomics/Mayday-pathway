package mayday.graphviewer.plugins.connect;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mayday.core.Probe;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.graph.Edge;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.GraphModel;

public class ConnectOnProbes extends AbstractGraphViewerPlugin
{
	public void run(final GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		if(components.isEmpty())
		{
			return;
		}

		// options: uniform weight / number / percentage of larger node
		RestrictedStringSetting weightSetting=new RestrictedStringSetting("Edge weight", null, 0, 
				new String[]{
				"Uniform weight",
				"Number of matching probes",
				"percentage of largest node",
				"percentage of smallest node",
		"percentage of all selected probes"});
		SettingDialog dialog=new SettingDialog(canvas.getOutermostJWindow(), "Connect on Probes", weightSetting);
		dialog.showAsInputDialog();
		if(!dialog.closedWithOK())
			return;

		int opt=weightSetting.getSelectedIndex();

		int numProbes=0;
		if(weightSetting.getSelectedIndex()==4)
		{
			Set<Probe> uniqueProbes=new HashSet<Probe>();
			for(int i=0; i!= components.size(); ++i)
			{
				if(components.get(i) instanceof MultiProbeComponent )
				{
					MultiProbeComponent compI=(MultiProbeComponent)components.get(i);
					uniqueProbes.addAll(compI.getProbes());
				}
			}
			numProbes=uniqueProbes.size();
		}

		for(int i=0; i!= components.size(); ++i)
		{
			if(components.get(i) instanceof MultiProbeComponent )
			{
				MultiProbeComponent compI=(MultiProbeComponent)components.get(i);
				Set<Probe> pi=new HashSet<Probe>( compI.getProbes());
				for(int j=i+1; j < components.size(); ++j)
				{
					if(!(components.get(j) instanceof MultiProbeComponent) )
						continue;
					MultiProbeComponent compJ=(MultiProbeComponent)components.get(j);
					Set<Probe> pj=new HashSet<Probe>( compJ.getProbes());
					boolean larger=pj.size() > pi.size();
					pj.retainAll(pi);

					if(pj.isEmpty())
						continue;

					Edge e=null;
					if(larger)
						e=((SuperModel)model).connect(compI, compJ);
					else
						e=((SuperModel)model).connect(compJ, compI);

					if(opt==1)
						e.setWeight(pj.size());
					if(opt==2)
					{
						if(larger)
							e.setWeight((1.0*pj.size()) / (1.0*compJ.getProbes().size() ) );
						else
							e.setWeight((1.0*pj.size()) / (1.0*pi.size() ));
					}
					if(opt==3)
					{
						if(larger)
							e.setWeight((1.0*pj.size()) / (1.0*pi.size() ));
						else
							e.setWeight((1.0*pj.size()) / (1.0*compJ.getProbes().size() ) );
					}
					if(opt==4)
					{
						e.setWeight((1.0*pj.size()) / (numProbes*1.0d));						
					}

				}
			}
		}


		canvas.revalidateEdges();		
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.ConnectOnProbes",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Connect all nodes that have the same probes",
				"Connect on Probes"				
		);
		pli.addCategory(CONNECT_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/connect_probes.png");
		return pli;	

	}
}
