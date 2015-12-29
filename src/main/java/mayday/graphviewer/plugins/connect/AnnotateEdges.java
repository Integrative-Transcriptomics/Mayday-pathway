package mayday.graphviewer.plugins.connect;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;

import mayday.core.Probe;
import mayday.core.math.distance.measures.EuclideanDistance;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.methods.DistanceMeasureSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.core.structures.graph.Edge;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.GraphModel;

public class AnnotateEdges extends AbstractGraphViewerPlugin
{
	public void run(final GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		DistanceMeasureSetting measure=new DistanceMeasureSetting("Measure", null, new EuclideanDistance());

		BooleanHierarchicalSetting setNameSetting=new BooleanHierarchicalSetting("Set Edge Label","Write the distance into the edge label",false);
		StringSetting prefix =new StringSetting("Prefix", null, "d=");
		setNameSetting.addSetting(prefix);

		BooleanSetting setWeightSetting=new BooleanSetting("Set Weight", "Set the distance as the edge weight", true);

		HierarchicalSetting setting=new HierarchicalSetting("Distance Measure");
		setting.addSetting(measure).addSetting(setNameSetting).addSetting(setWeightSetting);

		SettingDialog sd=new SettingDialog(canvas.getOutermostJWindow(), "Annotate Edges", setting);
		sd.showAsInputDialog();
		if(!sd.closedWithOK()) 
			return;

		NumberFormat nf=NumberFormat.getNumberInstance();

		for(Edge e: model.getGraph().getEdges())
		{
			CanvasComponent s=model.getComponent(e.getSource());
			CanvasComponent t=model.getComponent(e.getTarget());

			if(s instanceof MultiProbeComponent && t instanceof MultiProbeComponent)
			{
				double v=0; 
				int m=0; 
				for(Probe sp:((MultiProbeComponent)s).getProbes())
					for(Probe tp:((MultiProbeComponent)t).getProbes())
					{
						v+=measure.getInstance().getDistance(sp.getValues(), tp.getValues());
						++m;
					}
				v/=(1.0*m);
				if(!Double.isNaN(v)){
					if(setNameSetting.getBooleanValue())
						e.setName(prefix.getStringValue()+nf.format(v));
					if(setWeightSetting.getBooleanValue())
						e.setWeight(v);
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
				"PAS.GraphViewer.AnnotateEdges",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Annotate all edges with a distance measure",
				"Annotate Edges with distance"				
		);
		pli.setIcon("mayday/pathway/gvicons/annotateedges.png");
		pli.addCategory(CONNECT_CATEGORY);
		return pli;	

	}
}