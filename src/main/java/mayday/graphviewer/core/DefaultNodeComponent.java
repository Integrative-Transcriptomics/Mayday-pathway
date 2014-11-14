package mayday.graphviewer.core;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;

import mayday.core.Probe;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.graph.nodes.Nodes;
import mayday.graphviewer.action.AddSummaryProbeAction;
import mayday.graphviewer.action.CenterAction;
import mayday.graphviewer.action.GraphComponentSettingsAction;
import mayday.graphviewer.action.URLAction;
import mayday.graphviewer.action.UpdateSummaryProbes;
import mayday.graphviewer.core.auxItems.ConfigureAuxAction;
import mayday.graphviewer.core.bag.BagComponent;
import mayday.graphviewer.gui.RendererPluginMenu;
import mayday.vis3.graph.GraphCanvas;
import mayday.vis3.graph.actions.RoleAction;
import mayday.vis3.graph.actions.SelectAction;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.components.NodeComponent;
import mayday.vis3.graph.dialog.ComponentZoomFrame;
import mayday.vis3.graph.menus.RendererMenu;
import mayday.vis3.graph.model.SummaryProbe;
import mayday.vis3.graph.renderer.ComponentRenderer;
import mayday.vis3.graph.renderer.dispatcher.RendererDispatcher;
import mayday.vis3.graph.renderer.dispatcher.RendererPluginSetting;

@SuppressWarnings("serial")
public class DefaultNodeComponent extends MultiProbeComponent 
{
	private Map<String, ComponentRenderer> rendererMap=new HashMap<String, ComponentRenderer>();
	private RendererDispatcher rendererDispatcher;

	public DefaultNodeComponent(MultiProbeNode node) 
	{
		super(node);
	}

	public DefaultNodeComponent(Node node) 
	{
		super(node);
	}

	public void setRendererDispatcher(RendererDispatcher r)
	{
		this.rendererDispatcher=r;		
	}

	@Override
	public void paint(Graphics g1) 
	{		
		Graphics2D g=(Graphics2D)g1;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		rendererDispatcher.render(g, getNode(), getDisplayProbes(), false, this);
	}

	@Override
	public JPopupMenu setCustomMenu(JPopupMenu menu) 
	{
		menu=super.setCustomMenu(menu);
		menu.add(new ClearIndividualRendererAction(this, rendererDispatcher));
		menu.add(new ConfigureAuxAction(this));
		menu.addSeparator();
		menu.add(new AddSummaryProbeAction((GraphViewerPlot)getParent(),this));
		menu.add(new UpdateSummaryProbes(this));
		menu.add(new GraphComponentSettingsAction(this));
		menu.add(conversionMenu());
		menu.addSeparator();
		menu.add(selectionMenu());
		menu.add(new CenterAction((GraphCanvas)getParent(),this));		
		menu.add(new URLAction((GraphViewerPlot)getParent()));
		return menu;
	}
	
	@Override
	public String getToolTipText() 
	{
		return super.getToolTipText();
	}

	protected JMenu selectionMenu()
	{
		GraphCanvas viewer=(GraphCanvas)getParent();
		JMenu menu=new JMenu("Select");
		menu.add(new SelectAction(viewer, viewer.getModel(), this, SelectAction.NEIGHBORS));
		menu.add(new SelectAction(viewer, viewer.getModel(), this, SelectAction.IN_NEIGHBORS));
		menu.add(new SelectAction(viewer, viewer.getModel(), this, SelectAction.OUT_NEIGHBORS));
		menu.add(new SelectAction(viewer, viewer.getModel(), this, SelectAction.ALL_REACHABLE));
		return menu;
	}

	protected JMenu conversionMenu()
	{
		JMenu rolesMenu=new JMenu("Set role");
		rolesMenu.add(new RoleAction(Nodes.Roles.NODE_ROLE,this));
		rolesMenu.add(new RoleAction(Nodes.Roles.PROBE_ROLE,this));
		rolesMenu.add(new RoleAction(Nodes.Roles.PROBES_ROLE,this));
		rolesMenu.add(SBGNRoles.roleSelectionMenu(this));
		return rolesMenu;
	}

	public void setRendererSetting(RendererPluginSetting renderer)
	{
		rendererDispatcher.addIndividualRenderer(this, renderer);
	}


	public void setRenderer(Map<String,ComponentRenderer> rendererMap)
	{
		this.rendererMap=rendererMap;
	}

	public Map<String, ComponentRenderer> getRendererMap() {
		return rendererMap;
	}

	@Override
	public ComponentRenderer getRenderer() 
	{
		return rendererDispatcher.getRenderer(this, getNode());
	}

	@Override
	public RendererMenu getRendererMenu() 
	{
		if(rendererMenu==null)
		{
			RendererPluginSetting setting=null;
			if(rendererDispatcher.getIndividualRenderers().containsKey(this))
			{
				setting=rendererDispatcher.getIndividualRenderers().get(this);
			}else
			{
				setting=rendererDispatcher.getRendererSetting(this, getNode()).clone();
			}
			setRendererMenu(new RendererPluginMenu(setting, null, this));
		}
		return rendererMenu;
	}

	@Override
	public void mouseDragged(MouseEvent event) 
	{
		if(getParent() instanceof GraphViewerPlot)
			if( (event.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK)	
			{
				((GraphViewerPlot)getParent()).setConnectingNode(this);
				((GraphViewerPlot)getParent()).setConnectingPoint(new Point(getX()+event.getX(),getY()+event.getY()));		

				Rectangle reprec=new Rectangle(getBounds());
				reprec.add(new Point(getX()+event.getX(),getY()+event.getY()));
				getParent().repaint(reprec.x-50, reprec.y-50, reprec.width+100, reprec.height+100);

				//				((GraphCanvas)getParent()).updatePlotNow();			
			}
		if( (event.getModifiersEx() & InputEvent.ALT_DOWN_MASK) == InputEvent.ALT_DOWN_MASK)	
		{
			Rectangle reprec=new Rectangle(getBounds());
			reprec.add(new Point(getX()+event.getX(),getY()+event.getY()));
			getParent().repaint(reprec.x-50, reprec.y-50, reprec.width+100, reprec.height+100);
		}
		super.mouseDragged(event);
	}

	@Override
	public void mouseEntered(MouseEvent arg0) 
	{
		if(getParent() instanceof GraphViewerPlot)
		{
			if( ((GraphViewerPlot)getParent()).getConnectingNode()!=null)
			{
				((GraphViewerPlot)getParent()).setTargetNode(this);			
			}
		}
	}

	@Override
	public void mouseExited(MouseEvent arg0) 
	{
		if(getParent() instanceof GraphViewerPlot)
		{
			if( ((GraphViewerPlot)getParent()).getConnectingNode()!=null)
			{
				((GraphViewerPlot)getParent()).setTargetNode(null);			
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent event)
	{
		//check for popup menu
		if(event.isPopupTrigger())
		{
			super.mousePressed(event);
		}	
		if(event.getButton()==MouseEvent.BUTTON2)
		{
			zoom=new ComponentZoomFrame(this,getRenderer());
			zoom.setLocation(event.getXOnScreen(),event.getYOnScreen());
			zoom.setVisible(true);
		}		
	}

	@Override
	public void mouseReleased(MouseEvent event) 
	{

		if(getParent() instanceof GraphViewerPlot)
		{
			GraphViewerPlot gv=((GraphViewerPlot)getParent());
			if(gv.getConnectingNode() instanceof BagComponent)
			{				
				BagComponent bc=(BagComponent)gv.getConnectingNode();

				if(bc.isAdding())
				{
					for(CanvasComponent cc: gv.getMovingComponents())
					{
						if(!bc.getBag().contains(cc))
						{
							bc.getBag().addComponent(cc);					
							if(gv.getComponentZOrder(bc) < gv.getComponentZOrder(cc))
							{
								gv.setComponentZOrder(bc, gv.getComponentZOrder(cc));						
							}
						}
					}
				}
				if(bc.isRemoving())
				{
					for(CanvasComponent cc: gv.getMovingComponents())
					{
						if(bc.getBag().contains(cc))
						{
							bc.getBag().removeComponent(cc);					
							if(gv.getComponentZOrder(bc) < gv.getComponentZOrder(cc))
							{
								gv.setComponentZOrder(bc, gv.getComponentZOrder(cc));						
							}
						}
					}
				}
				gv.setConnectingNode(null);

			}else
			{
				if( gv.getConnectingNode()!=null)
				{
					if(gv.getTargetNode()!=null)
					{
						gv.getModel().connect(gv.getConnectingNode(), gv.getTargetNode());
						((NodeComponent)gv.getConnectingNode()).nodeUpdated(NodeUpdate.EDGES);
						((NodeComponent)gv.getTargetNode()).nodeUpdated(NodeUpdate.EDGES);
						gv.setConnectingNode(null);
						gv.setTargetNode(null);				
					}
					gv.setConnectingNode(null);
					gv.setTargetNode(null);
				}
			}
		}
		if(drag)
			super.mouseReleased(event);
		else
			((GraphCanvas)getParent()).updatePlotNow();
	}

	public void resetSize()
	{
		super.resetSize();
		setSize(rendererDispatcher.getRenderer(this,getNode()).getSuggestedSize(getNode(),getPayload()));
		repaint();
	}
	
	public void invalidateCache(){
		rendererDispatcher.clearCache(this);
	}
	
	@Override
	public void nodeUpdated(NodeUpdate cause) {
		if(cause==NodeUpdate.ROLE)
			invalidateCache();
		if(cause==NodeUpdate.EDGES) {
			System.out.println(getLabel());
			new UpdateSummaryProbes(this).actionPerformed(null);
			
			for(Probe p: getProbes())
			{
				if(p instanceof SummaryProbe)
				{
					System.out.println("!");
					((SummaryProbe) p).updateSummary();
					invalidateCache();
					updateDisplayMode();					
					repaint();
					revalidate();
					updateParentLocal();
				}
			}

		}			
	}
	
	/**
	 * set the selection state. do not notify. 
	 */
	public void setSelected(boolean selected) 
	{
		invalidateCache();
		super.setSelected(selected);
	}


	/**
	 * toggle selection of this component and notify the selection listeners.
	 */
	public void toggleSelection() 
	{
		invalidateCache();
		super.toggleSelection();
	}
}
