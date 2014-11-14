package mayday.graphviewer.plugins.connect;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mayday.core.Probe;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;
import mayday.core.meta.NumericMIO;
import mayday.core.meta.types.DoubleMIO;
import mayday.core.meta.types.StringListMIO;
import mayday.core.meta.types.StringMIO;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.SelectableHierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.MIGroupSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.core.structures.graph.Edge;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.graphviewer.plugins.AbstractGraphViewerPlugin;
import mayday.graphviewer.plugins.GraphViewerPlugin;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.model.GraphModel;

public class ConnectOnMIO extends AbstractGraphViewerPlugin
{
	public void run(final GraphViewerPlot canvas, GraphModel model, List<CanvasComponent> components) 
	{
		if(components.isEmpty())
		{
			return;
		}

		MIManager mgmt=canvas.getModelHub().getViewModel().getDataSet().getMIManager();

		HierarchicalSetting stringMIO=new HierarchicalSetting("String");
		MIGroupSetting stringMIGroup=new MIGroupSetting("MI Group", null, null, mgmt, true);
		stringMIGroup.setAcceptableClass(StringMIO.class);
		BooleanSetting ignoreCase=new BooleanSetting("ignore case", null, false);
		stringMIO.addSetting(stringMIGroup).addSetting(ignoreCase);

		HierarchicalSetting stringListMIO=new HierarchicalSetting("String List");
		MIGroupSetting stringListMIGroup=new MIGroupSetting("MI Group", null, null, mgmt, true);
		stringListMIGroup.setAcceptableClass(StringListMIO.class);
		RestrictedStringSetting anyAllSetting=new RestrictedStringSetting("Match ", null, 0, new String[]{"any element","all elements"});
		stringListMIO.addSetting(stringListMIGroup).addSetting(anyAllSetting);

		HierarchicalSetting numberMIO=new HierarchicalSetting("Number");
		MIGroupSetting numberMIGroup=new MIGroupSetting("MI Group", null, null, mgmt, true);
		numberMIGroup.setAcceptableClass(NumericMIO.class);
		DoubleSetting maximumDifference=new DoubleSetting("Maximum Difference ",null,0);
		numberMIO.addSetting(numberMIGroup).addSetting(maximumDifference);

		// build setting:
		HierarchicalSetting[] predef=new HierarchicalSetting[]{stringMIO,stringListMIO, numberMIO};
		SelectableHierarchicalSetting mioSetting=new SelectableHierarchicalSetting("Connect on Meta Information",null,0,predef);

		BooleanSetting annotateSetting=new BooleanSetting("Annotate Edges", null, true);
		HierarchicalSetting setting=new HierarchicalSetting("Connect");
		setting.addSetting(mioSetting).addSetting(annotateSetting);

		SettingDialog sd=new SettingDialog(canvas.getOutermostJWindow(), "Connect ", setting);
		sd.showAsInputDialog();
		if(!sd.closedWithOK()) 
			return;

		if(mioSetting.getSelectedIndex()==0)
		{
			connectOnStringMIO(components, stringMIGroup.getMIGroup(), (SuperModel)model, ignoreCase.getBooleanValue());
		}
		if(mioSetting.getSelectedIndex()==1)
		{
			connectOnStringListMIO(components, stringListMIGroup.getMIGroup(), (SuperModel)model, anyAllSetting.getSelectedIndex()==1);
		}
		if(mioSetting.getSelectedIndex()==2)
		{
			connectOnNumberMIO(components, numberMIGroup.getMIGroup(), (SuperModel)model, maximumDifference.getDoubleValue());
		}
		canvas.revalidateEdges();		
	}
	
	private void connectOnNumberMIO(List<CanvasComponent> components, MIGroup grp, SuperModel model, double maxDist)
	{
		for(int i=0; i!= components.size(); ++i)
		{
			if( !(components.get(i) instanceof MultiProbeComponent)  )
				continue;
			MultiProbeComponent ci=(MultiProbeComponent)components.get(i);
			Set<Double> pi=new HashSet<Double>();
			for(Probe p: ci.getProbes())
			{
				if(grp.contains(p) )
				{
					pi.add(((DoubleMIO)grp.getMIO(p)).getValue());					
				}						
			}		
			
			for(int j=i+1; j<components.size(); ++j)
			{
				if( !(components.get(j) instanceof MultiProbeComponent)  )
					continue;
				MultiProbeComponent cj=(MultiProbeComponent)components.get(j);
				Set<Double> pj=new HashSet<Double>();
				for(Probe p: cj.getProbes())
				{
					if(grp.contains(p) )
					{	
						pj.add(((DoubleMIO)grp.getMIO(p)).getValue());
					}						
				}
				
				boolean found=false;
				for(double d: pi)
				{
					for(double e: pj)
					{
						double m=Math.abs(d-e);
						if(m < maxDist)
						{
							found=true;
							Edge edge= model.connect(ci, cj);
							edge.setName(grp.getName());
							edge.setWeight(m);
						}
					}
					if(found)
						break;
				}
			}
		}
	}

	private void connectOnStringMIO(List<CanvasComponent> components, MIGroup grp, SuperModel model, boolean ignoreCase)
	{
		for(int i=0; i!= components.size(); ++i)
		{
			if( !(components.get(i) instanceof MultiProbeComponent)  )
				continue;
			MultiProbeComponent ci=(MultiProbeComponent)components.get(i);
			Set<String> pi=new HashSet<String>();
			for(Probe p: ci.getProbes())
			{
				if(grp.contains(p) )
				{
					if(ignoreCase)
						pi.add(((StringMIO)grp.getMIO(p)).getValue().toLowerCase());
					else
						pi.add(((StringMIO)grp.getMIO(p)).getValue());
					
				}						
			}
			
			
			for(int j=i+1; j<components.size(); ++j)
			{
				if( !(components.get(j) instanceof MultiProbeComponent)  )
					continue;
				MultiProbeComponent cj=(MultiProbeComponent)components.get(j);
				Set<String> pj=new HashSet<String>();
				for(Probe p: cj.getProbes())
				{
					if(grp.contains(p) ){
						if(ignoreCase)
							pj.add(((StringMIO)grp.getMIO(p)).getValue().toLowerCase());
						else
							pj.add(((StringMIO)grp.getMIO(p)).getValue());
					}						
				}
				// compare if there is an intersect
				pj.retainAll(pi);
				if(!pj.isEmpty())
				{
					Edge e= model.connect(ci, cj);
					e.setName(pj.toString());
				}
			}
		}
	}
	
	private void connectOnStringListMIO(List<CanvasComponent> components, MIGroup grp, SuperModel model, boolean all)
	{
		for(int i=0; i!= components.size(); ++i)
		{
			if( !(components.get(i) instanceof MultiProbeComponent)  )
				continue;
			MultiProbeComponent ci=(MultiProbeComponent)components.get(i);
			Set<List<String>> pi=new HashSet<List<String>>();
			for(Probe p: ci.getProbes())
			{
				if(grp.contains(p) )
				{
					pi.add(((StringListMIO)grp.getMIO(p)).getValue());					
				}						
			}
			
			
			for(int j=i+1; j<components.size(); ++j)
			{
				if( !(components.get(j) instanceof MultiProbeComponent)  )
					continue;
				MultiProbeComponent cj=(MultiProbeComponent)components.get(j);
				Set<List<String>> pj=new HashSet<List<String>>();
				for(Probe p: cj.getProbes())
				{
					if(grp.contains(p) ){
						pj.add(((StringListMIO)grp.getMIO(p)).getValue());		
					}						
				}
				// compare if there is an intersect
				
				
				boolean hasMatch=false;
				for(List<String> li: pi)
				{
					for(List<String> lj:pj)
					{
						Set<String> pli=new HashSet<String>(li);
						Set<String> plj=new HashSet<String>(lj);
						plj.retainAll(pli);
						if(!pli.isEmpty())
						{
							if(all && pli.size()==plj.size())
							{
								hasMatch=true;
							}
							if(!all)
							{
								hasMatch=true;
							}
						}
						if(hasMatch)
						{
							Edge e= model.connect(ci, cj);
							e.setName(pli.toString());
							
							break;
						}
					}
					if(hasMatch)
						break;
				}
				pj.retainAll(pi);
				if(!pj.isEmpty())
				{
					Edge e= model.connect(ci, cj);
					e.setName(pj.toString());
				}
			}
		}
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"PAS.GraphViewer.Connect.OnMIO",
				new String[]{},
				GraphViewerPlugin.MC_GRAPH,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Connect all nodes that have probes matching on a certain Meta Information group",
				"Connect by Meta Information"				
		);
		pli.setIcon("mayday/pathway/gvicons/connectmio.png");
		pli.addCategory(CONNECT_CATEGORY);
		return pli;	

	}
}
