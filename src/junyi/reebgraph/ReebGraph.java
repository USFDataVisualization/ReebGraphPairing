package src.junyi.reebgraph;

//import usf.saav.topology.join;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import src.junyi.reebgraph.cmd.paulReebMesh;
import src.junyi.reebgraph.cmd.paulReebMesh.ReebVertex;
import usf.saav.mesh.Mesh;
import usf.saav.topology.merge.MergeTree;
import usf.saav.topology.split.SplitTree;





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
	
	public HashMap<Integer, ReebVertex> rvmap = new HashMap<Integer, ReebVertex>();
	
	float min = Float.MAX_VALUE;
	float max = -Float.MAX_VALUE;
	float persistence;
	
	paulReebMesh rb = new paulReebMesh();
	
	ArrayList<ReebVertex> rv = new ArrayList<ReebVertex>();
	
	
	
	public void addNode(int v, float fn) {
		Node n = new Node();
		n.fn = fn;
		n.id = v;
		
		ReebVertex reebV= rb.createVertex(v,fn);

		an.add(n);
		vmap.put(v, n);
		
		rv.add(reebV);
		rvmap.put(v, reebV);
		
		
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
	
	
	
		public void printVertices() {
		   for(ReebVertex reebv : rv) {
		     System.out.println("Vertex = " + reebv.id()); 
		     System.out.println(vmap.get(reebv.id()).neighbors().size());
		     for(Node neighbor : vmap.get(reebv.id()).neighbors())
		     {  
		    	 System.out.println(neighbor.id()+ " Vertex");	    	 
		     }
		  } 
		} 
	
	
	public void run() {
		
		//paulReebMesh rb = new paulReebMesh();
		
		//ArrayList<ReebVertex> rv = new ArrayList<ReebVertex>();
		
		for(Node nd : an) {
			 rv.add(rb.createVertex(nd.id(), nd.value())); 
		 }
		 
		for(ReebVertex reebv : rv) {
		    for(Node neighbor : vmap.get(reebv.id()).neighbors())
		     {  
				  reebv.addNeighbor(rvmap.get(neighbor.id()));	    	 
		     }
			 
		 }
			   
	
		MergeTree mt = new MergeTree(rb);
		mt.run();
		System.out.println(mt.toDot());
		
		SplitTree st = new SplitTree(rb);
		st.run();
		System.out.println(st.toDot());

		
		//print_info_message( "Building tree complete" );
	}
	
}