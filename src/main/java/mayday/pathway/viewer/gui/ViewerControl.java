package mayday.pathway.viewer.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Collections;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import mayday.core.gui.MaydayFrame;
import mayday.core.pluma.PluginInfo;
import mayday.core.structures.graph.Node;
import mayday.pathway.sbgn.graph.AbstractSBGNPathwayGraph;
import mayday.pathway.sbgn.processdiagram.container.Complex;
import mayday.pathway.sbgn.processdiagram.entitypool.Macromolecule;
import mayday.pathway.sbgn.processdiagram.entitypool.SimpleChemical;
import mayday.pathway.sbgn.processdiagram.process.Transition;
import mayday.pathway.viewer.SBGNModel;
import mayday.pathway.viewer.SBGNViewer;
import mayday.pathway.viewer.canvas.AlignLayouter;
import mayday.vis3.graph.GraphCanvas;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.NodeComponent;
import mayday.vis3.graph.components.LabelRenderer.Orientation;
import mayday.vis3.graph.layout.FruchtermanReingoldLayout;
import mayday.vis3.graph.model.DefaultGraphModel;
import mayday.vis3.graph.renderer.ComponentRenderer;

@SuppressWarnings("serial")
public final class ViewerControl extends MaydayFrame
{
	private SBGNViewer viewer;
	
	private GraphCanvas reactionViewer;
	
	private static final Image image= PluginInfo.getIcon("mayday/sbgn/reaction.png").getImage().getScaledInstance(32,32,Image.SCALE_SMOOTH);
	
	public ViewerControl(SBGNViewer viewer)
	{
		super("Control");
		this.viewer=viewer;
		init();
		pack();
	}
	
	private void init()
	{
		setLayout(new BorderLayout());
		
		SBGNModel model=(SBGNModel)viewer.getModel();
		
		// init and deploy display tool bar:
		
		JButton showSideNodesButton=new JButton(new ShowSideNodeAction(true));
		showSideNodesButton.setText("");
		showSideNodesButton.setToolTipText("Show side components");	
		showSideNodesButton.setIcon(new ImageIcon(PluginInfo.getIcon("mayday/sbgn/sidenodes1.png").getImage().getScaledInstance(32,32,Image.SCALE_SMOOTH)));
		
		JButton hideSideNodesButton=new JButton(new ShowSideNodeAction(false));
		hideSideNodesButton.setText("");
		hideSideNodesButton.setToolTipText("Hide side components");
		hideSideNodesButton.setIcon(new ImageIcon(PluginInfo.getIcon("mayday/sbgn/sidenodes2.png").getImage().getScaledInstance(32,32,Image.SCALE_SMOOTH)));
		
		JButton showSBGNButton=new JButton(new ShowSBGNAction(true));
		showSBGNButton.setText("");
		showSBGNButton.setToolTipText("Show SBGN");	
		showSBGNButton.setIcon(new ImageIcon(PluginInfo.getIcon("mayday/sbgn/sbgn1.png").getImage().getScaledInstance(32,32,Image.SCALE_SMOOTH)));
		
		JButton hideSBGNButton=new JButton(new ShowSBGNAction(false));
		hideSBGNButton.setText("");
		hideSBGNButton.setToolTipText("Hide SBGN");
		hideSBGNButton.setIcon(new ImageIcon(PluginInfo.getIcon("mayday/sbgn/sbgn2.png").getImage().getScaledInstance(32,32,Image.SCALE_SMOOTH)));
	
		JButton showSummaryButton=new JButton(new ShowSummaryAction(true));
		showSummaryButton.setText("");
		showSummaryButton.setToolTipText("Show Reaction Summary");	
		showSummaryButton.setIcon(new ImageIcon(PluginInfo.getIcon("mayday/sbgn/summary1.png").getImage().getScaledInstance(32,32,Image.SCALE_SMOOTH)));
		
		JButton hideSummaryButton=new JButton(new ShowSummaryAction(false));
		hideSummaryButton.setText("");
		hideSummaryButton.setToolTipText("Hide Reaction Summary");
		hideSummaryButton.setIcon(new ImageIcon(PluginInfo.getIcon("mayday/sbgn/summary2.png").getImage().getScaledInstance(32,32,Image.SCALE_SMOOTH)));
		
		JToolBar display=new JToolBar("Display");
		display.add(showSideNodesButton);
		display.add(hideSideNodesButton);
		display.add(showSBGNButton);
		display.add(hideSBGNButton);
		display.add(showSummaryButton);
		display.add(hideSummaryButton);
		
		
		add(display, BorderLayout.PAGE_START);
	
		//Initialize and deploy entity lists. 
		
		TypedComponentList reactionList=new TypedComponentList(model,viewer, Transition.class);		
		TypedComponentList compoundList=new TypedComponentList(model,viewer, SimpleChemical.class);
		TypedComponentList proteinList=new TypedComponentList(model,viewer, Macromolecule.class);
		TypedComponentList complexList=new TypedComponentList(model,viewer, Complex.class);
		
		JTabbedPane entityLists=new JTabbedPane();
		entityLists.addTab("Reactions", reactionList);
		entityLists.addTab("Small Chemicals", compoundList);
		entityLists.addTab("Proteins and Enzymes", proteinList);
		entityLists.addTab("Complexes", complexList);
		
		entityLists.setBorder(BorderFactory.createTitledBorder("Pathway Entities"));
				
		add(entityLists,BorderLayout.CENTER);
		
		//Initialize and deploy reaction subgraph viewer. 
		
//		GraphModel reactionModel=new DefaultGraphModel( ((SBGNPathwayGraph)model.getGraph()).getReactionGraph());
		ReactionViewModel reactionModel=new ReactionViewModel(model);
		reactionViewer=new GraphCanvas(reactionModel);
		reactionViewer.setRenderer(new ReactionViewRenderer());

		reactionViewer.setEnabled(true);
		reactionViewer.setPreferredSize(new Dimension(400,300));
		reactionViewer.setMaximumSize(new Dimension(400,300));
		JPanel reactionPanel=new JPanel();
		reactionPanel.setBorder(BorderFactory.createTitledBorder("Reactions"));
		reactionPanel.add(new JScrollPane(reactionViewer));
		
		add(new JScrollPane(reactionViewer),BorderLayout.SOUTH);	
//		reactionViewer.setLayouter(viewer.getSettings().getLayouter());
//		reactionViewer.setLayouter(new AlignLayouter());
		reactionViewer.setLayouter(new FruchtermanReingoldLayout());
		reactionViewer.updateLayout(new Rectangle(400,300));
		
	}
	
	private class ReactionViewModel extends DefaultGraphModel
	{
		private SBGNModel model;
		
		public ReactionViewModel(SBGNModel model) 
		{
			super( ((AbstractSBGNPathwayGraph)model.getGraph()).getReactionGraph());
			this.model=model;
			init();
		}
		
		@Override
		protected void init() 
		{
			if(model==null)
				return;
			clear();
			
			for(Node n: ((AbstractSBGNPathwayGraph)model.getGraph()).getReactionGraph().getNodes())
			{
				CanvasComponent comp=new ReactionProxyComponent(n,model.getComponent(n),viewer);
				addComponent(comp);	
				getNodeMap().put(comp, n);
				getComponentMap().put(n, comp);			
			}
			Collections.sort(getComponents());			
		}
	}
	
	private class ReactionViewRenderer implements ComponentRenderer
	{
		
		
		public void draw(Graphics2D g, Node node, Rectangle bounds, Object value,
				String label, boolean selected) 
		{
			g.drawImage(image, 0, 0, 32, 32, null);
			
		}

		public Dimension getSuggestedSize(Node node, Object value) 
		{
			return new Dimension(32,32);
		}
		
		@Override
		public Orientation getLabelOrientation(Node node, Object value) 
		{
			return null;
		}
		
		@Override
		public boolean hasLabel(Node node, Object value) 
		{
			return false;
		}
	}
	
	private class ReactionProxyComponent extends NodeComponent
	{
		CanvasComponent parent;
		GraphCanvas canvas;
		
		public ReactionProxyComponent(Node node, CanvasComponent component, GraphCanvas owner) 
		{
			super(node);
			this.parent=component;
			this.canvas=owner;
			addMouseListener(this);
		}
		
		@Override
		public void mouseClicked(MouseEvent event) 
		{
			canvas.center(parent.getBounds(), true);
			event.consume();			
		}
		
		
		
	}
	
	private class ShowSideNodeAction extends AbstractAction
	{
		private boolean show;
				
		public ShowSideNodeAction(boolean b) 
		{
			super( b?"Show":"Hide" + " Side Components");
			show=b;
		}
		
		public void actionPerformed(ActionEvent e) 
		{
			viewer.displaySideNodes(show);			
		}
	}
	
	private class ShowSBGNAction extends AbstractAction
	{
		private boolean show;
				
		public ShowSBGNAction(boolean b) 
		{
			super( b?"Show":"Hide" + " SBGN Glyphs");
			show=b;
		}
		
		public void actionPerformed(ActionEvent e) 
		{
			viewer.setSBGNRendering(show);			
		}
	}
	
	private class ShowSummaryAction extends AbstractAction
	{
		private boolean show;
				
		public ShowSummaryAction(boolean b) 
		{
			super( b?"Show":"Hide" + " Reaction Summaries");
			show=b;
		}
		
		public void actionPerformed(ActionEvent e) 
		{
			viewer.setSummaryNodes(show);			
		}
	}
	
	
	
}
