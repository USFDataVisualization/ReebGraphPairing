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

	
	


	class Arc implements  Serializable, Simplex {
		private static final long serialVersionUID = 1L;

		int v1;
		int v2;
		//float fn;
		
		boolean removed;
		//ArrayList <Integer> comps = new ArrayList<Integer>();
		
		
		ArrayList<Vertex> path = new ArrayList<Vertex>();
		HashSet<Vertex> segment = new HashSet<Vertex>();
		
		

		//public int compareTo(Arc o) {
		//	float ff = fn - o.fn;
		//	if(ff < 0)
		//		return -1;
		//	else if(ff > 0)
		//		return 1;
		//	return 0;
		//}
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
	
	
	public HashMap<Integer, Node> vmap = new HashMap<Integer, Node>();
	//int ct = 0;
	//short ect = 0;
	float min = Float.MAX_VALUE;
	float max = -Float.MAX_VALUE;
	float persistence;
	
	
	
	public void addNode(int v, float fn) {
		Node n = new Node();
		n.fn = fn;
		n.v = v;
		

		an.add(n);
		vmap.put(v, n);
		
		
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
		a.v1 = v1;
		a.v2 = v2;
		
	

		a.path = new ArrayList<Vertex>();
		
		
		ar.add(a);
		
		an.get(a.v1).next.add(a);
		an.get(a.v2).prev.add(a);
	}

	
	public void removeDeg2Nodes() {
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
		//if(e1.icol > e2.icol) {
		//	e1.icol = e2.icol;
		//}
		//e1.fn += e2.fn;
		nodes[e1.v2].prev.remove(e2);
		nodes[e1.v2].prev.add(e1);
		
	}

	
	public void outputReebGraph(PrintStream op) {
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

		op.println(nv + " " + ne);
		for(int i = 0;i < nodes.length;i ++) {
			if(!nodes[i].removed) {
				op.println(nodes[i].v + " " + nodes[i].fn );
			}
		}
		
		for(int i = 0;i < arcs.length;i ++) {
			if(!arcs[i].removed) {
				op.print(nodes[arcs[i].v1].v + " " + nodes[arcs[i].v2].v + " ");
				//for(int j = 0;j < arcs[i].comps.size();j ++) {
					//p.print(arcs[i].comps.get(j) + " ");
				//}
				op.println();
			}
		}
	}

	
	
	public void simplify2Merge() {
		//sort nodes by fn
		//pass for lowest to highest nodes
		//find merge nodes 
		
		
	}
	
	
	
   public void simplify2Split() {
		
		
		
	}
	
}