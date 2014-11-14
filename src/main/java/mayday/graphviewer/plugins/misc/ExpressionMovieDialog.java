package mayday.graphviewer.plugins.misc;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.core.gui.MaydayDialog;
import mayday.core.gui.components.ExcellentBoxLayout;
import mayday.core.pluma.PluginInfo;
import mayday.graphviewer.core.GraphViewerPlot;

@SuppressWarnings("serial")
public class ExpressionMovieDialog extends MaydayDialog 
{
	protected Timer timer;
	
	protected JLabel currentExp;
	protected JLabel totalExp;
	protected JLabel currentExpName;
	protected SpinnerNumberModel delayModel;
	protected GraphViewerPlot canvas;
	protected TimerListener listener;
	
	public ExpressionMovieDialog(GraphViewerPlot canvas) 
	{
		super();
		this.canvas=canvas;
		setTitle("ExpressionMovie");
		setLayout(new ExcellentBoxLayout(true, 15));
		
		listener=new TimerListener(canvas);
		timer=new Timer(1000,listener);
		timer.setInitialDelay(0);
		
		delayModel=new SpinnerNumberModel(1000, 50, 5000, 50);
		delayModel.addChangeListener(new ChangeListener() 
		{
			public void stateChanged(ChangeEvent e) 
			{
				timer.setDelay((Integer) delayModel.getValue());
				
			}});
		
		Font largeFont=new Font(Font.MONOSPACED, Font.BOLD, 24);
		currentExp=new JLabel(prepareLabel(1));
		currentExp.setFont(largeFont);
		
		JLabel slash=new JLabel("/");
		slash.setFont(largeFont);
		
		totalExp=new JLabel(prepareLabel(canvas.getModelHub().getViewModel().getDataSet().getMasterTable().getNumberOfExperiments() ));
		totalExp.setFont(largeFont);
		
		Box expBox=Box.createHorizontalBox();
		expBox.add(currentExp);
		expBox.add(Box.createHorizontalStrut(15));
		expBox.add(slash);
		expBox.add(Box.createHorizontalStrut(15));
		expBox.add(totalExp);
		
		add(expBox);
		currentExpName=new JLabel(canvas.getModelHub().getViewModel().getDataSet().getMasterTable().getExperimentName(0));
		add(currentExpName);
		
		JSpinner delaySpinner=new JSpinner(delayModel);
		Box delayBox=Box.createHorizontalBox();
		delayBox.add(new JLabel("Delay:"));
		delayBox.add(Box.createHorizontalStrut(15));
		delayBox.add(delaySpinner);
		delayBox.add(Box.createHorizontalStrut(15));
		delayBox.add(new JLabel("ms"));
		
		add(delayBox);
		
		Box controlBox=Box.createHorizontalBox();
		controlBox.add(Box.createHorizontalGlue());
		controlBox.add(new JButton(new PlayAction("First",PluginInfo.getIcon("mayday/control/first.png"))));
		controlBox.add(new JButton(new PlayAction("Previous",PluginInfo.getIcon("mayday/control/previous.png"))));
		controlBox.add(new JButton(new PlayAction("Start",PluginInfo.getIcon("mayday/control/start.png"))));
		controlBox.add(new JButton(new PlayAction("Stop",PluginInfo.getIcon("mayday/control/stop.png"))));
		controlBox.add(new JButton(new PlayAction("Next",PluginInfo.getIcon("mayday/control/next.png"))));
		controlBox.add(new JButton(new PlayAction("Last",PluginInfo.getIcon("mayday/control/last.png"))));
		controlBox.add(Box.createHorizontalGlue());
//		PluginInfo.getIcon("mayday/sbgn/sidenodes2.png")
		add(controlBox);
		addWindowListener(new WindowHelper());
		pack();
	}
	
	protected String prepareLabel(int num)
	{
		String s=Integer.toString(num);
		while(s.length() < 3)
		{
			s=" "+s;
		}
		return s; 
	}
	
	protected void updateUIAndPlot()
	{
		canvas.getModelHub().getColorProvider().setExperiment(listener.getCurrent());
		canvas.updatePlotNow();
		currentExp.setText(prepareLabel(listener.getCurrent()+1));
		currentExpName.setText(canvas.getModelHub().getViewModel().getDataSet().getMasterTable().getExperimentName(listener.getCurrent()));
	}
	
	protected class TimerListener implements ActionListener
	{
		private int current;
		private int max;
		
		public TimerListener(GraphViewerPlot canvas) 
		{
			current=0;
			max=canvas.getModelHub().getViewModel().getDataSet().getMasterTable().getNumberOfExperiments();
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{			
			updateUIAndPlot();
			current++;
			if(current >=max)
			{
				current=0;
			}
		}

		public int getCurrent() {
			return current;
		}

		public void setCurrent(int current) {
			this.current = current;
		}	
		
		
	}
	
	protected class PlayAction extends AbstractAction
	{
		public PlayAction(String command, Icon icon) 
		{
			super("",icon);
			putValue(ACTION_COMMAND_KEY, command);
			putValue(SHORT_DESCRIPTION, command);
			
		}
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			if(e.getActionCommand().equals("Start"))
			{
				timer.start();
			}
			if(e.getActionCommand().equals("Stop"))
			{
				timer.stop();
			}
			if(e.getActionCommand().equals("First"))
			{
				timer.stop();
				listener.setCurrent(0);
				updateUIAndPlot();
			}
			if(e.getActionCommand().equals("Last"))
			{
				timer.stop();
				listener.setCurrent(canvas.getModelHub().getViewModel().getDataSet().getMasterTable().getNumberOfExperiments()-1);
				updateUIAndPlot();
			}
			if(e.getActionCommand().equals("Next"))
			{
				int ne=listener.getCurrent()+1;
				if(ne >=  canvas.getModelHub().getViewModel().getDataSet().getMasterTable().getNumberOfExperiments())
					return;
				timer.stop();
				listener.setCurrent(ne);
				updateUIAndPlot();
			}
			if(e.getActionCommand().equals("Previous"))
			{
				int ne=listener.getCurrent()-1;
				if(ne < 0)
					return;
				timer.stop();
				listener.setCurrent(ne);
				updateUIAndPlot();
			}
		}
	}
	
	protected class WindowHelper extends WindowAdapter
	{
		@Override
		public void windowClosed(WindowEvent e) 
		{
			timer.stop();
			dispose();
		}
	}
	
}
