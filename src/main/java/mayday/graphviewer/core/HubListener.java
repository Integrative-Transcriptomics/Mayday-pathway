package mayday.graphviewer.core;

import java.util.EventListener;

public interface HubListener extends EventListener
{
	public abstract void hubStateChanged(HubEvent event);
}
