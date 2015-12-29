package mayday.graphviewer.core.bag.tools;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.graphviewer.core.bag.BagComponent;

@SuppressWarnings("serial")
public class BagDisplayAction extends AbstractAction 
{
	public enum BagDisplayTargetComponent{
		Overview, TagCloud, HeatMap, Probes
	}

	private BagDisplayTargetComponent comp;
	private BagComponent target;

	public BagDisplayAction(BagComponent target, BagDisplayTargetComponent c) 
	{
		super(c.toString());
		comp=c;
		this.target=target;
	}


	@Override
	public void actionPerformed(ActionEvent e) 
	{
		BagCentralComponent cmp=null;
		switch(comp)
		{
		case Overview: cmp=new BagSimpleOverview(target.getBag(), target);
			break;
		case HeatMap: cmp=new BagHeatmap(target.getBag(), target);
			break;
		case Probes: cmp=new BagProbeOverview(target.getBag(), target);
			break;
		case TagCloud: cmp=new BagTagCloud(target.getBag(), target);
			break;
		}
		target.getBag().hide();
		target.setCentral(cmp);		
	}

}
