package src.junyi.reebgraph.cmd;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

import src.junyi.reebgraph.cmd.paulReebMesh.ReebVertex;
import usf.saav.mesh.Mesh;
import usf.saav.topology.join.JoinTree.Node;

public class essential extends paulReebMesh {

 
	
	private static final long serialVersionUID = 1L;
	/*Given Reeb graph with essentail fork
 * 
 * sort with f value
 * DFS 
 * add each complementary edge to MST
 * find candidate cycle pass  a
 * pick the largest one 
 * 
 
	paulReebMesh sf;
	private Comparator<? super ReebVertex> comparator;
	//	comparator = new Node.ComparatorValueAscending();
	
	private   int size;
	//private   Node head;
	
	public essential(paulReebMesh sf ) {
		this.sf = sf;
		this.comparator = new Node.ComparatorValueAscending();
		
		
		
		/*Queue< ReebVertex > tq = new PriorityQueue< ReebVertex >( size, comparator );	
		//for(int i = 0; i < sf.getWidth(); i++ ){
		//	tq.add( new ReebVertex( i, sf.get(i).value(), i ) );
		//}
		
		for each down fork node, run min spanning tree,  add each remaining
		 edge, 
		
		
		
		*/
		
		
		
		
	}
	
	
	
	
	
	
	
	
}
