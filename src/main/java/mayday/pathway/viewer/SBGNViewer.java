package mayday.pathway.viewer;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.geom.Path2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.Probe;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingsDialog;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.pathway.biopax.parser.BioPaxParser;
import mayday.pathway.biopax.parser.MasterObject;
import mayday.pathway.sbgn.graph.AbstractSBGNPathwayGraph;
import mayday.pathway.sbgn.graph.SBGNEdge;
import mayday.pathway.sbgn.graph.SBGNPathwayGraph;
import mayday.pathway.sbgn.processdiagram.arcs.Modulation;
import mayday.pathway.sbgn.processdiagram.entitypool.EntityPoolNode;
import mayday.pathway.sbgn.processdiagram.entitypool.StateDescription;
import mayday.pathway.sbgn.processdiagram.entitypool.StatefulEntityPoolNode;
import mayday.pathway.sbgn.processdiagram.entitypool.UnitOfInformation;
import mayday.pathway.sbgn.processdiagram.process.Transition;
import mayday.pathway.viewer.canvas.AlignLayouter;
import mayday.pathway.viewer.canvas.SBGNComponent;
import mayday.pathway.viewer.canvas.SBGNLayouterWrapper;
import mayday.pathway.viewer.canvas.StateDescriptionComponent;
import mayday.pathway.viewer.canvas.UnitOfInformationComponent;
import mayday.pathway.viewer.gui.PathwaySelectionDialog;
import mayday.pathway.viewer.gui.ViewerControl;
import mayday.vis3.ColorProviderSetting;
import mayday.vis3.graph.GraphCanvas;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.components.NodeComponent;
import mayday.vis3.graph.components.SputnikLabel;
import mayday.vis3.graph.layout.CanvasLayouter;
import mayday.vis3.graph.menus.RendererMenu;
import mayday.vis3.graph.model.DefaultGraphModel;
import mayday.vis3.graph.model.GraphModel;
import mayday.vis3.graph.model.MultiProbeSelectionModel;
import mayday.vis3.graph.model.SummaryProbe;
import mayday.vis3.graph.renderer.RendererListener;
import mayday.vis3.graph.vis3.SuperColorProvider;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;


@SuppressWarnings("serial")
public class SBGNViewer extends GraphCanvas implements ViewModelListener, SettingChangeListener
{
	private ViewModel viewModel;
	private SuperColorProvider coloring;
	private ViewerSettings settings; 
	
	private BooleanSetting showControls=new BooleanSetting("Show Controls",null,false);
	private ViewerControl control;
		
//	private TimedEllipse markEllipse;
		
	public SBGNViewer()
	{
		super(new DefaultGraphModel(new Graph()));		
	}	
	
	public SBGNViewer(GraphModel model)
	{
		super(model);		
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void setup(PlotContainer plotContainer) 
	{
		super.setup(plotContainer);		
		// initialize view model and coloring		
		viewModel=plotContainer.getViewModel();
		viewModel.addViewModelListener(this);		
		
		// create color provider 
		coloring=new  SuperColorProvider(viewModel);
		coloring.setMode(ColorProviderSetting.COLOR_BY_EXPERIMENT_VALUE);
		coloring.setExperiment(0);
		coloring.addChangeListener(new ChangeListener(){
			public void stateChanged(ChangeEvent e) 
			{
				updatePlot();				
			}});

		
		// initialize settings
		
		settings=new ViewerSettings(viewModel.getDataSet(),coloring);
		
		settings.getRendererSetting().addChangeListener(new RendererListener(this));
		// open the dialog
		SwingUtilities.invokeLater(new Runnable(){
			public void run() 
			{
				queryAndProcessInput();		
			}});
	
		for(Setting s: settings.getRoot().getChildren())
		{
			plotContainer.addViewSetting(s, this);
		}
		
		plotContainer.addViewSetting(coloring.getSetting(), this);
		plotContainer.addViewSetting(showControls, this);
		settings.addSettingChangeListener(this);
		showControls.addChangeListener(this);
		plotContainer.setPreferredTitle("Pathway Viewer", this);	

		//Florian sagt ich darf das. 
		JMenu menu=plotContainer.getMenu(PlotContainer.ENHANCE_MENU, this);
		menu.add(new PathwaySelectionAction());
		
		requestFocus();
		
		plotContainer.setPreferredTitle("SBGN Pathway Viewer", this);
	}	
	

	
	@Override
	public void paintPlot(Graphics2D g) 
	{
		if(model==null)
			return;		
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		super.paintPlot(g);				
	}
	
//	@Override
//	public void center(Rectangle r, boolean withInsets) 
//	{
//		super.center(r, withInsets);
//		markEllipse=new TimedEllipse(r);
//		updatePlotNow();
//		
//	}
	
	// HANDLING OF MAPPING
	
	private void queryAndProcessInput()
	{
		SettingsDialog d= settings.getDialog();
		
		d.setModal(true);
		d.setVisible(true);
		
		if(d.canceled())
		{
			setVisible(false);
			return;
		}
		String fileName=settings.getPathwayFile();
		try 
		{
			loadPathway(fileName);
		} catch (Throwable e) 
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error reading pathway file:\n"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		((SBGNModel)getModel()).setAnnotation(viewModel.getProbes(), settings.getMappingSourceSetting());	
		
		if(settings.isDisplaySummaryNodes())
			addSummaryNodes();
		extractLayouter();
		setRenderer(settings.getRenderer());
		setSBGNRendering(settings.getDisplaySBGNSetting().getBooleanValue());
				
		updatePlot();
	}
	
	private void loadPathway(String fileName) throws Exception
	{
		BioPaxParser parser=new BioPaxParser();
		Map<String, MasterObject> res= parser.parse(fileName);

		PathwaySelectionDialog psd=new PathwaySelectionDialog(res);
		psd.setModal(true);
		psd.setVisible(true);
		
		if(psd.isCancelled())
			return;
		
		MasterObject selected=psd.getSelectedPathway();
		AbstractSBGNPathwayGraph g= new SBGNPathwayGraph(selected);
		GraphModel m=new SBGNModel(g);
		MultiProbeSelectionModel mpsm=new MultiProbeSelectionModel(getModel(),viewModel);
		viewModel.addViewModelListener(mpsm);
		setSelectionModel(mpsm);
		setModel(m);
		selectionModel.setModel(model);	
		
		for(CanvasComponent cc:model.getComponents())
		{
			if((cc instanceof MultiProbeComponent))
			{
				for(Probe p: ((MultiProbeComponent) cc).getProbes())
				{
					if(p instanceof SummaryProbe)
					{
						((SummaryProbe) p).updateSummary();
					}
				}
			}
		}
		
//		for(MasterObject o:res.values())
//		{
//			if(o.getObjectType().equals("pathway"))
//			{
//				AbstractSBGNPathwayGraph g= new SBGNPathwayGraph(o);
//				GraphModel m=new SBGNModel(g);
//				
//				MultiProbeSelectionModel mpsm=new MultiProbeSelectionModel(getModel(),viewModel);
//				viewModel.addViewModelListener(mpsm);
//				setSelectionModel(mpsm);
//					
//				
//				setModel(m);
//				
//				selectionModel.setModel(model);	
//			}
//		}
	}
	
	// MODEL, LAYOUTERS, RENDERERS
	
	public void setModel(GraphModel model)
	{			
		if(model==null) return;
		this.model = model;	
		removeAll();
		for(CanvasComponent c:getModel().getComponents())
		{
			add(c);	
			c.setRenderer(getRenderer());
			c.addCanvasComponentListener(selectionModel);
			c.addCanvasComponentListener(this);
			
			// add Rendering Capabilities 
			if(c instanceof MultiProbeComponent)
			{
				((MultiProbeComponent) c).setViewModel(viewModel);
				RendererMenu menu=new RendererMenu(coloring, c);
				c.setRendererMenu(menu);
			}
			
			// Transitions have no names:
			if(c instanceof NodeComponent)
			{
				if(((NodeComponent) c).getNode() instanceof Transition)
					continue;
			}
			
			// any other components do: 
			SputnikLabel lab=new SputnikLabel(c);
			add(lab);
			setComponentZOrder(c, 1);
			setComponentZOrder(lab, 0);

			
		}
		for(Node n:model.getGraph().getNodes())
		{
			if( n instanceof EntityPoolNode)
			{
				int c=0;
				int num=((EntityPoolNode) n).getUnitsOfInformation().size();
				if(n instanceof StatefulEntityPoolNode)
				{
					num+=((StatefulEntityPoolNode) n).getStateDescriptions().size();
				}
				for(UnitOfInformation u:((EntityPoolNode) n).getUnitsOfInformation())
				{					
					UnitOfInformationComponent uc=new UnitOfInformationComponent(u,model.getComponent(n),c,num);
					add(uc);
					setComponentZOrder(uc, 0);
					c++;
				}
				if(n instanceof StatefulEntityPoolNode)
				{
					for(StateDescription sd:((StatefulEntityPoolNode) n).getStateDescriptions())
					{
						StateDescriptionComponent sdc=new StateDescriptionComponent(sd,model.getComponent(n),c,num);
						add(sdc);
						setComponentZOrder(sdc, 0);
						c++;
					}
				}
			}
		}

		layouter.layout(this, new Rectangle(0,0,1000,1000), getModel());
		zoom(1.0);
//		new AlignLayouter(50).layout(this, getOutermostJWindow().getBounds(), getModel());
		
		updateSize();
		edgeShapes=new HashMap<Edge, Path2D>(model.getEdges().size());
		
		for(Edge e: model.getEdges())
		{
			if(e instanceof SBGNEdge)
				arrowSettings.put(e, ((SBGNEdge) e).getArrowSettings());
		}
		
		
		for(CanvasComponent comp:getModel().getComponents())
		{
			if(comp instanceof MultiProbeComponent)
			{
				RendererMenu menu=new RendererMenu(coloring,comp);
				comp.setRendererMenu(menu);
			}
		}
		if(model.componentCount() >0 )
		{
			updateRenderer();
			zoom(1.0d);
		}
	}
	
	private void addSummaryNodes()
	{
		((SBGNModel)getModel()).setSummaryNodes(viewModel.getDataSet().getMasterTable(), settings.getSummarySettings());
	}
	
	private void removeSummaryNodes()
	{
		((SBGNModel)getModel()).removeSummaryNodes(settings.getSummarySettings());
	}
	
	public void setSummaryNodes(boolean b)
	{
		if(b)
			addSummaryNodes();
		else
			removeSummaryNodes();
	}
	
	private void updateRenderer()
	{
		for(int i=0; i!= getComponentCount(); ++i)
		{
			Component comp=getComponent(i);
			if(comp instanceof MultiProbeComponent)
				((MultiProbeComponent)comp).setRenderer(getRenderer());
		}
		coloring.setExperiment(0);
		updatePlot();
	}
	
	private void extractLayouter()
	{
		CanvasLayouter layouter=null;
		if(settings.getLayoutSettings().isLayoutSideComponents())
		{
			layouter=new SBGNLayouterWrapper(settings.getLayoutSettings().getLayouter());			
		}else
		{
			layouter=settings.getLayouter();
		}
		setLayouter(layouter);
			
	}
	
	@Override
	public void updateLayout()
	{
		layouter.layout(this, getBounds(), getModel());
		AlignLayouter.getInstance().layout(this, getBounds(), getModel());
		updateSize();
		revalidateEdges();
	}
		
	
	// HANDLING LISTENERS

	public void viewModelChanged(ViewModelEvent vme) 
	{
		if(vme.getChange()==ViewModelEvent.DATA_MANIPULATION_CHANGED)
		{
			coloring.setExperimentExtremes();
			updatePlot();
		}
	}

	public void removeNotify()
	{
		super.removeNotify();
		viewModel.removeViewModelListener(this);
		viewModel.removeViewModelListener(coloring);
	}	

	public void stateChanged(SettingChangeEvent e) 
	{
		if(e.getSource()==settings.getLayoutSettings())
		{
			extractLayouter();
			return;
		}
//		if(e.getSource()==settings.getRendererSetting())
//		{
//			setRendererSilent(settings.getRenderer(coloring));
//			return;
//		}
		if(e.getSource()==settings.getDisplaySBGNSetting())
		{
			setSBGNRendering(settings.getDisplaySBGNSetting().getBooleanValue());
			return;
		}
		if(e.getSource()==settings.getDisplaySummaryNodes())
		{
			if(settings.getDisplaySummaryNodes().getBooleanValue())
				addSummaryNodes();
			else
				removeSummaryNodes();
			return;
		}
		if(e.getSource()==settings.getDisplaySideNodes())
		{
			displaySideNodes(settings.isDisplaySideNodes());
		}
		if(e.getSource()==showControls)
		{
			if(control==null)
				control=new ViewerControl(this);
			control.setVisible(showControls.getBooleanValue());
		}
			
	}
	
	public void setSBGNRendering(boolean renderSBGN)
	{
		if(model==null) return;
		settings.getDisplaySBGNSetting().setBooleanValue(renderSBGN);
		for(CanvasComponent c:this.model.getComponents())
		{
			if(c instanceof SBGNComponent)
			{
				((SBGNComponent) c).setDisplaySBGN(renderSBGN);
				c.setRenderer(getRenderer());
				c.setSize(getRenderer().getSuggestedSize(((SBGNComponent) c).getNode(), ((SBGNComponent) c).getProbes()));
			}			
		}
		updatePlot();
	}
	
	public  void displaySideNodes(boolean visible)
	{
		Graph reactionGraph=((SBGNModel)getModel()).getReactionGraph();
		for(Node n:model.getGraph().getNodes())
		{
			if(model.getGraph().getDegree(n) < 2)
				model.getComponent(n).setVisible(visible);	
			if(n instanceof Transition)
			{
				Transition t=(Transition)n;
				if( reactionGraph.getInDegree(t)==0)
				{
					for(Edge e: reactionGraph.getInEdges(t))
					{
						if(! (e instanceof Modulation) )
							model.getComponent(e.getSource()).setVisible(visible);	
					}
				}
				if( reactionGraph.getOutDegree(t)==0)
				{
					for(Edge e: reactionGraph.getOutEdges(t))
					{
						if(! (e instanceof Modulation) )
							model.getComponent(e.getTarget()).setVisible(visible);	
					}
				}
			}
		}
		
	}

	public ViewerSettings getSettings()
	{
		return settings;
	}
	
	// 1001 MEMBER CLASSES
	
	private class PathwaySelectionAction extends AbstractAction
	{
		public PathwaySelectionAction() 
		{
			super("Select Pathway...");
		}

		public void actionPerformed(ActionEvent event) 
		{
			try {
				loadPathway(settings.getPathwayFile());
			} catch (Exception e) 
			{
				JOptionPane.showMessageDialog(SBGNViewer.this, "Error reading pathway file:\n"+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
			((SBGNModel)getModel()).setAnnotation(viewModel.getProbes(), settings.getMappingSourceSetting());	
			
			if(settings.isDisplaySummaryNodes())
				addSummaryNodes();
			extractLayouter();
			setRenderer(settings.getRenderer());
			setSBGNRendering(settings.getDisplaySBGNSetting().getBooleanValue());
			updatePlot();
		}
	}

	
	@Override
	public void paintLabels(Graphics2D g)
	{		
		//do nothing;
	}

	
}


