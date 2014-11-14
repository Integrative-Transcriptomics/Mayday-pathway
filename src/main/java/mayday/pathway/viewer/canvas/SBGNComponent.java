package mayday.pathway.viewer.canvas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;

import mayday.pathway.sbgn.graph.SBGNNode;
import mayday.pathway.sbgn.processdiagram.entitypool.MultimerMacromolecule;
import mayday.pathway.sbgn.processdiagram.entitypool.MultimerNucleicAcidFeature;
import mayday.pathway.sbgn.processdiagram.entitypool.MultimerSimpleChemical;
import mayday.pathway.viewer.gui.SBGNAnnotationFrame;
import mayday.vis3.graph.components.MultiProbeComponent;
import mayday.vis3.graph.renderer.ComponentRenderer;

@SuppressWarnings("serial")
public class SBGNComponent extends MultiProbeComponent
{
	private boolean displaySBGN=true;
	
	private final ComponentRenderer sbgnRenderer =SBGNNodeRenderer.instance();
			
	public SBGNComponent(SBGNNode node) 
	{
		super(node);		
		renderer=sbgnRenderer;

		// set size of the component to the size of the glyph. 
		if(node instanceof MultimerSimpleChemical || node instanceof MultimerMacromolecule || node instanceof MultimerNucleicAcidFeature)
		{
			setSize(node.getGlyph().getBounds().width+11, node.getGlyph().getBounds().height+11);		
		}else
		{
			setSize(node.getGlyph().getBounds().width+1, node.getGlyph().getBounds().height+1);
		}	
		setBackground(Color.white);
	}
	
	public SBGNNode getNode()
	{
		return (SBGNNode)super.getNode();
	}

	public void paint(Graphics g1)
	{
		Graphics2D g=(Graphics2D)g1;
		if(displaySBGN || getProbes().isEmpty())
			sbgnRenderer.draw(g, getNode(), new Rectangle(getSize()), getNode(), getLabel(), isSelected());
		else
			renderer.draw(g, getNode(), new Rectangle(getSize()), getProbes(), "", isSelected());
		
	}

	public void setSize(Dimension d)
	{
		// do nothing
	}
	
	@Override
	public void setRenderer(ComponentRenderer renderer)
	{
		if(!displaySBGN)
		{
			this.renderer=renderer;
			setSize(renderer.getSuggestedSize(getNode(),getProbes()));
		}
	}

	/* (non-Javadoc)
	 * @see mayday.vis3.graph.components.CanvasComponent#setCustomMenu(javax.swing.JPopupMenu)
	 */
	@Override
	protected JPopupMenu setCustomMenu(JPopupMenu menu) 
	{
		menu=super.setCustomMenu(menu);
		menu.add(new AnnotationAction());		
		return menu;
	}

	private class AnnotationAction extends AbstractAction
	{
		public AnnotationAction() 
		{
			super("Annotations...");
		}
		
		public void actionPerformed(ActionEvent e) 
		{
			new SBGNAnnotationFrame(getNode()).setVisible(true);			
		}
	}

	/**
	 * @return the displaySBGN
	 */
	public boolean isDisplaySBGN() 
	{
		return displaySBGN;
	}

	/**
	 * @param displaySBGN the displaySBGN to set
	 */
	public void setDisplaySBGN(boolean displaySBGN) 
	{
		this.displaySBGN = displaySBGN;
		if(displaySBGN)
		{
			if(getNode() instanceof MultimerSimpleChemical || 
			   getNode() instanceof MultimerMacromolecule || 
			   getNode() instanceof MultimerNucleicAcidFeature)
			{
				setSize(getNode().getGlyph().getBounds().width+11, getNode().getGlyph().getBounds().height+11);		
			}else
			{
				setSize(getNode().getGlyph().getBounds().width+1, getNode().getGlyph().getBounds().height+1);
			}	
			setBackground(Color.white);
			renderer=sbgnRenderer;
		}else
		{
			setSize(renderer.getSuggestedSize(getNode(),getProbes()));
		}
	}	
}
