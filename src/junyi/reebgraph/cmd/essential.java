package src.junyi.reebgraph.cmd;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

import src.junyi.reebgraph.cmd.paulReebMesh.ReebVertex;
import usf.saav.mesh.Mesh;
import usf.saav.topology.join.JoinTree.Node;

public class essential extends paulReebMesh {
/*Given Reeb graph with essentail fork
 * 
 * sort with f value
 * DFS 
 * add each complementary edge to MST
 * find candidate cycle pass  a
 * pick the largest one 
 * 
 */
	paulReebMesh sf;
	private Comparator<? super ReebVertex> comparator;
	//	comparator = new Node.ComparatorValueAscending();
	
	private   int size;
	private   Node head;
	
	public essential(paulReebMesh sf ) {
		this.sf = sf;
		this.comparator = new Node.ComparatorValueAscending();
	}
	
	
	Queue< ReebVertex > tq = new PriorityQueue< ReebVertex >( size, comparator );	
	for(int i = 0; i < sf.getWidth(); i++ ){
		tq.add( new ReebVertex( i, sf.get(i).value(), i ) );
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
  void dfsDir( ReebVertex v, paulReebMesh reebMeshone ) {
 	v.setvisit();
 	//System.out.print( v.id()+"| ");
 	ReebVertex rvOne = reebMeshone.createVertex(v.id(), v.value(), rvmap.get(v.id()).neighbors(), v.id() );
 	
		for(int neighbor : rvmap.get(v.id()).neighbors()){
			if(rvmap.get(neighbor).visited()==false)
				dfs(rvmap.get(neighbor), reebMeshone );				
		}	    		    	
 }
	
	
	
	
	
	
}
