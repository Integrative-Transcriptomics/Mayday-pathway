package mayday.graphviewer.datasources.maygraph;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import mayday.core.ClassSelectionModel;
import mayday.core.DataSet;
import mayday.core.Preferences;
import mayday.core.datasetmanager.DataSetManager;
import mayday.core.pluma.PluginManager;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.ClassSelectionSetting;
import mayday.vis3.graph.renderer.RendererDecorator;
import mayday.vis3.graph.renderer.dispatcher.AssignedRendererSetting;
import mayday.vis3.graph.renderer.dispatcher.DecoratorListSetting;
import mayday.vis3.graph.renderer.dispatcher.RendererDispatcher;
import mayday.vis3.graph.renderer.dispatcher.RendererInstanceSetting;
import mayday.vis3.graph.vis3.SuperColorProvider;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class RendererFileParser 
{
	private DocumentBuilder builder;

	private DataSet ds;
	private SuperColorProvider coloring; 

	private AssignedRendererSetting defaultRenderer;
	private Map<String, AssignedRendererSetting> roleRenderers;
	private Map<String, AssignedRendererSetting> individualRenderers;
	private List<RendererDecorator> decorators;

	public RendererFileParser() throws Exception
	{
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		builder=dbf.newDocumentBuilder();
	}

	public RendererFileParser(DataSet ds, SuperColorProvider coloring) throws Exception
	{
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		builder=dbf.newDocumentBuilder();
		this.ds=ds;
		this.coloring=coloring;
	}

	public void parse(InputStream in) throws Exception
	{
		Document document = builder.parse(in);

		// parse default renderer
		Element defaultRendererE = (Element) document.getElementsByTagName(RendererDispatcher.DEFAULT_RENDERER).item(0);
		Element defaultRendererInstanceE= (Element) defaultRendererE.getChildNodes().item(0);
		defaultRenderer=parseRendererConfiguartion(defaultRendererInstanceE);
		
		// parse role renderers
		Element roleRendererE = (Element) document.getElementsByTagName(RendererDispatcher.ROLE_RENDERER).item(0);
		NodeList roleRendererList= roleRendererE.getChildNodes();
		roleRenderers=new TreeMap<String, AssignedRendererSetting>();
		for(int i=0; i!= roleRendererList.getLength(); ++i)
		{
			Element roleRendererInstanceE= (Element)roleRendererList.item(i);
			AssignedRendererSetting asr= parseRendererConfiguartion(roleRendererInstanceE);
			roleRenderers.put(asr.getTarget().getStringValue(), asr);

		}
		// parse overall decorators
		decorators=new ArrayList<RendererDecorator>();
		NodeList overallDecList=document.getElementsByTagName(RendererDispatcher.OVERALL_DECORATORS);
		if(overallDecList.getLength()!=0)
		{			
			Element overallDecE = (Element) overallDecList.item(0);
			Element decListE= (Element) overallDecE.getChildNodes().item(0);
			NodeList decList= decListE.getChildNodes();
			for(int i=0; i!= decList.getLength(); ++i)
			{
				Element decorator= (Element)decList.item(i);
				String plid=decorator.getAttribute(RendererInstanceSetting.PLID);
				RendererDecorator dec=(RendererDecorator)PluginManager.getInstance().getInstance(plid);
				if(decorator.hasAttribute(RendererDispatcher.SETTINGS))
				{
					String setting=decorator.getAttribute(RendererDispatcher.SETTINGS);
					Preferences p2=Preferences.createUnconnectedPrefTree("key", "value");
					try{
					p2.loadFrom(new BufferedReader(new StringReader(setting)));
					Map<String, DataSet> datasets=parseDataSetHints(decorator);
					if(datasets.isEmpty())
					{
						dec.setDataSet(ds);
					}else
					{
						dec.setDataSet(datasets.values().iterator().next());
					}
					dec.getSetting().fromPrefNode(p2);
					Map<String, ClassSelectionModel> models=parseClassHints(decorator);
					infuseClassSelection(dec.getSetting(), models);
					
					}catch(Exception ex){
						ex.printStackTrace();				
					}					
				}				
				decorators.add(dec);
//				System.out.println(decorator.getAttribute(RendererInstanceSetting.PLID));
			}
		}

		Element individualRendererListE= (Element) document.getElementsByTagName(RendererDispatcher.INDIVIDUAL_RENDERER_LIST).item(0);
		NodeList individualRenderersNL= individualRendererListE.getChildNodes();
		individualRenderers=new HashMap<String, AssignedRendererSetting>();
		for(int i=0; i!=individualRenderersNL.getLength(); ++i)
		{
			Element indRend=(Element) individualRenderersNL.item(i);
			String target=indRend.getAttribute(RendererDispatcher.TARGET_COMPONENT);
			AssignedRendererSetting r= parseRendererConfiguartion((Element)indRend.getChildNodes().item(0));
			individualRenderers.put(target, r);
//			System.out.println(r.getDecorators().getSelection());
//			System.out.println();
		}

	}	

	private AssignedRendererSetting parseRendererConfiguartion(Element e)
	{
		Element ri= (Element) e.getElementsByTagName(RendererInstanceSetting.RENDERER_INSTANCE).item(0);
		String plid= ri.getAttribute(RendererInstanceSetting.PLID);
		Preferences pref=null; 
		if(ri.hasAttribute(RendererDispatcher.SETTINGS))
		{
			
			
			
			String setting=ri.getAttribute(RendererDispatcher.SETTINGS);
			pref=Preferences.createUnconnectedPrefTree("key", "value");
			try{
			pref.loadFrom(new BufferedReader(new StringReader(setting)));
//			System.out.println(pref.toDebugString());
			}catch(Exception ex){
				ex.printStackTrace();				
			}			
		}
		
		String target="";
		if(e.hasAttribute(AssignedRendererSetting.TARGET))
		{
			target=e.getAttribute(AssignedRendererSetting.TARGET);
//			System.out.println(target);
		}
		NodeList decLL=e.getElementsByTagName(DecoratorListSetting.DECORATOR_LIST);
		List<RendererDecorator> decoratorsList=new ArrayList<RendererDecorator>();
		if(decLL.getLength()!=0)
		{
			Element decList= (Element)decLL.item(0);
			NodeList decorators=decList.getChildNodes();
			for(int i=0; i!= decorators.getLength(); ++i)
			{
				Element decorator=(Element)decorators.item(i);
				String decPlid=decorator.getAttribute(RendererInstanceSetting.PLID);
				RendererDecorator dec=(RendererDecorator)PluginManager.getInstance().getInstance(decPlid);
					
				if(decorator.hasAttribute(RendererDispatcher.SETTINGS))
				{
					Map<String, DataSet> datasets=parseDataSetHints(decorator);
					String setting=decorator.getAttribute(RendererDispatcher.SETTINGS);
					Preferences p2=Preferences.createUnconnectedPrefTree("key", "value");
					try	{
					p2.loadFrom(new BufferedReader(new StringReader(setting)));
//					System.out.println(p2.toDebugString());
					if(datasets.isEmpty())
					{
						dec.setDataSet(ds);
					}else
					{
						dec.setDataSet(datasets.values().iterator().next());
					}
					dec.getSetting().fromPrefNode(p2);
					Map<String, ClassSelectionModel> models=parseClassHints(decorator);
					infuseClassSelection(dec.getSetting(), models);
					}catch(Exception ex){
						ex.printStackTrace();				
					}					
				}
				decoratorsList.add(dec);
				
					
			}			
		}
		AssignedRendererSetting ars=new AssignedRendererSetting(target, ds,coloring);
		ars.setPrimaryRenderer(plid);
		if(pref!=null)
		{
			ars.getPrimaryRenderer().getInstance().getSetting().fromPrefNode(pref);
			Map<String, ClassSelectionModel> models=parseClassHints(ri);
			infuseClassSelection(ars.getPrimaryRenderer().getInstance().getSetting(), models);
		}
		for(RendererDecorator dec: decoratorsList)
		{
			ars.getDecorators().add(dec);
		}
		
		
		return ars;
	}
	
	private Map<String, DataSet> parseDataSetHints(Element decorator)
	{
		Map<String,DataSet> res=new HashMap<String, DataSet>();
		NodeList dsHints=decorator.getChildNodes();
		for(int i=0; i!= dsHints.getLength(); ++i)
		{
			Element ds=(Element)dsHints.item(i);
			if(ds.getTagName().equals(DecoratorListSetting.DATASET_HINT))
			{
				String s= ds.getAttribute(DecoratorListSetting.MI_GROUP_VALUE);
				for(DataSet dataset: DataSetManager.singleInstance.getDataSets())
				{
					if(dataset.getName().equals(s))
						res.put(ds.getAttribute(DecoratorListSetting.MI_GROUP_KEY), dataset);	
				}				
			}
		}
		return res;
	}
	
	private Map<String, ClassSelectionModel> parseClassHints(Element decorator)
	{
		Map<String,ClassSelectionModel> res=new HashMap<String, ClassSelectionModel>();
		NodeList dsHints=decorator.getChildNodes();
		for(int i=0; i!= dsHints.getLength(); ++i)
		{
			Element ds=(Element)dsHints.item(i);
			if(ds.getTagName().equals(DecoratorListSetting.CLASS_HINT))
			{
				String m= ds.getAttribute(DecoratorListSetting.CLASS_VALUE);
				ClassSelectionModel model=ClassSelectionModel.deserialize(m);
				res.put(ds.getAttribute(DecoratorListSetting.MI_GROUP_KEY), model);	
							
			}
		}
		return res;
	}
	
	private void infuseClassSelection(Setting setting, Map<String, ClassSelectionModel> models)
	{
		if(setting instanceof ClassSelectionSetting)
		{
			if(models.containsKey(setting.getName()))
				((ClassSelectionSetting) setting).setModel(models.get(setting.getName()));
		}
		if(setting instanceof HierarchicalSetting)
			for(Setting s: ((HierarchicalSetting) setting).getChildren())
			{
				infuseClassSelection(s, models);
			}
		if(setting instanceof BooleanHierarchicalSetting)
			for(Setting s: ((BooleanHierarchicalSetting) setting).getChildren())
			{
				infuseClassSelection(s, models);
			}
	}
	
	
	
	public AssignedRendererSetting getDefaultRenderer() {
		return defaultRenderer;
	}

	public void setDefaultRenderer(AssignedRendererSetting defaultRenderer) {
		this.defaultRenderer = defaultRenderer;
	}

	public Map<String, AssignedRendererSetting> getRoleRenderers() {
		return roleRenderers;
	}

	public void setRoleRenderers(Map<String, AssignedRendererSetting> roleRenderers) {
		this.roleRenderers = roleRenderers;
	}

	public Map<String, AssignedRendererSetting> getIndividualRenderers() {
		return individualRenderers;
	}

	public void setIndividualRenderers(
			Map<String, AssignedRendererSetting> individualRenderers) {
		this.individualRenderers = individualRenderers;
	}

	public List<RendererDecorator> getDecorators() {
		return decorators;
	}

	public void setDecorators(List<RendererDecorator> decorators) {
		this.decorators = decorators;
	}


	

	
	

}
