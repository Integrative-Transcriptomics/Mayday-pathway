package mayday.pathway.viewer;

import mayday.core.DataSet;
import mayday.core.pluma.PluginInfo;
import mayday.core.settings.Settings;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting.LayoutStyle;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.MappingSourceSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.vis3.graph.layout.CanvasLayouter;
import mayday.vis3.graph.model.SummaryProbeSetting;
import mayday.vis3.graph.renderer.ComponentRenderer;
import mayday.vis3.graph.renderer.dispatcher.RendererPluginSetting;
import mayday.vis3.graph.vis3.SuperColorProvider;

public class ViewerSettings extends Settings
{
	
	private PathSetting pathwaySetting;
	private MappingSourceSetting mappingSourceSetting;
	
	private BooleanSetting displaySBGNSetting;
	private RendererPluginSetting rendererSetting;
	
	private ViewerLayoutSettings layoutSettings;
	
	private BooleanSetting displaySideNodes;
	private BooleanSetting displaySummaryNodes;
		
	private SummaryProbeSetting summarySettings; 
	
	public ViewerSettings(DataSet ds, SuperColorProvider coloring) 
	{
		super(new HierarchicalSetting("Pathway Viewer Settings"), null);
		
		pathwaySetting=new PathSetting("Pathway file","A biopax owl flatfile containing a pathway.",null,false,true,false);
		mappingSourceSetting=new MappingSourceSetting(ds);
		

		rendererSetting=new RendererPluginSetting(ds, coloring);
//		=new RestrictedStringSetting("Renderer", "Set the node renderer", 5, RendererFactory.RENDERERS);
		displaySBGNSetting=new BooleanSetting("Display SBGN","Display pathway components as SBGN glyphs",true);
		
		layoutSettings=new ViewerLayoutSettings();		
		
		displaySideNodes=new BooleanSetting("Display Side Nodes","Display all pathway components, " +
				"even though they are present only in a single reaction. Switching this off retains only the main reaction components.",true);
		displaySummaryNodes=new BooleanSetting("Summarize reactions","Display a summary of some kind at each reaction node.",true);
		summarySettings=new SummaryProbeSetting();
		
		HierarchicalSetting rendererSettings=new HierarchicalSetting("Renderer Settings");
		rendererSettings.
			addSetting(displaySBGNSetting).
			addSetting(rendererSetting);
		
		HierarchicalSetting displaySettings=new HierarchicalSetting("Display Settings");
		displaySettings.
			addSetting(displaySideNodes).
			addSetting(displaySummaryNodes).
			addSetting(summarySettings);
		root.
			addSetting(pathwaySetting).
			addSetting(mappingSourceSetting).
			addSetting(rendererSettings).
			addSetting(layoutSettings).
			addSetting(displaySettings);
		root.setLayoutStyle(LayoutStyle.TABBED);
		connectToPrefTree(PluginInfo.getPreferences("mayday.canvas.SBGNViewer"));
	}
	
	public RendererPluginSetting getRendererSetting() {
		return rendererSetting;
	}

	public String getPathwayFile()
	{
		return pathwaySetting.getStringValue();
	}
	
	
	
	public String toString() {
		return root.toPrefNode().toDebugString();
	}
	
	public ComponentRenderer getRenderer()
	{
		return rendererSetting.getRenderer();
	}
	
	public CanvasLayouter getLayouter()
	{
		return layoutSettings.getLayouter();
	}
	
	public void addSettingChangeListener(SettingChangeListener changeListener)
	{
		root.addChangeListener(changeListener);
	}
	
	public boolean isDisplaySummaryNodes()
	{
		return displaySummaryNodes.getBooleanValue();
	}
	
	public boolean isDisplaySideNodes()
	{
		return displaySideNodes.getBooleanValue();
	}
	
	public void removeNotify(SettingChangeListener changeListener)
	{
		root.removeChangeListener(changeListener);
	}

	/**
	 * @return the pathwaySetting
	 */
	public PathSetting getPathwaySetting() {
		return pathwaySetting;
	}

	/**
	 * @return the mappingSourceSetting
	 */
	public MappingSourceSetting getMappingSourceSetting() {
		return mappingSourceSetting;
	}

	/**
	 * @return the displaySBGNSetting
	 */
	public BooleanSetting getDisplaySBGNSetting() {
		return displaySBGNSetting;
	}

	/**
	 * @return the layoutSettings
	 */
	public ViewerLayoutSettings getLayoutSettings() {
		return layoutSettings;
	}

	/**
	 * @return the displaySideNodes
	 */
	public BooleanSetting getDisplaySideNodes() {
		return displaySideNodes;
	}

	/**
	 * @return the displaySummaryNodes
	 */
	public BooleanSetting getDisplaySummaryNodes() {
		return displaySummaryNodes;
	}

	/**
	 * @return the summarySettings
	 */
	public SummaryProbeSetting getSummarySettings() {
		return summarySettings;
	}

	
	

	
}
