package mayday.experimentCorrelation;

import java.util.HashMap;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.gui.AbstractTableWindow;
import mayday.vis3.model.Visualizer;
import mayday.vis3.tables.TablePlugin;

/**
 * 
 * @author Alexander Stoppel
 * Source: compare level2.mayday.vis3.plots.distancematrix (Jennifer Lange)
 */

public class ExperimentCorrelationPlugin extends TablePlugin {

	public void init() {}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				(Class)this.getClass(),
				"IT.vis3.ExperimentCorrelationMatrix",
				new String[0],
				MC,
				new HashMap<String, Object>(),
				"Alexander Stoppel",
				"stoppel@informatik.uni-tuebingen.de",
				"Displays experiment-experiment correlation for all experiments as table",
				"Experiment Correlation"
				);
		//To be adapted
		pli.setIcon("mayday/vis3/DistanceMatrix128.png");
		pli.addCategory("Tables");
		return pli;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public AbstractTableWindow getTableWindow(Visualizer viz) {
		return new ExperimentCorrelationWindow(viz);
	}
}
