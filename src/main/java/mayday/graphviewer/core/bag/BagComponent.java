package mayday.graphviewer.core.bag;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

import mayday.graphviewer.action.GraphBagModel;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.bag.ComponentBag.BagStyle;
import mayday.graphviewer.core.bag.renderer.BagRenderer;
import mayday.graphviewer.core.bag.renderer.CompartmentBagRenderer;
import mayday.graphviewer.core.bag.renderer.ConvexHullBagRenderer;
import mayday.graphviewer.core.bag.renderer.DefaultBagRenderer;
import mayday.graphviewer.core.bag.renderer.EllipseBagRenderer;
import mayday.graphviewer.core.bag.renderer.SubmapBagRenderer;
import mayday.graphviewer.core.bag.tools.BagCentralComponent;
import mayday.graphviewer.core.bag.tools.BagDisplayAction;
import mayday.graphviewer.core.bag.tools.BagDisplayStatisticsAction;
import mayday.graphviewer.core.bag.tools.BagStatisticAction;
import mayday.graphviewer.core.bag.tools.ViewStatisticsAction;
import mayday.graphviewer.core.bag.tools.BagDisplayAction.BagDisplayTargetComponent;
import mayday.graphviewer.statistics.ResultSet;
import mayday.vis3.graph.GraphCanvas;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.LabelRenderer;
import mayday.vis3.graph.components.MetaComponent;
import mayday.vis3.graph.components.LabelRenderer.Orientation;

@SuppressWarnings("serial")
public class BagComponent extends MetaComponent
{
	protected ComponentBag bag;
	protected JLabel nameLabel;
	protected JPanel optionPanel;

	private BagCentralComponent central=null;

	private JRadioButtonMenuItem plainToggler=new JRadioButtonMenuItem("Default");
	private JRadioButtonMenuItem convexToggler=new JRadioButtonMenuItem("Convex Hull");
	private JRadioButtonMenuItem compartmentToggler=new JRadioButtonMenuItem("Compartment");
	private JRadioButtonMenuItem ellipseToggler=new JRadioButtonMenuItem("Ellipse");
	private JRadioButtonMenuItem submapToggler=new JRadioButtonMenuItem("Submap");

	private static final BagRenderer DEFAULT_BAG_RENDERER=new DefaultBagRenderer();
	private static final BagRenderer CONVEX_HULL_BAG_RENDERER=new ConvexHullBagRenderer();
	private static final BagRenderer COMPARTMENT_BAG_RENDERER=new CompartmentBagRenderer();
	private static final BagRenderer ELLIPSE_BAG_RENDERER=new EllipseBagRenderer();
	private static final BagRenderer SUBMAP_BAG_RENDERER=new SubmapBagRenderer();

	private boolean adding=false;
	private boolean removing=false;
	
	public BagComponent(ComponentBag bag)
	{
		super();
		setLayout(new BorderLayout());
		setName(bag.getName());
		this.bag=bag;
		nameLabel=new JLabel(getName());
		Font nameFont=new Font(Font.SANS_SERIF, Font.BOLD, 20);
		nameLabel.setFont(nameFont);		
		//		nameLabel.setLocation(0, 0);
		setLocation(bag.getBoundingRect().getLocation());
		setSize(bag.getBoundingRect().getSize());
		setLabel(bag.getName());

		plainToggler.addActionListener(new ToggleStyleAction(BagStyle.PLAIN));
		convexToggler.addActionListener(new ToggleStyleAction(BagStyle.CONVEX));
		compartmentToggler.addActionListener(new ToggleStyleAction(BagStyle.COMPARTMENT));
		ellipseToggler.addActionListener(new ToggleStyleAction(BagStyle.ELLIPSE));
		submapToggler.addActionListener(new ToggleStyleAction(BagStyle.SUBMAP));

		ButtonGroup grp=new ButtonGroup();
		grp.add(plainToggler);
		grp.add(convexToggler);
		grp.add(compartmentToggler);
		grp.add(ellipseToggler);
		grp.add(submapToggler);

		RemoveAction ra=new RemoveAction();
		ra.putValue(Action.NAME, "X");
		ra.putValue(Action.SHORT_DESCRIPTION, "X");
		ra.putValue(Action.LONG_DESCRIPTION, "Remove this group but keep nodes.");

		JButton raButton= new JButton(ra);
		raButton.setPreferredSize(new Dimension(20, 20));
		raButton.setBorder(BorderFactory.createLineBorder(Color.black,1));

		optionPanel=new JPanel(new FlowLayout(FlowLayout.LEFT));
		optionPanel.setBackground(bag.getColor());
		optionPanel.add( new BagButton(ra));
		optionPanel.add(new BagButton(new MinimizeAndCompressAction()));
		optionPanel.add(new BagButton(new RestoreAndCompressAction()));
		optionPanel.add(nameLabel);
		optionPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				if(e.getClickCount() > 1)
					new RenameAction().actionPerformed(null);				
			}
		});
		optionPanel.addMouseListener(this);
		add(optionPanel, BorderLayout.NORTH);		
	}

	public void setCentral(BagCentralComponent central) 
	{
		if(this.central!=null)
		{
			remove(this.central);
			revalidate();
			this.central.setVisible(false);
			repaint();

		}
		this.central = central;
		add(this.central, BorderLayout.CENTER);		
	}

	public BagRenderer getBagRenderer() 
	{
		switch (bag.getStyle()) 
		{
		case PLAIN: return DEFAULT_BAG_RENDERER;
		case CONVEX: return CONVEX_HULL_BAG_RENDERER;
		case COMPARTMENT: return COMPARTMENT_BAG_RENDERER;	
		case ELLIPSE: return ELLIPSE_BAG_RENDERER;	
		case SUBMAP: return SUBMAP_BAG_RENDERER;
		default:
			return null; // guaranteed not to happen.
		}
	}

	public void setBagName(String l)
	{
		setLabel(l);
		nameLabel.setText(l);
		bag.setName(l);
	}

	public void setBagColor(Color c)
	{
		bag.setColor(c);
	}

	protected JPopupMenu setCustomMenu(JPopupMenu menu)
	{
		menu.add(new RenameAction());	
		menu.add(new ColorAction());
		// build display menu
		JMenu displayMenu=new JMenu("Display");
		displayMenu.add(new DisplayComponentsAction());
		displayMenu.add(new BagDisplayAction(this,BagDisplayTargetComponent.Overview));
		displayMenu.add(new BagDisplayAction(this,BagDisplayTargetComponent.TagCloud));
		displayMenu.add(new BagDisplayAction(this,BagDisplayTargetComponent.Probes));
		displayMenu.add(new BagDisplayAction(this,BagDisplayTargetComponent.HeatMap));
		if(!getBag().getStatistics().isEmpty())
		{
			displayMenu.addSeparator();
			for(ResultSet res: getBag().getStatistics())
			{
				displayMenu.add(new BagDisplayStatisticsAction(this, res));
			}
		}
		menu.add(displayMenu);
		menu.add(new ForkBagAction(getBag(), ((GraphViewerPlot)getParent())));		
		menu.addSeparator();
		menu.add(new BagStatisticAction(getBag(), ((GraphViewerPlot)getParent()).getModelHub()));
		if(!getBag().getStatistics().isEmpty())
		{
			JMenu subMenu=new JMenu("Statistics");
			for(ResultSet res:getBag().getStatistics())
			{
				subMenu.add(new ViewStatisticsAction(res));
			}
			menu.add(subMenu);
		}
		menu.addSeparator();


		switch (bag.getStyle()) {
		case PLAIN:
			plainToggler.setSelected(true);
			break;
		case CONVEX:
			convexToggler.setSelected(true);
			break;
		case COMPARTMENT:
			compartmentToggler.setSelected(true);
			break;
		case ELLIPSE:
			ellipseToggler.setSelected(true);	
			break;
		case SUBMAP:
			submapToggler.setSelected(true);	
			break;	
		}
		JMenu styleMenu=new JMenu("Style");
		styleMenu.add(plainToggler);
		styleMenu.add(convexToggler);
		styleMenu.add(compartmentToggler);
		styleMenu.add(ellipseToggler);
		styleMenu.add(submapToggler);

		menu.add(styleMenu);
		menu.addSeparator();
		menu.add(new CompressAction());
		menu.add(new MinimizeAction());
		menu.add(new RestoreAction());
		menu.addSeparator();
		menu.add(new RemoveAction());
		menu.add(new CloseAction());
		return menu;
	}

	public void mouseDragged(MouseEvent event) 
	{
		if(dragPoint!=null)
		{
			int x= event.getX()-(int)dragPoint.getX();
			int y= event.getY()-(int)dragPoint.getY();
			for(CanvasComponent c:bag)
			{
				c.setLocation(c.getLocation().x+x, c.getLocation().y+y);
				((GraphCanvas)getParent()).revalidateEdge(c);
			}
		}
		if(event.getModifiers()==16 &&!locked)
		{
			if(drag)
			{
				int x= getX()+event.getX()-(int)dragPoint.getX();
				int y= getY()+event.getY()-(int)dragPoint.getY();
				setLocation(x, y);	
				updateParent();
			}else
			{
				drag=true;
				dragPoint=event.getPoint();
				if(!isSelected())
				{
					toggleSelection();
				}
			}
		}
		//		super.mouseDragged(event);
	}
	
	@Override
	public void update(Graphics g) 
	{
		updateBounds();
		super.update(g);
	}


	private void updateBounds()
	{
		Rectangle rect=getBagRenderer().getBoundingShape(this, getBag()).getBounds();
		if(optionPanel.isVisible())
			setBounds(rect.x, rect.y-optionPanel.getHeight(), rect.width, (int) (rect.height+1.5*optionPanel.getHeight()));
		else
			setBounds(rect.x, rect.y, rect.width, (int) (rect.height));
		
		// adjust label, if necessary
		for(int i=20; i!=11; --i)
		{
			Font nameFont=new Font(Font.SANS_SERIF, Font.BOLD, i);
			if(getGraphics()==null)
				break;
			FontMetrics fm= getGraphics().getFontMetrics(nameFont);
			if( fm.getStringBounds(bag.getName(), getGraphics()).getWidth() < getWidth()-70)
			{
				
				nameLabel.setFont(nameFont);
				break;
			}

		}
		nameLabel.setMaximumSize(new Dimension(getWidth()-70,Math.max(nameLabel.getHeight(),25)));
		nameLabel.setPreferredSize(new Dimension(getWidth()-70,Math.max(nameLabel.getHeight(),25)));
	}

	public Rectangle drawableRect()
	{
		Rectangle r=getBounds();
		r.x=optionPanel.getHeight()+optionPanel.getY();
		//		r.height-=optionPanel.getHeight()+optionPanel.getY();
		return r; 
	}

	public void paint(Graphics g1)
	{
		Graphics2D g=(Graphics2D)g1;
		updateBounds();
		//		JComponent cc=paintOverview(getBounds(), bag);
		//		add(cc);
		//		cc.paint(g);
		//		remove(cc);
		optionPanel.setVisible(!getBagRenderer().hideTitleBar());
		getBagRenderer().paint(g, this, getBag(), isSelected());


		
		paintEdges(g);
		paintChildren(g1);
//		paintLabels(g);
	}

	public ComponentBag getBag()
	{
		return this.bag;
	}

	public class RemoveAction extends AbstractAction
	{
		public RemoveAction()
		{
			super("Remove (retains content)");
		}

		public void actionPerformed(ActionEvent e) 
		{
			setVisible(false);
			bag.setComponentsVisible(true);
			if(getParent() instanceof GraphCanvas)
			{
				((GraphBagModel)((GraphCanvas)getParent()).getModel()).remove(BagComponent.this);
			}	
			bag.getModel().remove(bag);
		}		
	}

	public class CloseAction extends AbstractAction
	{
		public CloseAction()
		{
			super("Close (deletes content)");
		}

		public void actionPerformed(ActionEvent e) 
		{
			setVisible(false);
			if(getParent() instanceof GraphCanvas)
			{
				((GraphBagModel)((GraphCanvas)getParent()).getModel()).remove(BagComponent.this);
			}	
			bag.close();
		}		
	}

	public class RenameAction extends AbstractAction
	{
		public RenameAction()
		{
			super("Rename...");
		}

		public void actionPerformed(ActionEvent e) 
		{
			String n=(String)JOptionPane.showInputDialog(BagComponent.this, "Set Group name", "Group name", JOptionPane.QUESTION_MESSAGE, null, null, bag.getName());
			if(n!=null)
			{
				if(n.length() >0)
				{
					setBagName(n);					
				}
			}
		}		
	}

	public class ColorAction extends AbstractAction
	{
		public ColorAction()
		{
			super("Set color...");
		}

		public void actionPerformed(ActionEvent e) 
		{
			Color c=JColorChooser.showDialog(BagComponent.this, "Set Color", bag.getColor());
			if(c!=null)
			{
				setBagColor(c);
				optionPanel.setBackground(c);
			}
		}		
	}


	public class CompressAction extends AbstractAction
	{
		public CompressAction()
		{
			super("Compress");
		}

		public void actionPerformed(ActionEvent e) 
		{
			bag.compress();
		}		
	}

	public class MinimizeAction extends AbstractAction
	{
		public MinimizeAction()
		{
			super("Minimize all components");
		}

		public void actionPerformed(ActionEvent e) 
		{
			bag.minimizeAll();
		}		
	}

	public class MinimizeAndCompressAction extends AbstractAction
	{
		public MinimizeAndCompressAction()
		{
			super("M");
		}

		public void actionPerformed(ActionEvent e) 
		{
			bag.minimizeAll();
			bag.compress();
		}		
	}

	public class RestoreAndCompressAction extends AbstractAction
	{
		public RestoreAndCompressAction()
		{
			super("R");
		}

		public void actionPerformed(ActionEvent e) 
		{
			bag.restoreAll();
			bag.compress();
		}		
	}

	public class RestoreAction extends AbstractAction
	{
		public RestoreAction()
		{
			super("Restore all components");
		}

		public void actionPerformed(ActionEvent e) 
		{
			bag.restoreAll();
		}		
	}

	public class ToggleStyleAction implements ActionListener
	{
		private BagStyle style;

		public ToggleStyleAction(BagStyle style) 
		{
			this.style = style;
		}

		public void actionPerformed(ActionEvent e) 
		{
			bag.setStyle(style);	
			bag.setComponentsVisible(! getBagRenderer().hideComponents());
				
		}		
	}

	@Override
	public boolean hasLabel() 
	{
		return false;
	}

	@Override
	public boolean contains(int x, int y) 
	{
		return super.contains(x, y);
		//		return boundingShape.contains(x, y);
	}

	@Override
	public boolean contains(Point p) 
	{
		return contains(p.x,p.y);
		//		return boundingShape.contains(p);
	}

	private void paintEdges(Graphics2D g2)
	{	
		Graphics2D g=(Graphics2D)g2.create();
		Shape s= getBagRenderer().getBoundingShape(this, getBag());
		
		g.translate(-getX(), -getY());
		g.setClip(s);
		((GraphViewerPlot)getParent()).paintEdges(g, bag.getEdges());
		g.translate(getX(), getY());
		g.dispose();
	}

	private class BagButton extends JButton
	{
		public BagButton(AbstractAction a) 
		{
			super(a);
			setPreferredSize(new Dimension(15, 15));
			setBorder(BorderFactory.createLineBorder(bag.getColor().darker(),1));
		}
	}

	public class DisplayComponentsAction extends AbstractAction 
	{
		public DisplayComponentsAction() 
		{
			super("Components");
		}

		@Override
		public void actionPerformed(ActionEvent e) 
		{
			bag.setComponentsVisible(true);
			if(central!=null)
			{
				remove(central);
				revalidate();
				central.setVisible(false);
				repaint();				
			}
			central=null;			
		}
	}

	public void paintLabels(Graphics2D g)
	{
		LabelRenderer renderer=new LabelRenderer();
		renderer.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,12));

		Rectangle tmpRect = new Rectangle(); 
		Orientation ori=null;
		for (CanvasComponent comp:bag) {

			if(!(comp instanceof CanvasComponent))
				continue;
			if (comp.isVisible())
			{
				Rectangle cr;
				cr = comp.getBounds(tmpRect);

				boolean hitClip = g.hitClip(cr.x-getX(), cr.y-getY(), cr.width,cr.height+30);
				if(hitClip)
				{	
					CanvasComponent cc=((CanvasComponent)comp);  
					if(!cc.hasLabel())
						continue;
					JLabel rr=null;
					ori=cc.getLabelOrientation()==null?Orientation.BELOW:cc.getLabelOrientation();
					rr=renderer.getLabelComponent(null, cc, cc.isSelected(),ori);
					rr.getBounds(cr);
					Graphics cg = g.create(cr.x-getX(), cr.y-getY(), cr.width,cr.height);
					cg.setColor(comp.getForeground());
					cg.setFont(comp.getFont());	       
					rr.paint(cg);          	
				}
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent event) 
	{
		if( (event.getModifiersEx() & InputEvent.ALT_DOWN_MASK) == InputEvent.ALT_DOWN_MASK)	
		{
			adding=true;
			// inform!
			if(getParent() instanceof GraphViewerPlot)
			{
				GraphViewerPlot par=(GraphViewerPlot)getParent();
				par.setConnectingNode(this);
			}
		}
		if(removing)
		{
			removing=false;
			if(getParent() instanceof GraphViewerPlot)
			{
				GraphViewerPlot par=(GraphViewerPlot)getParent();
				par.setConnectingNode(null);
			}
		}
	}

	@Override
	public void mouseExited(MouseEvent event) 
	{
		if(adding)
		{
			adding=false;		
			if(getParent() instanceof GraphViewerPlot)
			{
				GraphViewerPlot par=(GraphViewerPlot)getParent();
				par.setConnectingNode(null);
			}
		}
		
		if( (event.getModifiersEx() & InputEvent.ALT_DOWN_MASK) == InputEvent.ALT_DOWN_MASK)	
		{
			removing=true;
			// inform!
			if(getParent() instanceof GraphViewerPlot)
			{
				GraphViewerPlot par=(GraphViewerPlot)getParent();
				par.setConnectingNode(this);
			}
		}	
	}

	public boolean isAdding() 
	{
		return adding;
	}
	
	public boolean isRemoving() 
	{
		return removing;
	}
	
	@Override
	public void repaint(int x, int y, int width, int height) 
	{
		updateBounds();
		super.repaint(x, y, width, height);
	}
	
	/**
	 * set the selection state. do not notify. 
	 */
	public void setSelected(boolean selected) 
	{
		updateBounds();
		super.setSelected(selected);

	}


	/**
	 * toggle selection of this component and notify the selection listeners.
	 */
	public void toggleSelection() 
	{
		super.toggleSelection();
		updateBounds();
		repaint();
		updateParentSurroundings();
	}


}
