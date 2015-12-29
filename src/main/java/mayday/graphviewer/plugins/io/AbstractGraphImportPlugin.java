package mayday.graphviewer.plugins.io;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.core.settings.Settings;
import mayday.core.settings.SettingsDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.MappingSourceSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.core.Utilities;
import mayday.graphviewer.graphmodelprovider.AbstractGraphModelProvider;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;


public abstract class AbstractGraphImportPlugin extends AbstractGraphViewerPlugin 
{
	protected PathSetting fileSetting;
	protected MappingSourceSetting mapping;
	protected boolean hasLayout=false;
	
	public void run(final GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		HierarchicalSetting setting=new HierarchicalSetting(getFormat()+" Import");
		String lastDir= Utilities.prefs.get(Utilities.LAST_GRAPH_IMPORT_DIR, System.getProperty("user.home"));
		fileSetting=new PathSetting("File",null,lastDir,false,true,false);
		mapping=new MappingSourceSetting(canvas.getModelHub().getViewModel().getDataSet());
		setting.addSetting(fileSetting).addSetting(mapping);
		
		SettingsDialog settingDialog=new SettingsDialog(null, getFormat()+" Import", new Settings(setting, getPluginInfo().getPreferences()));
		settingDialog.setModal(true);
		settingDialog.setVisible(true);
		
		if(!settingDialog.closedWithOK())
			return;
		
		try{
			Graph gr=importFile(canvas, model, components);
			
			Map<DataSet, MappingSourceSetting> mappings=new HashMap<DataSet,MappingSourceSetting>();
			mappings.put(canvas.getModelHub().getViewModel().getDataSet(),mapping);
			MultiHashMap<DataSet, ProbeList> probeLists=new MultiHashMap<DataSet, ProbeList>();
			for(ProbeList pl:canvas.getModelHub().getViewModel().getProbeLists(false) )
				probeLists.put(canvas.getModelHub().getViewModel().getDataSet(), pl);
			AbstractGraphModelProvider.annotateNodes(probeLists, gr, mappings);
			canvas.setModel(new SuperModel(gr));
			
			if(!hasLayout)
				canvas.updateLayout();
			
		}catch(Exception e)
		{
			throw new RuntimeException("Error importing file", e);
		}
		
		
	}
	
	protected abstract Graph importFile(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) throws Exception;
	
	protected abstract String getFormat();
}
