package src.junyi.reebgraph;

//import src.junyi.reebgraph.TriangleData.Vertex;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;



public class ReebGraph implements Serializable {
    private static final long serialVersionUID = 1L;
	
    public static final byte REGULAR = 0;
	public static final byte MINIMUM = 1;
	public static final byte MAXIMUM = 2;
	public static final byte SADDLE = 3;

	

	
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
		a.n1.v=v1;
		
		a.n2.v = v2;
		
	

		a.path = new ArrayList<Node>();
		
		
		ar.add(a);
		
		an.get(a.n1.v).next.add(a);
		an.get(a.n2.v).prev.add(a);
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
		e1.n2.v = e2.n2.v;
		e2.removed = true;
		if(e2.path.size() != 0) {
			e2.path.remove(0);
		}
		e1.path.addAll(e2.path);
		e1.segment.addAll(e2.segment);
		
		nodes[e1.n2.v].prev.remove(e2);
		nodes[e1.n2.v].prev.add(e1);
		
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
				op.print(nodes[arcs[i].n1.v].v + " " + nodes[arcs[i].n2.v].v + " ");
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
		// We first order the points for adding to the tree.
			//	Queue< Node > tq = new PriorityQueue< Node >( size, comparator );
			//	for(int i = 0; i < sf.getWidth(); i++ ){
			//		tq.add( new Node( sf.get(i).value(), i ) );
			//	}
		
	}
	
	
	
   public void simplify2Split() {
		
		
		
	}
	
}