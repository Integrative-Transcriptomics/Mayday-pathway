package mayday.pathway.keggview.pathways.gui.canvas;

/**
 * Listener that allows to react to changes in a pathway model.
 * @author Stephan Symons
 */
public interface PathwayModelListener 
{
	/**
	 * Invoked when the pathway has changed. 
	 */
	public void pathwayChanged();
}
