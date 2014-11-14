package mayday.pathway.sbgn.processdiagram.entitypool;

import mayday.core.structures.graph.Graph;
import mayday.pathway.biopax.core.Complex;
import mayday.pathway.biopax.core.DNA;
import mayday.pathway.biopax.core.Participant;
import mayday.pathway.biopax.core.Protein;
import mayday.pathway.biopax.core.RNA;
import mayday.pathway.biopax.core.Reference;
import mayday.pathway.biopax.core.SmallMolecule;
import mayday.pathway.sbgn.graph.SBGNNode;

/**
 * Creates EntityPoolNodes from BioPaxComponents
 * @author Stephan Symons
 *
 */
public class EntityPoolNodeFactory
{
	public static EntityPoolNode createEntityPoolNode(Participant p, Graph g)
	{
		EntityPoolNode node=createNode(p, g);
		extractAnnotation(node, p);
		extractReferences(node, p);
		return node;
	}
	
	private static EntityPoolNode createNode(Participant p, Graph g)
	{
		if(p.getEntity() instanceof SmallMolecule)
		{
			return new SimpleChemical(g,p.getEntity().getName());
		}
		if(p.getEntity() instanceof Protein)
		{
			return new Macromolecule(g,p.getEntity().getName());
		}
		if(p.getEntity() instanceof DNA || p.getEntity() instanceof RNA)
		{
			return new NucleicAcidFeature(g,p.getEntity().getName());
		}
		if(p.getEntity() instanceof Complex )
		{
			mayday.pathway.sbgn.processdiagram.container.Complex c=new mayday.pathway.sbgn.processdiagram.container.Complex(g,p.getEntity().getName());
			for(Participant cp:((Complex)p.getEntity()).getComponents())
			{
				c.addComponent(createNode(cp, null));
			}
			return c;
		}
		return new UnspecifiedEntity(g,p.getEntity().getName());
	}
		
	private static void extractAnnotation(SBGNNode node, Participant p)
	{
		node.addAnnotation(SBGNNode.NAME_ANNOTATION, p.getEntity().getName());
		node.addAnnotation(SBGNNode.SHORTNAME_ANNOTATION, p.getEntity().getShortName());
		for(String s: p.getEntity().getSynonyms())
		{
			node.addAnnotation(SBGNNode.SYNONYM_ANNOTATION, s);
		}
		node.addAnnotation(SBGNNode.COMMENT_ANNOTATION, p.getEntity().getComment());		
		node.setCompartmentName(p.getCellularLocation());
	}
	
	private static void extractReferences(SBGNNode node, Participant p)
	{
		for(Reference r: p.getEntity().getReferences())
		{
			node.addReference(r.getDatabase(), r.getId());
		}
	}
	

	
	
}
