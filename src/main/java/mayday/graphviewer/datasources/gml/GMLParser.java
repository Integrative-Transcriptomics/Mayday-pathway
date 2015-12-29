package mayday.graphviewer.datasources.gml;

import java.io.FileInputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mayday.core.structures.graph.Edge;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;
import mayday.core.structures.graph.edges.Edges;
import mayday.core.structures.graph.io.GraphExport;
import mayday.core.structures.graph.nodes.MultiProbeNode;

@SuppressWarnings("all")
public class GMLParser 
{
	private KeyValueObject masterObject;

	private static final String ID="id"; // Integer.  Defines an identification number for an object. This is usually used to represent pointers.
	private static final String	LABEL="label"; // string Defines a label attached to an object.

	private static final String COMMENT ="comment"; // string  Defines a comment embedded in a GML file. Comments are ignored by the application.
	private static final String CREATOR= "Creator"; // string Shows which application created this file and should therefore only be used once per file at the top level. .Creator is obviously unsafe.

	private static final String GRAPHICS="graphics"; // list Describes graphics which are used to draw a particular object.Within graphics, the following keys are defined:
	private static final String X="x"; // float Defines the x coordinate of the center of the object.
	private static final String Y="y"; // float Defines the y coordinate of the center of the object.
	private static final String Z="z"; // float Defines the z coordinate of the center of the object.

	private static final String W="w"; // float Defines the width of the object.
	private static final String H="h"; // float Defines the height of the object.
	private static final String D="d"; // float Defines the depth of the object.

	private static final String GRAPH="graph"; // list  Describes a graph. private static final String
	private static final String DIRECTED="directed"; // integer Specifies whether a graph is directed (1) or undirected (0). Default is undirected.
	private static final String NODE="node"; // list Describes a node. Each non isolated node must have an attribute
	private static final String EDGE="edge"; // list  Describes an edge. Each edge must have .graph.edge.source and.graph.edge.source attributes.
	private static final String SOURCE="source"; // int and
	private static final String TARGET="target"; // int Specify the end nodes of an edge by their id keys. 

	private GML parser;

	public GMLParser() 
	{
		try
		{
			parser=new GML(new StringReader(""));
		}catch(Error e)
		{
			parser.ReInit(new StringReader(""));
		}
	}

	public List<Graph> parse(String file) throws Exception
	{
//		if(parser==null)
//			parser = new GML(new FileInputStream(file));
//		else
		parser.ReInit(new FileInputStream(file));
		masterObject =parser.topLevel();

		List<Graph> res=new ArrayList<Graph>();

		for(KeyValueObject g:masterObject.getChildren())
		{
			//			
			if(g.getKey().equals(GRAPH))
			{
				Graph graph=new Graph();
				if(g.containsKey(LABEL))
					graph.setName(g.get(LABEL).substring(1, g.get(LABEL).length()-1));
				else
					graph.setName(g.get(ID));

				Map<String, Node> nodeMap=new HashMap<String, Node>();

				for(KeyValueObject gc:g.getChildren())
				{
					if(gc.getKey().equals(NODE))
					{
						MultiProbeNode n=new MultiProbeNode(graph);

						if(gc.containsKey(LABEL))
							n.setName(gc.get(LABEL).substring(1, gc.get(LABEL).length()-1));
						else
							n.setName(gc.get(ID));

						for(KeyValueObject np:gc.getChildren())
						{
							if(np.getKey().equals(GRAPHICS))
							{
								String s=np.get(X)+","+np.get(Y)+","+np.get(W)+","+np.get(H);
								n.setProperty(GraphExport.GEOMETRY_KEY, s);
							}
						}
						graph.addNode(n);
						nodeMap.put(gc.get(ID), n);					
					}
				}
				for(KeyValueObject gc:g.getChildren())
				{
					if(gc.getKey().equals(EDGE))
					{
						Node s=nodeMap.get(gc.get(SOURCE));
						Node t=nodeMap.get(gc.get(TARGET));
						Edge e=new Edge(s, t);
						e.setRole(Edges.Roles.EDGE_ROLE);
						if(gc.containsKey(LABEL))
							e.setName(gc.get(LABEL).substring(1, gc.get(LABEL).length()-1));
						else
							e.setName(gc.get(ID));

						graph.connect(e);						
					}
				}				
				res.add(graph);				
			}
		}

		return res;
	}






}
