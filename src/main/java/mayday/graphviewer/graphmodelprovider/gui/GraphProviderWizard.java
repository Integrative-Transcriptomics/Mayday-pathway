package mayday.graphviewer.graphmodelprovider.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.core.gui.MaydayDialog;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingComponent;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.structures.maps.MultiHashMap;
import mayday.core.tasks.AbstractTask;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.graphmodelprovider.AbstractGraphModelProvider;
import mayday.graphviewer.graphmodelprovider.GraphModelProvider;
import mayday.graphviewer.gui.ProbeListSelector;

@SuppressWarnings("serial")
public class GraphProviderWizard extends MaydayDialog 
{
	private int state=-1; 
	private GraphModelProvider provider;
	
	
	private GraphViewerPlot viewer;
	private JPanel centerComponent;
	private JPanel bottomComponent; 
	private JLabel topLabel;

	private ProbeListSelector probeListSelector;

	private Setting basicSetting;
	private SettingComponent basicSettingComponent;

	private Setting informedSetting;
	private SettingComponent informedSettingComponent;

	private CardLayout layout=new CardLayout();
	private boolean cancelled=false;
	
	private ObjectSelectionSetting<GraphModelProvider> providerSetting;
	private BooleanSetting addSetting; 
	private HierarchicalSetting l0Setting=new HierarchicalSetting("Import Settings");
	private SettingComponent providerSettingComponent;
	private MultiHashMap<DataSet, ProbeList> initialProbeLists;
	
	public GraphProviderWizard(GraphViewerPlot viewer, MultiHashMap<DataSet, ProbeList> pls) 
	{
		super(viewer.getOutermostJWindow());
		this.viewer=viewer;
		setTitle("Create new Graph");
		setModal(true);
		setLayout(new BorderLayout());

		bottomComponent=new JPanel(new FlowLayout());
		bottomComponent.add(new JButton(new CancelAction()));
		bottomComponent.add(new JButton(new NextAction()));


		topLabel=new JLabel();
		topLabel.setFont(new Font(Font.SANS_SERIF,Font.ITALIC,20));

		add(topLabel,BorderLayout.NORTH);
		add(bottomComponent,BorderLayout.SOUTH);

		centerComponent=new JPanel(layout);
		centerComponent.setPreferredSize(new Dimension(600, 400));
		add(centerComponent,BorderLayout.CENTER);

		Set<PluginInfo> plugins = PluginManager.getInstance().getPluginsFor(GraphModelProvider.MC);
		Set<AbstractGraphModelProvider> predef = new HashSet<AbstractGraphModelProvider>();
		for(PluginInfo pluginInfo:plugins) 
		{
			AbstractPlugin plugin = pluginInfo.getInstance();
			if(plugin instanceof AbstractGraphModelProvider) 
			{
				predef.add((AbstractGraphModelProvider)plugin);
			}
		}		
		AbstractGraphModelProvider[] predefs=(AbstractGraphModelProvider[]) predef.toArray(new AbstractGraphModelProvider[predef.size()]);	
		Arrays.sort(predefs, new AbstractPlugin.AbstractPluginInstanceComparator());
		providerSetting=new ObjectSelectionSetting<GraphModelProvider>("Graph Creation Method", null,0,predefs);
		addSetting=new BooleanSetting("Append Graph", "If checked, the current graph will be appended by the new one\\else the current graph is cleared", false);
		l0Setting.addSetting(providerSetting).addSetting(addSetting);
		initialProbeLists=pls;		
		nextState();
		pack();
	}

	private void nextState()
	{
		if(state==-1)
		{
			state0();
			return;
		}			
		if(state==0)
		{
			boolean updated=providerSettingComponent.updateSettingFromEditor(false);
			if(!updated)
				return;
			provider=providerSetting.getObjectValue();
			provider.setProbeLists(initialProbeLists);
			setTitle(provider.getName() + " - Create new Graph");
			if(provider.getBasicSetting()!=null)
			{
				state1();
				return;
			}
			if(provider.isAskForProbeLists())
			{
				state2();
				return;
			}
			if(provider.getInformedSetting()!=null)
			{
				state3();
				return;
			}
			parseFile();
			if(provider.isAskForFileSetting())
			{
				state4();
				return;
			}
		}
		if(state==1)
		{
			boolean updated=basicSettingComponent.updateSettingFromEditor(false);
			if(!updated)
				return;

			if(provider.isAskForProbeLists())
			{
				state2();
				return;
			}
			if(provider.getInformedSetting()!=null)
			{
				state3();
				return;
			}
			parseFile();
			if(provider.isAskForFileSetting())
			{
				
				state4();
				return;
			}
			done();
			return;
		}

		if(state==2)
		{			
			List<ProbeList> res=probeListSelector.getSelectedProbeLists();
			if(res.isEmpty())
			{
				JOptionPane.showMessageDialog(this, "Please select at least one Probe List", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			MultiHashMap<DataSet, ProbeList> probeLists=new MultiHashMap<DataSet, ProbeList>();
			for(ProbeList pl:res)
			{
				probeLists.put(pl.getDataSet(), pl);
			}			
			
			provider.setProbeLists(probeLists);
			if(provider.getInformedSetting()!=null)
			{
				state3();
				return;
			}
			parseFile();
			if(provider.isAskForFileSetting())
			{				
				state4();
				return;
			}
			done();
			return;
		}
		if(state==3)
		{
			boolean updated=informedSettingComponent.updateSettingFromEditor(false);
			if(!updated)
				return;
			parseFile();
			if(provider.isAskForFileSetting())
			{
				
				state4();
				return;
			}
			done();
			return;
		}		
		if(state==4)
		{	
			provider.updateFileSettings();
			done();
			return;
		}

	}

	private void done()
	{
		buildGraphModel();
		dispose();
	}
	
	private void state0()
	{
		state=0;
		providerSettingComponent=l0Setting.getGUIElement();
		centerComponent.add(providerSettingComponent.getEditorComponent(),"0");		
		layout.next(centerComponent);
		topLabel.setText("Step 1/5: Choose Method");
	}

	private void state1()
	{
		state=1;
		basicSetting=provider.getBasicSetting();
		basicSettingComponent=basicSetting.getGUIElement();		
		centerComponent.add(basicSettingComponent.getEditorComponent(),"1");
		layout.next(centerComponent);

		topLabel.setText("2/5: Configure Graph");
	}

	private void state2()
	{
		state=2;
		topLabel.setText("3/5: Select Probe Lists");

		List<DataSet> dataSets=viewer.getModelHub().getViewModel().getDataSet().getDataSetManager().getDataSets();
		probeListSelector=new ProbeListSelector(dataSets,viewer.getModelHub().getViewModel().getProbeLists(false));

		centerComponent.add(probeListSelector,"2");
		layout.show(centerComponent,"2");

	}

	private void state3()
	{
		state=3;
		topLabel.setText("4/5: Configure Probe Lists");
		informedSetting=provider.getInformedSetting();
		informedSettingComponent=informedSetting.getGUIElement();		
		centerComponent.add(informedSettingComponent.getEditorComponent(),"3");
		layout.next(centerComponent);
	}

	private void state4()
	{	
		state=4;
		topLabel.setText("5/5: Additional Configuration");
		centerComponent.add(provider.getAdditionalComponent(),"4");
		layout.next(centerComponent);		
	}

	private void buildGraphModel()
	{
		AbstractTask t=provider.buildGraph();
		t.start();
		t.waitFor();
	}

	private void parseFile()
	{
		AbstractTask t=provider.parseFile();
		if(t!=null)
		{
			t.start();
			t.waitFor();
		}
		
	}

	private class NextAction extends AbstractAction
	{
		public NextAction() 
		{
			super("Next");
		}

		@Override
		public void actionPerformed(ActionEvent e) 
		{
			nextState();

		}
	}

	private class CancelAction extends AbstractAction
	{
		public CancelAction() 
		{
			super("Cancel");
		}

		@Override
		public void actionPerformed(ActionEvent e) 
		{
			cancelled=true;
			dispose();			
		}
	}
	
	public boolean isCancelled() 
	{
		return cancelled;
	}

	public GraphModelProvider getProvider() 
	{
		return provider;
	}
	
	public boolean isAdd()
	{
		return addSetting.getBooleanValue();
	}

}
