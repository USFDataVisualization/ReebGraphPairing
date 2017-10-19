package src.junyi.reebgraph;
//package usf.saav.topology.join;

import java.io.BufferedReader;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import src.junyi.reebgraph.cmd.paulReebMesh;
import src.junyi.reebgraph.cmd.paulReebMesh.ReebVertex;
import src.junyi.reebgraph.loader.MeshLoader;
import usf.saav.topology.merge.MergeTree;
import usf.saav.topology.split.SplitTree;


public class ReebLoader2 implements MeshLoader{

    private BufferedReader reader;
	private int noNodes=0;
	private int noArcs=0;
	
	String inputReebGraph;
	
	
	private paulReebMesh reebMesh = new paulReebMesh();
	//private paulReebMesh reebMeshoneasdf = new paulReebMesh();
	
	// Connected components
	ArrayList<paulReebMesh> conn_comp = new ArrayList<paulReebMesh>();  //multiple componen
	
	// Vertices
	ArrayList<ReebVertex> rv = new ArrayList<ReebVertex>();  //multiple component
	
	//ArrayList<ReebVertex> rb = new ArrayList<ReebVertex>(); //real one reeb graph
	
	
	public HashMap<Integer, ReebVertex> rvmap = new HashMap<Integer, ReebVertex>();
	
public void setInputFile(String _inputReebGraph) {
	inputReebGraph = _inputReebGraph;
		try {
			reader = new BufferedReader(new FileReader(inputReebGraph));
			String s = reader.readLine();
			
			String[] r = s.split("\\s");
		
			while(s != null) {
				r = s.split("\\s");
			  if (r[0].trim().equals("v") == true) {			     
				  
				   noNodes++;
				   System.out.println(s);
				   int v;
					float  fn;
					
					v = Integer.parseInt(r[1].trim());
					
					fn = Float.parseFloat(r[2].trim());
					
				
					ReebVertex reebV= reebMesh.createVertex(v, fn);
					
					System.out.println(v + " <==> " + reebV.id() );
									
					rvmap.put(v, reebV);
					
					rv.add(reebV);
																
				   s = reader.readLine();
				   
				} 
			  if (r[0].trim().equals("e") == true) {
				    noArcs ++;
				    
				    int v1 = -1;
					int v2 = -1;
					
					System.out.println(s);
					
					if(r.length == 3) {
						v1 = Integer.parseInt(r[1]);
						v2 = Integer.parseInt(r[2]);
						
					} else {
						System.err.println("Invalid input");
						System.exit(0);
					}
			
					rvmap.get(v1).addNeighbor(rvmap.get(v2));
					rvmap.get(v2).addNeighbor(rvmap.get(v1));
	
					 System.out.println("neighbor# = " + rvmap.get(v1).neighbors().length); 
				     System.out.println("neighbor# = " + rvmap.get(v2).neighbors().length);
					s = reader.readLine();
			       }
			}
			

			System.out.println("Vertex =+++++++++++++++++++ "); 
            printVertices();
            /*
            for(ReebVertex reebv : rv) {
       	     System.out.println("Vertex = " + reebv.id()); 
       	     System.out.println(rvmap.get(reebv.id()).neighbors().length);
       	     for(int neighbor : rvmap.get(reebv.id()).neighbors())
       	     {  
       	    	 System.out.println(neighbor+ " Vertex");	    	 
       	     }
       	  } 
        	*/ 
       	run();

			System.out.println("No. of Nodes : " + noNodes);
			System.out.println("No. of Arcs : " + noArcs);
				

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}




public void printVertices() {
	   for(ReebVertex reebv : rv) {
	     System.out.println("Vertex = " + reebv.id()); 
	     System.out.println(rvmap.get(reebv.id()).neighbors().length);
	     for(int neighbor : rvmap.get(reebv.id()).neighbors())
	     {  
	    	 System.out.println(neighbor+ " Vertex");	    	 
	     }
	  } 
	} 

	/*
	1) Initialize all vertices as not visited.
	2) Do following for every vertex 'v'.
	       (a) If 'v' is not visited before, call DFSUtil(v)
	       (b) Print new line character

	DFSUtil(v)
	1) Mark 'v' as visited.
	2) Print 'v'
	3) Do following for every adjacent 'u' of 'v'.
	     If 'u' is not visited, then recursively call DFSUtil(u)
	     */
	
	
	
	// depth first search from v
 private void dfs( ReebVertex v, paulReebMesh reebMeshone ) {
 	v.setvisit();
 	//System.out.print( v.id()+"| ");
 	ReebVertex rv = reebMeshone.createVertex(v.id(), v.value(), rvmap.get(v.id()).neighbors() );
 	
		for(int neighbor : rvmap.get(v.id()).neighbors()){
			if(rvmap.get(neighbor).visited()==false)
				dfs(rvmap.get(neighbor), reebMeshone );				
		}	    		    	
 }

 public void componentPartitionDFS(){
		//for any node, dfs marked, form a tree, 			
		 for(ReebVertex reebv : rv) {
		     if(reebv.visited()==false)			  
		       {  
		    	 paulReebMesh reebMeshone = new paulReebMesh();
		    	 conn_comp.add(reebMeshone);
		          dfs(reebv, reebMeshone );	
		          System.out.println( "next");
		          //break;
		       }
		  } 		
	}
 
public void run() {	
	
	componentPartitionDFS();
	/*
	for(Node nd : an) {
		 rv.add(rb.createVertex(nd.id(), nd.value())); 
	 }
	 
	for(ReebVertex reebv : rv) {
	    for(int neighbor : rvmap.get(reebv.id()).neighbors())
	     {  
			  reebv.addNeighbor(rvmap.get(neighbor));	    	 
	     }
		 
	 }
	*/
	

	int curCC = 0;
	for( int i = 0; i < conn_comp.get(curCC).size(); i++ ){
		System.out.println( "   " + conn_comp.get(curCC).get(i).id() + " " + conn_comp.get(curCC).get(i).value() );
	}
	System.out.println(conn_comp.get(curCC).toDot());
	
	
	MergeTree mt = new MergeTree(conn_comp.get(0));
	mt.run();
	System.out.println(mt.toDot());
	
	SplitTree st = new SplitTree(conn_comp.get(0));
	st.run();
	System.out.println(st.toDot());

	
	//print_info_message( "Building tree complete" );
}


public int getRowCount() {
	return noNodes+noArcs;
}


public void reset() {
	// TODO Auto-generated method stub
	
}
	




}