package mayday.graphviewer.datasources.psimi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.edges.Edges;
import mayday.core.structures.graph.nodes.MultiProbeNode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class PsiMIParser 
{
	private DocumentBuilder builder;

	public static final String INTERACTOR="interactor";
	public static final String INTERACTOR_REF="interactorRef";
	public static final String INTERACTION="interaction";

	public static final String NAMES="names";
	public static final String SHORTLABEL="shortLabel";
	public static final String FULLNAME="fullName";
	public static final String ALIAS="alias";
	public static final String ORGANISM="organism";
	public static final String COMPARTMENT="compartment";
	public static final String TISSUE="tissue";
	public static final String XREF="xref";	
	public static final String INTERACTOR_TYPE="interactorType";	
	public static final String PRIMARY_REF="primaryRef";
	public static final String SECONDARY_REF="secondaryRef";
	public static final String PARTICIPANT_LIST="participantList";
	public static final String INTERACTION_TYPE="interactionType";

	public static final String BIOLOGICAL_ROLE="biologicalRole";
	public static final String EXPERIMENTAL_ROLE_LIST="experimentalRoleList";
	public static final String EXPERIMENTAL_ROLE="experimentalRole";


	public static final String GENE="gene";
	public static final String NULCEIC_ACID="nucleic acid";
	public static final String PROTEIN="protein";
	public static final String PEPTIDE="peptide";
	public static final String POLYSACCHARIDE="polysaccharide";
	public static final String SMALL_MOLECULE="small molecule";
	public static final String COMPLEX="complex";

	private Map<String, Interactor> interactorsMap;
	private Map<String, Interaction> interactionsMap;

	public PsiMIParser() throws Exception
	{
		DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
		builder=dbf.newDocumentBuilder();
	}

	private Interactor buildInteractor(Node interactorNode)
	{
		Interactor inter=new Interactor();
		inter.id=interactorNode.getAttributes().getNamedItem("id").getNodeValue();


		NodeList children=interactorNode.getChildNodes();
		for(int i=0; i!=children.getLength(); ++i)
		{
			Node ch=children.item(i);
			if(ch.getNodeType()!= Node.ELEMENT_NODE)
				continue;
			Element e=(Element)ch;		
			if(e.getTagName().equals(NAMES))
				inter.names=buildNames(e);
			if(e.getTagName().equals(XREF))
				inter.xref=buildXref(e);
			if(e.getTagName().equals(INTERACTOR_TYPE))
				inter.interactorType=buildInteractorType(e);
			if(e.getTagName().equals(ORGANISM))
			{
				inter.organism=getOrganism(e);	
				inter.compartment=getNameForElement(e, COMPARTMENT);
				inter.tissue=getNameForElement(e, TISSUE);
			}
		}
		return inter;
	}

	private Names buildNames(Element namesNode)
	{
		Names names=new Names();

		NodeList children=namesNode.getChildNodes();
		for(int i=0; i!=children.getLength(); ++i)
		{
			Node ch=children.item(i);
			if(ch.getNodeType()!= Node.ELEMENT_NODE)
				continue;
			Element e=(Element)ch;	
			if(e.getTagName().equals(FULLNAME))
				names.name=e.getFirstChild().getTextContent();
			if(e.getTagName().equals(SHORTLABEL))
				names.shortName=e.getFirstChild().getTextContent();
			if(e.getTagName().equals(ALIAS))
			{
				if(e.getFirstChild()!=null && e.getFirstChild().getTextContent()!=null)
					names.alias.add(e.getFirstChild().getTextContent());		
			}
					
		}
		return names;
	}

	private PsiXref buildXref(Element xrefNode)
	{
		PsiXref xref=new PsiXref();

		NodeList children=xrefNode.getChildNodes();
		for(int i=0; i!=children.getLength(); ++i)
		{
			Node ch=children.item(i);
			if(ch.getNodeType()!= Node.ELEMENT_NODE)
				continue;
			Element e=(Element)ch;
			if(e.getTagName().equals(PRIMARY_REF) || e.getTagName().equals(SECONDARY_REF) )
			{
				xref.addRef(e.getAttribute("db"), e.getAttribute("id"));
				if(e.hasAttribute("secondary"))
					xref.addSecondary(e.getAttribute("db"), e.getAttribute("secondary"));	
			}		
		}		
		return xref;
	}

	private InteractorType buildInteractorType(Element iaNode)
	{
		InteractorType it=new InteractorType();		
		NodeList children=iaNode.getChildNodes();
		for(int i=0; i!=children.getLength(); ++i)
		{
			Node ch=children.item(i);
			if(ch.getNodeType()!= Node.ELEMENT_NODE)
				continue;
			Element e=(Element)ch;
			if(e.getTagName().equals(NAMES))
				it.names=buildNames(e);
			if(e.getTagName().equals(XREF))
				it.xref=buildXref(e);
		}		
		return it;
	}

	private InteractionType buildInteractionType(Element node)
	{
		InteractionType it=new InteractionType();
		NodeList children=node.getChildNodes();
		for(int i=0; i!=children.getLength(); ++i)
		{
			Node ch=children.item(i);
			if(ch.getNodeType()!= Node.ELEMENT_NODE)
				continue;
			Element e=(Element)ch;
			if(e.getTagName().equals(NAMES))
				it.names=buildNames(e);
			if(e.getTagName().equals(XREF))
				it.xref=buildXref(e);
		}		
		return it;		
	}

	private String getOrganism(Element oNode)
	{
		NodeList children=oNode.getChildNodes();
		for(int i=0; i!=children.getLength(); ++i)
		{
			Node ch=children.item(i);
			if(ch.getNodeType()!= Node.ELEMENT_NODE)
				continue;
			Element e=(Element)ch;
			if(e.getTagName().equals(NAMES))
			{
				Names names=buildNames(e);
				return names.name;
			}
		}		
		return null;
	}

	private String getNameForElement(Element oNode,String target)
	{
		NodeList children=oNode.getChildNodes();
		for(int i=0; i!=children.getLength(); ++i)
		{
			Node ch=children.item(i);
			if(ch.getNodeType()!= Node.ELEMENT_NODE)
				continue;
			Element e=(Element)ch;

			if(e.getTagName().equals(target))
			{				
				NodeList cNodes=ch.getChildNodes();
				for(int j=0; j!=cNodes.getLength(); ++j)
				{
					Node ch2=cNodes.item(j);
					if(ch2.getNodeType()!= Node.ELEMENT_NODE)
						continue;
					Element e2=(Element)ch2;
					if(e2.getTagName().equals(NAMES))
					{
						Names names=buildNames(e2);
						return names.name;						
					}
				}
			}
		}		
		return null;
	}

	private Interaction buildInteraction(Node n)
	{
		Interaction inter=new Interaction();	
		if(n.hasAttributes())
		{
			inter.id=n.getAttributes().getNamedItem("id").getNodeValue();
		}

		NodeList children=n.getChildNodes();
		for(int i=0; i!=children.getLength(); ++i)
		{
			Node ch=children.item(i);
			if(ch.getNodeType()!= Node.ELEMENT_NODE)
				continue;
			Element e=(Element)ch;		
			if(e.getTagName().equals(NAMES))
				inter.names=buildNames(e);
			if(e.getTagName().equals(XREF))
				inter.xref=buildXref(e);
			if(e.getTagName().equals(INTERACTION_TYPE))
			{
				inter.interactionTypes.add(buildInteractionType(e));
			}
			if(e.getTagName().equals(PARTICIPANT_LIST))
			{
				NodeList cc=e.getChildNodes();
				for(int j=0; j!=cc.getLength(); ++j)
				{
					Node cch=cc.item(j);
					if(cch.getNodeType()!= Node.ELEMENT_NODE)
						continue;
					Element ee=(Element)cch;	
					inter.participants.add(buildParticipant(ee));
				}							
			}
		}
		return inter;
	}

	private Participant buildParticipant(Element pn)
	{
		Participant p=new Participant();
		NodeList children=pn.getChildNodes();
		for(int i=0; i!=children.getLength(); ++i)
		{
			Node ch=children.item(i);
			if(ch.getNodeType()!= Node.ELEMENT_NODE)
				continue;
			Element e=(Element)ch;

			if(e.getTagName().equals(NAMES))
				p.names=buildNames(e);
			if(e.getTagName().equals(XREF))
				p.xrefs=buildXref(e);

			if(e.getTagName().equals(INTERACTOR))
			{
				String iaKey=e.getAttribute("id");
				p.interactors.add(interactorsMap.get(iaKey));
			}			
			if(e.getTagName().equals(INTERACTOR_REF))
			{
				String iaKey=e.getTextContent().trim();
				p.interactors.add(interactorsMap.get(iaKey));				
			}			
			if(e.getTagName().equals(EXPERIMENTAL_ROLE_LIST))
			{
				p.experimentalRole.add(getNameForElement(pn, EXPERIMENTAL_ROLE));
			}			
		}	
		p.biologicalRole=getNameForElement(pn,BIOLOGICAL_ROLE);

		return p;
	}

	public void parse(String fileName)   throws SAXException, IOException 
	{
		Document document = builder.parse(fileName);

		interactionsMap=new HashMap<String, Interaction>();
		interactorsMap=new HashMap<String, Interactor>();

		NodeList interactors=document.getElementsByTagName(INTERACTOR);
		for(int i=0; i!=interactors.getLength(); ++i)
		{

			Node interactorNode=interactors.item(i);
			Interactor ia=buildInteractor(interactorNode);
			interactorsMap.put(ia.id, ia);
		}


		NodeList interactions=document.getElementsByTagName(INTERACTION);
		for(int i=0; i!=interactions.getLength(); ++i)
		{
			Element interaction = (Element) interactions.item(i);
			Interaction ia=buildInteraction(interaction);
			if(ia.id==null)
			{
				ia.id=""+i;
			}
			interactionsMap.put(ia.id, ia);
		}
		return;
	}

	public Map<String, Interaction> getInteractions() 
	{
		return interactionsMap;
	}

	public Map<String, Interactor> getInteractors() 
	{
		return interactorsMap;
	}

	public Graph createEdgeGraph()
	{
		Graph g=new Graph();

		Map<Interactor, MultiProbeNode> interactorNodeMap=new HashMap<Interactor, MultiProbeNode>();

		for(Interaction interaction: interactionsMap.values())
		{
			for(int i=0; i!= interaction.participants.size(); ++i)
			{
				for(int j=i+1; j< interaction.participants.size(); ++j)
				{
					Interactor inti= interaction.participants.get(i).interactors.get(0);
					MultiProbeNode ni=interactorNodeMap.get(inti);
					if(ni==null)
					{
						ni=new MultiProbeNode(g);
						ni.setName(inti.names.shortName);
						g.addNode(ni);
						for(String xrefKey: inti.xref.getRefs().keySet())
						{
							ni.setProperty(xrefKey, inti.xref.getRefs().get(xrefKey));
						}	
						interactorNodeMap.put(inti, ni);							
						ni.setRole(INTERACTOR);
					}

					Interactor intj= interaction.participants.get(j).interactors.get(0);
					MultiProbeNode nj=interactorNodeMap.get(intj);
					if(nj==null)
					{
						nj=new MultiProbeNode(g);
						nj.setName(intj.names.shortName);
						g.addNode(nj);
						for(String xrefKey: intj.xref.getRefs().keySet())
						{
							nj.setProperty(xrefKey, intj.xref.getRefs().get(xrefKey));
						}	
						interactorNodeMap.put(intj, nj);							
						nj.setRole(INTERACTOR);
					}

					Edge e=new Edge(ni,nj);		
					e.setRole(Edges.Roles.BIDIRECTIONAL_EDGE);
					g.connect(e);	

				}
			}	
		}
		return g;
	}

	public Graph getDirectInteractorsGraph(String id)
	{
		//  find core;
		Interactor core=null;
		for(Interactor i: interactorsMap.values())
		{
			if(i.names.name.equals(id) || i.names.shortName.equals(id) || i.names.alias.contains(id) )
			{
				core=i;
				break;
			}
		}
		if(core==null)
			return null;

		List<Interactor> partners=new ArrayList<Interactor>();
		Graph g=new Graph();
		for(Interaction i:interactionsMap.values())
		{
			boolean f=false;
			for(Participant p: i.participants)
			{
				if(p.interactors.contains(i))
					f=true;
			}
			if(f)
			{
				for(Participant p: i.participants)
				{
					partners.addAll(p.interactors);
				}
			}
		}

		MultiProbeNode coreNode=createInteractorNode(g, core);
		
		for(String s: core.xref.getRefs().keySet())
		{
			coreNode.setProperty(s,core.xref.getRefs().get(s));
		}	
		
		for(Interactor i: partners)
		{
			if(i!=core)
			{
				MultiProbeNode n=createInteractorNode(g, i);
				
				Edge e=new Edge(coreNode,n);
				g.connect(e);			
			}
		}
		return g;
	}

	private MultiProbeNode createInteractorNode(Graph g, Interactor i)
	{
		MultiProbeNode coreNode=new MultiProbeNode(g);
		g.addNode(coreNode);
		coreNode.setName(i.names.name);
		coreNode.setProperty("Short Name", i.names.shortName);
		coreNode.setProperty("Alias", i.names.alias.toString());
		
		for(String s: i.xref.getRefs().keySet())
		{
			coreNode.setProperty(s,i.xref.getRefs().get(s));
		}
		for(String s: i.xref.getSecondary().keySet())
		{
			coreNode.setProperty(s,i.xref.getSecondary().get(s).toString());
		}		
		return coreNode;
	}
	
	public Graph createNodeGraph()
	{
		Graph g=new Graph();

		Map<Interactor, MultiProbeNode> interactorNodeMap=new HashMap<Interactor, MultiProbeNode>();

		for(Interaction interaction: interactionsMap.values())
		{
			MultiProbeNode ia=new MultiProbeNode(g);
			if(interaction.names!=null)
				ia.setName(interaction.names.name);
			ia.setRole("interaction");
			g.addNode(ia);

			for(Participant p:interaction.participants)
			{
				int i=0;
				for(Interactor inta: p.interactors)
				{
					MultiProbeNode ian=interactorNodeMap.get(inta);
					if(ian==null)
					{
						ian=new MultiProbeNode(g);
						ian.setName(inta.names.shortName);
						g.addNode(ian);
						for(String xrefKey: inta.xref.getRefs().keySet())
						{
							ian.setProperty(xrefKey, inta.xref.getRefs().get(xrefKey));
						}	
						interactorNodeMap.put(inta, ian);							
						ian.setRole(PROTEIN);
					}
					Edge e=new Edge(ian,ia);
					e.setName(p.biologicalRole);
					e.addProperty(BIOLOGICAL_ROLE, p.biologicalRole);
					g.connect(e);					
					++i;
				}
			}
		}
		return g;
	}


	public static void main(String[] args) throws Exception
	{
		PsiMIParser parser=new PsiMIParser();
		parser.parse("/home/symons/Anaconda/PSI-MI/strco_small.xml");

		parser.getDirectInteractorsGraph("SCO5088");
	}
}
