package src.junyi.reebgraph;

//import usf.saav.topology.join;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.corba.se.impl.orbutil.graph.Graph;

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

	

	
	
	

	/*
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
		     
		
		
		
		// depth first search from v
	    private void dfs( ReebVertex v) {
	    	v.setvisit();
	    	System.out.print( v.id()+"| ");
	    	
			for(int neighbor : rvmap.get(v.id()).neighbors()){
				if(rvmap.get(neighbor).visited()==false)
					dfs(rvmap.get(neighbor));				
			}	    		    	
	    }

	    public void componentPartitionDFS(){
			//for any node, dfs marked, form a tree, 			
			 for(ReebVertex reebv : rv) {
			     if(reebv.visited()==false)			  
			       {  
			          dfs(reebv);	
			          System.out.println( "next");
			          break;
			       }
			  } 		
		}
	    
	public void run() {	
		
		componentPartitionDFS();
		
		//for(Node nd : an) {
		//	 rv.add(rb.createVertex(nd.id(), nd.value())); 
		// }
		 
		for(ReebVertex reebv : rv) {
		    for(int neighbor : rvmap.get(reebv.id()).neighbors())
		     {  
				  reebv.addNeighbor(rvmap.get(neighbor));	    	 
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
	
	*/
}