package mayday.correlationPlots;

import mayday.core.*;
import mayday.core.pluginrunner.ProbeListPluginRunner;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.PlotPlugin;
import mayday.vis3.VisualizationMenu;
import mayday.vis3.model.Visualizer;
import mayday.vis3.plots.heatmap2.HeatMap;

import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.List;
import java.util.*;

/**
 * Plugin for drawing Correlation HeatMaps.
 */
public class CorrelationHeat extends PlotPlugin {

    @Override
    public Component getComponent() {
        return null;
    }

    @Override
    public java.util.List<ProbeList> run(java.util.List<ProbeList> probeLists,
                                         MasterTable masterTable) {
        /*
         * Compute Correlation Matrix
         */
        Visualizer viz = new Visualizer(masterTable.getDataSet(), probeLists);
        ExperimentCorrelationComponent ecc = new ExperimentCorrelationComponent(viz);
        //ecc.init();
        TableModel corMatrix = ecc.getModel();
        /*
         * Transform matrix to a Probe-List
         */
        ProbeList data = new ProbeList(masterTable.getDataSet(), true);
        // Add Experiment Names
        // -1 because first column is meta info
        int nrExperiments = corMatrix.getColumnCount() - 1;
        // retrieve values
        for (int row=0; row < corMatrix.getRowCount(); row++) {
            String name = (String) corMatrix.getValueAt(row, 0);
            // link of p to masterTable will not distube as long as we do not directly add it
            Probe p = new Probe(masterTable, false);
            p.setName(name);
            double[] values = new double[nrExperiments];
            for (int i=0; i < nrExperiments; i++) {
                // +1 shift because of name column
                values[i] = (double) corMatrix.getValueAt(row, i + 1);
            }
            p.setValues(values);
            data.addProbe(p);
        }
        /*
         * Use Heatmap plugin to display 'correlation probe-list'
         */
        PluginInfo heatmap = PluginManager.getInstance().getPluginFromID(HeatMap.PluginID);
        ArrayList<ProbeList> pl = new ArrayList<>();
        pl.add(data);
        VisualizationMenu.runVisPlugin(heatmap, pl);
        // TODO find heatmap plugin
        return null;
    }


    @Override
    public PluginInfo register() throws PluginManagerException {
        PluginInfo pli = new PluginInfo(
                (Class)this.getClass(),
                "IT.vis3.CorrelationHeat",
                new String[0],
                MaydayDefaults.Plugins.CATEGORY_PLOT,
                new HashMap<String, Object>(),
                "Adrian Geissler",
                "geissler@uni-tuebingen.de",
                "Visualize correlation between Probes or Experiments in a HeatMap",
                "Correlation HeatMap"
        );
        //To be adapted
        pli.setIcon("mayday/vis3/heatmap128.png");
        pli.addCategory("Miscellaneous");
        return pli;
    }

    @Override
    public void init() {

    }
}
