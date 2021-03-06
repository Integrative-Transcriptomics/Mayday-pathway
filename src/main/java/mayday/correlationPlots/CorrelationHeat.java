package mayday.correlationPlots;

import jdk.nashorn.internal.scripts.JO;
import mayday.core.*;
import mayday.core.pluginrunner.ProbeListPluginRunner;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.pluma.PluginManagerException;
import mayday.vis3.PlotPlugin;
import mayday.vis3.model.Visualizer;
import mayday.vis3.plots.heatmap2.HeatMap;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
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

        assert probeLists.size() != 0;
        final int limit = 100;
        if (probeLists.get(0).getNumberOfProbes() > limit) {
            int x = JOptionPane.showConfirmDialog(null,
                    "You have selected a probelist with over " + limit +
                    "probes. If you correlate on Probes, Mayday might crash! " +
                            "Still continune?", "",
                    JOptionPane.OK_CANCEL_OPTION);
            if (x == JOptionPane.CANCEL_OPTION) {
                return null;
            }
        }
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
        DataSet ds = new DataSet();
        MasterTable tmt = new MasterTable(ds);
        ProbeList data = new ProbeList(ds, true);
        // Add Experiment Names + find longest column name length,
        // needed for lexicographic ordered insertion into probe-lists
        int nameLength = -1;
        for (int col=1; col < corMatrix.getColumnCount(); col++) {
            String current = corMatrix.getColumnName(col);
            tmt.addExperiment(new Experiment(tmt, current));
            if (current.length() > nameLength) {
                nameLength = current.length();
            }
        }
        // ask user if they wish to right pad the ids
        int diag = JOptionPane.showConfirmDialog(null,
                "Would you like to right pad experiment identifiers?\n" +
                "You probably want to do this if you have numeric identifiers. " +
                "Doing so ensures that the string comparison yields '_1' < '10'." +
                "\n(You still would have to choose sorting by identifiers from" +
                "the view menu)",
                "Right Padding ids?",
                JOptionPane.YES_NO_OPTION);
        boolean padd = diag == JOptionPane.YES_OPTION;
        // iterate over matrix
        // -1 because first column is meta info
        int nrExperiments = corMatrix.getColumnCount() - 1;
        for (int row=0; row < corMatrix.getRowCount(); row++) {
            // create probe
            String name = (String) corMatrix.getValueAt(row, 0);
            Probe p = new Probe(tmt, false);
            // right-justify name for lexi. order
            if(padd) {
                String filledName = String.format("%" + nameLength + "s",
                        name);
                p.setName(filledName);
            } else {
                p.setName(name);
            }

            data.addProbe(p);
            tmt.addProbe(p);
            // retrieve values
            double[] values = new double[nrExperiments];
            for (int i=0; i < nrExperiments; i++) {
                // +1 shift because of row index column
                values[i] = (double) corMatrix.getValueAt(row, i + 1);
            }
            p.setValues(values);
        }
        /*
         * Use Heatmap plugin to display 'correlation probe-list'
         */
        PluginInfo heatmap = PluginManager.getInstance().getPluginFromID(HeatMap.PluginID);
        ArrayList<ProbeList> pl = new ArrayList<>();
        pl.add(data);
        /*
         * Run Heatmap plugin with correlation data
         */
        ProbeListPluginRunner plpr = new ProbeListPluginRunner(heatmap, pl, ds);
        plpr.execute();
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
