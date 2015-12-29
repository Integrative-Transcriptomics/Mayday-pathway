/**
 * 
 */
package mayday.pathway.keggview.pathways.gui.action;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.prototypes.SupportPlugin;

@SuppressWarnings("serial")
public class LookUpAction extends AbstractAction
{
	URL url;
	public LookUpAction(String name, URL link)
	{
		super(name);
		url=link;
		
	}

	public void actionPerformed(ActionEvent arg0) 
	{
		PluginInfo pli = PluginManager.getInstance().getPluginFromID("PAS.core.StartBrowser");
		Boolean success=false;
		if (pli!=null) {
			SupportPlugin StartBrowser = (SupportPlugin)(pli.getInstance());
			success = (Boolean)StartBrowser.run(url.toString());			
		}
		if (!success) {
			JOptionPane.showMessageDialog((Component)null, 
					"Could not start your web browser.",
					"Sorry",
					JOptionPane.ERROR_MESSAGE										
			);
		}			
	}		
}