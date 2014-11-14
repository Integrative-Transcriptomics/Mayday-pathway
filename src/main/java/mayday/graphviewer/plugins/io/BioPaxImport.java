package mayday.graphviewer.plugins.io;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.PluginManager.IGNORE_PLUGIN;
import mayday.core.settings.Settings;
import mayday.core.settings.SettingsDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.MappingSourceSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.pathway.biopax.parser.BioPaxParser;
import mayday.pathway.biopax.parser.MasterObject;
import mayday.pathway.sbgn.graph.SBGNPathwayGraph;
import mayday.pathway.viewer.SBGNModel;
import mayday.pathway.viewer.canvas.AlignLayouter;
import mayday.pathway.viewer.canvas.PathwayLayouter;
import mayday.pathway.viewer.canvas.SBGNLayouterWrapper;
import mayday.pathway.viewer.gui.PathwaySelectionDialog;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.layout.FruchtermanReingoldLayout;
import mayday.vis3.graph.layout.SimpleCircularLayout;
import mayday.vis3.graph.layout.SnakeLayout;
import mayday.vis3.graph.layout.SugiyamaLayout;
import mayday.vis3.graph.model.GraphModel;

@IGNORE_PLUGIN
public class BioPaxImport extends AbstractGraphViewerPlugin
{
	private PathSetting biopaxFile=new PathSetting("BioPax file", "The pathway file to be imported", null, false, true, false);
	private MappingSourceSetting mapping;
//	private ObjectSelectionSetting<DataSet> metaboliteDataSetSetting;
	
	@Override
	public void run(final GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		HierarchicalSetting setting=new HierarchicalSetting("BioPax Import");
		mapping=new MappingSourceSetting(canvas.getModelHub().getViewModel().getDataSet());
		setting.addSetting(biopaxFile).addSetting(mapping);
		
		SettingsDialog settingDialog=new SettingsDialog(null, "BioPax Import", new Settings(setting, getPluginInfo().getPreferences()));
		settingDialog.setModal(true);
		settingDialog.setVisible(true);
		
		if(!settingDialog.closedWithOK())
			return;
		
		BioPaxParser parser=new BioPaxParser();
		Map<String, MasterObject> res;
		try 
		{
			res = parser.parse(biopaxFile.getStringValue());			
		} catch (Exception e) 
		{
			throw new RuntimeException("Error reading file", e);
		}
		
		PathwaySelectionDialog psd=new PathwaySelectionDialog(res);
		psd.setModal(true);
		psd.setVisible(true);
		
		if(psd.isCancelled())
			return;
		
		MasterObject selected=psd.getSelectedPathway();
		SBGNPathwayGraph g= new SBGNPathwayGraph(selected);
		SBGNModel m=new SBGNModel(g);
		m.setAnnotation(canvas.getModelHub().getViewModel().getProbes(),mapping);	
		
		PathwayLayouter layouter=new PathwayLayouter();
		layouter.setBranchedLayouter(new SugiyamaLayout());
		layouter.setCircularLayouter(new SimpleCircularLayout());
		layouter.setLinearLayouter(new SnakeLayout());
		layouter.setComplexLayouter(new FruchtermanReingoldLayout());
		
//		layouter.layout(canvas, canvas.getBounds(), m);
		canvas.setLayouter(new SBGNLayouterWrapper(layouter));
		canvas.setModel(m);
		AlignLayouter.getInstance().layout(canvas, canvas.getBounds(), canvas.getModel());
		canvas.revalidateEdges();
		canvas.updateSize();
		canvas.updatePlotNow();
	}
	
	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.BioPaxImport",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Import a pathway from a BioPax file",
				"BioPax Import"				
		);
		pli.addCategory(IMPORT_CATEGORY);
		return pli;	
	}
}
