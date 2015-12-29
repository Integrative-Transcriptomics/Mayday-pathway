package mayday.graphviewer.core.bag.tools;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.graphviewer.core.bag.BagComponent;
import mayday.graphviewer.statistics.ResultSet;

@SuppressWarnings("serial")
public class BagDisplayStatisticsAction extends AbstractAction {

	private BagComponent target;
	private ResultSet res;

	
	public BagDisplayStatisticsAction(BagComponent target, ResultSet res) {
		super(res.getTitle());
		this.target = target;
		this.res = res;
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		target.getBag().hide();
		BagStatisticPanel bsp=new BagStatisticPanel(res, target.getBag(), target);
		target.setCentral(bsp);
	}

}
