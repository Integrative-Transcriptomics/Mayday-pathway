package mayday.graphviewer.graphprovider;

import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.maps.MultiHashMap;
import mayday.vis3.graph.layout.CanvasLayouter;

/**
 * Base class for all graph providers
 * @author Stephan Symons
 *
 */
public interface GraphProvider 
{
	public static final String MC="GraphViewer/GraphProvider";
	
	public static final String PROBES="Probes";
	public static final String PROBE_LISTS="Probe Lists";
	public static final String EXTERNAL="Import";
	
	public Graph createGraph(MultiHashMap<DataSet, ProbeList> probeLists);
	
	public String getName();
	
	public CanvasLayouter defaultLayouter();
	

}
