package mayday.graphviewer.core;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.xml.stream.XMLStreamWriter;

import mayday.core.DataSet;
import mayday.core.Preferences;
import mayday.core.ProbeList;
import mayday.core.math.distance.DistanceMeasureManager;
import mayday.core.math.distance.DistanceMeasurePlugin;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.settings.Setting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.generic.PluginInstanceSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.edges.Edges;
import mayday.core.structures.graph.nodes.Nodes;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.action.AddNodeAction;
import mayday.graphviewer.action.AddProbesAction;
import mayday.graphviewer.action.AddReactionTemplate;
import mayday.graphviewer.action.AddSingleNodeAction;
import mayday.graphviewer.action.AddSingleProbeAction;
import mayday.graphviewer.action.DeleteAction;
import mayday.graphviewer.action.EdgeSettingsAction;
import mayday.graphviewer.action.ForkSubgraphAction;
import mayday.graphviewer.action.GroupSelectedAction;
import mayday.graphviewer.action.LoadAction;
import mayday.graphviewer.action.SaveAction;
import mayday.graphviewer.action.ScaleNodesToValues2;
import mayday.graphviewer.action.SelectionInspectorAction;
import mayday.graphviewer.action.ToggleAction;
import mayday.graphviewer.core.auxItems.AuxItemStrategy;
import mayday.graphviewer.core.auxItems.DefaultAuxItemStrategy;
import mayday.graphviewer.core.auxItems.NoneAuxItemStrategy;
import mayday.graphviewer.core.auxItems.ProbesAuxItemStrategy;
import mayday.graphviewer.core.auxItems.SBGNAuxItemStrategy;
import mayday.graphviewer.core.bag.BagComponent;
import mayday.graphviewer.core.ccp.GraphTransferHandler;
import mayday.graphviewer.core.edges.EdgeCustomAction;
import mayday.graphviewer.core.edges.EdgeDispatcher;
import mayday.graphviewer.core.edges.EdgeResetAction;
import mayday.graphviewer.core.edges.EdgeTransformationSetting;
import mayday.graphviewer.core.edges.EdgeWeightAction;
import mayday.graphviewer.core.edges.EdgeWeightTransformation;
import mayday.graphviewer.datasources.maygraph.ViewerSettingsFileParser;
import mayday.graphviewer.graphprovider.GraphProvider;
import mayday.graphviewer.graphprovider.GraphProviderApplicator;
import mayday.graphviewer.graphprovider.ProbeGraphProvider;
import mayday.graphviewer.gui.SelectionInspector;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.graphviewer.plugins.RunGraphViewerPluginAction;
import mayday.graphviewer.plugins.shrink.JoinProbes;
import mayday.graphviewer.plugins.shrink.MergeNodes;
import mayday.graphviewer.plugins.shrink.MergeWithNeighbors;
import mayday.pathway.sbgn.processdiagram.ProcessDiagram;
import mayday.vis3.DelayedPlotUpdater;
import mayday.vis3.graph.GraphCanvas;
import mayday.vis3.graph.actions.EdgeRoleAction;
import mayday.vis3.graph.arrows.Arrow;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.DataSetLabelRenderer;
import mayday.vis3.graph.components.LabelRenderer;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.components.NodeComponent;
import mayday.vis3.graph.components.LabelRenderer.Orientation;
import mayday.vis3.graph.components.NodeComponent.NodeUpdate;
import mayday.vis3.graph.edges.EdgeSetting;
import mayday.vis3.graph.edges.router.EdgePoints;
import mayday.vis3.graph.edges.strokes.HighlightStroke;
import mayday.vis3.graph.edges.strokes.TextStroke;
import mayday.vis3.graph.layout.CanvasLayouter;
import mayday.vis3.graph.layout.CanvasLayouterPlugin;
import mayday.vis3.graph.layout.GridLayouter;
import mayday.vis3.graph.menus.RendererMenu;
import mayday.vis3.graph.model.GraphModel;
import mayday.vis3.graph.model.GraphModelListener;
import mayday.vis3.graph.renderer.dispatcher.AssignedRendererSetting;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ManipulationMethod;

@SuppressWarnings("serial")
public class GraphViewerPlot extends GraphCanvas implements GraphModelListener, HubListener
{
	/********************************************
	 * Settings
	 ********************************************/
	private PluginInstanceSetting<AbstractPlugin> graphProviderSetting=new PluginInstanceSetting<AbstractPlugin>(
			"Graph Provider", 
			"A graph provider creates graphs from Mayday data structures.\n" +
			" There are several ways of doing this:\n" +
			"- Use the probes or probe lists without structure" +
			"- The probe lists with their hierarchical order" , GraphProvider.MC);

	private PluginInstanceSetting<AbstractPlugin> layoutMethodSetting=new PluginInstanceSetting<AbstractPlugin>(
			"Layout Method",
			null , 
			CanvasLayouterPlugin.MC);
	private HierarchicalSetting layoutSetting=new HierarchicalSetting("Layout"); 
	private RestrictedStringSetting layoutSizeSetting=new RestrictedStringSetting("Layout Size",  null, 0, new String[]{"Window Size", "Current Size", "Custom Size"});
	private IntSetting layoutBoundsWidth=new IntSetting("Width", null, 1024, 0, 4000,false, true );
	private IntSetting layoutBoundsHeight=new IntSetting("Height", null, 768, 0, 4000,false, true );

	private BooleanHierarchicalSetting drawLables=new BooleanHierarchicalSetting("Node Labels", null, true);
	private IntSetting labelSize=new IntSetting("Font Size",null,12);
	private BooleanSetting labelColorByDataSet= new BooleanSetting("Color Label by DataSet", null, true);
	private RestrictedStringSetting labelOrientationSetting=new RestrictedStringSetting("Label Placement", null, 4, 
			new String[]{"Above","Upper","Center","Lower","Below"}); 
	private BooleanSetting scaleLabelSetting=new BooleanSetting("Scale Labels", null, scaleLabels);

	private BooleanSetting hideTransientEdges=new BooleanSetting("Hide Edges from offscreen nodes", null, true);
	private EdgeSetting edgeSetting=new EdgeSetting("Default Edge");

	private BooleanHierarchicalSetting drawTitle=new BooleanHierarchicalSetting("Title", null, true);
	private StringSetting title=new StringSetting("Graph Title",null,"");

	private BooleanSetting sizeSetting=new BooleanSetting("Scale Nodes to value",null,false);

	private ObjectSelectionSetting<AuxItemStrategy> auxItemStrategy=new ObjectSelectionSetting<AuxItemStrategy>(
			"Auxiliary Items","Specifies the way auxiliary items are drawn",0,
			new AuxItemStrategy[]{new DefaultAuxItemStrategy(),new NoneAuxItemStrategy(), new ProbesAuxItemStrategy(), new SBGNAuxItemStrategy() });

	private HierarchicalSetting edgeMasterSetting=new HierarchicalSetting("Edges");
	private BooleanSetting drawEdgesSetting=new BooleanSetting("Draw Edges", "enables or disables drawing of edges", true);
	private BooleanSetting drawEdgesLabelsSetting=new BooleanSetting("Draw Edge Lables", "globally enables or disables drawing of edge labels", true);
	private BooleanSetting drawEdgeWeight=new BooleanSetting("Draw Edge Weight", "draw the weight of the edge on the edge", true);
	private BooleanHierarchicalSetting hideSmallEdges=new BooleanHierarchicalSetting("Filter Edges", "filter edges with low weight (after Transformation)", true);
	private DoubleSetting hideEdgeCutoff=new DoubleSetting("Cutoff", "Cutoff for hiding edges (after transformation)", 0.5);
	private EdgeTransformationSetting edgeTransformationSetting=new EdgeTransformationSetting();
	private boolean lowQ=false; // grayBoxRendering flag: if true, only render necessary components. 
	private BooleanSetting highQualityRendering=new BooleanSetting("High Quality Rendering", "Enable antialiasing and other high quality rendering\n" +
			"techniques. ", false);

	/********************************************
	 * Components
	 ********************************************/

	private ModelHub modelHub;
	private HubRevolver rendererDispatcher;
	private EdgeDispatcher dispatcher=new EdgeDispatcher(edgeSetting);
	private CanvasComponent connectingNode;
	private CanvasComponent targetNode;
	private Point connectingPoint; 	
	private SelectionInspector selectionInspector;
	private GraphProvider initGraphProvider;
	private boolean modelAsIs=false;
	private GraphModel givenModel=null;
	private static boolean showedSplash=false;

	/********************************************
	 * Initialization
	 ********************************************/	

	public GraphViewerPlot(GraphProvider gp)
	{
		this();	
		initGraphProvider=gp;		
	}
	public GraphViewerPlot()
	{
		super(null);		
		setLayouter(new GridLayouter());
		setFocusable(true);
		setOpaque(true);
		setBackground(Color.white);
		initGraphProvider=new ProbeGraphProvider();
		setTransferHandler(new GraphTransferHandler(this));
		setAutoscrolls(true);
	}

	public GraphViewerPlot(GraphModel newModel, boolean b) 
	{
		this();
		givenModel=newModel;
		modelAsIs=b;
	}
	public void setup(PlotContainer plotContainer) 
	{
		super.setup(plotContainer);

		if(modelHub==null)
		{
			create(plotContainer);	
		}

		if(!showedSplash)
		{
			SwingUtilities.invokeLater(new Runnable(){

				@Override
				public void run() 
				{
					ImageIcon icon=PluginInfo.getIcon("mayday/pathway/gvicons/logo.png");
					message(icon);
				}
			});
			showedSplash=true;
		}

		HierarchicalSetting customBounds=new HierarchicalSetting("Custom");
		customBounds.addSetting(layoutBoundsWidth).addSetting(layoutBoundsHeight);
		layoutSetting.addSetting(layoutMethodSetting).addSetting(layoutSizeSetting).addSetting(customBounds);
		// add settings
		plotContainer.addViewSetting(modelHub.getMasterSetting(), this);
		plotContainer.addViewSetting(modelHub.getDataSetSetting(), this);
		plotContainer.addViewSetting(graphProviderSetting, this);
		plotContainer.addViewSetting(layoutSetting, this);

		HierarchicalSetting renderingSetting= new HierarchicalSetting("Node Rendering");
		renderingSetting.addSetting(rendererDispatcher.getRoleRenderersSetting()).
		addSetting(rendererDispatcher.getOverallDecorators()).
		addSetting(rendererDispatcher.getDefaultRenderer()).
		addSetting(rendererDispatcher.getRenderSmallSettings()).
		addSetting(highQualityRendering);


		plotContainer.addViewSetting(renderingSetting, this);
		plotContainer.addViewSetting(edgeMasterSetting, this);

		plotContainer.addViewSetting(drawLables, this);
		plotContainer.addViewSetting(drawTitle, this);
		plotContainer.addViewSetting(sizeSetting, this);
		plotContainer.addViewSetting(auxItemStrategy, this);

		DelayedPlotUpdater dpu=new DelayedPlotUpdater(this);
		edgeMasterSetting.addChangeListener(dpu);
		renderingSetting.addChangeListener(dpu);
		sizeSetting.addChangeListener(dpu);
		auxItemStrategy.addChangeListener(dpu);



		Set<PluginInfo> graphMenuPLIs= PluginManager.getInstance().getPluginsFor(GraphViewerPlugin.MC_GRAPH_GRAPH);
		Vector<RunGraphViewerPluginAction> graphRPIs = new Vector<RunGraphViewerPluginAction>();
		for (PluginInfo pli : graphMenuPLIs)
			graphRPIs.add(new RunGraphViewerPluginAction(pli,this));
		JMenu graphMenu=new JMenu("Graph");
		RunGraphViewerPluginAction.addToMenu(graphMenu, graphRPIs);
		plotContainer.addMenu(graphMenu);



		Set<PluginInfo> plis = PluginManager.getInstance().getPluginsFor(GraphViewerPlugin.MC_GRAPH);
		Vector<RunGraphViewerPluginAction> rpis = new Vector<RunGraphViewerPluginAction>();
		for (PluginInfo pli : plis)
			rpis.add(new RunGraphViewerPluginAction(pli,this));
		JMenu pluginMenu=new JMenu("Nodes");
		RunGraphViewerPluginAction.addToMenu(pluginMenu, rpis);
		plotContainer.addMenu(pluginMenu);

		Set<PluginInfo> crossMenuPLIs= PluginManager.getInstance().getPluginsFor(GraphViewerPlugin.MC_GRAPH_CROSS_DATASET);
		Vector<RunGraphViewerPluginAction> crossRPIs = new Vector<RunGraphViewerPluginAction>();
		for (PluginInfo pli : crossMenuPLIs)
			crossRPIs.add(new RunGraphViewerPluginAction(pli,this));
		JMenu crossMenu=new JMenu("Cross Dataset");
		RunGraphViewerPluginAction.addToMenu(crossMenu, crossRPIs);
		plotContainer.addMenu(crossMenu);
	}

	private void create(PlotContainer plotContainer)
	{
		plotContainer.setPreferredTitle("Graph Viewer", this);
		//init model hub
		modelHub=new ModelHub(plotContainer.getViewModel());
		modelHub.addHubListener(this);

		// prepare layouter and setting		
		setLayouter(new GridLayouter());
		layoutMethodSetting.setInstance(new GridLayouter());
		layoutMethodSetting.addChangeListener(new LayouterSettingListener());

		//prepare graph providers		
		graphProviderSetting.setInstance(new ProbeGraphProvider());
		graphProviderSetting.addChangeListener(new GraphProviderSettingListener());		

		//prepare renderer dispatcher
		setupRendererDispatcher(plotContainer);

		//prepare label orientation		
		labelOrientationSetting.addChangeListener(new SettingChangeListener() 
		{
			@Override
			public void stateChanged(SettingChangeEvent e) 
			{
				switch (labelOrientationSetting.getSelectedIndex()) {
				case 0: defaultLabelOrientation=Orientation.ABOVE;break;
				case 1: defaultLabelOrientation=Orientation.UPPER;break;
				case 2: defaultLabelOrientation=Orientation.CENTER;break;
				case 3: defaultLabelOrientation=Orientation.LOWER;break;
				case 4: defaultLabelOrientation=Orientation.BELOW;break;
				}
				updatePlot();
			}
		});

		//setup title 
		StringBuffer sb=new StringBuffer();
		for(ProbeList pl:modelHub.getViewModel().getProbeLists(false))
		{
			sb.append(pl.getName()).append(" ");
		}
		title.setStringValue(sb.toString());
		drawTitle.addSetting(title);

		// listener for guarding edge and label stuff;
		EdgeAndLabelListener eal=new EdgeAndLabelListener();

		hideSmallEdges.addSetting(hideEdgeCutoff);

		// setup edge stuff
		edgeMasterSetting.addSetting(drawEdgesSetting);
		edgeMasterSetting.addSetting(drawEdgesLabelsSetting);
		edgeMasterSetting.addSetting(edgeTransformationSetting);
		edgeMasterSetting.addSetting(drawEdgeWeight);
		edgeMasterSetting.addSetting(edgeSetting);		
		edgeMasterSetting.addSetting(hideSmallEdges);		
		edgeMasterSetting.addSetting(hideTransientEdges);
		edgeMasterSetting.addChangeListener(eal);

		// setup label drawing
		drawLables.addSetting(labelSize).addSetting(labelOrientationSetting).addSetting(labelColorByDataSet).addSetting(scaleLabelSetting);
		drawLables.addChangeListener(eal);

		setupKeyBindings();

		// node size setting
		ScaleNodesToValues2 sntv=new ScaleNodesToValues2(sizeSetting, this);
		sizeSetting.addChangeListener(sntv);
		modelHub.setNodeSizing(sntv);

		//create the graph
		if(modelAsIs)
			setModel(givenModel);
		else
			setupContents(initGraphProvider);
	}

	private void setupKeyBindings()
	{
		requestFocusInWindow();
		getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "nextRenderer");
		getActionMap().put("nextRenderer",new ToggleAction(this, rendererDispatcher));

		getInputMap().put(KeyStroke.getKeyStroke("F4"), "toggleTitle");
		getActionMap().put("toggleTitle",new TitleAction());

		getInputMap().put(KeyStroke.getKeyStroke("F10"), "showSelections");
		getActionMap().put("showSelections",new SelectionInspectorAction(this));

		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_G,KeyEvent.CTRL_MASK), "groupSelected");
		getActionMap().put("groupSelected",new GroupSelectedAction(this));

		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_R,KeyEvent.CTRL_MASK), "newNode");
		getActionMap().put("newNode",new AddNodeAction(this));

		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_N,KeyEvent.CTRL_MASK), "newWindow");
		getActionMap().put("newWindow",new ForkSubgraphAction(this));

		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_N,KeyEvent.CTRL_MASK+KeyEvent.SHIFT_MASK), "newProbes");
		getActionMap().put("newProbes",new AddProbesAction(this));

		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_S,KeyEvent.CTRL_MASK), "saveGraph");
		getActionMap().put("saveGraph",new SaveAction(this));

		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_L,KeyEvent.CTRL_MASK), "loadGraph");
		getActionMap().put("loadGraph",new LoadAction(this));	

		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,KeyEvent.CTRL_MASK), "deleteNode");
		getActionMap().put("deleteNode",new DeleteAction(this));	

		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_M,KeyEvent.CTRL_MASK), "mergeNodes");
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_M,KeyEvent.CTRL_MASK+KeyEvent.SHIFT_MASK), "joinProbes");
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_U,KeyEvent.CTRL_MASK), "mergeWithNeighbors");

		getActionMap().put("mergeNodes",new MergeNodes.MergeNodesAction(this));	
		getActionMap().put("joinProbes",new JoinProbes.JoinAction(this));	
		getActionMap().put("mergeWithNeighbors",new MergeWithNeighbors.MergeAction(this));

		getActionMap().put(TransferHandler.getCutAction().getValue(Action.NAME),
				TransferHandler.getCutAction());
		getActionMap().put(TransferHandler.getCopyAction().getValue(Action.NAME),
				TransferHandler.getCopyAction());
		getActionMap().put(TransferHandler.getPasteAction().getValue(Action.NAME),
				TransferHandler.getPasteAction());

		InputMap imap = this.getInputMap();
		imap.put(KeyStroke.getKeyStroke("ctrl X"),
				TransferHandler.getCutAction().getValue(Action.NAME));
		imap.put(KeyStroke.getKeyStroke("ctrl C"),
				TransferHandler.getCopyAction().getValue(Action.NAME));
		imap.put(KeyStroke.getKeyStroke("ctrl V"),
				TransferHandler.getPasteAction().getValue(Action.NAME));

		setupKeyBindingsNodes();		
	}

	private void setupKeyBindingsNodes()
	{
		getInputMap().put(KeyStroke.getKeyStroke('n'), "newNode2");
		getActionMap().put("newNode2",new AddSingleNodeAction(this, Nodes.Roles.NODE_ROLE));

		getInputMap().put(KeyStroke.getKeyStroke('t'), "newNodet");
		getActionMap().put("newNodet",new AddSingleNodeAction(this, Nodes.Roles.NOTE_ROLE));

		getInputMap().put(KeyStroke.getKeyStroke('p'), "newNodep");
		getActionMap().put("newNodep",new AddSingleProbeAction(this));

		getInputMap().put(KeyStroke.getKeyStroke('M'), "newNodeM");
		getInputMap().put(KeyStroke.getKeyStroke('S'), "newNodeS");
		getInputMap().put(KeyStroke.getKeyStroke('N'), "newNodeN");

		getInputMap().put(KeyStroke.getKeyStroke('A'), "newNodeA");
		getInputMap().put(KeyStroke.getKeyStroke('P'), "newNodeP");
		getInputMap().put(KeyStroke.getKeyStroke('O'), "newNodeO");
		getInputMap().put(KeyStroke.getKeyStroke('D'), "newNodeD");

		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_P,KeyEvent.CTRL_MASK+KeyEvent.SHIFT_MASK), "addReactionTemplate");
		
		getActionMap().put("newNodeM",new AddSingleNodeAction(this, SBGNRoles.MACROMOLECULE_ROLE));
		getActionMap().put("newNodeS",new AddSingleNodeAction(this, SBGNRoles.SIMPLE_CHEMICAL_ROLE));
		getActionMap().put("newNodeN",new AddSingleNodeAction(this, SBGNRoles.NUCLEIC_ACID_FEATURE_ROLE));

		getActionMap().put("newNodeA",new AddSingleNodeAction(this, SBGNRoles.ASSOCIATION_ROLE));		
		getActionMap().put("newNodeP",new AddSingleNodeAction(this, SBGNRoles.PROCESS_ROLE));
		getActionMap().put("newNodeO",new AddSingleNodeAction(this, SBGNRoles.OMITTED_PROCESS_ROLE));
		getActionMap().put("newNodeD",new AddSingleNodeAction(this, SBGNRoles.DISSOCIATION_ROLE));
		
		getActionMap().put("addReactionTemplate",new AddReactionTemplate(this));
	}

	private void setupRendererDispatcher(PlotContainer plotContainer)
	{
		rendererDispatcher=new HubRevolver(modelHub);
		for(String s: ProcessDiagram.ROLES)
		{
			AssignedRendererSetting sbgnRenderer=new AssignedRendererSetting(s,modelHub.getViewModel().getDataSet(), modelHub.getColorProvider());
			sbgnRenderer.setPrimaryRenderer("PAS.GraphViewer.Renderer.SBGN");	
			rendererDispatcher.addRoleRenderer(s, sbgnRenderer);
		}		
	}

	private void setupContents(GraphProvider graphProvider)
	{
		if(graphProvider==null)
			return; // do nothing
		MultiHashMap<DataSet, ProbeList> probeLists=null;
		probeLists=new MultiHashMap<DataSet, ProbeList>();
		for(ProbeList pl: modelHub.getViewModel().getProbeLists(false))
		{
			probeLists.put(pl.getDataSet(), pl);
		}
		SuperModel model=new SuperModel(graphProvider.createGraph(probeLists));
		setLayouter(graphProvider.defaultLayouter());
		setModel(model);
	}

	@Override
	protected void setCustomMenu() 
	{
		contextMenu.add(new AddNodeAction(this));
		if(selectionModel.numberOfSelectedComponents()==2)
		{
			CanvasComponent c1=selectionModel.getSelectedComponents().get(0);
			CanvasComponent c2=selectionModel.getSelectedComponents().get(1);
			contextMenu.add(new ConnectNodesAction(c1,c2));
			contextMenu.add(new ConnectNodesAction(c2,c1));
			JMenu cmMenu=new JMenu("Connect and measure");
			cmMenu.add(new ConnectAndMeasureNodesAction(c1, c2, DistanceMeasureManager.get("Euclidean"),"Euclidean"));
			cmMenu.add(new ConnectAndMeasureNodesAction(c1, c2, DistanceMeasureManager.get("Pearson Correlation"),"Pearson"));
			contextMenu.add(cmMenu);
		}
	}

	protected void setEdgeMenu()
	{
		super.setEdgeMenu();
		edgeMenu.addSeparator();
		edgeMenu.add(new EdgeSettingsAction(this));
		edgeMenu.add(new EdgeWeightAction(this));
		JMenu conversionMenu=new JMenu("Convert");
		for(String e: Edges.Roles.ROLES)
			conversionMenu.add(new EdgeRoleAction(this, e));
		conversionMenu.addSeparator();
		for(String e: ProcessDiagram.EDGE_ROLES)
			conversionMenu.add(new EdgeRoleAction(this, e));
		edgeMenu.add(conversionMenu);
		edgeMenu.addSeparator();
		edgeMenu.add(new EdgeCustomAction(this));
		edgeMenu.add(new EdgeResetAction(this));
	}

	public void removeNotify()
	{
		super.removeNotify();
		modelHub.removeNotify();

		if(selectionInspector!=null)
			selectionInspector.removeNotify();

		modelHub.getViewModel().removeViewModelListener((HubSelectionModel)getSelectionModel());
	}

	/**********************************************
	 * Listeners
	 **********************************************/

	public void graphModelChanged(mayday.vis3.graph.model.GraphModelEvent event) 
	{
		switch (event.getChange()) 
		{
		case ComponentsAdded:
			for(CanvasComponent comp:event.getAffectedComponents())
			{
				addComponent(comp);			
			}
			updateSize();			
			break;
		case ComponentsRemoved:
			for(CanvasComponent comp:event.getAffectedComponents())
			{
				remove(comp);				
			}
			updateSize();			
			break;
		case AllComponentsChanged:
			setModel(getModel()); // reset model;
			break;	

		default:
			break;
		}
	}

	@Override
	public void hubStateChanged(HubEvent event) 
	{
		if(event.getChange()==HubEvent.UPDATE)
			updatePlot();

	}

	/**********************************************
	 * Adding components
	 **********************************************/

	public void addComponent(CanvasComponent comp)
	{
		super.add(comp);
		processComponent(comp);
	}

	public void setModel(GraphModel model)
	{	
		if(model==null) return;
		if(this.model!=null)
		{
			this.model.removeGraphModelListener(this);
		}
		this.model = model;	
		this.model.addGraphModelListener(this);

		removeAll();
		for(CanvasComponent c:getModel().getComponents())
		{
			addComponent(c);
		}

		layouter.layout(this, new Rectangle(0,0,1000,1000), getModel());
		updateSize();
		edgeShapes=new HashMap<Edge, Path2D>(model.getEdges().size());
		updateRenderer();
		zoom(1.0d);
		if(modelHub!=null)
		{
			if(getSelectionModel()!=null && getSelectionModel() instanceof HubListener)
				modelHub.removeHubListener((HubListener) getSelectionModel());
			HubSelectionModel psm=new HubSelectionModel(getModel(),modelHub);
			modelHub.addHubListener(psm);
			setSelectionModel(psm);
			//			selectionModel.setModel(model);		
		}
		if(model.getGraph().getName()!=null)
			title.setStringValue(model.getGraph().getName());
		updateSize();
		updatePlot();
	}

	public void processComponent(CanvasComponent c)
	{
		c.setRenderer(getRenderer());
		c.addCanvasComponentListener(selectionModel);
		c.addCanvasComponentListener(this);

		if(c instanceof DefaultNodeComponent)
		{
			((DefaultNodeComponent) c).setRendererDispatcher(rendererDispatcher);
			c.setSize(rendererDispatcher.getRenderer(c, ((DefaultNodeComponent) c).getNode()).getSuggestedSize(((DefaultNodeComponent) c).getNode(), c.getPayload()));
		}
		if(c instanceof MultiProbeComponent && !(c instanceof DefaultNodeComponent))
		{
			RendererMenu menu=new RendererMenu(modelHub.getColorProvider(),c);
			c.setRendererMenu(menu);
		}

		if(c instanceof BagComponent)
		{
			int i=0;
			for(CanvasComponent comp:((BagComponent)c).getBag())
			{
				if(i < getComponentZOrder(comp))
					i=getComponentZOrder(comp);
			}
			setComponentZOrder(c, i+1);
		}
	}

	private void updateRenderer()
	{
		for(int i=0; i!= getComponentCount(); ++i)
		{
			Component comp=getComponent(i);
			if(comp instanceof MultiProbeComponent)
				((MultiProbeComponent)comp).setRenderer(getRenderer());
		}
		if(modelHub!=null)
			modelHub.resetColoring();			
		updatePlot();
	}

	/**********************************************
	 * Painting 
	 **********************************************/

	@Override
	public void paintPlot(Graphics2D g) 
	{
		long t=System.currentTimeMillis();
		if(!dragRect && movingComponents.isEmpty())
		{
			if(highQualityRendering.getBooleanValue())
			{
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			}
			lowQ=false;
		}else
		{			
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_SPEED);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			lowQ=true;
		}

		if(model.componentCount() < 500 && model.getGraph().edgeCount() < 100)
			lowQ=false;
		// paint the background
		g.setColor(lowQ?Color.LIGHT_GRAY:Color.white);
		g.fillRect(g.getClipBounds().x,g.getClipBounds().y, g.getClipBounds().width,g.getClipBounds().height);

		if(!lowQ && title.getStringValue()!=null && drawTitle.getBooleanValue() && (!title.getStringValue().isEmpty()) )
		{
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(0, 0, getWidth(), 25);
			g.setColor(Color.BLACK);
			Font f=new Font("Arial",Font.BOLD,20);
			Font f2=g.getFont();
			g.setFont(f);
			g.drawString(title.getStringValue(), 25, 20);
			g.setFont(f2);
			g.drawLine(0,25,getWidth(),25);
		}
//		System.out.println("preps: "+(System.currentTimeMillis()-t));

		long te=System.currentTimeMillis();
		g.setColor(Color.black);

		if(!movingComponents.isEmpty()){
			Set<Edge> movEdge=new HashSet<Edge>();
			for(CanvasComponent cc:movingComponents)
				for(Edge e:model.getGraph().getAllEdges(model.getNode(cc)))
				{
					movEdge.add(e);
				}
			paintEdges(g, movEdge);
		} else{
			paintEdges(g, model.getEdges());
//			if(edgeShapes.size()==model.getGraph().edgeCount() && img!=null)
//			{
//				System.out.println("hit");
//				//			g.drawImage(img, null, 0, 0);
//				g.drawImage(img, 
//						g.getClipBounds().x, g.getClipBounds().y, 
//						g.getClipBounds().x+g.getClipBounds().width, g.getClipBounds().y+g.getClipBounds().height,  
//						g.getClipBounds().x, g.getClipBounds().y, 
//						g.getClipBounds().x+g.getClipBounds().width, g.getClipBounds().y+g.getClipBounds().height,
//						null);
//			}else{
//				System.out.println("miss");
//				paintEdges(g, model.getEdges());
//				img=new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_USHORT_555_RGB);
//				Graphics2D imgG=(Graphics2D)img.getGraphics();
//				imgG.setBackground(getBackground());
//				imgG.clearRect(0, 0, getWidth(), getHeight());
//				paintEdges(imgG, model.getEdges());
//			}
		}

		g.setStroke(new BasicStroke());
		te=System.currentTimeMillis()-te;

		long tc=System.currentTimeMillis();
		paintChildren(g);
		tc=System.currentTimeMillis()-tc;



		long tl=System.currentTimeMillis();
		if(drawLables.getBooleanValue())
		{
			LabelRenderer r=labelColorByDataSet.getBooleanValue()?new DataSetLabelRenderer():new LabelRenderer();
			paintLabels(g,r);
		}
		tl=System.currentTimeMillis()-tl;

		long ta=System.currentTimeMillis();
		if(!lowQ)
		{
			paintAuxiliaries(g);
		}
		ta=System.currentTimeMillis()-ta;
		g.setColor(Color.black);
		if(dragRect)
		{   	
			g.setStroke(new BasicStroke(2));
			g.drawRect(selectionRect.x, selectionRect.y, selectionRect.width, selectionRect.height);
		}

		if(markEllipse!=null)
		{

			if(!markEllipse.stillAlive())
			{
				markEllipse=null;
			}else
			{
				g.setStroke(new BasicStroke(2));
				g.setColor(Color.red);
				g.draw(markEllipse);
			}
		}
		if(connectingNode!=null)
		{
			if(connectingNode instanceof BagComponent)
			{
				g.setStroke(new BasicStroke(5));
				g.setColor(Color.blue);

				Iterator<CanvasComponent> iter=movingComponents.iterator();
				Rectangle r=new Rectangle();
				if(iter.hasNext())
					r=new Rectangle(iter.next().getBounds());
				while(iter.hasNext())
				{
					r.add(iter.next().getBounds());
				}
				g.draw(r);
				g.setStroke(new TextStroke("Add to "+connectingNode.getLabel(), getFont(),TextStroke.BEGIN,5));
				g.draw(r);
			}else
			{
				g.setStroke(new BasicStroke(2));
				g.drawLine((int)connectingNode.getBounds().getCenterX(), (int)connectingNode.getBounds().getCenterY(), connectingPoint.x, connectingPoint.y);
				if(targetNode!=null)
				{
					g.setStroke(new TextStroke("Connect "+connectingNode.getLabel()+" to "+targetNode.getLabel(), getFont(),TextStroke.BEGIN,5));
				}else
				{
					g.setStroke(new TextStroke("Connect "+connectingNode.getLabel()+" to...", getFont(),TextStroke.BEGIN,5));
				}
				g.drawLine((int)connectingNode.getBounds().getCenterX(), (int)connectingNode.getBounds().getCenterY(), connectingPoint.x, connectingPoint.y);
			}
		}
		t= System.currentTimeMillis()-t;
		System.out.println("fps="+(1000.0/t)+"    "+t );
//		System.out.println("n="+model.getGraph().nodeCount()+"\te="+model.getGraph().edgeCount());
//		System.out.println("size: "+g.getClipBounds());
//		System.out.println("Stats:\n"+"Edges: "+tl+"\nNodes: "+tc+"\nLabels:"+tl+"\nAux: "+ta);
//		System.out.println("Edge shapes: "+edgeShapes.size()+"\n++++++++++++++++++++++++");
	}

	@Override
	public void paintEdges(Graphics2D g, Collection<? extends Edge> edges) 
	{	
		if(!drawEdgesSetting.getBooleanValue())
			return;


		boolean edgeN=drawEdgesLabelsSetting.getBooleanValue();
		boolean edgeW=drawEdgeWeight.getBooleanValue();

		double cutoff=Double.NEGATIVE_INFINITY;
		if(hideSmallEdges.getBooleanValue())
			cutoff=hideEdgeCutoff.getDoubleValue();


		boolean hideTransient=hideTransientEdges.getBooleanValue();
		EdgeSetting edgeSetting=null;

		EdgeWeightTransformation transformation=edgeTransformationSetting.getTransformation();
		double w=0;

		int ec=0;
		int miss=0;
		for(Edge e: edges)	
		{
			//is edge visible?
			Rectangle r=new Rectangle();
			r.add(model.getComponent(e.getSource()).getLocation());
			r.add(model.getComponent(e.getTarget()).getLocation());
			if(!g.hitClip(r.x, r.y, r.width, r.height))
				continue;
			if(lowQ &&
					!(movingComponents.contains(model.getComponent(e.getSource())) || 
							movingComponents.contains(model.getComponent(e.getTarget()))) )
			{
				continue;
			}

			// calculate transformed weight;
			w=transformation.transformEdgeWeight(e.getWeight());
			if(w < cutoff){
				continue;
			}
			if(w < 0) 
				w=0;
			// do we need to paint the edge?
			if(hideTransient &&  
					(! getVisibleRect().contains((model.getComponent(e.getSource()).getLocation())) &&  
							! getVisibleRect().contains((model.getComponent(e.getTarget()).getLocation())) ))
				continue;

			edgeSetting=dispatcher.getSetting(e);

			// is any node invisible? 			
			if(!(model.getComponent(e.getSource()).isVisible()) ||!(model.getComponent(e.getTarget()).isVisible()) )
				continue;


			// 	
			Path2D es=edgeShapes.get(e);
			if(es==null)
			{
				es=edgeSetting.getRouter().routeEdge(e,this, model);
				edgeShapes.put(e,es);		
			}


			EdgePoints points=edgePoints.get(e);
			if(points==null){
				points=edgeSetting.getRouter().getAdjustedPoints(model.getComponent(e.getSource()), model.getComponent(e.getTarget()));
				edgePoints.put(e, points);
			}



			Point2D point=edgeSupportPoints.get(e);
			if(point==null){
				++miss;
				point=edgeSetting.getRouter().getSupportPoint(model.getComponent(e.getSource()), model.getComponent(e.getTarget()));
				// extract second last point in here: 
				PathIterator pi=es.getPathIterator(null);
				while(!pi.isDone())	{
					double[] cur=new double[6];
					int t=pi.currentSegment(cur);
					if(t!=PathIterator.SEG_CLOSE && t!=PathIterator.SEG_MOVETO)	{						
						if(t==PathIterator.SEG_CUBICTO)
							point.setLocation(cur[2], cur[3]);
						if(t==PathIterator.SEG_QUADTO)
							point.setLocation(cur[0], cur[1]);
					}
					pi.next();
				}				
				edgeSupportPoints.put(e, point);
			}

			Stroke backupStroke=g.getStroke();

			Stroke stroke= null;
			if(edgeSetting.isUseWeight())
			{
				float s=(float)(w==0?1:w);
				stroke=edgeSetting.getWidthStroke(s);//new BasicStroke(s);				
			}else
			{
				stroke=edgeSetting.getStroke();				
			}
			g.setStroke(stroke);
			if(e==highlightEdge)
			{
				g.setStroke(new HighlightStroke(g.getStroke()));
				g.setColor(Color.red);
			}else
			{
				g.setColor(edgeSetting.getColor());
			}

			g.draw(es);
			if(e==highlightEdge)
			{
				Shape hs=new HighlightStroke(g.getStroke()).createStrokedShape(es);
				g.fill(hs);
			}

			g.setStroke(backupStroke);	
			if(e==highlightEdge)
			{
				g.setStroke(new HighlightStroke(g.getStroke()));
			}

			// draw Arrowheads:
			if(edgeSetting.isDrawSourceArrow())
			{
				Shape s=Arrow.paintSource(points.source,point, edgeSetting.getSourceArrow());
				g.draw(s);	
				if(edgeSetting.isFillSourceArrow())
				{
					g.setColor(edgeSetting.getColor());						
				}else
				{
					g.setColor(Color.white);
				}
				g.fill(s);
				g.setColor(edgeSetting.getColor());	
				g.setStroke(new BasicStroke((float)(1.0f+w) ));
				g.draw(s);	

			}
			if(edgeSetting.isDrawTargetArrow())
			{
				Shape s=Arrow.paintTarget(point, points.target, edgeSetting.getTargetArrow());
				if(edgeSetting.isFillTargetArrow())
				{
					g.setColor(edgeSetting.getColor());						
				}else
				{
					g.setColor(Color.white);
				}
				g.fill(s);				
				g.setColor(edgeSetting.getColor());	
				g.setStroke(new BasicStroke((float)(1.0f+w) ));

				g.draw(s);						
			}

			if(edgeN && edgeSetting.isShowLabel())
			{
				// draw name			
				if(e.getName()!=null && !e.getName().isEmpty())
				{
					TextStroke str=new TextStroke(e.getName(), g.getFont(),edgeSetting.getLabelPlacement(),30);
					str.setLineOffset( ((int)(w/2))+5);
					g.setStroke(str);
					g.draw(es);
				}					
			}
			if(edgeW)
			{
				String et=NumberFormat.getNumberInstance().format(e.getWeight());
				TextStroke str=new TextStroke(et, g.getFont(),edgeSetting.getLabelPlacement(),30);
				str.setLineOffset(- ((int)(w/2)+12) );
				g.setStroke(str);
				g.draw(es);					
			}
			g.setStroke(backupStroke);
			ec++;

		}
		//	System.out.println("Painted "+ec+" edges");

	}

	private void paintAuxiliaries(Graphics2D g)
	{
		AuxItemStrategy strategy=auxItemStrategy.getObjectValue();
		// ask RenderingStrategy if we should paint stuff at all?
		if(!strategy.isDrawAuxItems())
			return;

		// nothing to do at all?
		int i = getComponentCount() - 1;
		if (i < 0) 
		{
			return;
		}
		// go over all components and render their aux. 
		Rectangle tmpRect = new Rectangle(); 
		for (; i >= 0 ; i--) 
		{
			Component comp = getComponent(i); 
			if(comp.getWidth() < 20 || comp.getHeight() < 20) 
				continue;
			if(!(comp instanceof NodeComponent)) // only nodes have aux elementd
				continue;
			if (comp.isVisible())
			{
				Rectangle cr;
				cr = comp.getBounds(tmpRect);

				boolean hitClip = g.hitClip(cr.x, cr.y, cr.width,cr.height+30);
				if(hitClip)
				{	
					NodeComponent cc=((NodeComponent)comp);  
					strategy.drawAuxItem(cc, g); // call strategy to decide what to do
				}
			}
		}
	}

	/**********************************************
	 * Getter and Setter
	 **********************************************/

	public HubRendererDispatcher getRendererDispatcher()
	{
		return rendererDispatcher;
	}

	public int getComponentMaxY()
	{
		int max=0;
		for (CanvasComponent c:model.getComponents())
			max=Math.max(max, c.getY()+c.getHeight());
		return max;
	}

	public ModelHub getModelHub() 
	{
		return modelHub;
	}

	@Override
	public Window getOutermostJWindow() 
	{
		return super.getOutermostJWindow();
	}

	public PluginInstanceSetting<AbstractPlugin> getGraphProviderSetting() {
		return graphProviderSetting;
	}

	public CanvasComponent getTargetNode() {
		return targetNode;
	}

	public void setTargetNode(CanvasComponent targetNode) {
		this.targetNode = targetNode;
	}

	public Point getConnectingPoint() {
		return connectingPoint;
	}

	public void setConnectingPoint(Point connectingPoint) {
		this.connectingPoint = connectingPoint;
	}

	public CanvasComponent getConnectingNode() {
		return connectingNode;
	}

	public void setConnectingNode(CanvasComponent connectingNode) {
		this.connectingNode = connectingNode;
	}

	public SelectionInspector getSelectionInspector() {
		return selectionInspector;
	}

	public void setSelectionInspector(SelectionInspector selectionInspector) {
		this.selectionInspector = selectionInspector;
	}

	/**********************************************
	 * Assistant classes
	 **********************************************/

	private class GraphProviderSettingListener implements SettingChangeListener
	{	
		@Override
		public void stateChanged(SettingChangeEvent e) 
		{
			if(e.getSource()==graphProviderSetting)
				SwingUtilities.invokeLater(new GraphProviderApplicator(GraphViewerPlot.this));		
		}
	}

	private class LayouterSettingListener implements SettingChangeListener
	{
		@Override
		public void stateChanged(SettingChangeEvent e) 
		{
			layouter=(CanvasLayouter)layoutMethodSetting.getInstance();
			Rectangle bounds=null;
			switch (layoutSizeSetting.getSelectedIndex()) {
			case 0:
				bounds=new Rectangle(getOutermostJWindow().getBounds());
				break;
			case 1:
				bounds=getBounds();
				break;
			case 2:
				bounds=new Rectangle(new Dimension(layoutBoundsWidth.getIntValue(), layoutBoundsHeight.getIntValue()));
				break;
			default:
				bounds=getBounds();
				break;
			}
			updateLayout(bounds);
			revalidateEdges();
		}
	}

	/**********************************************
	 * Actions!
	 **********************************************/

	private class ConnectAndMeasureNodesAction extends AbstractAction
	{
		private CanvasComponent source;
		private CanvasComponent target; 
		private DistanceMeasurePlugin dist;

		public ConnectAndMeasureNodesAction(CanvasComponent source, CanvasComponent target, DistanceMeasurePlugin dist, String string)
		{
			super("Connect "+source.getLabel()+" to "+target.getLabel()+" "+string);
			this.source=source;
			this.target=target;
			this.dist=dist;
		}

		public void actionPerformed(ActionEvent e) 
		{
			((SuperModel)getModel()).connect((MultiProbeComponent)source, (MultiProbeComponent)target, dist);
			((NodeComponent)source).nodeUpdated(NodeUpdate.EDGES);
			((NodeComponent)target).nodeUpdated(NodeUpdate.EDGES);
			updatePlot();			
		}		
	}

	private class ConnectNodesAction extends AbstractAction
	{
		private CanvasComponent source;
		private CanvasComponent target; 

		public ConnectNodesAction(CanvasComponent source, CanvasComponent target )
		{
			super("Connect "+source.getLabel()+" to "+target.getLabel());
			this.source=source;
			this.target=target;
		}

		public void actionPerformed(ActionEvent e) 
		{
			getModel().connect(source, target);
			((NodeComponent)source).nodeUpdated(NodeUpdate.EDGES);
			((NodeComponent)target).nodeUpdated(NodeUpdate.EDGES);
			updatePlot();			
		}		
	}

	private class TitleAction extends AbstractAction
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			drawTitle.setBooleanValue(!drawTitle.getBooleanValue());
			updatePlot();
		}
	}	

	private class EdgeAndLabelListener implements SettingChangeListener
	{
		@Override
		public void stateChanged(SettingChangeEvent e) 
		{
			labelFont=new Font(Font.SANS_SERIF, Font.PLAIN, labelSize.getIntValue());
			scaleLabels=scaleLabelSetting.getBooleanValue();
			updatePlotNow();			
		}
	}

	Set<CanvasComponent> getMovingComponents()
	{
		return movingComponents;
	}

	public EdgeDispatcher getEdgeDispatcher() 
	{
		return dispatcher;
	}

	public EdgeTransformationSetting getEdgeTransformationSetting() {
		return edgeTransformationSetting;
	}

	public void setRendererRevolver(HubRevolver revolver)
	{
		this.rendererDispatcher=revolver;
		for(CanvasComponent cc: model.getComponents())
		{
			if(cc instanceof DefaultNodeComponent)
			{
				((DefaultNodeComponent) cc).setRendererDispatcher(rendererDispatcher);
			}
		}			
	}

	public HubRevolver getRendererRevolver()
	{
		return this.rendererDispatcher;
	}

	public void  updateSettingsFromPreferences(ViewerSettingsFileParser parser)
	{
		edgeMasterSetting.fromPrefNode(parser.getEdgePreferences());
		drawLables.fromPrefNode(parser.getLabelPreferences());
		drawLables.setBooleanValue(parser.isShowLabels());
		auxItemStrategy.setSelectedIndex(parser.getSelectedAux());
		title.setStringValue(parser.getTitle());
		drawTitle.setBooleanValue(parser.isShowTitle());

		try{
			modelHub.getColorProvider().getSetting().fromPrefNode(parser.getDefaultColoring());
		}catch(Exception e)
		{

		}
		for(DataSet ds: modelHub.getDataSets())
		{
			if(parser.getColorings().containsKey(ds.getName()))
			{
				Preferences prefs=parser.getColorings().get(ds.getName());
				modelHub.getColorProvider(ds).getSetting().fromPrefNode(prefs);
			}
		}
		try{
			ManipulationMethod manip=(ManipulationMethod)PluginManager.getInstance().getPluginFromID(parser.getDataManipulation()).getInstance();
			modelHub.getViewModel().getDataManipulator().setManipulation(manip);
		}catch(Exception e){
			e.printStackTrace();
		}
		rendererDispatcher.getRenderSmallSettings().setBooleanValue(parser.isSimplify());
		highQualityRendering.setBooleanValue(parser.isHq());
		updatePlotNow();
	}

	public void exportBasicSettings(XMLStreamWriter writer) throws Exception
	{
		writer.writeStartElement("GraphViewerPlotSettings");
		// export LABELS
		writer.writeStartElement(SettingsTarget.LABELS.toString());
		writer.writeAttribute(SETTINGS, serializeSetting(drawLables));
		writer.writeAttribute("active", Boolean.toString(drawLables.getBooleanValue()));
		writer.writeEndElement();

		writer.writeStartElement(SettingsTarget.EDGES.toString());
		writer.writeAttribute(SETTINGS, serializeSetting(edgeMasterSetting));
		writer.writeEndElement();

		writer.writeStartElement(SettingsTarget.AUX.toString());
		writer.writeAttribute("selected", Integer.toString(auxItemStrategy.getSelectedIndex()));
		writer.writeEndElement();

		writer.writeStartElement(SettingsTarget.TITLE.toString());
		if(title.getStringValue()==null)
			writer.writeAttribute("title", "");
		else
			writer.writeAttribute("title", title.getStringValue());
		writer.writeAttribute("active", Boolean.toString(drawTitle.getBooleanValue()));
		writer.writeEndElement();

		writer.writeStartElement(SettingsTarget.RENDERING.toString());
		writer.writeAttribute("hqRendering", Boolean.toString(highQualityRendering.getBooleanValue()) );
		writer.writeAttribute("simplifyNodes", Boolean.toString(rendererDispatcher.getRenderSmallSettings().getBooleanValue()));
		writer.writeEndElement();

		//export vis3 settings:
		getModelHub().exportHubSettings(writer);

		writer.writeEndElement();
	}

	private String serializeSetting(Setting s) throws Exception
	{
		StringWriter w=new StringWriter();
		s.toPrefNode().saveTo(new BufferedWriter(w));
		return w.toString();
	}

	public static final String SETTINGS="settings";
	public static enum SettingsTarget
	{
		LABELS, EDGES, AUX, TITLE, RENDERING
	}

	public EdgeSetting getEdgeSetting() {
		return edgeSetting;
	}
}
