package mayday.graphviewer.plugins;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Stack;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import mayday.core.MaydayDefaults;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.prototypes.MenuMakingPlugin;
import mayday.graphviewer.core.GraphViewerPlot;



@SuppressWarnings("serial")
public class RunGraphViewerPluginAction extends AbstractAction 
{
	private PluginInfo plugin;
	private GraphViewerPlot viewer; 
	
	public RunGraphViewerPluginAction(PluginInfo pli, GraphViewerPlot viewer) 
	{			
		if (pli==null)
			return;
		putValue(Action.NAME, pli.getMenuName().replace("\0", ""));
		ImageIcon ico = pli.getIcon();
		if (ico!=null) {
			ImageIcon sico = new ImageIcon(ico.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));		
			this.putValue(AbstractAction.SMALL_ICON, sico);
		}
		plugin = pli;
		this.viewer=viewer;
	}
	
	public PluginInfo getPlugin() {
		return plugin;
	}	

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		Thread RunPluginThread = new Thread("PluginRunner")
		{
			public void run() {

				System.runFinalization();
				System.gc();

				GraphViewerPlugin gvp = ((GraphViewerPlugin)getPlugin().getInstance());

				try {  
					gvp.run(viewer,viewer.getModel(),viewer.getSelectionModel().getSelectedComponents());
				}
				catch ( Exception exception ) {
					exception.printStackTrace();			    	           
					JOptionPane.showMessageDialog( null,
							exception.toString(),
							MaydayDefaults.Messages.ERROR_TITLE,
							JOptionPane.ERROR_MESSAGE );
				}
			}
			
		};
		RunPluginThread.start();
	}

	@SuppressWarnings("unchecked")
	public static void addToMenu(JMenu menu, Vector<? extends RunGraphViewerPluginAction> plugins) {
		LinkedList< llitem > items = new LinkedList<llitem>();
		for (RunGraphViewerPluginAction rpi : plugins) {
			PluginInfo pli = rpi.getPlugin();
			Vector<String> plpath = (Vector<String>)(pli.getProperties().get(Constants.CATEGORIES));
			if (plpath==null)
				items.add(new llitem(pli.getMenuName(),rpi));
			else 
				for (String subcat : plpath)
					if (subcat.trim().length()==0)
						items.add(new llitem(pli.getMenuName(),rpi));
					else
						items.add(new llitem(subcat+"/"+pli.getMenuName(),rpi));			
		}
		Collections.sort(items);
		
		Stack<String> menustack = new Stack<String>();
		Stack<JMenu> menustack2 = new Stack<JMenu>();
		String prefix="";
		for(llitem lli:items) {
			
			// go up if needed
			while (!lli.path.startsWith(prefix)) {
				prefix = menustack.pop();
				JMenu last = menu;
				menu = menustack2.pop();
				menu.add(last);
			}
			
			if (lli.path.startsWith(prefix) || prefix=="") {
				String remainingpath=(lli.path.replace(prefix, ""));
			
				if (remainingpath.contains("/")) { // start new subpath
					String[] submenus = remainingpath.split("/");
					for(int i=0; i<submenus.length-1; ++i) {
						String submenu = submenus[i];
						menustack.push(prefix);
						menustack2.push(menu);
						menu=new JMenu(submenu);
						prefix += submenu+"/";
					}					
				}
				
				// make submenus if the plugin supports it
				try {
					if (lli.rpi.getPlugin().getInstance() instanceof MenuMakingPlugin) {
						for (JMenuItem item : ((MenuMakingPlugin)(lli.rpi.getPlugin().getInstance())).createMenu()) {
							menu.add(item);
						}
					} else {
						menu.add(lli.rpi);
					}
				} catch (Throwable t) {
					System.err.println("Could not add menu item resp. submenu: "+lli.rpi);
				}
			} 
			
		}
		while (prefix.length()>0) {
			prefix = menustack.pop();
			JMenu last = menu;
			menu = menustack2.pop();
			menu.add(last);
		}

	}
	
	@SuppressWarnings("unchecked")
	private static class llitem implements Comparable {
		public String path;
		public RunGraphViewerPluginAction rpi;
		public llitem(String s, RunGraphViewerPluginAction p) {
			path=s;
			rpi=p;
		}
		public int compareTo(Object arg0) {
			return path.compareTo(((llitem)arg0).path);
		}
	}
	
}
