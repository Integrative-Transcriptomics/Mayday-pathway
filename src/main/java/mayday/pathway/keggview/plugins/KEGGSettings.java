package mayday.pathway.keggview.plugins;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.util.ArrayList;

import mayday.core.DataSet;
import mayday.core.pluma.PluginInfo;
import mayday.core.settings.Settings;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting.LayoutStyle;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.core.settings.typed.FilesSetting;
import mayday.core.settings.typed.MappingSourceSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.vis3.graph.renderer.dispatcher.RendererPluginSetting;
import mayday.vis3.graph.vis3.SuperColorProvider;

public class KEGGSettings extends Settings
{
	private FilesSetting pathwaySetting;	
	private PathSetting keggDataDirectory;
	private MappingSourceSetting mappingSource;
	private StringSetting taxonSetting;
	private StringSetting defaultPathway;
	
	private ColorSetting reactionColor;
	private ColorSetting relationColor;
	
	private RendererPluginSetting rendererSetting;
	
	private BooleanSetting displayNeighborPathway;
	private BooleanSetting displayMapLinks;
	
	private RestrictedStringSetting reactionStroke;
	private RestrictedStringSetting relationStroke;
	
	
	private static float[] dashedPattern ={8.0f,8.0f};
	private static float[] dottedPattern ={2.0f, 2.0f};
	
	public static final Stroke solidStroke=new BasicStroke(2.0f);
	public static final Stroke dashedStroke=new BasicStroke(2.0f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,10.0f,dashedPattern,0.0f);
	public static final Stroke dottedStroke=new BasicStroke(2.0f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,10.0f,dottedPattern,0.0f);
	
	private String[] strokes={"solid","dashed","dotted"};
	
	public KEGGSettings(DataSet ds, SuperColorProvider coloring) 
	{
		super(new HierarchicalSetting("KEGG Pathway Settings"), null);
		
		keggDataDirectory=new PathSetting("KEGG Data directory","The directory where KEGG files are " +
				"stored and downloaded files can be kept. It must contain the following files: " +
				"compound, ko, map_title.tab. These files may automatically be downloaded, but this is very" +
				"time consuming. ","",true,true,false);
		
		mappingSource = new MappingSourceSetting(ds);
		taxonSetting=new StringSetting("Taxon","The organism the data originates \nfrom in KEGG three letter shortcut notation.\n" +
				"Human: hsa\n E.Coli: eco\n Mouse: mmu\n C.elegans: cel\n Stremptomyces coelicolor: sco.","hsa");
		
		pathwaySetting=new FilesSetting("Pathway file","A KEGG xml file with the pathway to be displayed.\n" +
				" If no file is selected, the default pathway is loaded.",new ArrayList<String>(),true,"*.xml");
		
//		rendererSetting=new RestrictedStringSetting("Renderer", "Set the node renderer",5 , RendererFactory.RENDERERS);
		
		reactionColor=new ColorSetting("Reaction color",null,Color.black);
		reactionStroke=new RestrictedStringSetting("Reaction style",null,0,strokes);
		
		relationColor=new ColorSetting("Relation color",null,Color.black);
		relationStroke=new RestrictedStringSetting("Relation style",null,1,strokes);
		
		HierarchicalSetting reaction=new HierarchicalSetting("Reaction Settings").addSetting(reactionColor).addSetting(reactionStroke);
		
		HierarchicalSetting relation=new HierarchicalSetting("Relation Settings").addSetting(relationColor).addSetting(relationStroke);
		
	
		
		displayNeighborPathway=new BooleanSetting("Display neighbour pathways",null,true);
		displayMapLinks=new BooleanSetting("Display Maplinks",null,true);
		defaultPathway=new StringSetting("Default Pathway",null,"00010",false);
		
		HierarchicalSetting display=new HierarchicalSetting("Display Settings");
		display.addSetting(displayNeighborPathway).addSetting(displayMapLinks);
		
		rendererSetting=new RendererPluginSetting(ds, coloring);
		rendererSetting.setPrimaryRenderer("PAS.GraphViewer.Renderer.Chromogram");
		
		root.
		addSetting(taxonSetting).
		addSetting(keggDataDirectory).
		addSetting(pathwaySetting).
		addSetting(defaultPathway).
		addSetting(mappingSource).
		addSetting(reaction).
		addSetting(relation).
		addSetting(display).
		addSetting(rendererSetting);
				
		connectToPrefTree(PluginInfo.getPreferences("mayday.pathway.keggviewer"));
		
		root.setLayoutStyle(LayoutStyle.TREE);
	}
	
	public boolean isDisplayMapLinks()
	{
		return displayMapLinks.getBooleanValue();		
	}
	
	public boolean isDisplayNeighbors()
	{
		return displayNeighborPathway.getBooleanValue();
	}

	public String getTaxon()
	{
		return taxonSetting.getStringValue();
	}
	
	public String getKEGGPath()
	{
		return keggDataDirectory.getStringValue();
	}
	
	public String getFile()
	{
		return pathwaySetting.getFileNames().get(0);
	}
	
	public boolean hasFile()
	{
		return !pathwaySetting.getFileNames().isEmpty();
	}

	/**
	 * @return the pathwaySetting
	 */
	public FilesSetting getPathwaySetting() {
		return pathwaySetting;
	}


	/**
	 * @param pathwaySetting the pathwaySetting to set
	 */
	public void setPathwaySetting(FilesSetting pathwaySetting) {
		this.pathwaySetting = pathwaySetting;
	}


	/**
	 * @return the keggDataDirectory
	 */
	public PathSetting getKeggDataDirectory() {
		return keggDataDirectory;
	}


	/**
	 * @param keggDataDirectory the keggDataDirectory to set
	 */
	public void setKeggDataDirectory(PathSetting keggDataDirectory) {
		this.keggDataDirectory = keggDataDirectory;
	}


	/**
	 * @return the mappingSource
	 */
	public MappingSourceSetting getMappingSource() {
		return mappingSource;
	}


	/**
	 * @param mappingSource the mappingSource to set
	 */
	public void setMappingSource(MappingSourceSetting mappingSource) {
		this.mappingSource = mappingSource;
	}


	/**
	 * @return the taxonSetting
	 */
	public StringSetting getTaxonSetting() {
		return taxonSetting;
	}


	/**
	 * @param taxonSetting the taxonSetting to set
	 */
	public void setTaxonSetting(StringSetting taxonSetting) {
		this.taxonSetting = taxonSetting;
	}
	
	/**
	 * @return the reactionStroke
	 */
	public Stroke getReactionStroke() 
	{
		
		switch (reactionStroke.getSelectedIndex()) 
		{
		case 0: return solidStroke;
		case 1: return dashedStroke;
		case 2: return dottedStroke;
		default:return solidStroke;			
		}
	}
	
	/**
	 * @return the relationStroke
	 */
	public Stroke getRelationStroke() 
	{
		
		switch (relationStroke.getSelectedIndex()) 
		{
		case 0: return solidStroke;
		case 1: return dashedStroke;
		case 2: return dottedStroke;
		default:return solidStroke;			
		}
	}

	/**
	 * @return the defaultPathway
	 */
	public StringSetting getDefaultPathway() {
		return defaultPathway;
	}

	/**
	 * @return the reactionColor
	 */
	public ColorSetting getReactionColor() {
		return reactionColor;
	}

	/**
	 * @return the relationColor
	 */
	public ColorSetting getRelationColor() {
		return relationColor;
	}

	/**
	 * @return the displayNeighborPathway
	 */
	public BooleanSetting getDisplayNeighborPathway() {
		return displayNeighborPathway;
	}

	/**
	 * @return the displayMapLinks
	 */
	public BooleanSetting getDisplayMapLinks() {
		return displayMapLinks;
	}

	/**
	 * @return the dashedPattern
	 */
	public static float[] getDashedPattern() {
		return dashedPattern;
	}

	/**
	 * @return the dottedPattern
	 */
	public static float[] getDottedPattern() {
		return dottedPattern;
	}

	/**
	 * @return the strokes
	 */
	public String[] getStrokes() {
		return strokes;
	}

	public RendererPluginSetting getRendererSetting() {
		return rendererSetting;
	}

	public void setRendererSetting(RendererPluginSetting rendererSetting) {
		this.rendererSetting = rendererSetting;
	}
	
	
	
}
