package mayday.experimentCorrelation;


import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JSeparator;

import mayday.core.settings.Setting;
import mayday.vis3.gui.AbstractTableWindow;
import mayday.vis3.gui.PlotComponent;
import mayday.vis3.gui.actions.ExportTableAction;
import mayday.vis3.gui.actions.GoToProbeAction;
import mayday.vis3.model.Visualizer;
/**
 * 
 * @author Alexander Stoppel
 * Source: compare level2.mayday.vis3.plots.distancematrix (Jennifer Lange)
 */

@SuppressWarnings("serial")
public class ExperimentCorrelationWindow extends AbstractTableWindow<ExperimentCorrelationComponent> {

	public ExperimentCorrelationWindow(Visualizer viz) {
		super(viz,"Experiment Correlation Matrix Table");
		for (Setting s : tabular.getSettings())
			addViewSetting(s, null);
				
		JMenu settings = getMenu(VIEW_MENU, (PlotComponent)null);
		settings.add(new GoToProbeAction() {
			public boolean goToProbe(String probeIdentifier) {
				return tabular.goToProbe(probeIdentifier);
			}
		});
		settings.add(new JumpToSelectionAction());
	}
	
	public String getPreferredTitle() {
		return "Experiment Correlation";
	}
	
	@Override
	protected ExperimentCorrelationComponent createTableComponent() {
		return new ExperimentCorrelationComponent(visualizer);
	}

	@Override
	protected void goToProbe(String name) {
		tabular.goToProbe(name);
	}
	
	protected boolean manageExperimentSelection() {
		return false;
	}

	protected boolean manageProbeSelection() {
		return true;
	}

	protected JMenu makeFileMenu() {
		JMenu table = new JMenu("Experiment Correlation Matrix");
		//changed from 'D' to 'C'
		table.setMnemonic('C');		
		table.add(new ExportTableAction(tabular, getViewModel()));
		table.add(new JSeparator());
		table.add(new AbstractAction("Close") {
			public void actionPerformed(ActionEvent e) {
				visualizer.removePlot(ExperimentCorrelationWindow.this);
			}
		});
		return table;
	}
}
