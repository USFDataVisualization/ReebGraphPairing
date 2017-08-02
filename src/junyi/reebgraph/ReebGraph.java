package src.junyi.reebgraph;

//import src.junyi.reebgraph.TriangleData.Vertex;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ReebGraph implements Serializable {
    private static final long serialVersionUID = 1L;
	
    public static final byte REGULAR = 0;
	public static final byte MINIMUM = 1;
	public static final byte MAXIMUM = 2;
	public static final byte SADDLE = 3;

	
	


	class Arc implements Comparable<Arc>, Serializable, Simplex {
		private static final long serialVersionUID = 1L;

		int v1;
		int v2;
		float fn;
		short icol;
		boolean removed;
		//ArrayList <Integer> comps = new ArrayList<Integer>();
		
		
		ArrayList<Vertex> path = new ArrayList<Vertex>();
		HashSet<Vertex> segment = new HashSet<Vertex>();
		
		public boolean equals(Object obj) {
			Arc a = (Arc) obj;
			return (a.icol == icol);
		}

		public int compareTo(Arc o) {
			float ff = fn - o.fn;
			if(ff < 0)
				return -1;
			else if(ff > 0)
				return 1;
			return 0;
		}
	}
	
	 
	class Node implements Serializable, Simplex {
		private static final long serialVersionUID = 1L;

		int v;
		boolean removed;
		float fn;
		//VertexType type;
		ArrayList<Arc> prev = new ArrayList<Arc>();
		ArrayList<Arc> next = new ArrayList<Arc>();
	}
	
	public Node [] nodes;
	public Arc [] arcs;

	ArrayList<Node> an = new ArrayList<Node>();
	ArrayList<Arc> ar = new ArrayList<Arc>();
	
	
	public HashMap<Integer, Integer> vmap = new HashMap<Integer, Integer>();
	int ct = 0;
	short ect = 0;
	float min = Float.MAX_VALUE;
	float max = -Float.MAX_VALUE;
	float persistence;
	
	
	
	public void addNode(int v, float fn) {
		Node n = new Node();
		n.fn = fn;
		n.v = v;
		

		an.add(n);
		vmap.put(v, ct);
		ct ++;
		
		max = Math.max(max, fn);
		min = Math.min(min, fn);
	}
	
	
	public void setupReebGraph() {
		nodes = (Node[]) an.toArray(new Node[0]);
		arcs = (Arc[]) ar.toArray(new Arc[0]);
		persistence = max - min;
	}
	
	public void addArc(int v1, int v2) {
		Arc a = new Arc();
		a.v1 = vmap.get(v1);
		a.v2 = vmap.get(v2);
		
		a.fn = an.get(a.v2).fn - an.get(a.v1).fn;

		a.path = new ArrayList<Vertex>();
		
		ect ++;
		a.icol = ect;
		ar.add(a);
		
		an.get(a.v1).next.add(a);
		an.get(a.v2).prev.add(a);
	}

	
	protected void removeDeg2Nodes() {
		// remove degree 2 vertices
		for(int i = 0;i < nodes.length;i ++) {
			if(!nodes[i].removed && nodes[i].next.size() == 1 && nodes[i].prev.size() == 1) {
				mergeNode(i);
			}
		}
	}
	
	private void mergeNode(int i) {
		Arc e1 = nodes[i].prev.get(0);
		Arc e2 = nodes[i].next.get(0);
		
		nodes[i].removed = true;
		e1.v2 = e2.v2;
		e2.removed = true;
		if(e2.path.size() != 0) {
			e2.path.remove(0);
		}
		e1.path.addAll(e2.path);
		e1.segment.addAll(e2.segment);
		if(e1.icol > e2.icol) {
			e1.icol = e2.icol;
		}
		e1.fn += e2.fn;
		nodes[e1.v2].prev.remove(e2);
		nodes[e1.v2].prev.add(e1);
		
	}

	
	public void outputReebGraph(PrintStream p) {
		int nv = 0;
		int ne = 0;
		for(int i = 0;i < nodes.length;i ++) {
			if(!nodes[i].removed) {
				nv ++;
			}
		}
		
		for(int i = 0;i < arcs.length;i ++) {
			if(!arcs[i].removed) {
				ne ++;
			}
		}

		p.println(nv + " " + ne);
		for(int i = 0;i < nodes.length;i ++) {
			if(!nodes[i].removed) {
				p.println(nodes[i].v + " " + nodes[i].fn );
			}
		}
		
		for(int i = 0;i < arcs.length;i ++) {
			if(!arcs[i].removed) {
				p.print(nodes[arcs[i].v1].v + " " + nodes[arcs[i].v2].v + " ");
				//for(int j = 0;j < arcs[i].comps.size();j ++) {
					//p.print(arcs[i].comps.get(j) + " ");
				//}
				p.println();
			}
		}
	}

}