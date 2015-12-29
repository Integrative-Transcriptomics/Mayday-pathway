package mayday.graphviewer.statistics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mayday.core.settings.SettingDialog;
import mayday.vis3.components.PlotScrollPane;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.gradient.gui.GradientPreviewPanel;
import mayday.vis3.gui.actions.ExportPlotAction;

@SuppressWarnings("serial")
public class MatrixPlotter extends JPanel
{
	private JTable table;
	private MatrixTableModel model;
	protected int boxSizeY = 15;
	private List<MatrixPlotterListener> listeners;
	private ResultSet result;

	protected final static int MINZOOM=1;
	protected final static int MAXZOOM=50;

	public MatrixPlotter(ResultSet res) 
	{
		result=res;
		setBackground(Color.white);
		setName(res.getTitle());
		model=new MatrixTableModel(res);
		table=new JTable(model){
			public Dimension getPreferredScrollableViewportSize() {
				return super.getPreferredScrollableViewportSize();
//				return getPreferredSize();
			}
		};

		table.getSelectionModel().addListSelectionListener(new MatrixSelectonListener());
		table.setBackground(Color.WHITE);
		table.setShowGrid(false);
		table.setIntercellSpacing(new Dimension(0,0));		
		setLayout(new BorderLayout());
		add(new PlotScrollPane(table),BorderLayout.CENTER);	
		table.setAutoCreateRowSorter(true);
		table.getTableHeader().setReorderingAllowed(false);
		listeners=new ArrayList<MatrixPlotterListener>();

		Dimension size = table.getPreferredScrollableViewportSize();
		table.setPreferredScrollableViewportSize (new Dimension(Math.min(getPreferredSize().width, size.width), size.height));

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
		table.getColumnModel().getColumn(0).setPreferredWidth(12);
		for(int i=1; i!=table.getColumnCount(); ++i)
			table.getColumnModel().getColumn(i).setPreferredWidth(24);
	}

	public MatrixPlotter(ResultSet res, DoubleTableRenderer renderer) 
	{
		this(res);
		table.setDefaultRenderer(Double.class, renderer);	
		table.setDefaultRenderer(String.class, renderer);	
		initPanel(renderer);

	}

	public MatrixPlotter(ResultSet res, ColorGradient grad) 
	{
		this(res);
		DoubleTableRenderer renderer=new DoubleTableRenderer(grad);
		table.setDefaultRenderer(Double.class, renderer);
		table.setDefaultRenderer(String.class, renderer);	
		initPanel(renderer);
		//		GradientPreviewPanel gpp=new GradientPreviewPanel(renderer.getGradient());
		//		add(gpp,BorderLayout.SOUTH);
	}

	private void initPanel(DoubleTableRenderer renderer)
	{
		JPanel panel=new JPanel(new FlowLayout());
		GradientPreviewPanel gpp=new GradientPreviewPanel(renderer.getGradient());
		panel.add(new JButton(new ResetSizeAction()));
		panel.add(gpp);
		panel.add(new JButton(new EditRendererAction(renderer)));
		panel.add(new JButton(new ExportPlotAction(table.getParent().getParent())));
		panel.add(new JButton(new ExportAction()));
		add(panel,BorderLayout.NORTH);
	}

	public static ColorGradient createGradient(ResultSet res)
	{
		double min=0;
		double max=0;
		if(res.getPreferredScope()==ResultScope.Groups)
		{
			min=res.groupMin();
			max=res.groupMax();
		}
		if(res.getPreferredScope()==ResultScope.Probes)
		{
			min=res.probeMin();
			max=res.probeMax();
		}
		ColorGradient grad=ColorGradient.createDefaultGradient(min,max);
		return grad;
	}

	private class MatrixSelectonListener implements ListSelectionListener
	{
		@Override
		public void valueChanged(ListSelectionEvent e) 
		{
			int[] selectedModelRows=new int[table.getSelectedRows().length];

			int i=0; 
			for(int r:table.getSelectedRows())
			{
				int mr= table.convertRowIndexToModel(r);
				selectedModelRows[i]=mr;
				++i;
			}

			for(MatrixPlotterListener l:listeners)
			{
				l.selectedRows(selectedModelRows);
			}
		}

	}

	public void addMatrixPlotterListener(MatrixPlotterListener listener)
	{
		listeners.add(listener);
	}

	public void removeMatrixPlotterListener(MatrixPlotterListener listener)
	{
		listeners.remove(listener);
	}

	public void zoom(boolean in, boolean x, boolean y) {
		int delta = in?5:-5;
		int ydelta = y?delta:0;
		boxSizeY+=ydelta;
		boxSizeY = (boxSizeY<MINZOOM)?MINZOOM:boxSizeY;
		boxSizeY = (boxSizeY>MAXZOOM)?MAXZOOM:boxSizeY;
		table.setRowHeight(boxSizeY);
	}

	private class EditRendererAction extends AbstractAction{

		private DoubleTableRenderer renderer;

		public EditRendererAction(DoubleTableRenderer renderer) {
			super("Configure Table");
			this.renderer=renderer;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			SettingDialog dialog=new SettingDialog(null, "Display Options", renderer.getSetting());
			dialog.showAsInputDialog();			
		}

	}
	
	private class ExportAction extends AbstractAction{

		public ExportAction() 
		{
			super("Export");
		}

		@Override
		public void actionPerformed(ActionEvent e) 
		{
			// ask for file
			JFileChooser chooser=new JFileChooser();
			int i=chooser.showSaveDialog(MatrixPlotter.this);

			// write file
			if(i == JFileChooser.APPROVE_OPTION) 
			{
				try {
					BufferedWriter w=new BufferedWriter(new FileWriter(chooser.getSelectedFile()));
					if(result.getPreferredScope()==ResultScope.Groups)
						w.write(result.getGroupResults().toString());
					if(result.getPreferredScope()==ResultScope.Probes)
						w.write(result.getProbeResults().toString());
					w.close();
				} catch (Exception e1) 
				{
					throw new RuntimeException("Error exporting statistics",e1);
				}
			}		
		}		
	}



	private class ResetSizeAction extends AbstractAction {

		public ResetSizeAction() {
			super("Reset Size");

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			boxSizeY=12;
			table.setRowHeight(boxSizeY);
			table.getColumnModel().getColumn(0).setPreferredWidth(12);
			for(int i=1; i!=table.getColumnCount(); ++i)
				table.getColumnModel().getColumn(i).setPreferredWidth(24);
		}

	}

}
