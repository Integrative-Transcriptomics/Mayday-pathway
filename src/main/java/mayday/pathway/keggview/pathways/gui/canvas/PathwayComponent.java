package mayday.pathway.keggview.pathways.gui.canvas;

import java.awt.Image;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import mayday.core.Probe;
import mayday.pathway.keggview.pathways.graph.PathwayGraph;
import mayday.pathway.keggview.pathways.graph.PathwayNode;
import mayday.pathway.keggview.pathways.gui.action.LookUpAction;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.renderer.ComponentRenderer;
import mayday.vis3.graph.renderer.primary.DefaultComponentRenderer;
import mayday.vis3.model.ViewModel;

@SuppressWarnings("serial")
public abstract class PathwayComponent extends MultiProbeComponent 
{
	
	protected ViewModel viewModel;
	
	public PathwayComponent(PathwayNode node, PathwayGraph graph)
	{
		super(node);
		setRenderer(DefaultComponentRenderer.getDefaultRenderer());
		setBounds(graph.getEntry(node).getGraphics().x, 
				graph.getEntry(node).getGraphics().y, 
				graph.getEntry(node).getGraphics().width, 
				graph.getEntry(node).getGraphics().height);
		setLabel(node.getName());
	}
		
	protected JPopupMenu setCustomMenu(JPopupMenu menu)
	{
		menu.addSeparator();
		menu.add(new JMenuItem(new LookUpAction("Look up in KEGG", ((PathwayNode)getNode()).getLink())));	
		return menu;
	}
	
	public List<Probe> getProbes()
	{
		return ((PathwayNode)getNode()).getProbes();
	}

	public abstract Image getImage();

	/* (non-Javadoc)
	 * @see mayday.canvas.components.CanvasComponent#setRenderer(mayday.canvas.renderer.ComponentRenderer)
	 */
	@Override
	public void setRenderer(ComponentRenderer renderer) 
	{
		super.setRenderer(renderer);
//		super.setRenderer(new ImageDecorator(renderer, getImage()));
	}
	
	public abstract void setProbeNamesAsLabel();
	
	public abstract void resetLabel();
	
	
	
	
}
