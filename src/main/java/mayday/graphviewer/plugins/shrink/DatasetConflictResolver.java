package mayday.graphviewer.plugins.shrink;

import java.util.List;
import java.util.Set;

import mayday.core.DataSet;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.gui.InformationSetting;
import mayday.graphviewer.plugins.group.BagSelected;
import mayday.vis3.graph.components.CanvasComponent;

public class DatasetConflictResolver
{
	public static final String OMIT_PROBES="Omit Probes";
	public static final String CREATE_GROUP="Create Node Group";
	public static final String CANCEL="Cancel";
	public static final String JOIN_ON_DATASET="Only join Nodes from one dataset (select below)";
	
	private static final String[] texts={OMIT_PROBES, CREATE_GROUP, JOIN_ON_DATASET, CANCEL}; 

	private Set<DataSet> dataSets; 
	private DataSet prefDataSet;
	private GraphViewerPlot viewer;
	private List<CanvasComponent> components;
	
	private boolean omitProbes=false;
	private boolean filterDS=false;
	private DataSet selectedDataSet; 
	
	public DatasetConflictResolver(Set<DataSet> dataSets, DataSet prefDataSet,
			GraphViewerPlot plot, List<CanvasComponent> compponents) 
	{
		this.dataSets = dataSets;
		this.prefDataSet = prefDataSet;
		this.viewer = plot;
		this.components = compponents;
	}
	
	/**
	 * @return true, if the caller can continue working, false otherwise.
	 */
	public boolean resolveConflict()
	{
		HierarchicalSetting setting=new HierarchicalSetting("Dataset Conflict");
		
		InformationSetting message=new InformationSetting("Note", "Can not join together probes from different datasets.\n" +
				"Please select one of the options below to continue, \n or click cancel to stop ");
		setting.addSetting(message);
		
		RestrictedStringSetting optionSetting=new RestrictedStringSetting("Option:", null, 3, texts);
		setting.addSetting(optionSetting);
		
		DataSet[] ds=(DataSet[]) dataSets.toArray(new DataSet[dataSets.size()]);
		int selected=-1;
		for(int i=0; i!= ds.length; ++i)
		{
			if(ds[i]==prefDataSet)
				selected=i;
		}
		ObjectSelectionSetting<DataSet> dsSetting=new ObjectSelectionSetting<DataSet>("DataSet", null, selected, ds);
		setting.addSetting(dsSetting);
		
		SettingDialog dialog=new SettingDialog(viewer.getOutermostJWindow(), "DataSet Conflict", setting);
		dialog.showAsInputDialog();
		
		if(dialog.canceled())
		{			
			return false;
		}
		
		if(optionSetting.getSelectedIndex()==0) // omit probes
		{
			omitProbes=true;
			return true;
		}
		
		if(optionSetting.getSelectedIndex()==1) //  create bag, stop after that. 
		{
			new BagSelected().run(viewer, viewer.getModel(), components);
			return false;
		}
		
		if(optionSetting.getSelectedIndex()==2) //  create bag
		{
			selectedDataSet=dsSetting.getObjectValue();
			filterDS=true;
			return true;
		}
				
		if(optionSetting.getSelectedIndex()==3) // cancel & do nothing. 
		{
			return false;
		}
		
		
		return false;
	}
	
	/**
	 * The return value of this method is not meaningful unless resolveConflict() has run. 
	 * @return true, if the caller should not aggregate the probes.
	 * @see resolveConflict(); 
	 */
	public boolean isOmitProbes() 
	{
		return omitProbes;
	}
	
	/**
	 * The return value of this method is not meaningful unless resolveConflict() has run. 
	 * @return true, if the caller should only aggregate probes from the selected DataSet
	 * @see getSelectedDataSet();
	 *  @see resolveConflict(); 
	 */
	public boolean isFilterDS() {
		return filterDS;
	}

	/**
	 * The return value of this method is not meaningful unless resolveConflict() has run. 
	 * @return the dataset which is used to aggregate the probes on. 
	 *  @see resolveConflict(); 
	 */
	public DataSet getSelectedDataSet() 
	{
		return selectedDataSet;
	}

	

	
	
}
