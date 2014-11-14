package mayday.graphviewer.core.auxItems;

import java.awt.Graphics2D;

import mayday.vis3.graph.components.NodeComponent;

public interface AuxItemStrategy 
{
	public boolean isDrawAuxItems();
	
	public void drawAuxItem(NodeComponent comp, Graphics2D g);
}
