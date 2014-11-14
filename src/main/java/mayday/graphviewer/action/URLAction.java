package mayday.graphviewer.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.prototypes.SupportPlugin;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.core.structures.graph.nodes.Nodes;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.util.URLHandler;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.NodeComponent;

@SuppressWarnings("serial")
public class URLAction extends AbstractAction
{
	private GraphViewerPlot viewer;	
	
	public URLAction(GraphViewerPlot viewer) 
	{
		super("Open in Web Browser",PluginInfo.getIcon("mayday/pathway/gvicons/lookup.png",16,16));
		this.viewer=viewer;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		List<String> urls=new ArrayList<String>();
		for(CanvasComponent cc: viewer.getSelectionModel().getSelectedComponents())
		{
			Node n=((NodeComponent)cc).getNode();
			if(n instanceof DefaultNode)
			{
				if(((DefaultNode) n).hasProperty(Nodes.URL_KEY))
				{
					urls.add(((DefaultNode) n).getPropertyValue(Nodes.URL_KEY)  );					
				}else
				{
					urls.addAll(URLHandler.findURLs(((DefaultNode)n).getProperties()));
				}
			}
		}
		if(urls.isEmpty())
			return;
		PluginInfo pli = PluginManager.getInstance().getPluginFromID("PAS.core.StartBrowser");
		Boolean success=false;
		if (pli!=null) 
		{
			SupportPlugin StartBrowser = (SupportPlugin)(pli.getInstance());
			for(String url:urls)
			{
				success = (Boolean)StartBrowser.run(url);	
				if(!success)
					break;
			}				
		}
		if (!success) 
		{
			JOptionPane.showMessageDialog((Component)null, "Could not start your web browser.",
					"Error opening URL",
					JOptionPane.ERROR_MESSAGE);
		}
		
		
		
	}
	
	
}
