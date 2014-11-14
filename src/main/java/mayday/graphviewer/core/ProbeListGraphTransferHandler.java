package mayday.graphviewer.core;

import java.awt.Component;
import java.util.Arrays;

import javax.swing.JComponent;

import mayday.core.ProbeList;
import mayday.core.gui.dragndrop.MaydayTransferHandler;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.graphviewer.action.AddProbeListAction;

@SuppressWarnings("serial")
class ProbeListGraphTransferHandler extends MaydayTransferHandler<ProbeList> {

	private GraphViewerPlot viewer;


	
	public ProbeListGraphTransferHandler(GraphViewerPlot viewer) {
		super(ProbeList.class);
		this.viewer = viewer;
	}
	
//	public boolean importData(TransferHandler.TransferSupport info) 
//	{
//		if (!info.isDrop()) 
//			return false;		
//		
//		// find out the source
//		Transferable tf = info.getTransferable();		
//		
//		DataSet sourceDataSet = null;
//		ProbeListNode[] nodes = null;
//		
//
//
//		try {
//			sourceDataSet = (DataSet)tf.getTransferData(ProbeListNodesTransferable.sourceDataSetFlavor);
//			nodes = ((ProbeListNodeProtector)tf.getTransferData(ProbeListNodesTransferable.probeListNodesFlavor)).getNodes();
//		} catch (IOException transferException) {
//			transferException.printStackTrace();
//			return false;
//		} catch (UnsupportedFlavorException e) {
//			// ignore
//		}
//		
//		String[] options={"single node", "node for each probe","Profile Plot","Heat Map","Box Plot"};
//		RestrictedStringSetting method=new RestrictedStringSetting("Display as...",null,0,options);
//		
//		SettingDialog dialog=new SettingDialog(viewer.getOutermostJWindow(), "Adding "+nodes.length+" ProbeLists", method);
//		dialog.setModal(true);
//		dialog.setVisible(true);
//		if(!dialog.closedWithOK())
//			return false;
//		
//		if (sourceDataSet==null || nodes==null) {
//			// this is not an internal drag
//			// try to interpret input as a list of probe names and create a new probelist
//			try {
//				DataSet targetDataSet=viewer.getModelHub().getViewModel().getDataSet();
//				String str = tf.getTransferData(DataFlavor.stringFlavor).toString();
//				// first copy the probeset content
//				ProbeList targetPL = new ProbeList(targetDataSet, true);
//				targetPL.setName("Dropped ProbeList");
//				targetPL.getAnnotation().setQuickInfo("ProbeList received via drag&drop from another application");
//				
//				String[] nameCandidates = str.split("[\\s]+");
//				if (nameCandidates.length==1)
//					nameCandidates = str.split(",");
//				if (nameCandidates.length==1)
//					nameCandidates = str.split(";");				
//				for (String nameCandidate : nameCandidates) {
//					Probe targetpb = targetDataSet.getMasterTable().getProbe(nameCandidate.trim());
//					if (targetpb!=null) {
//						targetPL.addProbe(targetpb);
//					}
//				}
//				List<ProbeList> probeLists=new ArrayList<ProbeList>();
//				probeLists.add(targetPL);
//				AddProbeListAction.addProbeLists(viewer,probeLists, method.getSelectedIndex());							
//				return true;
//			} catch (UnsupportedFlavorException ufl) {
//				// ignore
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			return false;
//		}else{
//			try{
//				List<ProbeList> probeLists=new ArrayList<ProbeList>();
//				for(int i=nodes.length-1; i >=0; --i){
//					ProbeListNode sourcePLo = nodes[i];
//					ProbeList sourcePL = sourcePLo.getProbeList();
//					probeLists.add(sourcePL);
//				}
//				AddProbeListAction.addProbeLists(viewer,probeLists, method.getSelectedIndex());	
//				return true;
//			}catch(Exception e){} // die silently
//		}
//			
//
//		return true;
//	}

	@Override
	protected Object getContextObject() 
	{
		return null;
	}

	@Override
	public ProbeList[] getDragObject(JComponent c) 
	{
		return null;
	}

	@Override
	protected void processDrop(Component c, ProbeList[] droppedObjects,	TransferSupport info)
	{
		String[] options={"single node", "node for each probe","Profile Plot","Heat Map","Box Plot"};
		RestrictedStringSetting method=new RestrictedStringSetting("Display as...",null,0,options);
		
		SettingDialog dialog=new SettingDialog(viewer.getOutermostJWindow(), "Adding "+droppedObjects.length+" ProbeLists", method);
		dialog.setModal(true);
		dialog.setVisible(true);
		if(!dialog.closedWithOK())
			return;
		
		AddProbeListAction.addProbeLists(viewer,Arrays.asList(droppedObjects), method.getSelectedIndex());		
	}
	
	

	

}
