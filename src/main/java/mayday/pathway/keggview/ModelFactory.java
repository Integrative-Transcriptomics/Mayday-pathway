package mayday.pathway.keggview;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mayday.core.Probe;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.structures.graph.Node;
import mayday.pathway.keggview.kegg.pathway.Pathway;
import mayday.pathway.keggview.pathways.AnnotationHelper;
import mayday.pathway.keggview.pathways.AnnotationManager;
import mayday.pathway.keggview.pathways.PathwayLink;
import mayday.pathway.keggview.pathways.PathwayManager;
import mayday.pathway.keggview.pathways.graph.CompoundNode;
import mayday.pathway.keggview.pathways.graph.GeneNode;
import mayday.pathway.keggview.pathways.graph.PathwayGraph;
import mayday.pathway.keggview.pathways.gui.canvas.PathwayModelListener;

public class ModelFactory 
{
	private AnnotationManager annotationManager;
	private PathwayManager pathwayManager;
	private Map<String, Probe> probeMapping;
	private List<PathwayModelListener> listeners;
	
	private Pathway currentPathway;
	private PathwayModel currentModel;
	
	public ModelFactory(AnnotationManager annotationManager, PathwayManager pathwayManager)
	{
		this.annotationManager=annotationManager;
		this.pathwayManager=pathwayManager;
		listeners=new ArrayList<PathwayModelListener>();
	}
	
	public void addPathwayModelListener(PathwayModelListener listener)
	{
		listeners.add(listener);
	}
	
	public void initalize(String path, String taxon, MIGroupSelection<MIType> selection, Iterable<Probe> probes) throws IOException
	{
		AnnotationHelper.catchupDirectoryVisual(path);
		annotationManager.init(path, taxon);
		pathwayManager.setDataDirectory(path);
		pathwayManager.setTaxon(taxon);
		pathwayManager.readDirectory();	
		setMapping(selection, probes);
	}
	
	public void initalize(String path, String taxon, Map<String,Probe> mapping, Iterable<Probe> probes) throws IOException
	{
		AnnotationHelper.catchupDirectoryVisual(path);
		annotationManager.init(path, taxon);
		pathwayManager.setDataDirectory(path);
		pathwayManager.setTaxon(taxon);
		pathwayManager.readDirectory();	
		probeMapping=mapping;
		if(currentPathway!=null)
		{
			processPathway(currentPathway);
		}
	}
	
	
	public void setMapping(MIGroupSelection<MIType> selection, Iterable<Probe> probes)
	{
		probeMapping=AnnotationManager.createMapping(selection, probes);
		if(currentPathway!=null)
		{
			processPathway(currentPathway);
		}
	}
	
	public void setMapping(Map<String,Probe> mapping)
	{
		probeMapping=mapping;
		if(currentPathway!=null)
		{
			processPathway(currentPathway);
		}
	}
	
	public PathwayModel getPathway(File f) throws Exception
	{
		currentPathway=pathwayManager.loadPathway(f);
		return processPathway(currentPathway);		
	}
	
	public PathwayModel getPathway(String f) throws Exception
	{
		currentPathway=pathwayManager.loadPathway(f);
		return processPathway(currentPathway);		
	}
	
	public PathwayModel getPathway(PathwayLink f) throws Exception
	{
		currentPathway=pathwayManager.loadPathway(f);
		
		return processPathway(currentPathway);		
	}
	
	private PathwayModel processPathway(Pathway pathway)
	{		
		PathwayGraph pathwayGraph=new PathwayGraph(pathway);
		if(pathwayGraph.getReactions().size()!=0)
			pathwayGraph.removeOrphans();
		pathwayGraph.setCompounds(annotationManager.getCompounds());			
		pathwayGraph.setGenes(annotationManager.getNameEntryMap());	
		
		for(Node n:pathwayGraph.getNodes())
		{
			if(n instanceof GeneNode)
			{
				((GeneNode) n).setPlotData(probeMapping);
			}
			if(n instanceof CompoundNode)
			{
				((CompoundNode) n).setPlotData(probeMapping);
			}			
		}		
		currentPathway=pathway;
		currentModel=new PathwayModel(pathwayGraph,probeMapping);
		notifyListeners();
		return currentModel;
	}
	
	private void notifyListeners()
	{
		for(PathwayModelListener listener: listeners)
		{
			listener.pathwayChanged();
		}
	}

	/**
	 * @return the currentModel
	 */
	public PathwayModel getCurrentModel() 
	{
		return currentModel;
	}

	/**
	 * @return the pathwayManager
	 */
	public PathwayManager getPathwayManager() {
		return pathwayManager;
	}
	
	
	
}
