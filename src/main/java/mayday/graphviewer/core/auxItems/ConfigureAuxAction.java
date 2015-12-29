package mayday.graphviewer.core.auxItems;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.core.structures.graph.nodes.DefaultNode;
import mayday.graphviewer.core.DefaultNodeComponent;

@SuppressWarnings("serial")
public class ConfigureAuxAction extends AbstractAction 
{
	private DefaultNodeComponent comp; 
	
	public ConfigureAuxAction(DefaultNodeComponent comp) 
	{
		super("Configure Auxiliaries");
		this.comp = comp;
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		HierarchicalSetting setting=new HierarchicalSetting("Auxiliary Items");		
		DefaultNode dn=((DefaultNode)comp.getNode());	
		
		String s="!star";
		BooleanHierarchicalSetting starSetting=new BooleanHierarchicalSetting("Star", null, dn.hasProperty(s));
		String c= dn.getPropertyValue(s);
		if(c==null)
			c=Integer.toHexString(AuxItems.STAR.getFillColor().getRGB()).substring(2);
		ColorSetting starColor=new ColorSetting("Color",null, DefaultAuxItemStrategy.parseColor(c));
		starSetting.addSetting(starColor);
		
		
		s="!circle";
		BooleanHierarchicalSetting circleSetting=new BooleanHierarchicalSetting("Circle", null, dn.hasProperty(s));
		c= dn.getPropertyValue(s);
		if(c==null)
			c=Integer.toHexString(AuxItems.CIRCLE.getFillColor().getRGB()).substring(2);
		ColorSetting circleColor=new ColorSetting("Color",null, DefaultAuxItemStrategy.parseColor(c));
		circleSetting.addSetting(circleColor);
		
		s="!box";
		BooleanHierarchicalSetting boxSetting=new BooleanHierarchicalSetting("Box", null, dn.hasProperty(s));
		c= dn.getPropertyValue(s);
		if(c==null)
			c=Integer.toHexString(AuxItems.BOX.getFillColor().getRGB()).substring(2);
		ColorSetting boxColor=new ColorSetting("Color",null, DefaultAuxItemStrategy.parseColor(c));
		boxSetting.addSetting(boxColor);
		
		s="!triangle";
		BooleanHierarchicalSetting triangleSetting=new BooleanHierarchicalSetting("Triangle", null, dn.hasProperty(s));
		c= dn.getPropertyValue(s);
		if(c==null)
			c=Integer.toHexString(AuxItems.TRIANGLE.getFillColor().getRGB()).substring(2);
		ColorSetting triangleColor=new ColorSetting("Color",null, DefaultAuxItemStrategy.parseColor(c));
		triangleSetting.addSetting(triangleColor);
		
		
		s="!warning";
		BooleanSetting warningSetting=new BooleanSetting("Warning", null, dn.hasProperty(s));
		s="!question";
		BooleanSetting questionSetting=new BooleanSetting("Question", null, dn.hasProperty(s));
		s="!important";
		BooleanSetting importantSetting=new BooleanSetting("Important", null, dn.hasProperty(s));
		
		
		
		s="!note";
		BooleanHierarchicalSetting noteSetting=new BooleanHierarchicalSetting("Note", null, dn.hasProperty(s));
		c= dn.getPropertyValue(s);
		if(c==null)
			c="";
		StringSetting nodeText=new StringSetting("Note Text", null, c);
		noteSetting.addSetting(nodeText);
		
		s="!UofI";
		BooleanHierarchicalSetting uofiSetting=new BooleanHierarchicalSetting("Unit of Information", null, dn.hasProperty(s));
		c= dn.getPropertyValue(s);
		if(c==null)
			c="";
		StringSetting uofiText=new StringSetting("Information Text", null, c);
		uofiSetting.addSetting(uofiText);
		
		s="!state";
		BooleanHierarchicalSetting stateSetting=new BooleanHierarchicalSetting("State", null, dn.hasProperty(s));
		c= dn.getPropertyValue(s);
		if(c==null)
			c="";
		StringSetting stateText=new StringSetting("State Text", null, c);
		stateSetting.addSetting(stateText);
		
		
		setting.addSetting(warningSetting);
		setting.addSetting(questionSetting);
		setting.addSetting(importantSetting);
		
		setting.addSetting(boxSetting);
		setting.addSetting(circleSetting);
		setting.addSetting(triangleSetting);
		setting.addSetting(starSetting);
		
		setting.addSetting(noteSetting);
		setting.addSetting(uofiSetting);
		setting.addSetting(stateSetting);
		
		SettingDialog dialog=new SettingDialog(null, "Auxiliary Items", setting);
		dialog.showAsInputDialog();
		
		if(dialog.canceled())
			return;
		
		s="!warning";
		if(warningSetting.getBooleanValue())
		{
			dn.setProperty(s, "true");
		}else
		{
			killProperty(s, dn);
		}	
		
		s="!question";		
		if(questionSetting.getBooleanValue())
		{
			dn.setProperty(s, "true");
		}else
		{
			killProperty(s, dn);
		}
		
		s="!important";		
		if(importantSetting.getBooleanValue())
		{
			dn.setProperty(s, "true");
		}else
		{
			killProperty(s, dn);
		}
		
		s="!star";
		if(starSetting.getBooleanValue())
		{
			dn.setProperty(s, Integer.toHexString(starColor.getColorValue().getRGB()).substring(2));
		}else
		{
			killProperty(s, dn);
		}
		
		
		s="!circle";
		if(circleSetting.getBooleanValue())
		{
			dn.setProperty(s, Integer.toHexString(circleColor.getColorValue().getRGB()).substring(2));
		}else
		{
			killProperty(s, dn);
		}
		
		s="!box";
		if(boxSetting.getBooleanValue())
		{
			dn.setProperty(s, Integer.toHexString(boxColor.getColorValue().getRGB()).substring(2));
		}else
		{
			killProperty(s, dn);
		}
		s="!triangle";
		if(triangleSetting.getBooleanValue())
		{
			dn.setProperty(s, Integer.toHexString(triangleColor.getColorValue().getRGB()).substring(2));
		}else
		{
			killProperty(s, dn);
		}	
		
		s="!triangle";
		if(triangleSetting.getBooleanValue())
		{
			dn.setProperty(s, Integer.toHexString(triangleColor.getColorValue().getRGB()).substring(2));
		}else
		{
			killProperty(s, dn);
		}	
		
		s="!note";
		if(noteSetting.getBooleanValue())
		{
			dn.setProperty(s, noteSetting.getValueString());
		}else
		{
			killProperty(s, dn);
		}
		s="!UofI";
		if(uofiSetting.getBooleanValue())
		{
			dn.setProperty(s, uofiSetting.getValueString());
		}else
		{
			killProperty(s, dn);
		}
		s="!state";
		if(stateSetting.getBooleanValue())
		{
			dn.setProperty(s, stateSetting.getValueString());
		}else
		{
			killProperty(s, dn);
		}

		comp.repaint();

	}
	
	private void killProperty(String p, DefaultNode node)
	{
		if(node.hasProperty(p))
				node.getProperties().remove(p);
	}

}
