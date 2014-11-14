package mayday.graphviewer.util.grouping;

import java.util.List;

import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.core.ModelHub;

public interface GroupStrategy 
{
	public static final String EMPTY_GROUP="EMPTY_GROUP";
	
	public MultiHashMap<String,DefaultNode > groupNodes(List<DefaultNode> nodes);
	
	public void setHub(ModelHub hub);
	
	

}
