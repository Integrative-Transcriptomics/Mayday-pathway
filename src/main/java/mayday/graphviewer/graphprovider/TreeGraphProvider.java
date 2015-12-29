package mayday.graphviewer.graphprovider;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import mayday.clustering.hierarchical.TreeInfo;
import mayday.clustering.hierarchical.TreeMIO;
import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIType;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.edges.Edges;
import mayday.core.structures.graph.nodes.MultiProbeNode;
import mayday.core.structures.graph.nodes.Nodes;
import mayday.core.structures.maps.MultiHashMap;
import mayday.core.structures.trees.tree.Edge;
import mayday.core.structures.trees.tree.Node;
import mayday.vis3.graph.layout.CanvasLayouter;
import mayday.vis3.graph.layout.FruchtermanReingoldLayout;

public class TreeGraphProvider extends AbstractPlugin implements GraphProvider 
{
	private Map<Node, MultiProbeNode> nodeMap;

	@Override
	public Graph createGraph(MultiHashMap<DataSet, ProbeList> probeLists) 
	{
		return findUsableTrees(probeLists);


	}

	public Graph findUsableTrees(MultiHashMap<DataSet, ProbeList> probeLists) 
	{
		// find out how many trees are present in the viewmodel
		final HashMap<ProbeList,TreeInfo> candidates = new HashMap<ProbeList,TreeInfo>();

		for (ProbeList pl : probeLists.everything()) 
		{
			MIGroupSelection<MIType> mgs = pl.getDataSet().getMIManager().getGroupsForType("PAS.MIO.HierarchicalClusteringTree");
			MIType mt=null;
			for (MIGroup mg : mgs) {
				mt = mg.getMIO(pl);
				if (mt!=null)
					break;
			}
			if (mt!=null) {
				TreeInfo ti = ((TreeMIO)mt).getValue();
				candidates.put(pl, ti);
			}
		}

		if (candidates.isEmpty()) 
		{
			return null;
		}
		if (candidates.size()==1) 
		{
			ProbeList pl=candidates.keySet().iterator().next();
			return buildGraph(pl,candidates.get(pl));
		} else 
		{
			Object result = JOptionPane.showInputDialog((Component)null, "Please select a tree to display", 
					"Multiple trees found", JOptionPane.QUESTION_MESSAGE, null, 
					candidates.keySet().toArray(), candidates.keySet().iterator().next());
			if (result==null)
				return null;
			else 
			{
				return buildGraph((ProbeList)result, candidates.get((ProbeList)result));
			}
		}

	}

	private Graph buildGraph(ProbeList pl, TreeInfo ti)
	{
		if(ti.getSettings().isMatrixTransposed())
		{
			return buildExperimentGraph(ti);
		}else
		{
			return buildProbeGraph(pl, ti);
		}

	}

	private Graph buildExperimentGraph(TreeInfo ti)
	{
		Node n=ti.getTree();
		Graph g=new Graph();
		nodeMap=new HashMap<Node, MultiProbeNode>();

		traverseTree(g, n,null);

		return g;
	}

	private void traverseTree(Graph g, Node node, Node parent)
	{
		// add current node
		MultiProbeNode gn=new MultiProbeNode(g);
		gn.setName(node.getLabel());
		g.addNode(gn);
		nodeMap.put(node, gn);
		
		for(Edge e: node.getEdges())
		{
			Node nextNode=e.getOtherNode(node);
			if(nextNode==parent)
				continue;
			traverseTree(g, nextNode, node);
			mayday.core.structures.graph.Edge ge=new mayday.core.structures.graph.Edge(gn, nodeMap.get(nextNode));
			ge.setWeight(e.getLength());
			ge.setRole(Edges.Roles.NO_ARROW_EDGE);
			g.connect(ge);				
		}
	}

	private Graph buildProbeGraph(ProbeList pl, TreeInfo ti)
	{		
		Node n=ti.getTree();
		Graph g=new Graph();
		nodeMap=new HashMap<Node, MultiProbeNode>();

		traverseTree(g, n,null);
		
		for(mayday.core.structures.graph.Node node: g.getNodes())
		{
			if(node.getName().isEmpty())
			{
				node.setRole(Nodes.Roles.NODE_ROLE);
				continue;
			}
			
			MultiProbeNode mpn=(MultiProbeNode)node;
			mpn.setRole(Nodes.Roles.PROBE_ROLE);
			if(pl.contains(mpn.getName()))
			{
				Probe p=pl.getProbe(mpn.getName());
				mpn.addProbe(p);
				mpn.setName(p.getDisplayName());
			}
		}
		return g;
	}



	@Override
	public CanvasLayouter defaultLayouter() 
	{
		return new FruchtermanReingoldLayout(250);
	}

	@Override
	public String getName() 
	{
		return "Trees";
	}

	@Override
	public String toString() 
	{
		return getName();
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException 
	{
		PluginInfo pli = new PluginInfo(
				this.getClass(),
				"mayday.graphprovider.trees",
				new String[]{},
				MC,
				new HashMap<String, Object>(),
				"Stephan Symons",
				"symons@informatik.uni-tuebingen.de",
				"Import a prevoiusly calculated tree",
				getName()				
		);
		pli.addCategory(PROBES);
		return pli;			
	}
}
