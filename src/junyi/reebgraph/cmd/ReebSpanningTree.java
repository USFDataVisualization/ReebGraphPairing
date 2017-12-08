package junyi.reebgraph.cmd;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import junyi.reebgraph.cmd.ReebMesh.ReebVertex;

public class ReebSpanningTree {

	Vector<STEdge> inST  = new Vector<STEdge>();
	Vector<STEdge> outST = new Vector<STEdge>();
	
	Cycle pairing = null;
	
	public ReebSpanningTree( ReebMesh reebMesh, ReebVertex r ){
		
		reebMesh.resetVisited();
		
		Queue<ReebVertex> proc = new LinkedList<ReebVertex>();
		
		for( ReebVertex n : r.neighbors ){
			if( n.value() < r.value() ){
				inST.add( new STEdge(r,n) );
				proc.add(n);
				n.visted = true;
			}
		}
		
		while( !proc.isEmpty() ){
			ReebVertex top = proc.poll();
			for( ReebVertex n : top.neighbors ){
				if( n.value() < top.value() ){
					
					if( n.visted ){
						outST.add( new STEdge(top, n ) );
					}
					else {
						inST.add( new STEdge( top, n ) );
						proc.add(n);
						n.visted = true;
					}
				}				
			}
		}
		
		
		for( STEdge currE : outST ){
			Cycle curCycle = walkCycle(r,currE);
			//if( curCycle == null ) continue; 
			if( pairing == null || curCycle.upFork.value() > pairing.upFork.value() ){
				pairing = curCycle;
			}
		}
	
	}
	
	public String toString(){
		return pairing.toString();
	}
	
	public ReebVertex getUpFork(){ return pairing.upFork; }
	public ReebVertex getDownFork(){ return pairing.downFork; }
	
	
	private Cycle walkCycle( ReebVertex r, STEdge closure ){
		@SuppressWarnings("unchecked")
		Vector<STEdge> edges = (Vector<STEdge>)inST.clone();
		edges.add(closure);
		
		for( STEdge e : edges){ e.visited = false; }
		
		Cycle cycle = new Cycle();
		cycle.downFork = r;
		
		for( STEdge e : edges ){
			if( e.visited ) continue; 
			if( e.v0 == r ){
				e.visited = true;
				if( walkStep( cycle, e.v1, edges ) ) {
					cycle.path.add(r);
					return cycle;
				}
				return null;
			}
			else if( e.v1 == r ){
				e.visited = true;
				if( walkStep( cycle, e.v0, edges ) ){
					cycle.path.add(r);
					return cycle;
				}
				return null;
			}
		}
		
		
		return null;
	
	}
	
	private boolean walkStep( Cycle cycle, ReebVertex currVert, Vector<STEdge> edges ){
		if( currVert == cycle.downFork ){
			cycle.path.add(currVert);
			return true;
		}
		
		for( STEdge e : edges ){
			if( e.visited ) continue;
			if( e.v0 == currVert ){
				e.visited = true;
				if( walkStep( cycle, e.v1, edges ) ){
					if( currVert.isupfork() && currVert.essent) cycle.setUpFork( currVert );
					cycle.path.add( currVert );
					return true;
				}
			}
			else if( e.v1 == currVert ){
				e.visited = true;
				if( walkStep( cycle, e.v0, edges ) ){
					if( currVert.isupfork() && currVert.essent) cycle.setUpFork( currVert );
					cycle.path.add( currVert );
					return true;
				}
			}
		}
		return false;
	}
	
	
	class Cycle {
		Vector<ReebVertex> path = new Vector<ReebVertex>();
		ReebVertex downFork;
		ReebVertex upFork;
		
		public String toString(){
			String ret = downFork + " || " + upFork + " --> ";
			for(ReebVertex v : path ){
				ret += v.gid + " ";
			}
			return ret;
		}

		public void setUpFork(ReebVertex currVert) {
			if( upFork == null  || upFork.value() > currVert.value() )
				upFork = currVert;
		}
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