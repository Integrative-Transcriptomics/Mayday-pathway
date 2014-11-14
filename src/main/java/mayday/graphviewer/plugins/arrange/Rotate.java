package mayday.graphviewer.plugins.arrange;

import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.gui.MaydayDialog;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.model.GraphModel;
import mayday.vis3.graph.model.GraphModelSelectionListener;

public class Rotate extends AbstractGraphViewerPlugin 
{
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		if(components.isEmpty())
			return;
		
		new RotationDialog(canvas, model, components).setVisible(true);
		
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Rotate",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Rotate the nodes",
				"Rotate"				
		);
		pli.addCategory(ARRANGE_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/rotate.png");
		return pli;	
	}

	@SuppressWarnings("serial")
	private class RotationDialog extends MaydayDialog implements GraphModelSelectionListener
	{
		private Point2D center;
		private GraphViewerPlot canvas;
		private List<CanvasComponent> components;
		private List<Point> origins; 
		
		private JRadioButton useCenter;
		private JRadioButton useWindowCenter;
		private JRadioButton useComponent;
		private JRadioButton clockwise;
		private JRadioButton counterclockwise;
		private JSlider slider;
		private JComboBox componentBox;
		
		public RotationDialog(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
		{
			super(canvas.getOutermostJWindow(), "Rotate");
			this.canvas=canvas;
			this.components=components;
			
			Rectangle rect=getBoundingRect(components);
			center=new Point2D.Double(rect.getCenterX(), rect.getCenterY());
			
			origins=new ArrayList<Point>();
			for(CanvasComponent cc:components)
			{
				origins.add(new Point(cc.getX()+cc.getWidth()/2, cc.getY()+cc.getHeight()/2));
			}
			
			canvas.getSelectionModel().addSelectionListener(this);
			
			slider=new JSlider(0,360, 0);
			slider.setMajorTickSpacing(45);
			slider.setMinorTickSpacing(15);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			
			ButtonGroup centerGroup=new ButtonGroup();
			useCenter=new JRadioButton("Center of selected nodes");
			useWindowCenter=new JRadioButton("Center of window");
			useComponent=new JRadioButton("Use Node as center");
			centerGroup.add(useCenter);
			centerGroup.add(useWindowCenter);
			centerGroup.add(useComponent);
			useCenter.setSelected(true);
			
			componentBox=new JComboBox();
			for(CanvasComponent cc: components)
			{
				componentBox.addItem(cc);
			}
			
			
			ButtonGroup dirGroup=new ButtonGroup();
			clockwise=new JRadioButton("Clockwise");
			counterclockwise=new JRadioButton("Counter clockwise");
			dirGroup.add(clockwise);
			dirGroup.add(counterclockwise);
			clockwise.setSelected(true);
			
			ActionListener listener1=new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) 
				{
					rotate();					
				}
			};
			
			useCenter.addActionListener(listener1);
			useWindowCenter.addActionListener(listener1);
			useComponent.addActionListener(listener1);
			clockwise.addActionListener(listener1);
			counterclockwise.addActionListener(listener1);
			
			slider.addChangeListener(new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) {
					rotate();					
				}
			});
			
			JButton okButton=new JButton(new OkAction());
			JButton resetButton=new JButton(new ResetAction());
			
			setLayout(new GridLayout(4, 1));
			slider.setBorder(BorderFactory.createTitledBorder("Rotation Angle"));
			add(slider);
			
			JPanel dirPanel=new JPanel();
			dirPanel.setBorder(BorderFactory.createTitledBorder("Rotation Direction"));
			dirPanel.add(clockwise);
			dirPanel.add(counterclockwise);
			add(dirPanel);
			
			JPanel centerPanel=new JPanel();
			centerPanel.setBorder(BorderFactory.createTitledBorder("Rotation Center "));
			centerPanel.add(useCenter);
			centerPanel.add(useWindowCenter);
			centerPanel.add(useComponent);
			centerPanel.add(componentBox);
			add(centerPanel);
			
			JPanel bp=new JPanel();
			bp.add(resetButton);
			bp.add(okButton);
			add(bp);
			
			
			pack();
		}
		
		@Override
		public void selectionChanged() 
		{
			components=canvas.getSelectionModel().getSelectedComponents();			
			origins=new ArrayList<Point>();
			for(CanvasComponent cc:components)
			{
				origins.add(new Point(cc.getX()+cc.getWidth()/2, cc.getY()+cc.getHeight()/2));
			}
			componentBox.removeAllItems();
			for(CanvasComponent cc: components)
			{
				componentBox.addItem(cc);
			}
		}
		
	

		private class OkAction extends AbstractAction
		{
			public OkAction() {
				super("Ok");
			}
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		}
		
		private class ResetAction extends AbstractAction
		{
			public ResetAction() {
				super("Reset");
			}
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				reset();
			}
		}
		
		private void rotate()
		{
			Point2D c=null;
			CanvasComponent centerC=null;
			if(useWindowCenter.isSelected())
				c=new Point2D.Double(canvas.getBounds().getCenterX(),canvas.getBounds().getCenterY() );
			if(useCenter.isSelected())
				c=center;
			if(useComponent.isSelected()){
				centerC=(CanvasComponent) componentBox.getItemAt(componentBox.getSelectedIndex());
				Point p=origins.get(components.indexOf(centerC));
				c=new Point2D.Double(p.getX()+centerC.getWidth()/2.0, p.getY()+centerC.getHeight()/2.0);
			}
			
			int dir=1;
			if(counterclockwise.isSelected())
				dir=-1;
			
			double angle=dir* slider.getValue()*Math.PI/180.0;
						
			for(int i=0; i!= components.size(); ++i)
			{
				CanvasComponent cc=components.get(i);
				if(cc==centerC)
					continue;
				Point pc=origins.get(i);
				if(cc==null)
					continue;
				
				Rectangle2D rect=cc.getBounds().getBounds2D();
				Point2D pcc=new Point2D.Double(pc.getX() - c.getX(), pc.getY()-c.getY());
				double xp= pcc.getX()*Math.cos(angle) - (-pcc.getY())*Math.sin(angle);
				double yp= pcc.getX()*Math.sin(angle) + (-pcc.getY())*Math.cos(angle);
				xp+=center.getX()-0.5*rect.getWidth();
				yp+=center.getY()-0.5*rect.getHeight();
				cc.setLocation((int)xp, (int)yp);				
			}
			canvas.revalidateEdges();
			canvas.updatePlotNow();
		}
		
		private void reset()
		{
			for(int i=0; i!= components.size(); ++i)
			{
				components.get(i).setLocation(origins.get(i));				
			}
			slider.setValue(0);
		}
	}

}
