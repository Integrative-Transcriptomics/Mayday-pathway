package mayday.graphviewer.datasources.biopax2;

import java.io.FileReader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.edges.Edges;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.graph.nodes.Nodes;
import mayday.core.structures.maps.MultiHashMap;
import mayday.graphviewer.core.SBGNRoles;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class BioPaxSqueezer2
{
	private Connection con;

	private PreparedStatement subjectForPO;
	private PreparedStatement objectForXRefId;
	private PreparedStatement objectForSubject;
	private PreparedStatement subjectForObject;
	private PreparedStatement objectTypeQuery;
	private PreparedStatement propertyQuery;

	public static final String SMALL_MOLECULE = "smallMolecule";
	public static final String PROTEIN = "protein";
	public static final String RNA = "rna";
	public static final String DNA = "dna";
	public static final String COMPLEX = "complex";

	public static final String BIOCHEMICAL_REACTION="biochemicalReaction";
	public static final String CATALYSIS="catalysis";
	public static final String MODULATION="modulation";


	public static final String NAME="NAME";
	public static final String COMMENT="COMMENT";
	public static final String SHORT_NAME="SHORT_NAME";
	public static final String SYNONYMS="SYNONYMS";

	public static final String LEFT="LEFT";
	public static final String RIGHT="RIGHT";
	public static final String TERM="TERM";
	public static final String CONTROLLED="CONTROLLED";
	public static final String CONTROLLER="CONTROLLER";
	public static final String CONTROL_TYPE="CONTROL-TYPE";
	public static final String DIRECTION="DIRECTION";
	public static final String EC_NUMBER="EC-NUMBER";

	public static final String CELLULAR_LOCATION="CELLULAR-LOCATION";
	public static final String STOICHIOMETRIC_COEFFICIENT="STOICHIOMETRIC-COEFFICIENT";
	public static final String PHYSICAL_ENTITY="PHYSICAL-ENTITY";

	public static final String PATHWAY="pathway";
	public static final String PATHWAY_COMPONENTS="PATHWAY-COMPONENTS";
	public static final String PATHWAY_STEP="pathwayStep";
	public static final String STEP_INTERACTIONS="STEP-INTERACTIONS";
	public static final String NEXT_STEP="NEXT-STEP";

	public static final String REVERSIBLE="REVERSIBLE";
	public static final String PHYSIOL_LEFT_TO_RIGHT="PHYSIOL-LEFT-TO-RIGHT";
	public static final String PHYSIOL_RIGHT_TO_LEFT="PHYSIOL-RIGHT-TO-LEFT"; 
	public static final String IRREVERSIBLE_LEFT_TO_RIGHT="IRREVERSIBLE-LEFT-TO-RIGHT";
	public static final String IRREVERSIBLE_RIGHT_TO_LEFT="IRREVERSIBLE-RIGHT-TO-LEFT";

	public static final String INHIBITION="INHIBITION";
	public static final String ACTIVATION="ACTIVATION";

	public static final String XREF="xref";
	public static final String XREF_KEY="XREF";
	public static final String DB="DB";
	public static final String ID="ID";

	private static Map<String, BioPaxSqueezer2> staticMap;

	public static BioPaxSqueezer2 getSharedInstance(String file) throws Exception
	{
		if(staticMap==null)
			staticMap=new HashMap<String, BioPaxSqueezer2>();
		if(!staticMap.containsKey(file))
		{
			BioPaxSqueezer2 sq=new BioPaxSqueezer2(file);
			staticMap.put(file,sq);
		}
		return staticMap.get(file);
	}

	public BioPaxSqueezer2(String file, Map<String, Boolean> skipTokens) throws Exception
	{

		Class.forName("org.sqlite.JDBC");
		con = DriverManager.getConnection("jdbc:sqlite::memory:");
		Statement st=con.createStatement();

		st.execute("CREATE TABLE tuples( " +
		"subject TEXT,  pred TEXT, object TEXT);");
		st.execute("CREATE INDEX subjectIdx ON tuples (subject)");

		XMLReader parser;
		parser = XMLReaderFactory.createXMLReader();
		parser.setEntityResolver(new EntityResolver() 
		{
			public InputSource resolveEntity(String publicID, String systemID)
			throws SAXException 
			{
				return new InputSource(new StringReader(""));
			}
		});

		BioPaxHandler3 handler=new BioPaxHandler3(con, skipTokens);
		parser.setContentHandler(handler);		
		parser.parse(new InputSource(new FileReader(file)));


		subjectForPO=con.prepareStatement("SELECT subject FROM tuples WHERE pred=? AND object=?");
		objectForXRefId=con.prepareStatement("SELECT t2.subject FROM tuples AS t1, tuples AS t2 WHERE t1.object=? AND t1.subject=t2.object");
		objectForSubject=con.prepareStatement("SELECT object FROM tuples WHERE subject=? AND pred=? ");
		subjectForObject=con.prepareStatement("SELECT subject FROM tuples WHERE object=? AND pred=? ");
		objectTypeQuery=con.prepareStatement("SELECT object FROM tuples WHERE subject=? AND pred='OBJECT-TYPE' LIMIT 1");
		propertyQuery=con.prepareStatement("SELECT pred,object FROM tuples WHERE subject=?");
	}

	public BioPaxSqueezer2(String file) throws Exception
	{
		this(file,new HashMap<String, Boolean>());
	}

	public static void main(String[] args) throws Exception
	{
		long t=System.currentTimeMillis();
		BioPaxSqueezer2 sq=new BioPaxSqueezer2("/home/symons/Anaconda/yeast/biopax-level2.owl");
		System.out.println("#2:   "+ (System.currentTimeMillis()-t));
		t=System.currentTimeMillis();

		List<String> pws=sq.getObjectsOfType(BioPaxSqueezer2.PATHWAY);
		for(String pw: pws)
		{
			System.out.println(sq.getObject(pw, BioPaxSqueezer2.NAME));
			t=System.currentTimeMillis();
			List<String> pc=sq.getParticipantXRefForPathway(pw);		
			System.out.println("#2:   "+ (System.currentTimeMillis()-t));
//			for(String s: pc)
//				System.out.println(s);
		}
System.out.println("#2:   "+ (System.currentTimeMillis()-t));

		//		Statement st=sq.con.createStatement();
		//		ResultSet res=st.executeQuery("SELECT * FROM tuples WHERE subject='chemicalStructure618216'");
		//		while(res.next())
		//		{
		//			System.out.println(res.getString(1)+"\t"+res.getString(2)+"\t"+res.getString(3));
		//		}
		t=System.currentTimeMillis();
		//		System.out.println(sq.getObjectsOfType(BIOCHEMICAL_REACTION).size());
		//		System.out.println("#2:   "+ (System.currentTimeMillis()-t));
		//
		//		t=System.currentTimeMillis();
		//		System.out.println(sq.getProteinForXref("YLR038C-MONOMER"));
		//		System.out.println("#2:   "+ (System.currentTimeMillis()-t));
		//
		//		t=System.currentTimeMillis();
		//		System.out.println(sq.getReactionsForPathway("pathway632731") );
		//		System.out.println("#2:   "+ (System.currentTimeMillis()-t));
	}

	public List<String> getObjectsOfType(String targetType) throws SQLException
	{		
		subjectForPO.setString(1, TripleStorage.OBJECT_TYPE);
		subjectForPO.setString(2, targetType);

		ResultSet res= subjectForPO.executeQuery();
		List<String> result=new ArrayList<String>();
		while(res.next())
		{
			result.add(res.getString(1));
		}
		res.close();
		return result;
	}

	public String getProteinForXref(String id) throws SQLException
	{		
		objectForXRefId.setString(1,id);
		ResultSet res=objectForXRefId.executeQuery();
		String result=null;
		while(res.next())
			result=res.getString(1);
		res.close();
		return result;
	}

	public List<String> getReactionsForPathway(String pathwayID) throws SQLException
	{
		Statement st=con.createStatement();
		ResultSet res=st.executeQuery(
				"SELECT t3.subject FROM tuples AS t1, tuples AS t2, tuples AS t3 " +
				" WHERE  t1.subject='"+pathwayID+"' " +
				" AND t1. pred='PATHWAY-COMPONENTS' AND t1.object=t2.subject " +
				" AND t2.pred='STEP-INTERACTIONS' AND t2.object=t3.subject AND t3.pred='OBJECT-TYPE'" +
		" AND t3.object='biochemicalReaction'");
		List<String> result=new ArrayList<String>();

		while(res.next())
		{
			result.add(res.getString(1));
		}
		res.close();
		return result;
	}

	public List<String> getObject(String subject, String pred) throws SQLException
	{
		objectForSubject.setString(1, subject);
		objectForSubject.setString(2, pred);
		ResultSet res=objectForSubject.executeQuery();
		List<String> result=new ArrayList<String>();
		while(res.next())
		{
			result.add(res.getString(1));
		}
		res.close();
		return result;
	}

	public List<String> getSubject(String object, String pred) throws SQLException
	{
		subjectForObject.setString(1, object);
		subjectForObject.setString(2, pred);
		ResultSet res=subjectForObject.executeQuery();
		List<String> result=new ArrayList<String>();
		while(res.next())
		{
			result.add(res.getString(1));
		}
		res.close();
		return result;
	}

	public String getObjectType(String subject) throws SQLException
	{
		objectTypeQuery.setString(1, subject);
		ResultSet res=objectTypeQuery.executeQuery();
		String result=null;
		if(res.next())
		{
			result=res.getString(1);
		}
		res.close();
		return result;
	}

	public Graph getReactionGraph(String id) throws SQLException
	{
		List<String> ids=new ArrayList<String>();
		ids.add(id);
		return getReactionGraph(ids);
	}

	public Graph getReactionGraph(List<String> reactionIDs) throws SQLException
	{
		Graph res=new Graph();
		Map<String, Node> nodeMap=new HashMap<String, Node>();
		for(String reactionID: reactionIDs)
		{
			// create the reaction node and equip it with comments. 
			MultiProbeNode reactionNode=new MultiProbeNode(res);
			reactionNode.setRole(SBGNRoles.PROCESS_ROLE);

			String name=getObject(reactionID, NAME).get(0);
			reactionNode.setName(name);

			List<String> lastRes=getObject(reactionID, EC_NUMBER);
			if(!lastRes.isEmpty())
				reactionNode.setProperty(EC_NUMBER, lastRes.get(0));
			res.addNode(reactionNode);

			// get participants
			List<Node> leftNodes=new ArrayList<Node>();
			lastRes=getObject(reactionID, LEFT);

			for(String left:lastRes)
			{
				// get the entity for the participant:
				String entity=getObject(left, PHYSICAL_ENTITY).get(0);
				if(nodeMap.containsKey(entity))
				{
					leftNodes.add(nodeMap.get(entity));
				}else
				{
					Node leftNode=createParticipantNode(left, res);
					res.addNode(leftNode);
					leftNodes.add(leftNode);					
					nodeMap.put(entity, leftNode);
				}
			}

			List<Node> rightNodes=new ArrayList<Node>();
			lastRes=getObject(reactionID, RIGHT);

			for(String right:lastRes)
			{
				String entity=getObject(right, PHYSICAL_ENTITY).get(0);
				if(nodeMap.containsKey(entity))
				{
					rightNodes.add(nodeMap.get(entity));
				}else
				{
					Node rightNode=createParticipantNode(right, res);
					res.addNode(rightNode);
					rightNodes.add(rightNode);	
					nodeMap.put(entity, rightNode);
				}
			}	
			// get controls
			lastRes=getSubject(reactionID, CONTROLLED);
			boolean ltr=true;
			for(String control: lastRes)
			{
				String controllerId= getObject(control, CONTROLLER).get(0);
				String entity=getObject(controllerId, PHYSICAL_ENTITY).get(0);
				// change ltr if necessary
				Node cNode=nodeMap.get(entity);
				if(cNode==null)
				{
					cNode= createParticipantNode(controllerId, res);
					res.addNode(cNode);
					nodeMap.put(entity, cNode);
				}
				String objectType=getObjectType(control);
				String controlName=getObject(control, NAME).get(0);
				if(objectType.equals(CATALYSIS))
				{
					Edge e=new Edge(cNode,reactionNode);
					e.setName(controlName);
					e.setRole(SBGNRoles.CATALYSIS_ROLE);
					res.connect(e);
					List<String> dirs=getObject(control, DIRECTION);
					if(!dirs.isEmpty())
					{
						if(dirs.get(0).contains("RIGHT-TO-LEFT"))
							ltr=false;					
					}
				}
				if(objectType.equals(MODULATION))
				{
					Edge e=new Edge(cNode,reactionNode);
					e.setName(controlName);
					List<String> cts=getObject(control, CONTROL_TYPE);
					if(!cts.isEmpty())
					{
						String ct=cts.get(0);
						if(ct.startsWith(INHIBITION))
							e.setRole(SBGNRoles.INHIBITION_ROLE);
						if(ct.startsWith(ACTIVATION))
							e.setRole(SBGNRoles.NECESSARY_STIMULATION_ROLE);
					}else
						e.setRole(SBGNRoles.MODULATION_ROLE);

					res.connect(e);
				}
			}
			// connect according to rule	
			for(Node n: leftNodes)
			{
				Edge e=new Edge(n,reactionNode);
				e.setRole(ltr?SBGNRoles.CONSUMPTION_ROLE:SBGNRoles.PRODUCTION_ROLE);
				res.connect(e);
			}
			for(Node n: rightNodes)
			{
				Edge e=new Edge(reactionNode,n);
				e.setRole(ltr?SBGNRoles.PRODUCTION_ROLE:SBGNRoles.CONSUMPTION_ROLE);
				res.connect(e);
			}			
		}

		return res;
	}

	public Graph getReactionSubgraph(List<String> ids, Set<String> ignore) throws Exception
	{
		Graph res=new Graph();
		Map<String, Node> nodeMap=new HashMap<String, Node>();
		MultiHashMap<String, String> reactionParticipantsMap=new MultiHashMap<String, String>();

		Statement stR=con.createStatement();
		ResultSet res1=stR.executeQuery("SELECT t1.subject, t3.object " +
				" FROM tuples AS t1, tuples AS t2, tuples AS t3 "+
				" WHERE t1.object='biochemicalReaction' AND t1.subject=t2.subject " +
				"  AND (t2.pred='RIGHT' OR t2.pred='LEFT') " +
		"  AND t3.subject=t2.object AND t3.pred='PHYSICAL-ENTITY'");

		while(res1.next())
		{
			reactionParticipantsMap.put(res1.getString(1), res1.getString(2));
		}

		for(String reactionID: reactionParticipantsMap.keySet())
		{
			// create the reaction node and equip it with comments. 
			MultiProbeNode reactionNode=new MultiProbeNode(res);
			reactionNode.setRole(SBGNRoles.PROCESS_ROLE);

			String name=getObject(reactionID, NAME).get(0);
			reactionNode.setName(name);

			List<String> lastRes=getObject(reactionID, EC_NUMBER);
			if(!lastRes.isEmpty())
				reactionNode.setProperty(EC_NUMBER, lastRes.get(0));
			res.addNode(reactionNode);

			MultiHashMap<String, String> xrefs=new MultiHashMap<String, String>();

			lastRes=getSubject(reactionID, CONTROLLED);
			for(String control: lastRes)
			{
				String controllerId= getObject(control, CONTROLLER).get(0);
				String entity=getObject(controllerId, PHYSICAL_ENTITY).get(0);
				Map<String, String> xr= getXRefIds(entity);

				for(String s: xr.keySet())
				{
					xrefs.put(s, xr.get(s));
				}					
			}

			for(String s: xrefs.keySet())
			{
				reactionNode.setProperty(s, xrefs.get(s).toString());
			}
			nodeMap.put(reactionID, reactionNode);			
		}

		for(int i=0; i!= ids.size(); ++i)
		{
			for(int j=i; j!=ids.size(); ++j)
			{
				if(i==j) continue;				
				Set<String> ids1=new HashSet<String>(reactionParticipantsMap.get(ids.get(i)));
				Set<String> ids2=new HashSet<String>(reactionParticipantsMap.get(ids.get(j)));

				ids1.retainAll(ids2);

				for(Iterator<String> iter=ids1.iterator(); iter.hasNext(); )
				{
					String s=iter.next();
					if(ignore.contains(getObject(s, NAME).get(0)))
					{
						iter.remove();
					}					
				}

				if(!ids1.isEmpty())
				{
					Edge e=new Edge(nodeMap.get(ids.get(i)),nodeMap.get(ids.get(j)));
					e.setRole(Edges.Roles.NO_ARROW_EDGE);
					res.connect(e);
				}					
			}
		}
		return res;
	}

	public Graph getPathwayGraph(List<String> ids, Set<String> ignore) throws SQLException
	{
		Graph res=new Graph();
		Map<String, Node> nodeMap=new HashMap<String, Node>();
		MultiHashMap<String, String> pathwayParticipantsMap=new MultiHashMap<String, String>();

		PreparedStatement stmt=con.prepareStatement(
				"SELECT t4.object " +
				"FROM tuples AS t1, tuples AS t2, tuples AS t3, tuples AS t4 " +
				" WHERE t1.subject=? AND t1.pred='PATHWAY-COMPONENTS' " +
				" AND t1.object=t2.subject AND t2.pred='STEP-INTERACTIONS' AND t2.object=t3.subject AND ( t3.pred='LEFT' OR t3.pred='RIGHT')" +
		" AND t3.object=t4.subject AND t4.pred='PHYSICAL-ENTITY'");

		PreparedStatement controllerQuery=con.prepareStatement("SELECT t4.object " +
				" FROM tuples AS t1, tuples AS t2, tuples AS t3, tuples AS t4" +
				" WHERE  t1.subject=?  AND t1.pred='PATHWAY-COMPONENTS' " +
				" AND t1.object=t2.subject AND  t2.pred='STEP-INTERACTIONS'" +
				" AND t2.object=t3.subject AND t3.pred='CONTROLLER' " +
		" AND t3.object=t4.subject AND t4.pred='PHYSICAL-ENTITY'");

		for(String pathwayId: ids)
		{
			// create the pathway node and equip it with comments. 
			MultiProbeNode pathwayNode=new MultiProbeNode(res);
			pathwayNode.setRole(Nodes.Roles.PROBES_ROLE);

			String name=getObject(pathwayId, NAME).get(0);
			pathwayNode.setName(name);
			res.addNode(pathwayNode);

			nodeMap.put(pathwayId, pathwayNode);	

			controllerQuery.setString(1,pathwayId);
			ResultSet cres=controllerQuery.executeQuery();
			List<String> controllers=new ArrayList<String>();
			while(cres.next())
			{
				controllers.add(cres.getString(1));
			}

			MultiHashMap<String, String> xrefs=new MultiHashMap<String, String>();

			for(String controllerId:controllers)
			{
				Map<String, String> xr=getXRefIds(controllerId);
				for(String s: xr.keySet())
				{
					xrefs.put(s, xr.get(s));
				}				
			}			
			for(String s: xrefs.keySet())
			{
				pathwayNode.setProperty(s, xrefs.get(s).toString());
			}

			stmt.setString(1, pathwayId);
			cres=stmt.executeQuery();

			while(cres.next())
			{
				pathwayParticipantsMap.put(pathwayId,cres.getString(1));
			}		
		}



		for(int i=0; i!= ids.size(); ++i)
		{
			for(int j=i; j!=ids.size(); ++j)
			{
				if(i==j) continue;				
				Set<String> ids1=new HashSet<String>(pathwayParticipantsMap.get(ids.get(i)));
				Set<String> ids2=new HashSet<String>(pathwayParticipantsMap.get(ids.get(j)));

				ids1.retainAll(ids2);

				for(Iterator<String> iter=ids1.iterator(); iter.hasNext(); )
				{
					String s=iter.next();
					if(ignore.contains(getObject(s, NAME).get(0)))
					{
						iter.remove();
					}					
				}

				if(!ids1.isEmpty())
				{
					Edge e=new Edge(nodeMap.get(ids.get(i)),nodeMap.get(ids.get(j)));
					e.setRole(Edges.Roles.NO_ARROW_EDGE);
					e.setWeight(ids1.size());
					res.connect(e);
				}					
			}
		}
		return res;
	}

	public Node createParticipantNode(String participantID, Graph g) throws SQLException
	{
		MultiProbeNode node=new MultiProbeNode(g);

		List<String> lastRes=getObject(participantID, STOICHIOMETRIC_COEFFICIENT);
		if(!lastRes.isEmpty())
			node.setProperty(STOICHIOMETRIC_COEFFICIENT, lastRes.get(0));

		Statement st=con.createStatement();
		ResultSet res= st.executeQuery("" +
				"SELECT t2.object " +
				"FROM tuples AS t1, tuples AS t2 " +
				"WHERE t1.subject='"+participantID+"' " +
		"AND t1.pred='CELLULAR-LOCATION' AND t1.object=t2.subject AND t2.pred='TERM'");
		if(res.next())
			node.setProperty(CELLULAR_LOCATION, res.getString(1));

		List<String> pes=getObject(participantID, PHYSICAL_ENTITY);
		String eId=pes.get(0);

		Map<String, String> xrefs=getXRefIds(eId);
		for(String x:xrefs.keySet())
		{
			node.setProperty(x, xrefs.get(x));
		}

		String type=getObject(eId, TripleStorage.OBJECT_TYPE).get(0);

		if(type.equals(PROTEIN))
			node.setRole(SBGNRoles.MACROMOLECULE_ROLE);
		if(type.equals(DNA) || type.equals(RNA))
			node.setRole(SBGNRoles.NUCLEIC_ACID_FEATURE_ROLE);
		if(type.equals(SMALL_MOLECULE) )
			node.setRole(SBGNRoles.SIMPLE_CHEMICAL_ROLE);
		if(type.equals(COMPLEX) )
			node.setRole(SBGNRoles.COMPLEX_ROLE);

		String name=getObject(eId, NAME).get(0);
		node.setName(name);
		node.setProperty(NAME, name);

		lastRes=getObject(participantID, SHORT_NAME);
		if(!lastRes.isEmpty())
			node.setProperty(SHORT_NAME, lastRes.get(0));

		return node;
	}

	private Map<String, String> getXRefIds(String id) throws SQLException
	{
		Statement st=con.createStatement();
		ResultSet r=st.executeQuery("" +
				"SELECT t2.subject ,t2.pred, t2.object " +
				"FROM tuples AS t1, tuples AS t2 " +
				"WHERE t1.subject='"+id+"' AND t1.pred='XREF' AND t1.object=t2.subject");

		Map<String, String> ids=new HashMap<String, String>();
		Map<String, String> dbs=new HashMap<String, String>();
		while(r.next())
		{
			if(r.getString(2).equals(DB))
				dbs.put(r.getString(1), r.getString(3));
			if(r.getString(2).equals(ID))
				ids.put(r.getString(1), r.getString(3));
		}		
		Map<String,String> res=new HashMap<String, String>();
		for(String s: dbs.keySet())
		{
			res.put(dbs.get(s), ids.get(s));
		}	
		return res;
	}

	public static Set<String> getListOfSmallMolecules()
	{
		Set<String> smallMolecules=new HashSet<String>();
		smallMolecules.add("H2O");
		smallMolecules.add("O2");
		smallMolecules.add("CO2");
		smallMolecules.add("ATP");
		smallMolecules.add("ADP");
		smallMolecules.add("H+");
		smallMolecules.add("CoA");
		smallMolecules.add("coenzyme A");
		smallMolecules.add("phosphate");
		smallMolecules.add("AMP");
		smallMolecules.add("NADH");
		smallMolecules.add("NADPH");
		smallMolecules.add("NADP+");
		smallMolecules.add("NAD+");
		smallMolecules.add("NADP");
		smallMolecules.add("NAD");
		smallMolecules.add("oxygen");
		smallMolecules.add("ammonia");
		smallMolecules.add("a reduced electron acceptor");
		smallMolecules.add("an oxidized electron acceptor");
		smallMolecules.add("NH3");
		smallMolecules.add("pyrophosphate");

		return smallMolecules;
	}

	public List<String> getControlsForProtein(String proteinId) throws SQLException
	{
		Statement st=con.createStatement();
		ResultSet res= st.executeQuery("" +
				"SELECT t2.subject "+
				"FROM tuples AS t1 , tuples AS t2 " +
				"WHERE  t1.object='"+proteinId+"'  AND t1.pred='PHYSICAL-ENTITY' " +
		"AND t1.subject=t2.object AND (t2.pred='CONTROLLER')");

		List<String> result=new ArrayList<String>();
		while(res.next())
		{
			result.add(res.getString(1));
		}
		res.close();
		return result;	
	}

	public List<String> getReactionsForProtein(String s) throws SQLException
	{
		Statement st=con.createStatement();
		ResultSet res=st.executeQuery("SELECT t3.object FROM tuples AS t1, tuples AS t2, tuples AS t3  " +
				" WHERE  t1.object='"+s+"'  AND t1.pred='PHYSICAL-ENTITY' " +
				" AND t1.subject=t2.object AND (t2.pred='CONTROLLER')" +
		" AND t3.subject=t2.subject AND t3.pred='CONTROLLED'");
		List<String> result=new ArrayList<String>();
		while(res.next())
		{
			result.add(res.getString(1));
		}
		res.close();
		return result;
	}

	public List<String> getParticipantsForPathway(String s) throws SQLException
	{
		Statement st=con.createStatement();
		ResultSet res=st.executeQuery("SELECT t4.object " +
				" FROM tuples AS t1  , tuples AS t2, tuples AS t3 , tuples AS t4 " +
				" WHERE t1.subject='"+s+"' AND t1.pred='PATHWAY-COMPONENTS' " +
				"    AND t2.subject=t1.object  AND t2.pred='STEP-INTERACTIONS' " +
				"    AND t3.subject=t2.object AND ( t3.pred='LEFT' OR t3.pred='RIGHT' OR t3.pred='CONTROLLER') " +
		"    AND t4.subject=t3.object AND t4.pred='PHYSICAL-ENTITY' ");


		List<String> result=new ArrayList<String>();
		while(res.next())
		{
			result.add(res.getString(1));
		}
		res.close();
		return result;
	}
	
	public List<String> getParticipantXRefForPathway(String s) throws SQLException
	{
		Statement st=con.createStatement();
		ResultSet res=st.executeQuery("SELECT t6.object " +
				" FROM tuples AS t1, tuples AS t2, tuples AS t3, tuples AS t4, tuples AS t5, tuples AS t6 " +
				" WHERE t1.subject='"+s+"' AND t1.pred='PATHWAY-COMPONENTS' " +
				"     AND t2.subject=t1.object  AND t2.pred='STEP-INTERACTIONS' " +
				"     AND t3.subject=t2.object AND ( t3.pred='LEFT' OR t3.pred='RIGHT' OR t3.pred='CONTROLLER') " +
				"     AND t4.subject=t3.object AND t4.pred='PHYSICAL-ENTITY' " +
				"     AND t5.subject=t4.object AND t5.pred='XREF' " +
				"	  AND t6.subject=t5.object AND t6.pred='ID'");


		List<String> result=new ArrayList<String>();
		while(res.next())
		{
			result.add(res.getString(1));
		}
		res.close();
		return result;
	}

	public List<String> getPathwaysForProtein(String s) throws SQLException
	{
		Statement st=con.createStatement();
		ResultSet res=st.executeQuery("SELECT t4.subject " +
				" FROM tuples AS t1  , tuples AS t2, tuples AS t3 , tuples AS t4 " +
				" WHERE  t1.object LIKE '"+s+"' AND t1.pred='PHYSICAL-ENTITY'" +
				" AND t1.subject=t2.object AND (t2.pred='CONTROLLER') " +
				" AND t3.object=t2.subject AND t3.pred='STEP-INTERACTIONS' " +
		" AND t3.subject=t4.object AND t4.pred='PATHWAY-COMPONENTS'");
		List<String> result=new ArrayList<String>();
		while(res.next())
		{
			result.add(res.getString(1));
		}
		res.close();
		return result;
	}

	public Map<String,String> getAttributesForItem(String id) throws SQLException
	{
		propertyQuery.setString(1, id);
		ResultSet res=propertyQuery.executeQuery();
		Map<String,String> result=new HashMap<String, String>();
		while(res.next())
		{
			result.put(res.getString(1), res.getString(2));
		}
		res.close();
		return result;
	}

	public List<String[]> getXrefForItem(String id) throws SQLException
	{
		PreparedStatement st=con.prepareStatement(
				"SELECT t2.pred, t2.object " +
				" FROM tuples AS t1, tuples AS t2 " +
				" WHERE t1.subject=? AND t1.pred='XREF' AND t1.object=t2.subject " +
		" ORDER BY t1.subject ");
		st.setString(1, id);
		List<String[]> result=new ArrayList<String[]>();
		ResultSet res=st.executeQuery();
		String[] r=null;
		while(res.next())
		{
			if(res.getString(1).equals(TripleStorage.OBJECT_TYPE))
			{	
				if(r!=null)
					result.add(r);
				r=new String[2];
			}
			if(res.getString(1).equals(DB))
				r[1]=res.getString(2);
			if(res.getString(1).equals(ID))
				r[2]=res.getString(2);

		}
		if(r!=null)
			result.add(r);
		return result;
	}

}
