package mayday.graphviewer.core;

import mayday.core.structures.maps.DefaultValueMap;
import mayday.pathway.sbgn.processdiagram.ProcessDiagram;
import mayday.vis3.graph.renderer.dispatcher.AssignedRendererSetting;

public class HubRevolver extends HubRendererDispatcher
{
	private DefaultValueMap<String, AssignedRendererSetting> defaultStyle;
	private DefaultValueMap<String, AssignedRendererSetting> onlyCircle;
	private DefaultValueMap<String, AssignedRendererSetting> onlyProbes;
	private DefaultValueMap<String, AssignedRendererSetting> profilePlots;
	private DefaultValueMap<String, AssignedRendererSetting> gradients;
	private DefaultValueMap<String, AssignedRendererSetting> onlySBGN;

	private AssignedRendererSetting defaultDefaultRenderer;
	private AssignedRendererSetting circleDefaultRenderer;
	private AssignedRendererSetting probesDefaultRenderer;
	private AssignedRendererSetting profilePlotsDefaultRenderer;
	private AssignedRendererSetting gradientsDefaultRenderer;
	
	private int current;
	
	public HubRevolver(ModelHub hub) 
	{
		super(hub);
		current=0;
		defaultDefaultRenderer=defaultRenderer;
		onlyCircle=onlyCircles(hub).getRoleRenderers();
		circleDefaultRenderer=onlyCircles(hub).getDefaultRenderer();
		
		profilePlots=profilePlots(hub).getRoleRenderers();
		gradients=gradient(hub).getRoleRenderers();
		
		onlySBGN=onlyCircles(hub).getRoleRenderers();
		for(String s: ProcessDiagram.ROLES)
		{
			AssignedRendererSetting sbgnRenderer=new AssignedRendererSetting(s,hub.getViewModel().getDataSet(),
					hub.getColorProvider()	);
			sbgnRenderer.setPrimaryRenderer("PAS.GraphViewer.Renderer.SBGN");	
			onlySBGN.put(s, sbgnRenderer);
		}
		onlyProbes=onlyProbes(hub).getRoleRenderers();
		probesDefaultRenderer=onlyProbes(hub).getDefaultRenderer();
		defaultStyle=getRoleRenderers();
		profilePlotsDefaultRenderer=profilePlots(hub).getDefaultRenderer();
		gradientsDefaultRenderer=gradient(hub).getDefaultRenderer();
	}

	public void next()
	{
		current++;
		if(current==6)
			current=0;
		switch (current) 
		{
		case 0: roleRenderers=defaultStyle; 
		defaultRenderer=defaultDefaultRenderer;
		break;
		case 1: roleRenderers=onlyCircle; 
		defaultRenderer=circleDefaultRenderer;
		break;
		case 2: roleRenderers=onlyProbes; 
		defaultRenderer=probesDefaultRenderer;
		break;
		case 3: roleRenderers=profilePlots; 
		defaultRenderer=profilePlotsDefaultRenderer;
		break;
		case 4: roleRenderers=gradients; 
		defaultRenderer=gradientsDefaultRenderer;
		break;
		case 5: roleRenderers=onlySBGN; 
		defaultRenderer=circleDefaultRenderer;
		break;
		}		
	}

	public String getCurrentSet()
	{
		switch (current) 
		{
		case 0: return "Default";
		case 1: return "Circle";
		case 2: return "HeatStream";
		case 3: return "ProfilePlot";
		case 4: return "Gradient";
		case 5: return "SBGN";
		}
		return null;
	}
}
