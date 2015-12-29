package mayday.graphviewer.core.bag.tools;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIType;
import mayday.core.meta.types.StringListMIO;
import mayday.core.meta.types.StringMIO;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.MIGroupSetting;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.ModelHub;
import mayday.graphviewer.core.bag.BagComponent;
import mayday.graphviewer.core.bag.ComponentBag;
import mayday.vis3.graph.vis3.SuperColorProvider;
import mayday.vis3.plots.tagcloud.AbstractTagCloudFactory;
import mayday.vis3.plots.tagcloud.Tag;

@SuppressWarnings("serial")
public class BagTagCloud extends BagCentralComponent 
{
	private List<Tag> tags;
	private ModelHub hub;
	private JList tagList;
	private Map<DataSet, MIGroupSetting> mappings=new HashMap<DataSet, MIGroupSetting>();
	private Map<DataSet, BooleanHierarchicalSetting> activated=new HashMap<DataSet,BooleanHierarchicalSetting >();

	public BagTagCloud(ComponentBag bag, BagComponent comp) 
	{
		super(bag, comp);
		setLayout(new BorderLayout());
		List<Probe> probes=bag.getProbes();
		SuperColorProvider coloring=null;
		Map<ProbeList, Integer> map=new HashMap<ProbeList, Integer>();
		MultiHashMap<ProbeList, Probe> probeMap=new MultiHashMap<ProbeList, Probe>();
		hub=((GraphViewerPlot)comp.getParent()).getModelHub();

		for(Probe p: probes)
		{
			coloring=hub.getColorProvider(p.getMasterTable().getDataSet());
			ProbeList pl=coloring.getViewModel().getTopPriorityProbeList(p);
			probeMap.put(pl, p);
			if(map.containsKey(pl))
				map.put(pl, map.get(pl)+1);			
			else
				map.put(pl,1);
		}
		tags=AbstractTagCloudFactory.getLinearInstance().fromMap(map);
		for(Tag t: tags)
		{
			t.setProbes(probeMap.get((ProbeList)t.getTag()));
		}
		
		//		tagsLabel=new JLabel();
		tagList=new JList();
		tagList.setCellRenderer(new TagListCellRenderer());
		tagList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) 
			{
				List<Probe> probes=new ArrayList<Probe>();
				for(int i:tagList.getSelectedIndices())
				{
					probes.addAll( ((Tag)tags.get(i)).getProbes());
				}
				if(probes.isEmpty())
					return;
				selectProbes(probes);
			}});
		updateTags();
		JButton mioButton=new JButton(new MIOTagAction());
		JButton miListButton=new JButton(new MIOListTagAction());
		JButton plButton=new JButton(new ProbeListTagAction());
		JButton dsButton=new JButton(new DataSetTagAction());
		JPanel buttonPanel=new JPanel();
		buttonPanel.add(mioButton);
		buttonPanel.add(miListButton);
		buttonPanel.add(plButton);
		buttonPanel.add(dsButton);

		add(buttonPanel,BorderLayout.NORTH);
		add(new JScrollPane(tagList),BorderLayout.CENTER);
	}

	private void updateTags()
	{
		DefaultListModel model=new DefaultListModel();
		model.clear();
		Collections.sort(tags);
		Collections.reverse(tags);		
		for(Tag t:tags)
		{
			model.addElement(t);
		}
		tagList.setModel(model);
	}

	private class ProbeListTagAction extends AbstractAction
	{
		public ProbeListTagAction() {
			super("ProbeLists");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			List<Probe> probes=bag.getProbes();

			SuperColorProvider coloring=null;
			Map<ProbeList, Integer> map=new HashMap<ProbeList, Integer>();
			hub=((GraphViewerPlot)comp.getParent()).getModelHub();

			MultiHashMap<ProbeList, Probe> probeMap=new MultiHashMap<ProbeList, Probe>();
			for(Probe p: probes)
			{
				coloring=hub.getColorProvider(p.getMasterTable().getDataSet());
				ProbeList pl=coloring.getViewModel().getTopPriorityProbeList(p);
				probeMap.put(pl,p);
				if(map.containsKey(pl))
					map.put(pl, map.get(pl)+1);
				else
					map.put(pl,1);
			}
			tags=AbstractTagCloudFactory.getLinearInstance().fromMap(map);
			for(Tag t: tags)
			{
				t.setProbes(probeMap.get((ProbeList)t.getTag()));
			}
			updateTags();			
		}
	}

	private class DataSetTagAction extends AbstractAction
	{
		public DataSetTagAction() {
			super("DataSets");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			List<Probe> probes=bag.getProbes();
			Map<DataSet, Integer> map=new HashMap<DataSet, Integer>();
			MultiHashMap<DataSet, Probe> probeMap=new MultiHashMap<DataSet, Probe>();
			for(Probe p: probes)
			{
				DataSet pl=p.getMasterTable().getDataSet();
				probeMap.put(pl, p);
				if(map.containsKey(pl))
					map.put(pl, map.get(pl)+1);
				else
					map.put(pl,1);
			}
			tags=AbstractTagCloudFactory.getLinearInstance().fromMap(map);
			for(Tag t: tags)
			{
				t.setProbes(probeMap.get((DataSet)t.getTag()));
			}
			updateTags();	
		}
	}

	private class MIOTagAction extends AbstractAction
	{
		public MIOTagAction() {
			super("MI Group");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			HierarchicalSetting setting=new HierarchicalSetting("Configure Meta Information");
			for(DataSet dataSet: hub.getViewModel().getDataSet().getDataSetManager().getDataSets())
			{
				MIGroupSetting model=new MIGroupSetting(dataSet.getName(), null,null,dataSet.getMIManager(),true);
				model.setAcceptableClass(StringMIO.class);
				BooleanHierarchicalSetting activator=new BooleanHierarchicalSetting(dataSet.getName(), null, false);
				activator.addSetting(model);
				mappings.put(dataSet,model); 
				activated.put(dataSet, activator);
				setting.addSetting(activator);
			}
			SettingDialog dialog=new SettingDialog(null,"Meta Information",setting);
			dialog.showAsInputDialog();
			if(!dialog.closedWithOK())
			{
				return;
			}
			Map<String, Integer> map=new HashMap<String, Integer>();
			MultiHashMap<DataSet, Probe> dsMap=new MultiHashMap<DataSet, Probe>();
			MultiHashMap<String, Probe> probeMap=new MultiHashMap<String, Probe>();
			for(Probe p: bag.getProbes())
			{
				dsMap.put(p.getMasterTable().getDataSet(), p);
			}

			for(DataSet ds: activated.keySet())
			{
				if(activated.get(ds).getBooleanValue()==false)
					continue;
				if(mappings.get(ds).getMIGroup()==null)
					continue;
				MIGroup grp=mappings.get(ds).getMIGroup();

				for(Probe p: dsMap.get(ds))
				{
					if(grp.contains(p)){
						MIType mio=grp.getMIO(p);
						String s= mio.toString();
						probeMap.put(s, p);
						if(map.containsKey(s))
							map.put(s, map.get(s)+1);
						else
							map.put(s,1);
					}
				}
			}
			tags=AbstractTagCloudFactory.getLinearInstance().fromMap(map);
			for(Tag t: tags)
			{
				t.setProbes(probeMap.get((String)t.getTag()));
			}			
			
			updateTags();				
		}
	}

	private class MIOListTagAction extends AbstractAction
	{
		public MIOListTagAction() {
			super("String List MI");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			HierarchicalSetting setting=new HierarchicalSetting("Configure Meta Information");
			for(DataSet dataSet: hub.getViewModel().getDataSet().getDataSetManager().getDataSets())
			{
				MIGroupSetting model=new MIGroupSetting(dataSet.getName(), null,null,dataSet.getMIManager(),true);
				model.setAcceptableClass(StringListMIO.class);
				BooleanHierarchicalSetting activator=new BooleanHierarchicalSetting(dataSet.getName(), null, false);
				activator.addSetting(model);
				mappings.put(dataSet,model); 
				activated.put(dataSet, activator);
				setting.addSetting(activator);
			}
			SettingDialog dialog=new SettingDialog(null,"Meta Information",setting);
			dialog.showAsInputDialog();
			if(!dialog.closedWithOK())
			{
				return;
			}
			Map<String, Integer> map=new HashMap<String, Integer>();

			MultiHashMap<DataSet, Probe> dsMap=new MultiHashMap<DataSet, Probe>();
			MultiHashMap<String, Probe> probeMap=new MultiHashMap<String, Probe>();
			for(Probe p: bag.getProbes())
			{
				dsMap.put(p.getMasterTable().getDataSet(), p);
			}
			
			for(DataSet ds: activated.keySet())
			{
				if(activated.get(ds).getBooleanValue()==false)
					continue;
				if(mappings.get(ds).getMIGroup()==null)
					continue;
				MIGroup grp=mappings.get(ds).getMIGroup();

				for(Probe p: dsMap.get(ds))
				{

					if(grp.contains(p)){
						StringListMIO m=(StringListMIO)grp.getMIO(p);
						for(String s: m.getValue())
						{
							probeMap.put(s, p);
							if(map.containsKey(s))
								map.put(s, map.get(s)+1);
							else
								map.put(s,1);
						}
					}
				}
			}
			if(!map.isEmpty())
			{
					
				tags=AbstractTagCloudFactory.getLinearInstance().fromMap(map);
				for(Tag t: tags)
				{
					t.setProbes(probeMap.get(t.getTag().toString()));
				}
				updateTags();
			}
		}
	}

}
