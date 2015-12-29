package mayday.graphviewer.core.bag.tools;

import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mayday.graphviewer.core.bag.BagComponent;
import mayday.graphviewer.core.bag.ComponentBag;

@SuppressWarnings("serial")
public class BagSimpleOverview extends BagCentralComponent
{
	
	private Font h1=new Font(Font.SANS_SERIF, Font.BOLD, 40);
	
	public BagSimpleOverview(ComponentBag bag, BagComponent comp) 
	{
		super(bag, comp);
		JPanel panel=new JPanel(new GridLayout(2, 1));
		panel.setPreferredSize(bag.getBoundingRect().getSize());
		panel.setBorder(BorderFactory.createTitledBorder("Overview"));
		JLabel lab1=new JLabel(bag.size()+" Nodes");
		JLabel lab2=new JLabel(bag.getProbes().size()+" Probes");
		lab1.setFont(h1);
		lab2.setFont(h1);
		panel.add(lab1);
		panel.add(lab2);
		add(panel);
	}

}
