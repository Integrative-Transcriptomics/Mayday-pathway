package mayday.graphviewer.datasources.sbml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.graphviewer.core.SBGNRoles;
import mayday.pathway.sbgn.processdiagram.ProcessDiagram;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SBMLParser 
{
	private DocumentBuilder builder;

	private static final String COMPARTMENT="compartment";
	private static final String SPECIES="species";
	private static final String REACTION="reaction";

	private static final String LIST_OF_REACTANDS="listOfReactants";
	private static final String LIST_OF_PRODUCTS="listOfProducts";
	private static final String LIST_OF_MODIFIERS="listOfModifiers";

	public static final String SBO_TERM="sboTerm";
	
	private Map<String, SBMLCompartment> compartmentMap;
	private Map<String, SBMLSpecies> speciesMap;
	private List<SBMLReaction> reactions;

	public SBMLParser() throws Exception
	{
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		builder=dbf.newDocumentBuilder();
	}

	private void initSBase(Element e, SBase s)
	{		
		s.id=e.getAttribute("id");
		if(e.hasAttribute("name")) 
			s.name=e.getAttribute("name");
		if(e.hasAttribute("metaid")) 
			s.metaId=e.getAttribute("metaid");
		if(e.hasAttribute(SBO_TERM)) 
			s.sboTerm=e.getAttribute(SBO_TERM);
	}

	private SBMLCompartment buildCompartment(Element e)
	{
		SBMLCompartment comp=new SBMLCompartment();
		initSBase(e, comp);
		if(e.hasAttribute("spatialDimensions")) 
			comp.spatialDimensions=Integer.parseInt(e.getAttribute("spatialDimensions"));
		if(e.hasAttribute("size")) 
			comp.size=Double.parseDouble(e.getAttribute("size"));
		if(e.hasAttribute("outside")) 
			comp.outside=e.getAttribute("outside");
		if(e.hasAttribute("constant")) 
			comp.constant=Boolean.parseBoolean(e.getAttribute("constant"));
		return comp;
	}


	private SBMLSpecies buildSpecies(Element e)
	{
		SBMLSpecies comp=new SBMLSpecies();
		initSBase(e, comp);

		if(e.hasAttribute("compartment")) 
		{
			comp.compartment=compartmentMap.get(e.getAttribute("compartment"));
		}
		if(e.hasAttribute("initialAmount")) 
			comp.initialAmount=Double.parseDouble(e.getAttribute("initialAmount"));
		if(e.hasAttribute("initialConcentration")) 
			comp.initialConcentration=Double.parseDouble(e.getAttribute("initialConcentration"));		
		if(e.hasAttribute("boundaryCondition")) 
			comp.boundaryCondition=Boolean.parseBoolean(e.getAttribute("boundaryCondition"));
		if(e.hasAttribute("charge")) 
			comp.charge=Integer.parseInt(e.getAttribute("charge"));	
		if(e.hasAttribute("constant")) 
			comp.constant=Boolean.parseBoolean(e.getAttribute("constant"));
		return comp;
	}

	private SBMLReaction buildReaction(Element e)
	{
		SBMLReaction comp=new SBMLReaction();
		initSBase(e, comp);

		if(e.hasAttribute("reversible")) 
			comp.reversible=Boolean.parseBoolean(e.getAttribute("reversible"));
		if(e.hasAttribute("fast")) 
			comp.fast=Boolean.parseBoolean(e.getAttribute("fast"));

		NodeList children=e.getChildNodes();
		for(int i=0; i!=children.getLength(); ++i)
		{
			Node ch=children.item(i);
			if(ch.getNodeType()!= Node.ELEMENT_NODE) continue;

			Element ce=(Element)ch;		
			if(ce.getTagName().equals(LIST_OF_REACTANDS))
			{
				List<SBMLSpeciesRef> spc=parseSpeciesList(ce);

				comp.listOfReactants=spc;			
			}
			if(ce.getTagName().equals(LIST_OF_PRODUCTS))
			{
				List<SBMLSpeciesRef> spc=parseSpeciesList(ce);
				comp.listOfProducts=spc;			
			}
			if(ce.getTagName().equals(LIST_OF_MODIFIERS))
			{
				List<SBMLSpeciesRef> spc=parseSpeciesList(ce);
				comp.listOfModifiers=spc;			
			}			
		}
		return comp;
	}

	private List<SBMLSpeciesRef> parseSpeciesList(Element e)
	{
		List<SBMLSpeciesRef> ids=new ArrayList<SBMLSpeciesRef>();
		NodeList children=e.getChildNodes();
		for(int i=0; i!=children.getLength(); ++i)
		{
			Node ch=children.item(i);
			if(ch.getNodeType()!= Node.ELEMENT_NODE) continue;

			Element ce=(Element)ch;		
			String id=ce.getAttribute("species");
			SBMLSpeciesRef ref=new SBMLSpeciesRef();
			initSBase(ce, ref);
			ref.species=speciesMap.get(id);
			ids.add(ref);						
		}		
		return ids;
	}

	public Graph createGraph()
	{
		Graph graph=new Graph();
		Map<SBMLSpecies, MultiProbeNode> speciesNodeMap=new HashMap<SBMLSpecies, MultiProbeNode>();

		for(SBMLReaction reaction: reactions)
		{
			// create a node for reaction
			MultiProbeNode reaNode=createNodeFromSBase(graph, reaction);
			reaNode.setProperty("fast", ""+reaction.fast);
			reaNode.setProperty("reversible", ""+reaction.reversible);
			reaNode.setRole(SBGNRoles.PROCESS_ROLE);	
			graph.addNode(reaNode);
			// go over all lefts and rights and see if we already have them, then connect to reaction. 
			for(SBMLSpeciesRef leftRef:reaction.listOfReactants)
			{
				MultiProbeNode leftNode=speciesNodeMap.get(leftRef.species);

				if(leftNode==null)
				{
					leftNode=createNodeFromSBase(graph, leftRef.species);
					speciesNodeMap.put(leftRef.species, leftNode);
					leftNode.setRole(SBGNRoles.SIMPLE_CHEMICAL_ROLE);		
					graph.addNode(leftNode);
				}
				Edge e=new Edge(leftNode,reaNode);
				if(leftRef.sboTerm!=null)
					e.addProperty(SBO_TERM, leftRef.sboTerm);
				e.setRole(SBGNRoles.CONSUMPTION_ROLE);
				graph.connect(e);			
			}
			// if no reactands are available, add a source
			if(reaction.listOfReactants.isEmpty()){
				System.out.println("rea"+reaNode+"--<<<<<<<<");
				MultiProbeNode source=new MultiProbeNode(graph);
				graph.addNode(source);
				source.setName(reaction.name+"_source");
				source.setRole(ProcessDiagram.SOURCE_ROLE);
				Edge e=new Edge(source,reaNode);
				e.setRole(ProcessDiagram.CONSUMPTION_ROLE);
				graph.connect(e);
			}
				
			for(SBMLSpeciesRef rightRef:reaction.listOfProducts)
			{
				MultiProbeNode rightNode=speciesNodeMap.get(rightRef.species);

				if(rightNode==null)
				{
					rightNode=createNodeFromSBase(graph, rightRef.species);
					speciesNodeMap.put(rightRef.species, rightNode);
					rightNode.setRole(SBGNRoles.SIMPLE_CHEMICAL_ROLE);	
					graph.addNode(rightNode);
				}
				Edge e=new Edge(reaNode,rightNode);
				if(rightRef.sboTerm!=null)
					e.addProperty(SBO_TERM, rightRef.sboTerm);
				e.setRole(SBGNRoles.PRODUCTION_ROLE);
				graph.connect(e);			
			}
			// if no products are available, add a source
			if(reaction.listOfProducts.isEmpty()){
				System.out.println("rea"+reaNode+"-->>>>");
				MultiProbeNode sink=new MultiProbeNode(graph);
				graph.addNode(sink);
				sink.setName(reaction.name+"_sink");
				sink.setRole(ProcessDiagram.SINK_ROLE);
				Edge e=new Edge(reaNode,sink);
				e.setRole(ProcessDiagram.PRODUCTION_ROLE);
				graph.connect(e);
			}
			// go over all modifiers; connect to reaction. 
			for(SBMLSpeciesRef modRef:reaction.listOfModifiers)
			{
				MultiProbeNode modNode=speciesNodeMap.get(modRef.species);

				if(modNode==null)
				{
					modNode=createNodeFromSBase(graph, modRef.species);
					speciesNodeMap.put(modRef.species, modNode);
					modNode.setRole(SBGNRoles.MACROMOLECULE_ROLE);	
					graph.addNode(modNode);
				}
				Edge e=new Edge(modNode,reaNode);
				if(modRef.sboTerm!=null)
					e.addProperty(SBO_TERM, modRef.sboTerm);
				e.setRole(SBGNRoles.MODULATION_ROLE);
				graph.connect(e);			
			}			
		}
		return graph;
	}

	private MultiProbeNode createNodeFromSBase(Graph g, SBase sBase)
	{
		MultiProbeNode node=new MultiProbeNode(g);
		if(sBase.name==null)
			node.setName(sBase.id);
		else
			node.setName(sBase.name);
		if(sBase.sboTerm!=null)
			node.setProperty(SBO_TERM, sBase.sboTerm);
		return node; 
	}
	
	private MultiProbeNode createNodeFromSBase(Graph g, SBMLSpecies spec)
	{
		MultiProbeNode node=createNodeFromSBase(g,(SBase)spec);

		if(spec.compartment!=null)
			node.setProperty(COMPARTMENT, spec.compartment.name);
		
		node.setProperty("initialAmount", ""+spec.initialAmount);
		node.setProperty("initialConcentration", ""+spec.initialConcentration);
		node.setProperty("constant",""+spec.constant);		
		
		return node; 
	}

	public void parse(String fileName) throws Exception
	{
		Document document = builder.parse(fileName);

		compartmentMap=new HashMap<String, SBMLCompartment>();
		NodeList l=document.getElementsByTagName(COMPARTMENT);
		for(int i=0; i!=l.getLength(); ++i)
		{
			Element e=(Element)l.item(i);
			SBMLCompartment comp=buildCompartment(e);
			compartmentMap.put(comp.id, comp);			
		}
		l=document.getElementsByTagName(SPECIES);
		speciesMap=new HashMap<String, SBMLSpecies>();
		for(int i=0; i!=l.getLength(); ++i)
		{
			Element e=(Element)l.item(i);
			SBMLSpecies comp=buildSpecies(e);			
			speciesMap.put(comp.id, comp);			
		}
		l=document.getElementsByTagName(REACTION);
		reactions=new ArrayList<SBMLReaction>();
		for(int i=0; i!=l.getLength(); ++i)
		{
			Element e=(Element)l.item(i);
			SBMLReaction comp=buildReaction(e);			
			reactions.add(comp);			
		}


	}

	public static void main(String[] args) throws Exception
	{
		SBMLParser parser=new SBMLParser();
		parser.parse("/home/symons/Anaconda/sbml/curated/BIOMD0000000001.xml");		
//		Graph g=parser.createGraph();
//		GraphModel model=new DefaultGraphModel(g);
//		GraphCanvas canvas=new GraphCanvas(model);
//		canvas.setLayouter(new FruchtermanReingoldLayout());
//		JFrame frame=new JFrame("Zorp");
////		frame.add(canvas);
////		frame.pack();
////		frame.setVisible(true);
//		
		for(int i=1; i!=9; ++i)
		{
			parser.parse("/home/symons/Anaconda/sbml/curated/BIOMD000000000"+i+".xml");	
		}
		
		for(int i=10; i!=99; ++i)
		{
			parser.parse("/home/symons/Anaconda/sbml/curated/BIOMD00000000"+i+".xml");	
		}
		
		for(int i=100; i!=250; ++i)
		{
			parser.parse("/home/symons/Anaconda/sbml/curated/BIOMD0000000"+i+".xml");	
		}
		

	}
}
