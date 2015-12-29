package mayday.graphviewer.core.ccp;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class GraphTransferable implements Transferable
{
	private GraphWrap wrap;

	
	
	public GraphTransferable(GraphWrap wrap) {
		this.wrap = wrap;
	}

	public GraphWrap getWrap() {
		return wrap;
	}

	public void setWrap(GraphWrap wrap) {
		this.wrap = wrap;
	}

	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if(flavor==GraphTransferHandler.GRAPH_DATA_FLAVOR)
		{
			return wrap;
		}
		return null;			
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() 
	{
		DataFlavor[] fl={GraphTransferHandler.GRAPH_DATA_FLAVOR};
		return fl;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) 
	{
		if(flavor==GraphTransferHandler.GRAPH_DATA_FLAVOR)
		{
			return true;
		}
		return false;
	}
	
}
