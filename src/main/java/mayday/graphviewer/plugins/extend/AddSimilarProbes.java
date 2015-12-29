package mayday.graphviewer.plugins.extend;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.meta.types.AnnotationMIO;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Edge;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.gui.ProbeDistanceDialog;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.GraphModel;

public class AddSimilarProbes extends AbstractGraphViewerPlugin  
{
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		if(components.size()!=1)
		{
			JOptionPane.showMessageDialog(canvas, "Please select excactly one component", "Add similar Probes", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// prepare a list of selected probes.
		List<Probe> probes=new ArrayList<Probe>();
		for(CanvasComponent comp:components)
		{
			probes.addAll(((MultiProbeComponent)comp).getProbes());
		}
		if(probes.isEmpty()) return;

		ProbeDistanceDialog pdd=new ProbeDistanceDialog(probes);
		pdd.setVisible(true);
		if(pdd.isCancelled())
			return;
		DistanceMeasurePlugin distance=pdd.getDistanceMeasure();
		boolean isLimited=pdd.isLimitNeighbors();
		int limit=pdd.getNumberOfNeighbors();
		double tol=pdd.getTolerance();

		Probe orig=probes.get(0);
		List<WeightedProbe> results = new ArrayList<WeightedProbe>();

		for(Probe p:probes.get(0).getMasterTable().getProbes().values())
		{

			if(orig==p) continue;
			double d=distance.getDistance(orig.getValues(), p.getValues());
			if(d < tol)
			{
				results.add(new WeightedProbe(d, p));
			}
		}

		Collections.sort(results);

		int x=20;
		int y=canvas.getComponentMaxY()+20;

		CanvasComponent oComp=components.get(0);
		ProbeList pl=new ProbeList(orig.getMasterTable().getDataSet(), false);
		SuperModel sm=(SuperModel)model;
		int step=5;
		int i=0;
		for(WeightedProbe pr:results)
		{
			pl.addProbe(pr.p);
			if(isLimited && i >= limit)
			{
				break;
			}
			List<MultiProbeComponent> nComps=sm.getComponents(pr.p);
			if(nComps!=null)
			{
				for(MultiProbeComponent nComp:nComps)
				{
					Edge e=new Edge(sm.getNode(oComp), sm.getNode(nComp));
					e.setWeight(pr.dist);
					e.setName("d="+NumberFormat.getNumberInstance().format(pr.dist));
					model.getGraph().connect(e);
					
				}				
//				continue;
			}
			CanvasComponent nComp=sm.addProbe(pr.p);	
			nComp.setLocation(x,y);
			x+=nComp.getWidth()+20;
			if(x>canvas.getWidth())
			{
				x=20;
				y+=20+nComp.getHeight();
			}	
			canvas.addComponent(nComp);
			nComp.setLocation(oComp.getX()+(i*step), oComp.getY()+(i*step));

			Edge e=new Edge(sm.getNode(oComp), sm.getNode(nComp));
			e.setWeight(pr.dist);
			e.setName("d="+NumberFormat.getNumberInstance().format(pr.dist));
			model.getGraph().connect(e);
			++i;
		}

		pl.setName("Similar to "+orig.getDisplayName()+" ("+SimpleDateFormat.getDateTimeInstance().format(new Date())+")");		
		pl.setAnnotation(new AnnotationMIO("", "Distance: "+distance.toString()+", Cutoff: "+NumberFormat.getNumberInstance().format(tol)));
		pl.setParent(canvas.getModelHub().getAddedProbes(orig.getMasterTable().getDataSet()).getParent());
		canvas.getModelHub().getViewModel(orig.getMasterTable().getDataSet())
		.getDataSet().getProbeListManager().addObject(pl);
		canvas.getModelHub().getViewModel(orig.getMasterTable()
				.getDataSet()).addProbeListToSelection(pl);

		canvas.updateSize();
	}

	private class WeightedProbe implements Comparable<WeightedProbe>
	{
		public double dist; 
		public Probe p;

		public WeightedProbe(double dist, Probe p){
			this.dist = dist;
			this.p = p;
		}

		@Override
		public int compareTo(WeightedProbe o) {
			return Double.compare(dist, o.dist);
		}
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.AddSimilarProbes",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Add probes similar to the selected probes",
				"Add similar Probes"				
		);
		pli.setIcon("mayday/pathway/gvicons/similarprobes.png");
		pli.addCategory(EXTEND_CATEGORY);
		return pli;	
	}


}
