package mayday.graphviewer.core;

public class HubEvent 
{
	private	ModelHub sourceHub;
	private int change; 
	
	public static final int UPDATE=0x00;
	
	
	public HubEvent(ModelHub source) 
	{
		this.sourceHub=source;
	}
	
	public HubEvent(ModelHub source, int change) 
	{
		this.sourceHub=source;
		this.change=change;
	}
	


	public int getChange() {
		return change;
	}


	public void setChange(int change) {
		this.change = change;
	}


	public ModelHub getSourceHub() {
		return sourceHub;
	}
	
	
	
	
}
