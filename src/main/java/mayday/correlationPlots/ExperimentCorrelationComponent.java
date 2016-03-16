package mayday.correlationPlots;
import java.lang.reflect.Method;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import mayday.core.*;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.graphviewer.statistics.Correlations;
import mayday.vis3.model.Visualizer;
import mayday.vis3.tables.AbstractTabularComponent;
//import mayday.vis3.tables.AbstractTabularComponent.IdentifierTableCellRenderer;

@SuppressWarnings("serial")
public class ExperimentCorrelationComponent extends AbstractTabularComponent implements ProbeListListener
{
	double[][] corMatrix;

	String metric;
	boolean correlateExperiments;


	/**
	 * @author stoppel
	 */
	public double[][] transposeMatrix(double [][] matrix){
		double[][] transposed = new double[matrix[0].length][matrix.length];
		for (int i = 0; i < matrix.length; i++)
			for (int j = 0; j < matrix[0].length; j++)
				transposed[j][i] = matrix[i][j];
		return transposed;

		}
	
	/**
	 * @author stoppel
	 */
	protected double getCorrelation(double[] vecA, double vecB[], String setting){
		double res = 0;
		//evtl weiter Korrelationsmaße
		if (vecA.length != vecB.length) 
		{
			throw new IllegalArgumentException("Dimensions of the vectors are different");
		}
		switch (setting) {
			case "pearson":
			res = Correlations.cor(vecA, vecB);
			break;
			
			case "spearman":
			res = Correlations.spearman(vecA, vecB);
			break;
			
			case "covariance":
			res = Correlations.cov(vecA, vecB);
			break;	
			default:
			break;
		}
		return res;
	}
	
	/**
	 * @author stoppel
	 */
	protected double[][] getCorrelationMatrix(double[][] matrix){
	
		double[][] res = new double[matrix.length][matrix.length];
	
		for(int i = 0; i < matrix.length; i++){ //iterate over experiments
			for(int j = 0; j < matrix.length; j++){ //for each experiment calculate correlation to each other
				res[i][j] = getCorrelation(matrix[i], matrix[j], metric);
			}
		}
		corMatrix = res;
		return res;
	}
	
	/**
	 * @author stoppel
	 */
	protected double[][] getOriginalProbeMatrix(){
		int i = 0; 
		double[][] temp = new double[probes.size()][probes.get(1).getNumberOfExperiments()];
		for(Probe pb :probes){
			temp[i] = pb.getValues();
			i++;
		}

		if (correlateExperiments) {
			return transposeMatrix(temp);
		} else {
			return temp;
		}
	}

	/**
	 * @author stoppel
	 */
	protected void fillRow(Object[] row, Probe db, int k) {
		fillRow(row, k);
	}
	
	/**
	 * @author stoppel
	 */
	private void fillRow(Object[] row, int k) {
		for(int j = 0; j < corMatrix[k].length; j++){
			//System.out.println("J: " + j);
			if(k < corMatrix.length){
				row[j+1] = corMatrix[k][j];
			}
		}
	}

	/**
	 * Ask user for settings
	 */
	private void askSettings() {
		HierarchicalSetting hs = new HierarchicalSetting("Settings");

		// ask user for correlation metric
		ObjectSelectionSetting<String> method
				= new ObjectSelectionSetting<>("Correlation metric", null,
				0, new String[] {"pearson", "spearman", "covariance"});

		// ask wheter to correlate experiments or probes
		BooleanSetting bs = new BooleanSetting("Correlate Experiments", null, true);

		hs.addSetting(method).addSetting(bs);

		SettingDialog sd = new SettingDialog(null, "Settings", hs);
		sd.showAsInputDialog();
		metric = method.getObjectValue();
		correlateExperiments = bs.getBooleanValue();
	}

	/**
	 * Initializes the view.
	 * Overriden by Alexander Stoppel
	 */
	@Override
	protected void init()
	{
		askSettings();
		// columns are set to their preferred width
		setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
		
		DefaultTableModel l_tableData = new DefaultTableModel(); // first index: row, second index: column
		
		// add columns to the model
		addColumns(l_tableData);

		Object[] l_row = new Object[l_tableData.getColumnCount()];
		
		corMatrix = getCorrelationMatrix(getOriginalProbeMatrix());
		for(int i = 0; i < corMatrix.length; i++){
			MasterTable mt = visualizer.getViewModel().getDataSet().getMasterTable();
			if (correlateExperiments) {
				l_row[0] = mt.getExperimentDisplayName( i );
			} else {
				l_row[0] = probes.get(i).getDisplayName();
			}

			fillRow(l_row, null, i);
			l_tableData.addRow( l_row );   
		}
		
		isSilent=true;
		setModel( l_tableData );
		isSilent=true;
		
		TableColumn l_identifierColumn = getColumnModel().getColumn( PROBECOL );
		l_identifierColumn.setCellRenderer( new IdentifierTableCellRenderer() );
		
		try { // try to modify table headers to show tooltips
			Method m = JTableHeader.class.getDeclaredMethod("createDefaultRenderer");
			m.setAccessible(true);
			JTableHeader jth = getTableHeader();
			
			for (int i=PROBECOL+1; i!=l_tableData.getColumnCount(); ++i) {
				TableColumn tc = getColumnModel().getColumn(i);
				JLabel jl = (JLabel)m.invoke(jth);
				jl.setToolTipText( getToolTip(i-(PROBECOL+1))) ;
				tc.setHeaderRenderer((TableCellRenderer)jl);			
			}	
		} catch (Exception dieKraetze) {
			// too bad, no tooltips for you sir.
		}
	}

	
	public ExperimentCorrelationComponent( Visualizer visualizer )  {
		super(visualizer);
		// listen to all pl's for coloring
		visualizer.getViewModel().addRefreshingListenerToAllProbeLists(this, false);

	}
	
	protected void addColumns(DefaultTableModel tm) {
		MasterTable masterTable = visualizer.getViewModel().getDataSet().getMasterTable();
		if (correlateExperiments) {
			tm.addColumn( "Experiment #" );
			for (int i = 0; i < masterTable.getNumberOfExperiments(); ++i) {
				tm.addColumn(masterTable.getExperimentDisplayName(i));
			}
		} else {
			tm.addColumn( "ProbeSets" );
			for (int i = 0; i < probes.size(); i++) {
				tm.addColumn(probes.get(i).getDisplayName());
			}
		}
	}
	

	
	protected class IdentifierTableCellRenderer extends DefaultTableCellRenderer
	{
		public void setValue( Object value ) { 
			if ( value instanceof Probe ) {
				Probe l_probe = (Probe)value; 
				setForeground( coloring.getColor(l_probe) );
				if (displayNames)
					setText( l_probe.getDisplayName() );
				else 
					setText( l_probe.getName() );
			} else { 
				super.setValue(value); 
			} 
		}
	}

	public void probeListChanged(ProbeListEvent event) {
		if (event.getChange()==ProbeListEvent.LAYOUT_CHANGE)
			repaint();
		/* Handle content change differently, by listening to the viewmodel */
	}
	
	@Override
	public void removeNotify() {
		super.removeNotify();
		visualizer.getViewModel().removeRefreshingListenerToAllProbeLists(this);	
	}

	@Override
	protected String getToolTip(int column) {
		return visualizer.getViewModel().getDataSet().getMasterTable().getExperimentName(column);
	}
}