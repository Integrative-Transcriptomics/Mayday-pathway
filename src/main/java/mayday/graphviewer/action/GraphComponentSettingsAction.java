/**
 * 
 */
package mayday.graphviewer.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.graphviewer.core.DefaultNodeComponent;
import mayday.pathway.sbgn.processdiagram.ProcessDiagram;
import mayday.vis3.graph.dialog.GraphComponentEditor;

/**
 * Calls a graphComponentEditor that allows to modify the settings of the node 
 * @author Stephan Symons
 *
 */
@SuppressWarnings("serial")
public class GraphComponentSettingsAction extends AbstractAction
{
	private final DefaultNodeComponent component;

	public GraphComponentSettingsAction(DefaultNodeComponent defaultNodeComponent)
	{
		super("Settings");
		component = defaultNodeComponent;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		GraphComponentEditor d=new GraphComponentEditor((DefaultNode)component.getNode());
		d.addAdditionalRoles(ProcessDiagram.ROLES);
		d.setModal(true);
		d.setVisible(true);
		component.setLabel(component.getNode().getName());
		if(component.getLabelComponent()!=null)
			component.getLabelComponent().setText(component.getLabel());
		component.repaint();
		
	}
}