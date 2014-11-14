package mayday.graphviewer.core.bag.renderer;

import java.awt.Graphics2D;
import java.awt.Shape;

import mayday.graphviewer.core.bag.BagComponent;
import mayday.graphviewer.core.bag.ComponentBag;

public interface BagRenderer 
{
	/**
	 * Render the bag on the components graphic context. 
	 * @param g
	 * @param comp
	 * @param bag
	 * @param isSelected
	 */
	public void paint(Graphics2D g, BagComponent comp, ComponentBag bag, boolean isSelected);
	
	/**
	 * Calculate the bounding shape of the bag
	 * @param comp
	 * @param bag
	 * @return
	 */
	public Shape getBoundingShape(BagComponent comp, ComponentBag bag);
	
	
	/**
	 * Decides whether the components contained in the bag should be hidden. 
	 * @return
	 */
	public boolean hideComponents();
	
	/**
	 * Decides whether the bag title bar is to be shown.
	 * @return
	 */
	public boolean hideTitleBar();

}
