package mayday.graphviewer.action;

import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.graphviewer.core.GraphViewerPlot;
import mayday.graphviewer.core.SuperModel;
import mayday.pathway.sbgn.processdiagram.ProcessDiagram;
import mayday.vis3.graph.components.CanvasComponent;

@SuppressWarnings("serial")
public class AddReactionTemplate extends AbstractAction
{
	protected GraphViewerPlot canvas;
	protected static int num=1; 
	
	public AddReactionTemplate(GraphViewerPlot canvas) 
	{
		super("Add new Reaction");
		this.canvas=canvas;
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(canvas.getModel() instanceof SuperModel)
		{
			SuperModel model=(SuperModel) canvas.getModel();
			MultiProbeNode rea=new MultiProbeNode(model.getGraph());
			rea.setRole(ProcessDiagram.PROCESS_ROLE);
			rea.setName(ProcessDiagram.PROCESS_ROLE+" "+num);
			num++;
		
			CanvasComponent ccR=model.addNode(rea);
			
			Point pt=canvas.getMousePosition();
			
			ccR.setLocation(pt);
			
			MultiProbeNode substr=new MultiProbeNode(model.getGraph());
			substr.setRole(ProcessDiagram.SIMPLE_CHEMICAL_ROLE);
			substr.setName(ProcessDiagram.SIMPLE_CHEMICAL_ROLE+" "+num);
			num++;		
			CanvasComponent ccS=model.addNode(substr);
			ccS.setLocation(pt.x-100-ProcessDiagram.getGlyph(ProcessDiagram.SIMPLE_CHEMICAL_ROLE).getBounds().width,
					pt.y+ProcessDiagram.getGlyph(ProcessDiagram.PROCESS_ROLE).getBounds().height/2
					-ProcessDiagram.getGlyph(ProcessDiagram.SIMPLE_CHEMICAL_ROLE).getBounds().height/2-2);
						
			MultiProbeNode prod=new MultiProbeNode(model.getGraph());
			prod.setRole(ProcessDiagram.SIMPLE_CHEMICAL_ROLE);
			prod.setName(ProcessDiagram.SIMPLE_CHEMICAL_ROLE+" "+num);
			num++;		
			CanvasComponent ccP=model.addNode(prod);
			ccP.setLocation(pt.x+ProcessDiagram.getGlyph(ProcessDiagram.PROCESS_ROLE).getBounds().width+100,
					pt.y+ProcessDiagram.getGlyph(ProcessDiagram.PROCESS_ROLE).getBounds().height/2
					-ProcessDiagram.getGlyph(ProcessDiagram.SIMPLE_CHEMICAL_ROLE).getBounds().height/2-2);
			
			MultiProbeNode enz=new MultiProbeNode(model.getGraph());
			enz.setRole(ProcessDiagram.MACROMOLECULE_ROLE);
			enz.setName(ProcessDiagram.MACROMOLECULE_ROLE+" "+num);
			num++;		
			CanvasComponent ccE=model.addNode(enz);
			ccE.setLocation(
					pt.x+ProcessDiagram.getGlyph(ProcessDiagram.PROCESS_ROLE).getBounds().width/2-
					(ProcessDiagram.getGlyph(ProcessDiagram.MACROMOLECULE_ROLE).getBounds().width)/2+3,								
					pt.y-150);
			
			model.connect(ccS, ccR);
			model.connect(ccR, ccP);
			model.connect(ccE, ccR);
			
			
		}		
		
	}
}
