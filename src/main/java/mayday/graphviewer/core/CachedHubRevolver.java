package mayday.graphviewer.core;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import mayday.core.structures.graph.Node;
import mayday.vis3.graph.components.CanvasComponent;
import mayday.vis3.graph.renderer.dispatcher.RendererPluginSetting;
import mayday.vis3.graph.renderer.primary.MinimalRenderer;

public class CachedHubRevolver extends HubRevolver {

	Map<CanvasComponent, BufferedImage> cache=new HashMap<CanvasComponent, BufferedImage>();

	public CachedHubRevolver(ModelHub hub) {
		super(hub);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void render(Graphics2D g, Node node, Object value, boolean paintLabel, CanvasComponent comp) {
		//0: check if component is small:
		if(renderSmallSettings.getBooleanValue() && (comp.getWidth() <20 || comp.getHeight() < 20) )
		{				
			MinimalRenderer.sharedInstance.draw(g, node, new Rectangle(comp.getSize()), value, "", comp.isSelected());	
			return;			
		}		
		//1. check if component is cached
		if(renderCachedComponent(g, node, value, comp))
			return;

		//2. not cached: we need to render it ourselves. 
		// create an image and render component on it
		BufferedImage img=new BufferedImage(comp.getWidth(), comp.getHeight(), BufferedImage.TYPE_INT_RGB);
		
		RendererPluginSetting rnd=defaultRenderer;

		if(individualRenderers.containsKey(comp))
		{
			rnd= individualRenderers.get(comp);
		}else{
			//3. not an individualist component
			if(roleRenderers.containsKey(node.getRole()))
			{
				rnd= roleRenderers.get(node.getRole());
			}
		}
		renderComponentWithRenderer(g, node, value, comp, rnd,paintLabel);
		Graphics2D g2=(Graphics2D)img.getGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		renderComponentWithRenderer((Graphics2D)img.getGraphics(), node, value, comp, rnd, paintLabel);
		cache.put(comp, img);

	}

	@Override
	protected boolean renderCachedComponent(Graphics2D g, Node node, Object value, CanvasComponent comp) {
		if(cache.containsKey(comp)){
			BufferedImage img=cache.get(comp);
			if(img.getWidth() != comp.getWidth() || img.getHeight()!=comp.getHeight()) {
				cache.remove(comp);
				return false;
			}
				
			//cache hit: draw image
			g.drawImage(cache.get(comp), null, 0, 0);
			return true;
		}
		return false;
	}
	
	@Override
	public void next() {
		cache.clear();
		super.next();
	}
	
	public void clearCache(){
		cache.clear();
	}
	
	public void clearCache(CanvasComponent comp) {
		cache.remove(comp);
	}

}
