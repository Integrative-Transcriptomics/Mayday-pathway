package mayday.pathway.keggview.pathways.gui.canvas;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import mayday.core.pluma.PluginInfo;
import mayday.pathway.keggview.ModelFactory;
import mayday.pathway.keggview.pathways.graph.MapNode;
import mayday.pathway.keggview.pathways.graph.PathwayGraph;
import mayday.pathway.keggview.pathways.gui.MapRenderer;

@SuppressWarnings("serial")
public class MapComponent extends PathwayComponent
{
	private static final Image pathwayIcon=PluginInfo.getIcon("mayday/image/pathway.png").getImage();
	
	private MapNode node;
	private ModelFactory model; 
	
	public MapComponent(MapNode node, PathwayGraph graph)
	{
		super(node,graph);
		setSize(160, 40);
		this.node=node;		
		setToolTipText(makeToolTipText());
		setLabel(prepareLabel());		
	}

	private String prepareLabel()
	{
		String label="";
		label=node.getName();
		if(label.startsWith("TITLE:")) label=label.substring(6);
		return label;
	}
	
	public void paint(Graphics g1)
	{
		Graphics2D g=(Graphics2D)g1;
		MapRenderer.getDefaultRenderer().draw(g, getNode(), getBounds(), null, getLabel(), isSelected());
	}

	private String makeToolTipText()
	{
		return this.node.getName();
	}
	
	protected JPopupMenu setCustomMenu(JPopupMenu menu)
	{
		menu.add(new LoadPathwayAction());
		return menu;
	}
	
	/**
	 * @param pathwayModel the model to set
	 */
	public void setModel(ModelFactory modelFactory) 
	{
		this.model = modelFactory;
	}
	
	public void mouseClicked(MouseEvent event)
	{
		if(event.getClickCount() ==1 && event.getButton()==MouseEvent.BUTTON1)
		{
			toggleSelection();
			event.consume();
		}
		if(event.getClickCount() ==2 && event.getButton()==MouseEvent.BUTTON1)
		{
			loadPathway();
			event.consume();
		}
	}

	public void loadPathway()
	{
		try{
			model.getPathway(node.getPathwayId());
			
//			new Thread(){
//				public void run()
//				{
//					AbstractTask  task=new AbstractTask("Downloading Pathway "+getLabel()+".")
//					{
//						@Override
//						protected void doWork() throws Exception 
//						{	   
//							//model.setPathway(node.getPathwayId());
//						}
//						@Override
//						protected void initialize(){}	    					
//					};	    	    		
//					task.start();
//					task.waitFor();	
//				}
//			}.start();
			}catch(Exception ex){
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Cannot load Pathway " +getName(), "Error", JOptionPane.ERROR_MESSAGE);
		}

	}
	
	public class LoadPathwayAction extends AbstractAction
	{
		public LoadPathwayAction()
		{
			super("Load pathway "+getLabel());
		}
		
		public void actionPerformed(ActionEvent e) 
		{
			loadPathway();			
		}		
	}

	public Image getImage()
	{
		return pathwayIcon;
	}

	////// The following functions do not apply here
	
	@Override
	public void resetLabel() {}

	@Override
	public void setProbeNamesAsLabel() {}

}
