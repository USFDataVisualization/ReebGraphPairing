package junyi.reebgraph.cmd;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import junyi.reebgraph.cmd.ReebMesh.ReebVertex;

public class ReebSpanningTree {

	Vector<STEdge> inST  = new Vector<STEdge>();
	Vector<STEdge> outST = new Vector<STEdge>();
	
	public ReebSpanningTree( ReebVertex r ){
		
		
		Queue<ReebVertex> proc = new LinkedList<ReebVertex>();
		
		for( ReebVertex n : r.neighbors ){
			if( n.value() < r.value() ){
				inST.add( new STEdge(r,n) );
				proc.add(n);
				n.mstVisted = true;
			}
		}
		
		
		
		while( !proc.isEmpty() ){
			ReebVertex top = proc.poll();
			for( ReebVertex n : top.neighbors ){
				if( n.value() < top.value() ){
					
					if( n.mstVisted ){
						outST.add( new STEdge(top, n ) );
					}
					else {
						inST.add( new STEdge( top, n ) );
						proc.add(n);
						n.mstVisted = true;
					}
				}				
			}
		}
		
		/*
		System.out.println();
		System.out.println("IN");
		for( STEdge e : inST ){
			System.out.println(e.v0.gid + " " + e.v1.gid );
		}
		
		System.out.println();
		System.out.println("OUT");
		for( STEdge e : outST ){
			System.out.println(e.v0.gid + " " + e.v1.gid );
		}
		System.out.println();
		 */
		
		for( STEdge currE : outST ){
			System.out.println( walkCycle(r,currE) );
		}
		
		
	}
	
	
	private boolean walkCycle( ReebVertex r, STEdge closure ){
		@SuppressWarnings("unchecked")
		Vector<STEdge> edges = (Vector<STEdge>)inST.clone();
		edges.add(closure);
		
		System.out.println(r.gid);

		for( STEdge e : edges ){
			if( e.visited ) continue; 
			if( e.v0 == r ){
				e.visited = true;
				return walkStep( r, e.v1, edges );
			}
			else if( e.v1 == r ){
				e.visited = true;
				return walkStep( r, e.v0, edges );
			}
		}
		
		
		return false;
	
	}
	
	private boolean walkStep( ReebVertex rootVert, ReebVertex currVert, Vector<STEdge> edges ){
		if( currVert == rootVert ) return true;
		
		for( STEdge e : edges ){
			if( e.visited ) continue;
			if( e.v0 == currVert ){
				e.visited = true;
				if( walkStep( rootVert, e.v1, edges ) ){
					System.out.println(currVert.gid + " " + ( currVert.isupfork()?"upfork":"") + " " + (currVert.essent?"essential":"") );
					return true;
				}
				//e.visited = false;
			}
			else if( e.v1 == currVert ){
				e.visited = true;
				if( walkStep( rootVert, e.v0, edges ) ){
					System.out.println(currVert.gid + " " + ( currVert.isupfork()?"upfork":"") + " " + (currVert.essent?"essential":"") );
					return true;
				}
				//e.visited = false;
			}
		}

		return false;
		
		
	}
	
	class STEdge {
		
		boolean visited = false;
		ReebVertex v0,v1;
		
		STEdge( ReebVertex _v0, ReebVertex _v1 ){
			v0 = _v0;
			v1 = _v1;
		}
	}
	
}
