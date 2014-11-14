package mayday.graphviewer.gui;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mayday.core.gui.components.ExcellentBoxLayout;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.graphviewer.plugins.RunGraphViewerPluginAction;

@SuppressWarnings("serial")
public class GraphViewerPluginList extends JPanel
{
	private Map<String,JList> pluginLists;
	private GraphViewerPlot graphViewer;

	public GraphViewerPluginList(GraphViewerPlot viewer) 
	{
		super();
		this.graphViewer=viewer;
		init();
	}

	@SuppressWarnings("unchecked")
	private void init()
	{
		Set<PluginInfo> plis = PluginManager.getInstance().getPluginsFor(GraphViewerPlugin.MC_GRAPH);

		pluginLists=new TreeMap<String, JList>();

		Vector<PluginInfo> misc=new Vector<PluginInfo>();
		Map<String, Vector<PluginInfo>> categories=new TreeMap<String, Vector<PluginInfo>>();
		for (PluginInfo pli : plis)
		{
			Vector<String> plpath = (Vector<String>)(pli.getProperties().get(Constants.CATEGORIES));

			if (plpath==null)
			{
				misc.add(pli);				
			}else
			{		

				for (String subcat : plpath)
				{
					if (subcat.trim().length()==0)
					{
						misc.add(pli);
					}
					else
					{
						if(categories.containsKey(subcat))
						{
							categories.get(subcat).add(pli);
						}else
						{
							categories.put(subcat,new Vector<PluginInfo>());
							categories.get(subcat).add(pli);
						}

					}
				}
			}
		}
		requestFocusInWindow();
		getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "run");
		getActionMap().put("newNode",new RunAppAction());
		
		PluginInfoRenderer renderer=new PluginInfoRenderer();
		SelectionListener selectionListener=new SelectionListener();
		ListMouseAdapter adapter=new ListMouseAdapter();
//		setLayout(new GridLayout(2*(categories.size()+1), 1));
		setLayout(new ExcellentBoxLayout(true, 5));
		for(String s:categories.keySet())
		{
			JList catList=new JList(categories.get(s));
			catList.setLayoutOrientation(JList.VERTICAL_WRAP);
			catList.setCellRenderer(renderer);
			catList.setVisibleRowCount(5);
			catList.getSelectionModel().addListSelectionListener(selectionListener);
			catList.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "run");
			catList.getActionMap().put("newNode",new RunAppAction());
			pluginLists.put(s, catList);
			JLabel label=new JLabel(s);			
			label.setFont(new Font(Font.SANS_SERIF,Font.BOLD,16));
			add(label);
			add(catList);
			catList.addMouseListener(adapter);
			
		}

		JList catList=new JList(misc);
		catList.setLayoutOrientation(JList.VERTICAL_WRAP);
		catList.setCellRenderer(renderer);
		catList.setVisibleRowCount(5);
		catList.getSelectionModel().addListSelectionListener(selectionListener);
		catList.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "run");
		catList.getActionMap().put("newNode",new RunAppAction());
		pluginLists.put("Misc", catList);
		JLabel label=new JLabel("Misc");		
		label.setFont(new Font(Font.SANS_SERIF,Font.BOLD,16));
		add(label);
		add(catList);
		catList.addMouseListener(adapter);
	}
	
	public PluginInfo getSelectedPlugin()
	{
		for(JList list: pluginLists.values())
		{
			if(list.getSelectionModel().getMaxSelectionIndex()==-1)
			{
				continue;
			}
			PluginInfo pli=(PluginInfo)list.getSelectedValue();
			return pli;
		}
		return null;
	}
	
	

	
	private class PluginInfoRenderer extends DefaultListCellRenderer
	{
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) 
		{
			PluginInfo pli=(PluginInfo)value;

			JLabel c= (JLabel)super.getListCellRendererComponent(list, pli.getName(), index, isSelected,
					cellHasFocus);
			c.setToolTipText(pli.getAbout());
			return c;
		}
	}
	
	private void executeApp()
	{
		new RunGraphViewerPluginAction(getSelectedPlugin(), graphViewer).actionPerformed(null);
	}

	private class ListMouseAdapter extends MouseAdapter
	{

		@Override
		public void mouseClicked(MouseEvent e) 
		{
			if(e.getClickCount()>1)
			{
				executeApp();
			}
			super.mouseClicked(e);
		}
	}

	private class SelectionListener implements ListSelectionListener
	{
		public SelectionListener() 
		{
		}
		
		@Override
		public void valueChanged(ListSelectionEvent e) 
		{
			
			for(JList list: pluginLists.values())
			{
				if(list.getSelectionModel()==e.getSource())
				{
					list.requestFocusInWindow();
					continue;
				}
				list.clearSelection();
			}

		}
	}
	
	private class RunAppAction extends AbstractAction
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			executeApp();			
		}
	}




}
