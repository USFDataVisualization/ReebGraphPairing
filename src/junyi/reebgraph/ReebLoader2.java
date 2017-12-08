package junyi.reebgraph;
//package usf.saav.topology.join;

import java.io.BufferedReader;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import junyi.reebgraph.cmd.ReebMesh;
import junyi.reebgraph.cmd.ReebMesh.ReebVertex;
import junyi.reebgraph.cmd.ReebSpanningTree;
import junyi.reebgraph.loader.MeshLoader;
import usf.saav.mesh.Mesh.Vertex;
import usf.saav.topology.TopoTreeNode;
import usf.saav.topology.merge.MergeTree;
import usf.saav.topology.split.SplitTree;


public class ReebLoader2 implements MeshLoader{

    private BufferedReader reader;
	private int noNodes=0;
	private int noArcs=0;
	
	String inputReebGraph;
	
	
	private ReebMesh reebMesh = new ReebMesh();
	//private paulReebMesh reebMeshoneasdf = new paulReebMesh();
	
	// Connected components
	ArrayList<ReebMesh> conn_comp = new ArrayList<ReebMesh>();  
	
	// all Vertices
	ArrayList<ReebVertex> rv = new ArrayList<ReebVertex>();  //maybe multiple components
	
	
//	public HashMap<Integer, ReebVertex> rvmap = new HashMap<Integer, ReebVertex>();
	
public void setInputFile(String _inputReebGraph) {
	inputReebGraph = _inputReebGraph;
	
	HashMap<Integer, ReebVertex> rvmap = new HashMap<Integer, ReebVertex>();

		try {
			reader = new BufferedReader(new FileReader(inputReebGraph));
			String s = reader.readLine();
			
			String[] r = s.split("\\s");
		
			while(s != null) {
				r = s.split("\\s");
			  if (r[0].trim().equals("v") == true) {			     
				  
				   noNodes++;
				  // System.out.println(s);
				   int v;
					float  fn;
					
					v = Integer.parseInt(r[1].trim());
					
					fn = Float.parseFloat(r[2].trim());
					
				
					ReebVertex reebV= reebMesh.createVertex(fn, v);
					
					//System.out.println(v + " <==> " + reebV.id() );
									
					rvmap.put(v, reebV);
					
					rv.add(reebV);
																
				   s = reader.readLine();
				   
				} 
			  if (r[0].trim().equals("e") == true) {
				    noArcs ++;
				    
				    int v1 = -1;
					int v2 = -1;
					
				//	System.out.println(s);
					
					if(r.length == 3) {
						v1 = Integer.parseInt(r[1]);
						v2 = Integer.parseInt(r[2]);
						
					} else {
						System.err.println("Invalid input");
						System.exit(0);
					}
			
					rvmap.get(v1).addNeighbor(rvmap.get(v2));
					rvmap.get(v2).addNeighbor(rvmap.get(v1));
	
				//	 System.out.println("neighbor# = " + rvmap.get(v1).neighbors().length); 
				//     System.out.println("neighbor# = " + rvmap.get(v2).neighbors().length);
					s = reader.readLine();
			       }
			}
			

			System.out.println("Vertex =+++++++++++++++++++ "); 
           // printVertices(rv);
           
       	    run();

			System.out.println("No. of Nodes : " + noNodes);
			System.out.println("No. of Arcs : " + noArcs);
				

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}




public void printVertices(ArrayList<ReebVertex> rv) {
	/*
	   for(ReebVertex reebv : rv) {
	     System.out.println("Vertex = " + reebv.id()); 
	     System.out.println(rvmap.get(reebv.id()).neighbors().length);
	     for(int neighbor : rvmap.get(reebv.id()).neighbors())
	     {  
	    	 System.out.println(neighbor+ " Vertex");	    	 
	     }
	  } 
	} 
	*/
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

/*
 private void dfs( ReebVertex v, ReebMesh reebMeshone ) {
 	v.setvisit();
 	//System.out.print( v.id()+"| ");
 	ReebVertex rvOne = reebMeshone.createVertex(v.id(), v.value(), rvmap.get(v.id()).neighbors(), v.id() );
 	
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
		    	 ReebMesh reebMeshone = new ReebMesh();
		    	 conn_comp.add(reebMeshone);
		          dfs(reebv, reebMeshone );	
		          System.out.println( "next");		          
		       }
		  } 		
	}
 
 ReebMesh reebMeshIndex2Id = new ReebMesh();
 
 public void equalizeIndex2Id(){
	 
	 int curCC=0;
	 
	 HashMap<Integer,Integer> numMap = new HashMap<Integer,Integer>();
	  
	 for( int i = 0; i < conn_comp.get(curCC).size(); i++ ){
		 numMap.put( conn_comp.get(curCC).get(i).id(), i );
	 }
	
	 for( int i = 0; i < conn_comp.get(curCC).size(); i++ ){
		 int [] newN = conn_comp.get(curCC).get(i).neighbors().clone();
		 float newV = conn_comp.get(curCC).get(i).value();
		 int newGID = ((ReebVertex)conn_comp.get(curCC).get(i)).globalID();
		 for(int j = 0; j < newN.length; j++ ){
			 newN[j] = numMap.get(newN[j]);
		 }
	     reebMeshIndex2Id.createVertex(i,newV,newN,newGID );
	}
	 
	 System.out.println(reebMeshIndex2Id.toDot());
 } 
 */
 
public void run() {	
	
	//componentPartitionDFS();


	/*
	 for( int i = 0; i < conn_comp.get(curCC).size(); i++ ){
		System.out.println( "   " + conn_comp.get(curCC).get(i).id() + " " + conn_comp.get(curCC).get(i).value() );
	}
	*/
	//System.out.println(reebMesh.toDot());

	
	//equalizeIndex2Id();

	
	
	MergeTree mt = new MergeTree(reebMesh );
	mt.run();
	
	
	
	//for(int j=0; j<mt.size(); j++)
		//System.out.println( mt.getNode(j).getPosition());
	
	
	TopoTreeNode gmax =null, gmin=null;
	for(int i = 0; i < mt.size(); i++ ){
		TopoTreeNode x = mt.getNode(i);
		reebMesh.getByID( x.getPosition() ).essent = false;
		if( x.getPartner() == null ){ 
			gmin = x; 
		}
		else{
			reebMesh.getByID( x.getPosition() ).topoPartner = reebMesh.getByID( x.getPartner().getPosition() );
		}
	}
	
	System.out.println(mt.toDot());
	

	SplitTree st = new SplitTree(reebMesh );
	st.run();
	System.out.println(st.toDot());
	for(int i = 0; i < st.size(); i++ ){
		TopoTreeNode x = st.getNode(i);
		reebMesh.getByID( x.getPosition() ).essent = false;
		if( x.getPartner() == null ){ 
			gmax = x; 
		}
		else{
			System.out.println( x.getPosition() + " " + x.getPartner().getPosition() );
		}
	}

	reebMesh.getByID( gmin.getPosition() ).topoPartner = reebMesh.getByID( gmax.getPosition() );
	reebMesh.getByID( gmax.getPosition() ).topoPartner = reebMesh.getByID( gmin.getPosition() );
	
	System.out.println(reebMesh.toDot());

	System.out.println();
//	System.out.println(reebMeshIndex2Id.toDot());
	System.out.println();

	
	Vector<ReebVertex> ess = new Vector<ReebVertex>();
	//System.out.println(x.getPosition());
	for(int j = 0; j < reebMesh.size(); j++ ){
		if( ((ReebVertex)reebMesh.get(j)).essent ){
			ess.add( ((ReebVertex)reebMesh.get(j)) );
		}
	}
	
	
	for( ReebVertex r : ess ){
		
		if( r.isdownfork() ) {
			System.out.println();
			System.out.print("DOWNFORK -- ");
			System.out.println( r.toString() + "   " + r.isdownfork( ) );
			ReebSpanningTree pairing = new ReebSpanningTree(reebMesh, r);
			
			pairing.getUpFork().topoPartner = pairing.getDownFork();
			pairing.getDownFork().topoPartner = pairing.getUpFork();
			System.out.println(pairing);
		}
	}
	
	for( Vertex v : reebMesh ){
		ReebVertex rv = (ReebVertex)v;
		System.out.println( rv.globalID() + ": [" + rv.getBirth() + ", " + rv.getDeath() + "]");
	}
	
	//print_info_message( "Building tree complete" );
}


/*
void dfsDir( ReebVertex v, ReebMesh reebMeshone ) {
 	
	v.setmst();
 	//System.out.print( v.id()+"| ");
 	 reebMeshone.createVertex(v.id(), v.value(), rvmap.get(v.id()).neighbors(), v.id() );
 	
		for(int neighbor : rvmap.get(v.id()).neighbors()){
			if(rvmap.get(neighbor).visited()==false && rvmap.get(neighbor).value()<v.value() )
				dfsDir(rvmap.get(neighbor), reebMeshone );				
		}	    		    	
 }

public void essential(ReebVertex reebv){		 
	     if(reebv.essented()==true && isdownfork(reebv))			  
	       {  
	    	 ReebMesh reebMeshmst = new ReebMesh();
	    	 //initiate all vertex to be mst null
	    	 init();	    	 
	          dfsDir(reebv, reebMeshmst );	
	          System.out.println( "next");		          
	       }
	  //  for( each edge not in reebMeshmst)
	     
	     
	     
	  	
}	
	
//if two neighbors are smaller, then down fork, otherwise, up fork
public boolean isdownfork(ReebVertex v){
	int n=0;
	for(int neighbor : rvmap.get(v.id()).neighbors()) {
		if(v.value()>rvmap.get(neighbor).value()) n++;		
	}
	if(n==2) return true;
	else return false;
	
}

*/
/*
//initiate all vertex to be mst null
public void init(){
	for(ReebVertex reebv : rv) {
		reebv.unsetmst();		
	}
}

*/


public int getRowCount() {
	return noNodes+noArcs;
}


public void reset() {
	// TODO Auto-generated method stub
	
}
	




}