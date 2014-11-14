package mayday.graphviewer.illumina;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.datasetmanager.DataSetManager;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.maps.MultiHashMap;
import mayday.genetics.advanced.chromosome.LocusChromosomeObject;
import mayday.genetics.advanced.chromosome.LocusGeneticCoordinateObject;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.SpeciesContainer;
import mayday.genetics.basic.chromosome.Chromosome;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.genetics.coordinatemodel.GBAtom;
import mayday.genetics.locusmap.LocusMap;
import mayday.genetics.locusmap.LocusMapSetting;
import mayday.graphviewer.graphprovider.GraphProvider;
import mayday.vis3.graph.layout.CanvasLayouter;

public class GeneModelGraphProvider extends AbstractPlugin implements GraphProvider 
{
	private static final GeneModelStyle[] styles={GeneModelStyle.CONDENSED, GeneModelStyle.COMPRESSED, GeneModelStyle.VERBOSE};
	private GeneModelStyle style=GeneModelStyle.CONDENSED;

	private int minExonWidth=5;
	private int exonBaseHeight=40;
	private boolean scaleExons=true;
	@SuppressWarnings("unchecked")
	@Override
	public Graph createGraph(MultiHashMap<DataSet, ProbeList> probeLists) 
	{
		// get list of gene models from LocusMap
		HierarchicalSetting setting= new HierarchicalSetting("Gene Models");
		LocusMapSetting lms=new LocusMapSetting();
		setting.addSetting(lms);
		ObjectSelectionSetting<GeneModelStyle> styleSetting=new ObjectSelectionSetting<GeneModelStyle>("Style", null, 0, styles);	
		setting.addSetting(styleSetting);

		HierarchicalSetting scalingSetting=new HierarchicalSetting("Scaling");
		IntSetting minExonWidthSetting=new IntSetting("Minimum Exon Width", null, minExonWidth, 1,1000,true,true);
		scalingSetting.addSetting(minExonWidthSetting);
		//TODO: add chooser for scaling style
		IntSetting exonBaseHeight=new IntSetting("Exon Base Height", null, this.exonBaseHeight, 10, 200, true, true);
		BooleanSetting scaleExonHeight=new BooleanSetting("Scale Exon Height", null, scaleExons);
		scalingSetting.addSetting(exonBaseHeight).addSetting(scaleExonHeight);
		
		setting.addSetting(scalingSetting);
		
		
		BooleanHierarchicalSetting exonWrapperSetting=new BooleanHierarchicalSetting("Exon-Level Data",null,false);

		DataSet[] dss=(DataSet[])DataSetManager.singleInstance.getDataSets().toArray(new DataSet[DataSetManager.singleInstance.getDataSets().size()]);
		ObjectSelectionSetting<DataSet> exonDataSet=new ObjectSelectionSetting<DataSet>("Exons","Data Sets containing exon-level read data",1,dss);

		LocusMapSetting exonMapSetting=new LocusMapSetting();
		exonMapSetting.setName("Exon Locus Mapping");
		exonWrapperSetting.addSetting(exonDataSet).addSetting(exonMapSetting);
		setting.addSetting(exonWrapperSetting);

		SettingDialog dialog=new SettingDialog(null, getName(), setting);
		//		dialog.setModal(true);
		dialog.showAsInputDialog();

		if(!dialog.closedWithOK())
			return null;

		minExonWidth=minExonWidthSetting.getIntValue();
		scaleExons=scaleExonHeight.getBooleanValue();
		this.exonBaseHeight=exonBaseHeight.getIntValue();
		
		LocusMap map=lms.getLocusMap();

		MultiHashMap<String, GBAtom> exons=new MultiHashMap<String, GBAtom>();
		Map<String, Probe> isoformProbes=new HashMap<String, Probe>();
		MultiHashMap<GBAtom,Probe> probes=new MultiHashMap<GBAtom, Probe>();

		Chromosome chr=null;
		StringBuffer sb=new StringBuffer();
		for(DataSet ds: probeLists.keySet())
		{			
			for(ProbeList pl:probeLists.get(ds))
			{
				for(Probe p: pl)
				{
					isoformProbes.put(p.getName(),p);
					AbstractGeneticCoordinate agc= map.get(p.getName());
					chr=agc.getChromosome();
					for(GBAtom atom: agc.getCoordinateAtoms())
					{
						probes.put(atom, p); // add gene model summary
						exons.put(p.getName(), atom);
					}
				}
				sb.append(pl.getName()).append(" ");
			}			
		}
		style=styleSetting.getObjectValue();
		Graph graph= new GeneModelGraphFactory().buildGraph(exons, probes, isoformProbes, style);
		// here would be the ideal position to add the exon probes:

		if(exonWrapperSetting.getBooleanValue())
		{

			graph.setName(sb.toString()+style.toString().toLowerCase());
			ChromosomeSetContainer csc= exonMapSetting.getLocusMap().asChromosomeSetContainer();

			Map<GeneticNode, Probe> nodeProbeMap=new HashMap<GeneticNode, Probe>();
			for(Node n: graph.getNodes())
			{
				GeneticNode gn=(GeneticNode)n;
				GBAtom atom=gn.getAtom();

				List<LocusGeneticCoordinateObject> lolgcp = 
					((LocusChromosomeObject)csc.getChromosome(SpeciesContainer.getSpecies(chr.getSpecies().getName()),chr.getId())).getOverlappingLoci(atom.from,atom.to,atom.strand);
				gn.setChromosome(chr);
				for(LocusGeneticCoordinateObject p:lolgcp)
				{
					Probe pr=exonDataSet.getObjectValue().getMasterTable().getProbe(p.getObject().toString());
					nodeProbeMap.put(gn,pr);
				}		
			}
			// go over nodes 
			double min=Double.MAX_VALUE;
			double max=Double.MIN_VALUE;
			for(Probe p: nodeProbeMap.values())
			{
				max=Math.max(max, p.getMaxValue());
				min=Math.min(min, p.getMinValue());
			}

			for(GeneticNode n: nodeProbeMap.keySet())
			{
				Probe p=nodeProbeMap.get(n);
				double[] dd=new double[p.getNumberOfExperiments()];

				int i=0; 
				for(double d: p.getValues())
				{
					dd[i]=( (d-min)/(max-min) );
					++i;
				}	
				n.setExonValues(dd);			
			}
		}
		return graph;
	}

	@Override
	public CanvasLayouter defaultLayouter() 
	{
		CanvasLayouter layouter=null;
		switch(style)
		{
		case COMPRESSED:
			layouter= new GeneModelTreeLayout();
			break;
		case CONDENSED:
			layouter= new GeneModelStackedLayout();
			break;
		case VERBOSE:
			layouter= new GeneModelLayout();
			break;		
		}
		((IGeneModelLayout)layouter).setExonMinimumSize(minExonWidth);
		((IGeneModelLayout)layouter).setExonBaseHeight(exonBaseHeight);
		((IGeneModelLayout)layouter).setExonScaling(scaleExons);
		return layouter;
	}

	@Override
	public String getName() 
	{
		return "Gene Models"; 
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"mayday.graphprovider.genemodels",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"View Gene Models",
				getName()				
		);
		pli.addCategory(PROBE_LISTS);
		return pli;			
	}
}
