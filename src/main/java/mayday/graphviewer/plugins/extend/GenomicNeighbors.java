package mayday.graphviewer.plugins.extend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.meta.MIGroup;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Settings;
import mayday.core.settings.SettingsDialog;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.MIGroupSetting;
import mayday.core.structures.graph.Edge;
import mayday.genetics.LocusMIO;
import mayday.genetics.advanced.chromosome.LocusChromosomeObject;
import mayday.genetics.advanced.chromosome.LocusGeneticCoordinateObject;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.Strand;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.GraphModel;

public class GenomicNeighbors extends AbstractGraphViewerPlugin  
{
	private IntSetting maxDistance=new IntSetting("Distance", null, 1000);
	private BooleanSetting ignoreStrand=new BooleanSetting("Ignore Strand", null, true);
	private BooleanSetting acceptUpstream=new BooleanSetting("Upstream", null, true);
	private BooleanSetting acceptDownstream=new BooleanSetting("Downstream", null, true);

	private BooleanHierarchicalSetting overlapping=new BooleanHierarchicalSetting("Overlapping", null, true);
	private BooleanHierarchicalSetting neighbors=new BooleanHierarchicalSetting("Genomic Neighbors", null, true);

	private Settings settings;

	public GenomicNeighbors() 
	{
		neighbors.addSetting(maxDistance).addSetting(ignoreStrand).addSetting(acceptDownstream).addSetting(acceptUpstream);
		HierarchicalSetting setting=new HierarchicalSetting("Genomic Neighbors");
		setting.addSetting(neighbors).addSetting(overlapping);
		settings=new Settings(setting,PluginInfo.getPreferences("PAS.GraphViewer.GenomicNeighborhood"));	

	}

	@SuppressWarnings("unchecked")
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		if(components.isEmpty())
			return;

		MIGroupSetting miGroup=new MIGroupSetting("Locus Information", null, null, canvas.getModelHub().getViewModel().getDataSet().getMIManager(), false);
		miGroup.setAcceptableClass(LocusMIO.class);

		ChromosomeSetContainer csc=new ChromosomeSetContainer(new LocusChromosomeObject.Factory<Probe>());

		settings.getRoot().addSetting(miGroup);
		SettingsDialog sd=new SettingsDialog(null, "Genomic Neighbors", settings);

		sd.setModal(true);
		sd.setVisible(true);

		if(sd.canceled())
			return;

		MIGroup group=miGroup.getMIGroup();

		for(Probe p:canvas.getModelHub().getViewModel().getDataSet().getMasterTable().getProbes().values())
		{
			if(!group.contains(p))
				continue;
			AbstractGeneticCoordinate pc=((LocusMIO) group.getMIO(p)).getValue().getCoordinate();
			((LocusChromosomeObject<Probe>)csc.getChromosome(pc.getChromosome())).addLocus(pc.getFrom(), pc.getTo(), pc.getStrand(), p);		
		}

		SuperModel sm=(SuperModel) model;

		ProbeList pl=canvas.getModelHub().getAddedProbes();
		List<CanvasComponent> addedComponents=new ArrayList<CanvasComponent>();
		for(CanvasComponent comp:components)
		{
			List<Probe> cprobes=new ArrayList<Probe>();
			if(comp instanceof MultiProbeComponent)
			{
				for(Probe p: ((MultiProbeComponent) comp).getProbes())
				{
					cprobes.add(p);
				}				
			}
			if(cprobes.isEmpty())
				continue;


			for(Probe p:cprobes)
			{
				if(!group.contains(p))
					continue;

				AbstractGeneticCoordinate pc=((LocusMIO)group.getMIO(p)).getValue().getCoordinate();		

				if(neighbors.getBooleanValue())
				{
					List<LocusGeneticCoordinateObject<Probe>> lolgcp = 
						((LocusChromosomeObject<Probe>)csc.getChromosome(pc.getChromosome())).getOverlappingLoci(
								pc.getFrom()-maxDistance.getIntValue(),	pc.getTo()+maxDistance.getIntValue(),
								ignoreStrand.getBooleanValue()?Strand.UNSPECIFIED:pc.getStrand());

					for(LocusGeneticCoordinateObject<Probe> olgcp:lolgcp)
					{
						long d=pc.getDistanceTo(olgcp, ignoreStrand.getBooleanValue());
						Probe np=olgcp.getObject();
						if(np==p)
							continue;
						if(olgcp.isUpstreamOf(pc))
						{
							d*=-1;
						}
						List<MultiProbeComponent> comps=sm.getComponents(np);
						if(comps==null || comps.isEmpty())
						{
							CanvasComponent cc=sm.addProbe(np);
							addedComponents.add(cc);
							pl.addProbe(np);
							Edge e=sm.connect(comp, cc);
							e.setWeight(d);
							e.setName(olgcp.getChromosome().toString());
						}else
						{
							for(MultiProbeComponent cc:comps)
							{
								Edge e=sm.connect(comp, cc);
								e.setWeight(d);
								e.setName(olgcp.getChromosome().toString());
							}								
						}

					}					
				}
				if(overlapping.getBooleanValue())
				{
					List<LocusGeneticCoordinateObject<Probe>> lolgcp = 
						((LocusChromosomeObject<Probe>)csc.getChromosome(pc.getChromosome())).getOverlappingLoci(pc.getFrom(), pc.getTo(), Strand.BOTH);
					
					for(LocusGeneticCoordinateObject<Probe> olgcp:lolgcp)
					{
						long d=pc.getOverlappingBaseCount(olgcp);
						Probe np=olgcp.getObject();
						if(np==p)
							continue;
						List<MultiProbeComponent> comps=sm.getComponents(np);
						if(comps==null|| comps.isEmpty() )
						{
							CanvasComponent cc=sm.addProbe(np);
							
							addedComponents.add(cc);
							pl.addProbe(np);
							Edge e=sm.connect(comp, cc);
							e.setWeight(d);
							e.setName(olgcp.getChromosome().toString());

						}else
						{
							for(MultiProbeComponent cc:comps)
							{
								Edge e=sm.connect(comp, cc);
								e.setWeight(d);
								e.setName(olgcp.getChromosome().toString());
							}
						}
					}
				}				
			}
		}

		if(addedComponents.isEmpty())
			JOptionPane.showMessageDialog(canvas, "No genomic neighbors found","Genomic Neighbors", JOptionPane.WARNING_MESSAGE);
			
		placeComponents(addedComponents, canvas, 15, 20);
//		int x=20;
//		int y=canvas.getComponentMaxY()+20;
//		for(CanvasComponent comp:addedComponents)
//		{
//			comp.setLocation(x,y);
//			x+=comp.getWidth()+20;
//
//			if(x>canvas.getWidth())
//			{
//				x=20;
//				y+=20+comp.getHeight();
//			}
//		}	


	}



	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.GenomicNeighborhood",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Add probes in the genomic neighborhood of the selected probes",
				"Genomic Neighborhood"				
		);
		pli.addCategory(EXTEND_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/genomic_neighbors.png");
		return pli;	
	}


}
