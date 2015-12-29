/**
 * 
 */
package mayday.graphviewer.crossViz3.unitTree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;

import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.graph.renderer.RendererTools;

@SuppressWarnings("serial")
public class UnitCellRenderer extends JPanel implements TreeCellRenderer
{
	private JLabel nameLabel;
	private JLabel dsLabel;
	private ValuesComponent valuesPanel;

	private int h=15;

	private double min=0;
	private double max=16;
	
	public UnitCellRenderer() 
	{
		this(0,16);
	}
	
	public UnitCellRenderer(double min, double max) 
	{
		super();
		this.min=min;
		this.max=max;
		nameLabel=new JLabel();
		nameLabel.setMaximumSize(new Dimension(100,h));
		dsLabel=new JLabel();
		dsLabel.setMaximumSize(new Dimension(100,h));
		nameLabel.setSize(new Dimension(100, h));
		dsLabel.setSize(new Dimension(100, h));
		valuesPanel=new ValuesComponent(ColorGradient.createDefaultGradient(this.min, this.max));
		valuesPanel.setSize(new Dimension(500,h));
		setLayout(null);

		nameLabel.setLocation(0, 0);
		add(nameLabel);
		dsLabel.setLocation(105, 0);
		add(dsLabel);
		valuesPanel.setLocation(210, 0);
		add(valuesPanel);
		setPreferredSize(new Dimension(750, h));
		setBackground(Color.WHITE);
	}



	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) 
	{
		Object u=((DefaultMutableTreeNode)value).getUserObject();
		valuesPanel.selected=selected;
		if(selected)
		{
			setBackground(Color.lightGray);			
		}else
		{
			setBackground(Color.white);
		}
		if(u instanceof ProbeTreeObject)
		{
			nameLabel.setText(((ProbeTreeObject)u).probe.getDisplayName());
			//				nameLabel.setSize(new Dimension(100, 15));
			dsLabel.setText(((ProbeTreeObject)u).probe.getMasterTable().getDataSet().getName());
			//				dsLabel.setSize(new Dimension(100, 15));
			valuesPanel.setValues(((ProbeTreeObject)u).values);
			

		}else
		if(u instanceof UnitTreeObject)
		{
			nameLabel.setText(((UnitTreeObject)u).unit.getName());
			dsLabel.setText("("+  ((UnitTreeObject)u).unit.getNumberOfDataSets()+")");
			valuesPanel.setValues(((UnitTreeObject)u).values);
		}		
		else
		{
			nameLabel.setText(u.toString());
			dsLabel.setText("");
			valuesPanel.setValues(null);
		}
		return this;
	}
	
	static class ValuesComponent extends JComponent
	{
		public double[] values;
		private ColorGradient gradient;
		private boolean selected;

		public ValuesComponent(ColorGradient gradient)
		{
			this.gradient=gradient;
		}

		public double[] getValues() {
			return values;
		}

		public void setValues(double[] values) {
			this.values = values;
		}

		@Override
		public void paint(Graphics g) 
		{
			List<Color> colors=new ArrayList<Color>();
			g.fillRect(0, 0, getWidth(), getHeight());
			if(values==null) return;
			for(double d: values)
			{
				if(Double.isNaN(d))
				{
					colors.add(Color.white);
				}else
				{
					colors.add(gradient.mapValueToColor(d));
				}
			}
			if(selected)
				RendererTools.drawColorLineSelected((Graphics2D) g, colors, new Rectangle(0,0,getWidth(),getHeight()));
			else
				RendererTools.drawColorLine((Graphics2D) g, colors, new Rectangle(0,0,getWidth(),getHeight()));
		}
	}
}