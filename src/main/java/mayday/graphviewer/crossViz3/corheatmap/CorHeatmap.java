package mayday.graphviewer.crossViz3.corheatmap;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.ModelHub;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.crossViz3.experiments.IExperimentMapping;
import mayday.graphviewer.crossViz3.probes.IProbeMapping;
import mayday.vis3.export.ExportDialog;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.gradient.ColorGradient.MIDPOINT_MODE;
import mayday.vis3.gradient.agents.Agent_Tricolore;
import mayday.vis3.gradient.gui.GradientPreviewPanel;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.plots.termpyramid.AlignedLabelRenderer;
import mayday.vis3.plots.termpyramid.HeatStreamCellRenderer;

@SuppressWarnings("serial")
public class CorHeatmap extends JPanel
{
	private ModelHub hub;
	private GraphViewerPlot viewer;
	
	private ObjectSelectionSetting<DataSet> leftSetting;
	private ObjectSelectionSetting<DataSet> rightSetting;
	private RestrictedStringSetting corSetting;
	private HierarchicalSetting setting;
	private SettingComponent settingComponent;
	static final String[] CORRELATION_MEASURES={"Pearson", "Spearman", "Kendall"};
	private ColorGradient correlationGradient;

	private JTable table;
	private CorHeatMapModel model; 
	private HeatStreamCellRenderer leftRenderer;
	private HeatStreamCellRenderer rightRenderer;
	
	protected final static int MINZOOM=2;
	protected final static int MAXZOOM=50;
	protected int boxSizeY = 15;
	
	public CorHeatmap(GraphViewerPlot viewer, ModelHub hub, IProbeMapping probeMapping, IExperimentMapping expMapping) 
	{
		this.hub=hub;
		this.viewer=viewer;
		DataSet[] ds=(DataSet[]) hub.getDataSets().toArray(new DataSet[hub.getDataSets().size()]);
		leftSetting=new ObjectSelectionSetting<DataSet>("Left Dataset",null,0,ds);
		
		rightSetting=new ObjectSelectionSetting<DataSet>("Right Dataset",null,ds.length>1?1:0,ds);
		DataSetSettingListener dsl=new DataSetSettingListener();
		leftSetting.addChangeListener(dsl);
		rightSetting.addChangeListener(dsl);
		
		corSetting=new RestrictedStringSetting("Correlation measure", null, 0, CORRELATION_MEASURES);
		corSetting.addChangeListener(new SettingChangeListener() {
			
			@Override
			public void stateChanged(SettingChangeEvent e) {
				model.setCorrelationMeasure(corSetting.getStringValue());
				model.fireTableDataChanged();
				setTableHeader();
			}
		});
		
		correlationGradient=new ColorGradient(-1, 0, 1, true, 100, MIDPOINT_MODE.Center, new Agent_Tricolore(false, Color.BLUE, Color.white, Color.red, 1.0));
		
		leftRenderer=new HeatStreamCellRenderer(hub.getColorProvider(leftSetting.getObjectValue()));
		rightRenderer=new HeatStreamCellRenderer(hub.getColorProvider(rightSetting.getObjectValue()));
		
		
		model=new CorHeatMapModel(corSetting.getStringValue(), leftSetting, rightSetting, probeMapping, expMapping);
		table=new JTable(model);		
		setTableHeader();
		
		table.setAutoCreateRowSorter(true);
		table.getSelectionModel().addListSelectionListener(new TableListener());
		setLayout(new BorderLayout());
		add(new JScrollPane(table),BorderLayout.CENTER);
		add(new GradientPreviewPanel(correlationGradient), BorderLayout.SOUTH);
		
		setting=new HierarchicalSetting("Correlation Heatmap");
		setting.addSetting(leftSetting).addSetting(rightSetting).addSetting(corSetting);
				
		table.addMouseWheelListener(new MouseWheelListener() 
		{
			public void mouseWheelMoved(MouseWheelEvent e) 
			{
				int CONTROLMASK = getToolkit().getMenuShortcutKeyMask();
				if ((e.getModifiers()&CONTROLMASK) == CONTROLMASK) 
				{
					boolean zoomWidth = !e.isShiftDown();
					if (e.getWheelRotation()>0)
						zoom(false,zoomWidth,true);
					else 
						zoom(true,zoomWidth,true);
				}
			}			
		});
		table.setShowGrid(false);		
		table.setRowMargin(0);
		
		JPanel northPanel=new JPanel();
		northPanel.setLayout(new GridLayout(2, 1));
		settingComponent=setting.getGUIElement();
		northPanel.add(settingComponent.getEditorComponent(), BorderLayout.CENTER);
		JPanel buttonPanel=new JPanel();
		
		buttonPanel.add(new JButton(new UpdateAction()));
		buttonPanel.add(new JButton(new ExportAction()));
		northPanel.add(buttonPanel,BorderLayout.SOUTH);
		
		add(northPanel, BorderLayout.NORTH);
	}
	
	
	
	private void updateRenderers()
	{
		leftRenderer.setColoring(hub.getColorProvider(leftSetting.getObjectValue()));
		rightRenderer.setColoring(hub.getColorProvider(rightSetting.getObjectValue()));
		setTableHeader();
	}
	
	public void setTableHeader()
	{
		DefaultTableColumnModel columnModel=new DefaultTableColumnModel();	

		TableColumn col0=new TableColumn(0, 100, new AlignedLabelRenderer(SwingConstants.RIGHT),null);		
		TableColumn col1=new TableColumn(1, 250, leftRenderer,null);
		TableColumn col2=new TableColumn(2, 50, new CorRenderer(correlationGradient),null);
		TableColumn col3=new TableColumn(3, 250, rightRenderer,null);			

		col0.setHeaderValue(model.getColumnName(0));
		col1.setHeaderValue(leftSetting.getObjectValue().getName());
		col2.setHeaderValue(corSetting.getStringValue());
		col3.setHeaderValue(rightSetting.getObjectValue().getName());

		columnModel.addColumn(col0);
		columnModel.addColumn(col1);
		columnModel.addColumn(col2);
		columnModel.addColumn(col3);
		
		table.setColumnModel(columnModel);	
		JTableHeader header= new JTableHeader(columnModel);
		table.setTableHeader(header);
		table.getTableHeader().setReorderingAllowed(false);
	}
	
	public void zoom(boolean in, boolean x, boolean y) {
		int delta = in?5:-5;
		int ydelta = y?delta:0;
		boxSizeY+=ydelta;
		boxSizeY = (boxSizeY<MINZOOM)?MINZOOM:boxSizeY;
		boxSizeY = (boxSizeY>MAXZOOM)?MAXZOOM:boxSizeY;
		table.setRowHeight(boxSizeY);
	}
	
	
	private class DataSetSettingListener implements SettingChangeListener
	{
		@Override
		public void stateChanged(SettingChangeEvent e) 
		{
			updateRenderers();
//			model.fireTableDataChanged();
			
			model.setCorrelationMeasure(corSetting.getStringValue());
			model.fireTableDataChanged();
			setTableHeader();
		}
	}
	
	private class UpdateAction extends AbstractAction 
	{
		public UpdateAction() 
		{
			super("Update");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			settingComponent.updateSettingFromEditor(true);			
		}
	}
	
	private class ExportAction extends AbstractAction
	{
		public ExportAction() {
			super("Export");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			new ExportDialog(table, false);		
		}
	}
	
	
	private class TableListener implements ListSelectionListener
	{
		@SuppressWarnings("unchecked")
		@Override
		public void valueChanged(ListSelectionEvent e) 
		{
			if(e.getFirstIndex() > e.getLastIndex())
				return;
			
			SuperModel m=(SuperModel) viewer.getModel();
			viewer.getSelectionModel().clearSelection();
					
			for(int i:table.getSelectedRows())
			{
				Probe l=((List<Probe>)table.getValueAt(i, 1)).get(0);
				Probe r=((List<Probe>)table.getValueAt(i, 3)).get(0);	
				
				for(MultiProbeComponent comp: m.getComponents(l))
				{
					viewer.getSelectionModel().select(comp);
				}
				for(MultiProbeComponent comp: m.getComponents(r))
				{
					viewer.getSelectionModel().select(comp);
				}				
			}			
		}
	}
}
