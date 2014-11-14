package mayday.graphviewer.plugins.arrange;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
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

public class Scale extends AbstractGraphViewerPlugin 
{
	@Override
	public void run(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		if(components.isEmpty())
			return;
		
		new ScaleDialog(canvas, model, components).setVisible(true);
		
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Scale",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Scale the space between components, around the center, keeping the aspect ratio",
				"Scale"				
		);
		pli.addCategory(ARRANGE_CATEGORY);
		pli.setIcon("mayday/pathway/gvicons/scale.png");
		return pli;	
	}

	@SuppressWarnings("serial")
	private class ScaleDialog extends MaydayDialog implements GraphModelSelectionListener
	{
		private Rectangle oriRea; 
		private GraphViewerPlot canvas;
		private List<CanvasComponent> components;
		private List<Point> origins; 
		
		private JSlider slider;
		private JCheckBox sizeBox;  
		
		public ScaleDialog(GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
		{
			super(canvas.getOutermostJWindow(), "Scale");
			this.canvas=canvas;
			this.components=components;
			
			oriRea =getBoundingRect(components);
						
			origins=new ArrayList<Point>();
			for(CanvasComponent cc:components)
			{
				origins.add(new Point(cc.getX()+cc.getWidth()/2, cc.getY()+cc.getHeight()/2));
			}
			
			canvas.getSelectionModel().addSelectionListener(this);
			
			slider=new JSlider(0,300, 100);
			slider.setPreferredSize(new Dimension(450, 60));
			slider.setMajorTickSpacing(25);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			slider.addChangeListener(new ChangeListener() {
				
				@Override
				public void stateChanged(ChangeEvent e) {
					scale();					
				}
			});
			
			sizeBox=new JCheckBox("Scale nodes");
			sizeBox.setSelected(false);
			sizeBox.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					scale();					
				}
			});
			
			JButton okButton=new JButton(new OkAction());
			JButton resetButton=new JButton(new ResetAction());
			
			setLayout(new GridLayout(3, 1));
			slider.setBorder(BorderFactory.createTitledBorder("Scale factor(%)"));
			add(slider);
					
			JPanel sp=new JPanel();
			sp.add(sizeBox);
			add(sp);
			
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
		
		private void scale()
		{
			Point2D c=new Point2D.Double(oriRea.getCenterX(), oriRea.getCenterY());
			double f= ((double)slider.getValue())/100.0;
			
			for(int i=0; i!= components.size(); ++i)
			{
				CanvasComponent cc=components.get(i);
				Point pc=origins.get(i);
				if(cc==null)
					continue;
				
				Point2D pcc=new Point2D.Double(pc.getX() - c.getX(), pc.getY()-c.getY());
				
				pcc.setLocation(pcc.getX()*f  +c.getX() - cc.getWidth()/2.0 , pcc.getY()*f + c.getY() -cc.getHeight()/2.0);
				cc.setLocation((int) pcc.getX(),(int) pcc.getY());	
				if(sizeBox.isSelected())
					cc.zoomed(f);
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
			slider.setValue(100);
		}
	}
}
