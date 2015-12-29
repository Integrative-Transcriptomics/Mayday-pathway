package mayday.graphviewer.core.ccp;

import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.dragndrop.DragSupportManager;
import mayday.core.gui.dragndrop.DragSupportPlugin;
import mayday.core.pluma.PluginManager;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.action.AddProbeListAction;
import mayday.graphviewer.core.DefaultNodeComponent;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.edges.EdgeSetting;
import mayday.vis3.graph.renderer.RendererDecorator;
import mayday.vis3.graph.renderer.dispatcher.AssignedRendererSetting;
import mayday.vis3.graph.renderer.dispatcher.DecoratorListSetting;
import mayday.vis3.graph.renderer.dispatcher.RendererPluginSetting;

@SuppressWarnings("serial")
public class GraphTransferHandler extends TransferHandler{

	public static final String GRAPH_DATA_FLAVOR_NAME="GraphWrap";
	public static final DataFlavor GRAPH_DATA_FLAVOR=new DataFlavor(GraphWrap.class,GRAPH_DATA_FLAVOR_NAME );

	private GraphViewerPlot canvas;	

	public GraphTransferHandler(GraphViewerPlot canvas) {
		this.canvas = canvas;
	}

	@Override
	public int getSourceActions(JComponent c) 
	{
		return COPY;
	}

	@Override
	protected Transferable createTransferable(JComponent c) 
	{
		// cast c to GVP
		GraphViewerPlot canvas=(GraphViewerPlot)c;
		GraphWrap wrap=new GraphWrap();
		wrap.viewer=canvas;
		wrap.components=new ArrayList<CanvasComponent>(canvas.getSelectionModel().getSelectedComponents());
		List<Node> selectedNodes=new ArrayList<Node>();
		for(CanvasComponent cc: wrap.components)
		{
			selectedNodes.add(canvas.getModel().getNode(cc));
		}
		
		List<Edge> edges=new ArrayList<Edge>();
		for(Node n: selectedNodes)
		{
			for(Edge e: canvas.getModel().getGraph().getOutEdges(n))
			{
				if(selectedNodes.contains(e.getTarget()) )
				{
					edges.add(e);
				}
			}
		}
		wrap.edges=edges;		
		wrap.model=canvas.getModel().buildSubModel(selectedNodes);
		GraphTransferable t=new GraphTransferable(wrap);
		return t;
	}


	@Override
	public boolean canImport(TransferSupport support) 
	{
		DataFlavor[] flavors=support.getDataFlavors();
		for(DataFlavor df:flavors)
		{
			if(df==GRAPH_DATA_FLAVOR)
				return true;
		}
		// no luck with graph? maybe it's a probe list
		Transferable tf = support.getTransferable();		
		return DragSupportManager.getSupportFor(ProbeList.class, tf.getTransferDataFlavors())!=null;
	}

	@Override
	public boolean importData(TransferSupport support) 
	{
		if (!canImport(support)) {
			return false;
		}
		
		DragSupportPlugin dsp = DragSupportManager.getSupportFor(ProbeList.class, support.getDataFlavors());
		if (dsp!=null)
		{
//			dsp.setContext(getContextObject());
			ProbeList[] droppedObjects = dsp.processDrop(ProbeList.class, support.getTransferable());		
			processDrop(support.getComponent(), droppedObjects, support);
			return true;
		}
		// no luck with probe list? try graph!
		try {
			GraphWrap wrap=(GraphWrap)support.getTransferable().getTransferData(GRAPH_DATA_FLAVOR);
			handleImport(wrap);
		} catch (Exception e) {
			e.printStackTrace(); //TODO: die silently later;
		} 
		return true;
	}

	private void handleImport(GraphWrap wrap)
	{
		SuperModel sm=(SuperModel) canvas.getModel();
		MultiHashMap<DataSet, Probe> probes=new MultiHashMap<DataSet, Probe>();

		List<Point> positions=new ArrayList<Point>();
		List<DefaultNodeComponent> addedComponents=new ArrayList<DefaultNodeComponent>(); 
		Map<Node, Node> nodeMap=new HashMap<Node, Node>();

		int minX=Integer.MAX_VALUE;
		int minY=Integer.MAX_VALUE;

		for(int i=0; i!= wrap.components.size(); ++i)
		{
			CanvasComponent cc=wrap.components.get(i);
			MultiProbeNode n=(MultiProbeNode)wrap.viewer.getModel().getNode(cc);
			DataSet ds=canvas.getModelHub().getViewModel().getDataSet();
			for(Probe p:n.getProbes())
			{
				probes.put(p.getMasterTable().getDataSet(), p);
				ds=p.getMasterTable().getDataSet();
			}
			// safely copy the node
			MultiProbeNode copy=new MultiProbeNode(canvas.getModel().getGraph());
			copy.setName(n.getName());
			copy.setRole(n.getRole());
			
			for(String s: n.getProperties().keySet())
			{
				copy.setProperty(s, n.getPropertyValue(s));
			}
			copy.setProbes(new ArrayList<Probe>(n.getProbes()));
			nodeMap.put(n, copy);
			DefaultNodeComponent dnc=sm.addNode(copy);
			positions.add(cc.getLocation());
			minX=Math.min(minX, cc.getX());
			minY=Math.min(minY, cc.getY());
			addedComponents.add(dnc);

			if(wrap.viewer.getRendererDispatcher().getIndividualRenderers().containsKey(cc))
			{
				RendererPluginSetting plu=wrap.viewer.getRendererDispatcher().getIndividualRenderers().get(cc);
				RendererPluginSetting copyRenderer=new AssignedRendererSetting(n.getRole(),ds, canvas.getModelHub().getColorProvider(ds));
				copyRenderer.setPrimaryRenderer(plu.getPrimaryRenderer().getInstance().getPluginInfo().getIdentifier());
				copyRenderer.getPrimaryRenderer().fromPrefNode(plu.getPrimaryRenderer().toPrefNode());
				DecoratorListSetting decs=new DecoratorListSetting(ds);
				for(RendererDecorator dec: plu.getDecorators().getSelection())
				{
					RendererDecorator copyDec=(RendererDecorator)
						PluginManager.getInstance().getPluginFromID(dec.getPluginInfo().getIdentifier()).getInstance();
					copyDec.getSetting().fromPrefNode(dec.getSetting().toPrefNode());
					decs.add(copyDec);
				}
				copyRenderer.setDecorators(decs);
				canvas.getRendererDispatcher().addIndividualRenderer(dnc, copyRenderer);
			}
		}

		// handle edges in wrap.model;
		for(Edge e: wrap.edges)
		{
			Edge copy=canvas.getModel().connect(
					canvas.getModel().getComponent(nodeMap.get(e.getSource())),
					canvas.getModel().getComponent(nodeMap.get(e.getTarget())));	
			
			copy.setName(e.getName());
			copy.setRole(e.getRole());
			copy.setWeight(e.getWeight());
			if(e.getProperties()!=null)
			{
				for(String s: e.getProperties().keySet())
				{
					copy.getProperties().put(s, e.getProperties().get(s));
				}
			}
			if(wrap.viewer.getEdgeDispatcher().hasSpecificSetting(e))
			{
				EdgeSetting copySetting=new EdgeSetting(e.getRole());
				copySetting.fromPrefNode(wrap.viewer.getEdgeDispatcher().getSetting(e).toPrefNode());
				canvas.getEdgeDispatcher().putEdgeSetting(copy,copySetting);
			}				
		}

		//place stuff at mouse cursor
		Point b=canvas.getMousePosition();
		for(int i=0; i!= addedComponents.size(); ++i)
		{
			CanvasComponent comp=addedComponents.get(i);
			Point p=positions.get(i);
			comp.setLocation( (p.x-minX)+b.x,(p.y-minY)+b.y );
		}

		//this is only necessary if sender!=reciever
		if(wrap.viewer!=canvas){
		for(DataSet ds: probes.keySet())
			canvas.getModelHub().addProbes(ds, probes.get(ds));
		}
		canvas.revalidateEdges();
		canvas.updatePlotNow();
	}

	protected void processDrop(Component c, ProbeList[] droppedObjects,	TransferSupport info)
	{
		String[] options={"single node", "node for each probe","Profile Plot","Heat Map","Box Plot"};
		RestrictedStringSetting method=new RestrictedStringSetting("Display as...",null,0,options);
		
		SettingDialog dialog=new SettingDialog(canvas.getOutermostJWindow(), "Adding "+droppedObjects.length+" ProbeLists", method);
		dialog.setModal(true);
		dialog.setVisible(true);
		if(!dialog.closedWithOK())
			return;
		
		AddProbeListAction.addProbeLists(canvas,Arrays.asList(droppedObjects), method.getSelectedIndex());		
	}

}
