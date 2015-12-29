package mayday.pathway.sbgn.graph;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mayday.core.Probe;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.maps.MultiHashMap;

/**
 * Base class of all SBGN nodes. Each SBGN node has a paintable shape, the glyph, or is abstract. 
 * @author symons
 *
 */
public abstract class SBGNNode extends MultiProbeNode
{
	protected boolean showLabel=true;
	
	protected MultiHashMap<String,String> annotations=new MultiHashMap<String, String>();
	
	protected HashMap<String,String> references=new HashMap<String, String>(); 
	
	protected String compartmentName=""; 
	
	public static final String NAME_ANNOTATION="Name";
	public static final String SHORTNAME_ANNOTATION="Short Name";
	public static final String SYNONYM_ANNOTATION="Synonym";
	public static final String COMMENT_ANNOTATION="Comment";
	
	public SBGNNode(Graph graph, String name) 
	{
		super(graph, new ArrayList<Probe>());	
		setName(name);
	}
	
	/**
	 * @return The outline shape of the glyph of the node. The shape should be positioned at 0,0. 
	 */
	public abstract Shape getGlyph();

	/**
	 * @return the showLabel
	 */
	public boolean isShowLabel() {
		return showLabel;
	}

	/**
	 * @param showLabel the showLabel to set
	 */
	public void setShowLabel(boolean showLabel) {
		this.showLabel = showLabel;
	}

	public void addAnnotation(String key, String value)
	{
		annotations.put(key, value);		
	}
	
	public List<String> getAnnotation(String key)
	{
		return annotations.get(key);
	}
	
	public void addReference(String source, String acc)
	{
		references.put(source,acc);		
	}
	
	public String getReference(String key)
	{
		return references.get(key);
	}

	/**
	 * @return the compartment
	 */
	public String getCompartmentName() {
		return compartmentName;
	}

	/**
	 * @param compartment the compartment to set
	 */
	public void setCompartmentName(String compartment) {
		this.compartmentName = compartment;
	}

	/**
	 * @return the annotations
	 */
	public MultiHashMap<String, String> getAnnotations() {
		return annotations;
	}

	/**
	 * @return the references
	 */
	public HashMap<String, String> getReferences() {
		return references;
	}
	
	public List<String> getPossibleNames() 
	{
		List<String> res=new ArrayList<String>();
		res.add(name);
		
		if(annotations.containsKey(SHORTNAME_ANNOTATION))
		{
			for(String s:annotations.get(SHORTNAME_ANNOTATION))
			{
				res.add(s);
			}
		}	
		if(annotations.containsKey(SYNONYM_ANNOTATION))
		{
			for(String s:annotations.get(SYNONYM_ANNOTATION))
			{
				res.add(s);
			}
		}
		for(String s:references.keySet())
		{
			res.add(references.get(s));
		}
		
		return res;
	}
	
	
}
