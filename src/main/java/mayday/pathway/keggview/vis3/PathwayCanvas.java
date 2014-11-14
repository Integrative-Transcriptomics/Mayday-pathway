package mayday.pathway.keggview.vis3;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.typed.MappingSourceSetting;
import mayday.core.structures.graph.Edge;
import mayday.core.structures.maps.DefaultValueMap;
import mayday.pathway.keggview.ModelFactory;
import mayday.pathway.keggview.PathwayModel;
import mayday.pathway.keggview.kegg.KEGGLicenseDialog;
import mayday.pathway.keggview.pathways.AnnotationManager;
import mayday.pathway.keggview.pathways.PathwayManager;
import mayday.pathway.keggview.pathways.graph.GeneNode;
import mayday.pathway.keggview.pathways.graph.ReactionEdge;
import mayday.pathway.keggview.pathways.gui.MapRenderer;
import mayday.pathway.keggview.pathways.gui.PathwaySelector;
import mayday.pathway.keggview.pathways.gui.canvas.KEGGLayouter;
import mayday.pathway.keggview.pathways.gui.canvas.MapComponent;
import mayday.pathway.keggview.pathways.gui.canvas.PathwayComponent;
import mayday.pathway.keggview.pathways.gui.canvas.PathwayModelListener;
import mayday.pathway.keggview.plugins.KEGGSettings;
import mayday.vis3.ColorProviderSetting;
import mayday.vis3.ZoomController;
import mayday.vis3.graph.GraphCanvas;
import mayday.vis3.graph.actions.ClearSelectionAction;
import mayday.vis3.graph.actions.ComponentsAction;
import mayday.vis3.graph.actions.SelectAllAction;
import mayday.vis3.graph.actions.ShowAllAction;
import mayday.vis3.graph.arrows.Arrow;
import mayday.vis3.graph.arrows.ArrowSettings;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.components.SputnikLabel;
import mayday.vis3.graph.components.SputnikLabel.Orientation;
import mayday.vis3.graph.components.SputnikLabel.TextStyle;
import mayday.vis3.graph.edges.router.EdgePoints;
import mayday.vis3.graph.menus.RendererMenu;
import mayday.vis3.graph.model.GraphModel;
import mayday.vis3.graph.model.MultiProbeSelectionModel;
import mayday.vis3.graph.renderer.ComponentRenderer;
import mayday.vis3.graph.vis3.SuperColorProvider;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;

@SuppressWarnings("serial")
public class PathwayCanvas extends GraphCanvas implements ViewModelListener, PathwayModelListener, SettingChangeListener
{
	private ViewModel viewModel;
	private SuperColorProvider coloring;
	private ModelFactory modelFactory;
	private KEGGSettings keggSettings;

	public PathwayCanvas(AnnotationManager annotationManager, PathwayManager pathwayManager)
	{
		super(null);
		selectionModel=new MultiProbeSelectionModel(getModel(),viewModel);
		modelFactory=new ModelFactory(annotationManager,pathwayManager);
		modelFactory.addPathwayModelListener(this);
		setLayouter(new KEGGLayouter());
		setLayout(null);
		setSize(new Dimension(1000,800));
		setMinimumSize(new Dimension(1000,800));
		setPreferredSize(getMinimumSize());
		setBackground(Color.white);
		new ZoomController();
	}

	@Override
	public void setup(PlotContainer plotContainer) 
	{
		super.setup(plotContainer);
		// initialize view model and coloring		
		viewModel=plotContainer.getViewModel();
		viewModel.addViewModelListener(this);

		coloring=new  SuperColorProvider(viewModel);
		coloring.setMode(ColorProviderSetting.COLOR_BY_EXPERIMENT_VALUE);
		coloring.addChangeListener(new ChangeListener()	{
			public void stateChanged(ChangeEvent e) 
			{
				updatePlot();				
			}});
		keggSettings=new KEGGSettings(viewModel.getDataSet(),coloring);
		keggSettings.getRoot().addChangeListener(this);
		plotContainer.setPreferredTitle("Pathway Viewer", this);	
		plotContainer.addViewSetting(coloring.getSetting(), this);
		
		for(Setting s: keggSettings.getRoot().getChildren())
		{
			plotContainer.addViewSetting(s, this);
		}
		
//		plotContainer.addViewSetting(keggSettings.getRoot(), this);
		plotContainer.addMenu(createMenu());
		plotContainer.addMenu(createComponentMenu());
		setContextMenu();
		
		// open the dialog
		SwingUtilities.invokeLater(new Runnable(){

			public void run() 
			{
				queryAndProcessMapping();		

			}});
	}	

	public void setModel(GraphModel model)
	{			
		//		super.setModel(model);
		if(model==null) return;
		this.model = model;	
		removeAll();
		zoom(1.0);
		for(CanvasComponent c:getModel().getComponents())
		{
			add(c);	
			c.setRenderer(getRenderer());
			
			
			c.addCanvasComponentListener(selectionModel);
			c.addCanvasComponentListener(this);
//			((PathwayComponent)c).setProbeNamesAsLabel();
			SputnikLabel lab=new SputnikLabel(c,  new ImageIcon( ((PathwayComponent)c).getImage()),TextStyle.TRUNCATE);
			add(lab);
			if(c instanceof MapComponent)
				lab.setLabelOrientation(Orientation.CENTER);
			else
				lab.setLabelOrientation(Orientation.BELOW);
			c.setLabelComponent(lab);
			setComponentZOrder(c, 1);
			setComponentZOrder(lab, 0);
			
		}


		layouter.layout(this, new Rectangle(500,500,500,500), getModel());

		
		updateSize();
		edgeShapes=new HashMap<Edge, Path2D>(model.getEdges().size());
		selectionModel.setModel(model);
		if(viewModel!=null)
			viewModel.addViewModelListener(((MultiProbeSelectionModel)selectionModel));
		for(CanvasComponent comp:getModel().getComponents())
		{
			if(comp instanceof MapComponent)
			{
				((MapComponent) comp).setModel(modelFactory);
				comp.setRenderer(MapRenderer.getDefaultRenderer());
				continue;
			}
			((MultiProbeComponent) comp).setViewModel(viewModel);
			RendererMenu menu=new RendererMenu(coloring,comp);
			comp.setRendererMenu(menu);

		}

		applyEdgeSettings();
		
		coloring.setExperiment(0);
		updatePlot();
		
//		updateRenderer();
		zoom(1.0d);
	}

	private JMenu createComponentMenu()
	{
		JMenu menu=new JMenu("Components");
		menu.add(new ComponentsAction(this));
		menu.addSeparator();
		menu.add(new SelectAllAction(this));
		menu.add(new ClearSelectionAction(this));
		menu.addSeparator();
		menu.add(new ShowAllAction(getModel()));
		return menu;
	}

	private JMenu createMenu()
	{
		JMenu menu=new JMenu("Pathway");

		menu.add(new SelectPathwayAction());
		menu.add(new SelectProbeMappingAction());
//		menu.add(coloring.getSetting().getMenuItem(null));
	
		JMenu detailMenu=new JMenu("Details");
		JMenuItem fullDetails=new JMenuItem(new DetailAction(0, "All Nodes"));
		JMenuItem mediumDetails=new JMenuItem(new DetailAction(2, "Backbone"));
		JMenuItem lowDetails=new JMenuItem(new DetailAction(3, "Hubs"));
		detailMenu.add(fullDetails);
		detailMenu.add(mediumDetails);
		detailMenu.add(lowDetails);
		menu.add(detailMenu);
		
//		menu.add(new ClassSelectionAction());
		menu.add(new KEGGLicenseDialog.KEGGLicenseDialogAction());
		
		return menu;
	}

	/* (non-Javadoc)
	 * @see mayday.canvas.GraphCanvas#setEdgeMenu()
	 */
	@Override
	protected void setEdgeMenu() 
	{
		super.setEdgeMenu();
		edgeMenu.add(new EdgeDetailAction());
	}

	public void mouseMoved(MouseEvent event) 
	{	
		Rectangle eventRect=new Rectangle(event.getX()-1, event.getY()-1, 5, 5);
		boolean pre=highlightEdge==null;
		for(Edge e: getModel().getEdges())
		{		
			if(getShape(e)==null) continue;
			if( (getShape(e)).intersects(eventRect))
			{		
				highlightEdge=e;
				updatePlot();
				event.consume();
				return;
			}
		}
		if(!pre && highlightEdge==null)
		{
			updatePlot();
		}		
		event.consume();			
	}

	public void mousePressed(MouseEvent event) 
	{
		if(event.isPopupTrigger())
		{
			Rectangle eventRect=new Rectangle(event.getX()-1, event.getY()-1, 3, 3);
			for(Edge e: getModel().getEdges())
			{		
				if(getShape(e)==null) continue;
				if( ((Path2D)getShape(e)).intersects(eventRect))
				{		
					highlightEdge=e;
					updatePlot();
					edgeMenu.show(event.getComponent(),event.getX(), event.getY());
					event.consume();	
					return;
				}
			}			
			if(contextMenu==null) setContextMenu();
			contextMenu.show(event.getComponent(),event.getX(), event.getY());
			event.consume();		
		}		
	}

	public void paintPlot(Graphics2D g)
	{
		if(model==null)
			return;
		
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		PathwayModel model=((PathwayModel)getModel());

		// draw title
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, getWidth(), 25);
		g.setColor(Color.BLACK);
		Font f=new Font("Arial",Font.BOLD,20);
		Font f2=g.getFont();

		// draw error message if necessary and leave
		if(model.getGraph()==null)
		{
			g.drawString("Review Settings", 25, 20);
			g.setColor(Color.red);
			f=new Font("Arial",Font.BOLD,25);
			g.setFont(f);
			g.drawString("Error drawing the Pathway", 100, 100);
			g.setColor(Color.black);
			g.drawString("Please review the settings for this plugins:", 100, 130);
			g.drawString("See \"Mayday\" -> \"Preferences\" -> \"Plugins\" -> \"Pathway Viewer\" ", 100, 160);
			f=new Font("Arial",Font.BOLD,18);
			g.setFont(f);
			g.drawString("- Review Data Path ", 100, 190);
			g.drawString("- Data Path must contain ko, compounds, taxonomy, map_title.tab.", 100, 215);			
			g.setFont(f2);
			return;
		}

		g.setFont(f);
		g.drawString(model.getTitle() +" ("+model.getName()+")", 25, 20);
		g.setFont(f2);

		
		paintEdges(g,model.getPathwayGraph().getReactions(),keggSettings.getReactionStroke(), keggSettings.getReactionColor().getColorValue());
		paintEdges(g,model.getPathwayGraph().getRelations(), keggSettings.getRelationStroke(), keggSettings.getRelationColor().getColorValue());

		if(dragRect)
		{   			
			g.drawRect(selectionRect.x, selectionRect.y, selectionRect.width, selectionRect.height);
		}		
		paintChildren(g);	
	}
	
	protected void paintEdges(Graphics2D g, Collection<? extends Edge> edges, Stroke stroke, Color color)
	{
		for(Edge e: edges)
		{
			//			if(! getVisibleRect().contains((model.getComponent(e.getSource()).getLocation())) &&  
			//			   ! getVisibleRect().contains((model.getComponent(e.getTarget()).getLocation())) )
			//					continue;

			if(model.getComponent(e.getSource()).isVisible() && model.getComponent(e.getTarget()).isVisible())
			{				
				if(edgeShapes.get(e)==null)
				{
					edgeShapes.put(e,edgeRouter.routeEdge(e,this, model));					
				}
				EdgePoints points=edgeRouter.getAdjustedPoints(model.getComponent(e.getSource()), model.getComponent(e.getTarget()));
				Point2D point=edgeRouter.getSupportPoint(model.getComponent(e.getSource()), model.getComponent(e.getTarget()));
				// draw name

				Path2D es=edgeShapes.get(e);
				// extract second last point in here: 
				PathIterator pi=es.getPathIterator(null);
				while(!pi.isDone())
				{
					double[] cur=new double[6];
					int t=pi.currentSegment(cur);
					if(t!=PathIterator.SEG_CLOSE && t!=PathIterator.SEG_MOVETO)
					{						
						if(t==PathIterator.SEG_CUBICTO)
							point.setLocation(cur[2], cur[3]);
						if(t==PathIterator.SEG_QUADTO)
							point.setLocation(cur[0], cur[1]);
					}
					pi.next();
				}	

				Stroke backupStroke=g.getStroke();
				g.setStroke(stroke);
				g.setColor(color);
				if(e == highlightEdge)
				{
					g.draw(edgeShapes.get(e));
					if(arrowSettings.get(e).isRenderTarget())
					{
						g.fill(Arrow.paint(point, points.target,arrowSettings.get(e)));
						g.draw(Arrow.paint(point, points.target,arrowSettings.get(e)));
					}
					if(arrowSettings.get(e).isFillTarget())
					{
						g.fill(Arrow.paint(point, points.target,arrowSettings.get(e)));
						g.draw(Arrow.paint(point, points.target,arrowSettings.get(e)));
					}					
					g.setColor(Color.black);

				}else
				{
					Path2D p=edgeShapes.get(e);
					g.setColor(arrowSettings.get(e).getEdgeColor());
					g.draw(p);
					// draw Arrowheads:
					if(arrowSettings.get(e).isRenderTarget())
					{
						g.setColor(Color.white);
						g.fill(Arrow.paint(point, points.target,arrowSettings.get(e)));
						g.setColor(arrowSettings.get(e).getEdgeColor());
						g.draw(Arrow.paint(point, points.target,arrowSettings.get(e)));
					}
					if(arrowSettings.get(e).isFillTarget())
					{
						g.setColor(arrowSettings.get(e).getFillColor());
						g.fill(Arrow.paint(point, points.target,arrowSettings.get(e)));
						g.draw(Arrow.paint(point, points.target,arrowSettings.get(e)));
					}
				}
				g.setStroke(backupStroke);	
				// draw name
				//				if(e.getName()!=null && !e.getName().isEmpty())
				//				{
				//					g.drawString(e.getName(), (int)edgeShapes.get(e).getBounds().getCenterX(), (int)edgeShapes.get(e).getBounds().getCenterY());
				//				}


			}
		}
	}
	
	private void applyEdgeSettings()
	{
		ArrowSettings relationSetting=ArrowSettings.noArrows();
		relationSetting.setEdgeColor(keggSettings.getRelationColor().getColorValue());
		
		ArrowSettings onlyStartArrowSettings=ArrowSettings.bothArrows();
		onlyStartArrowSettings.setRenderTarget(false);
		onlyStartArrowSettings.setEdgeColor(keggSettings.getReactionColor().getColorValue());
		ArrowSettings onlyTargetArrowSettings=ArrowSettings.bothArrows();
		onlyTargetArrowSettings.setRenderSource(false);
		onlyTargetArrowSettings.setEdgeColor(keggSettings.getReactionColor().getColorValue());
		
		arrowSettings=new DefaultValueMap<Edge, ArrowSettings>(new HashMap<Edge, ArrowSettings>(),relationSetting);
		
		for(Edge e:getModel().getEdges())
		{
			if(e instanceof ReactionEdge)
			{	
				if(((ReactionEdge) e).isReversible() && e.getTarget() instanceof GeneNode)
				{
						arrowSettings.put(e, onlyStartArrowSettings);
				}				
				if(e.getSource() instanceof GeneNode)
				{
						arrowSettings.put(e, onlyTargetArrowSettings);
				}
			}
		}
	}

	public void viewModelChanged(ViewModelEvent vme) 
	{
		if(vme.getChange()==ViewModelEvent.DATA_MANIPULATION_CHANGED)
		{
			coloring.setExperimentExtremes();			
		}
		updatePlot();
	}

	@Override
	public void removeNotify()  
	{
		super.removeNotify();
		if (coloring!=null) coloring.removeNotify();
		viewModel.removeViewModelListener(this);
		viewModel.removeViewModelListener((MultiProbeSelectionModel)selectionModel);
	}



	public void pathwayChanged() 
	{
		setModel(modelFactory.getCurrentModel());
		updatePlot();
	}

	private class SelectPathwayAction extends AbstractAction
	{
		public SelectPathwayAction()
		{
			super("Select Pathway...");
		}

		public void actionPerformed(ActionEvent ev) 
		{
			PathwaySelector pathwaySelector=new PathwaySelector(modelFactory);
			pathwaySelector.setVisible(true);
		}	
	}

	private void queryAndProcessMapping()
	{
//		keggSettings=new KEGGSettings(viewModel.getDataSet());
		setRendererSilent(keggSettings.getRendererSetting().getRenderer());
		JDialog dialog=keggSettings.getDialog();
		dialog.setModal(true);
		dialog.setVisible(true);
		
		//Acquire information about taxon.
		String taxon=keggSettings.getTaxon();
		
		//intinalize annotation mapping
		try 
		{
			modelFactory.initalize(keggSettings.getKEGGPath(), taxon, (MIGroupSelection<MIType>)null, viewModel.getProbes());
		} catch (IOException e1) 
		{
			throw new RuntimeException("Error reading KEGG data");
		}

		switch (keggSettings.getMappingSource().getMappingSource()) 
		{
		case MappingSourceSetting.PROBE_NAMES:
			modelFactory.setMapping(AnnotationManager.createMappingByName(viewModel.getProbes()));
			break;
		case MappingSourceSetting.PROBE_DISPLAY_NAMES:
			modelFactory.setMapping(AnnotationManager.createMappingByDisplayName(viewModel.getProbes()));
			break;	
		case MappingSourceSetting.MIO:
			modelFactory.setMapping(AnnotationManager.createMappingByMIO(keggSettings.getMappingSource().getMappingGroup(),viewModel.getProbes()));
			break;	
		default:
			break;
		}
		
		coloring.setExperiment(0);
		String pathwayName=keggSettings.getDefaultPathway().getStringValue();
		MIGroupSelection<MIType> groups3=viewModel.getDataSet().getMIManager().getGroupsForName("Pathway Source").filterByType("PAS.MIO.String");
		if(!groups3.isEmpty())
		{
			try{
				pathwayName=groups3.get(0).getMIO(viewModel.getProbeLists(false).get(0)).toString();
			}catch(Exception e)
			{
				// do nothing. 
			}
		}
		
		try{
		if(keggSettings.hasFile())		
			modelFactory.getPathway(new File(keggSettings.getFile()));
		else
			modelFactory.getPathway(pathwayName);
		} catch (Exception e1) 
		{
			throw new RuntimeException("Error loading the pathway.");
		}
		((MultiProbeSelectionModel)selectionModel).setViewModel(viewModel);
		viewModel.addViewModelListener(((MultiProbeSelectionModel)selectionModel));
		updatePlot();
//		rendererMenu.setEnabled(true);
		
		
	}

	private class SelectProbeMappingAction extends AbstractAction
	{
		public SelectProbeMappingAction()
		{
			super("Select Probe Mapping...");
		}

		public void actionPerformed(ActionEvent ev) 
		{
			queryAndProcessMapping();
		}		
	}

	private class DetailAction extends AbstractAction
	{
		private int value;

		public DetailAction(int value, String name)
		{
			super(name);
			this.value=value;
		}

		public void actionPerformed(ActionEvent e) 
		{
			((PathwayModel)getModel()).applyImportance(value);
			repaint();			
		}		
	}

	private class EdgeDetailAction extends AbstractAction
	{
		public EdgeDetailAction()
		{
			super("Details...");
		}

		public void actionPerformed(ActionEvent e) 
		{
//				// TODO: show a dialog that gives interesting information about that edge!

		}		
	}

	public void stateChanged(SettingChangeEvent e) 
	{
		if(e.hasSource(keggSettings.getRendererSetting()))
		{
			setRendererSilent(keggSettings.getRendererSetting().getRenderer());
		}
		applyEdgeSettings();
	
	}
	
	/**
	 * @param renderer the renderer to set
	 */
	public void setRendererSilent(ComponentRenderer renderer) 
	{
		this.renderer = renderer;
		if(model==null) return;
		for(CanvasComponent c:this.model.getComponents())
		{
			if(c instanceof MapComponent) 
				continue;
			c.setRenderer(renderer);
//			c.setSize(renderer.getSuggestedSize(null,null));
		}
		revalidateEdges();
		updatePlot();
		
	}


}
