package mayday.graphviewer.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mayday.core.structures.graph.Graph;
import mayday.core.structures.graph.Node;

/**
 * Provides an implementation of the Bron-Kerbosch-Algorithm for finding maximal cliques.
 * @author Stephan Symons
 *
 */
public class BronKerbosch
{
	private List<Set<Node>> maximalCliques;
	
	private Graph g;
	
	private void BronKerboschRecurse(Set<Node> R, Set<Node> P, Set<Node> X )
	{
		if(P.isEmpty() && X.isEmpty())
		{
			maximalCliques.add(R);
		}
		Set<Node> PP=new HashSet<Node>(P);
		Set<Node> XX=new HashSet<Node>(X);
		for(Node v:P)
		{
			Set<Node> RR=new HashSet<Node>(R);
			RR.add(v);
			Set<Node> PPR= new HashSet<Node>(PP);
			Set<Node> Nv=g.getNeighbors(v);
			PPR.retainAll(Nv);
			Set<Node> XXR= new HashSet<Node>(XX);
			XXR.retainAll(Nv);
			BronKerboschRecurse(RR, PPR, XXR);
			PP.remove(v);
			XX.add(v);
		}
	}
	
	private void BronKerboschRecurse2(Set<Node> R, Set<Node> P, Set<Node> X )
	{
		if(P.isEmpty() && X.isEmpty())
		{
			maximalCliques.add(R);
			return;
		}
		
		int degU=0;
		Node u=null;
		for(Node n: P)
		{
			int d=g.getDegree(n);
			if(d >= degU)
			{
				degU=d;
				u=n;
			}
		}
		Set<Node> PP=new HashSet<Node>(P);
		Set<Node> PPP=new HashSet<Node>(P);
		PPP.removeAll(g.getNeighbors(u));
		Set<Node> XX=new HashSet<Node>(X);
		for(Node v:PPP)
		{
			Set<Node> RR=new HashSet<Node>(R);
			RR.add(v);
			Set<Node> PPR= new HashSet<Node>(PP);
			Set<Node> Nv=g.getNeighbors(v);
			PPR.retainAll(Nv);
			Set<Node> XXR= new HashSet<Node>(XX);
			XXR.retainAll(Nv);
			BronKerboschRecurse2(RR, PPR, XXR);
			PP.remove(v);
			XX.add(v);
		}
	}
	
	public List<Set<Node>> bronKerbosch(Graph g)
	{
		this.g=g;
		maximalCliques=new ArrayList<Set<Node>>();
		BronKerboschRecurse(Collections.<Node>emptySet(), g.getNodes(), Collections.<Node>emptySet());
		return maximalCliques;
	}
	
	public List<Set<Node>> bronKerbosch2(Graph g)
	{
		this.g=g;
		maximalCliques=new ArrayList<Set<Node>>();
		BronKerboschRecurse2(Collections.<Node>emptySet(), g.getNodes(), Collections.<Node>emptySet());
		return maximalCliques;
	}
	

}
