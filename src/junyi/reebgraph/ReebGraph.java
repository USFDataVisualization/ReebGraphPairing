package src.junyi.reebgraph;

//import usf.saav.topology.join;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import usf.saav.topology.join.JoinTree;

import usf.saav.mesh.Mesh;
//import usf.saav.topology.join.JoinTree;
//import usf.saav.topology.join.AugmentedJoinTree.AugmentedJoinTreeNode;
//import usf.saav.topology.join.JoinTree.Node;



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
	
	ArrayList<Node> neighbors = new ArrayList<Node>();
	
	public HashMap<Integer, Node> vmap = new HashMap<Integer, Node>();
	//int ct = 0;
	//short ect = 0;
	float min = Float.MAX_VALUE;
	float max = -Float.MAX_VALUE;
	float persistence;
	
	
	
	public void addNode(int v, float fn) {
		Node n = new Node();
		n.fn = fn;
		n.id = v;
		

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
		a.v1=v1;
		
		a.v2 = v2;

		a.path = new ArrayList<Node>();
		
		
		ar.add(a);
		
		//an.get(a.v1).next.add(a);
		//an.get(a.v2).prev.add(a);
	}

	public ArrayList<Node> neighbors() {
		// TODO Auto-generated method stub
		return neighbors;
	}
	
	public void addNeighbor(Node n) {
		// TODO Auto-generated method stub
		neighbors.add(n);
	}
	
	
	 // let us print all the elements available in list
	public void printNodes() {
	   for(Node nd : an) {
	   System.out.println("Node = " + nd.id()); 
	   System.out.println(nd.neighbors().size());
	     for(Node neighbor : nd.neighbors())
	     {  
	    	 System.out.println(neighbor.id()+ " node ");	    	 
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
	
	protected Mesh cl;
	private Comparator<? super Node> comparator;
	
	//@Override
	public void run() {
		//print_info_message( "Building tree..." );

		//JoinTree jt = new JoinTree();
		// Build a join tree.
		JoinTree jt = new JoinTree( cl, comparator );
		jt.run();

		//head = processTree( jt.getRoot() );
		
		//calculatePersistence();
		
		//for(int i = 0; i < size(); i++){
		//	float per = getPersistence(i);
		//	if( Float.isNaN(per) )
		//		global_extreme = (AugmentedJoinTreeNode) getNode(i);
		//}
		
		//print_info_message( "Building tree complete" );
	}
	
}