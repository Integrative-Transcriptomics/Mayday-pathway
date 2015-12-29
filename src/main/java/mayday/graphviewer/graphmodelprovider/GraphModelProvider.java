package mayday.graphviewer.graphmodelprovider;

import java.awt.Component;

import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.core.settings.Setting;
import mayday.core.structures.maps.MultiHashMap;
import mayday.core.tasks.AbstractTask;
import mayday.graphviewer.core.SuperModel;
import mayday.vis3.graph.layout.CanvasLayouter;

public interface GraphModelProvider 
{
	public SuperModel getGraphModel();
	
	public AbstractTask parseFile();
	public AbstractTask buildGraph();
	
	public Setting getBasicSetting();
	public Setting getInformedSetting();
		
	public boolean isAskForProbeLists();
	public boolean isAskForFileSetting();

	public Component getAdditionalComponent();
	
	public String getName();
	public String getDescription();
	
	public void setProbeLists(MultiHashMap<DataSet, ProbeList> probeLists);
	public MultiHashMap<DataSet, ProbeList> getProbeLists();
	
	public CanvasLayouter defaultLayouter();
	
	public static final String MC="GraphViewer/GraphModelProvider";

	public void updateFileSettings();
}
