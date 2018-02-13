package junyi.reebgraph.pairing.conventional;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import junyi.reebgraph.ReebGraph;
import junyi.reebgraph.ReebGraph.ReebGraphVertex;
import usf.saav.topology.TopoTreeNode.NodeType;


public class EssentialPairing {

	Vector<STEdge> inST  = new Vector<STEdge>();
	Vector<STEdge> outST = new Vector<STEdge>();
	
	Cycle pairing = null;
	
	EssentialPairing( ReebGraph reebMesh, ReebGraphVertex r ){
		
		Queue<ReebGraphVertex> proc = new LinkedList<ReebGraphVertex>();
		HashSet<ReebGraphVertex> visited = new HashSet<ReebGraphVertex>();

		for( ReebGraphVertex n : r.neighbors ){
			if( visited.contains(n) ) {
				pairing = specialCaseDirectNeighbors(r,n);
				return;
			}
			if( n.value() < r.value() ){
				inST.add( new STEdge(r,n) );
				proc.add(n);
				visited.add(n);
			}
		}
		
		while( !proc.isEmpty() ){
			ReebGraphVertex top = proc.poll();
			for( ReebGraphVertex n : top.neighbors ){
				if( n.value() >= r.value() ) continue;
				
				STEdge e = new STEdge(top, n );
				if( inST.contains( e ) || outST.contains(e) ) continue;
					
				if( visited.contains(n) ){
					outST.add( e );
				}
				else {
					inST.add( e );
					proc.add(n);
					visited.add(n);
				}
			}
		}
				
		for( STEdge currE : outST ){
			Cycle curCycle = walkCycle(r,currE);
			if( curCycle == null ) continue; 
			if( pairing == null || curCycle.upFork.value() > pairing.upFork.value() ){
				pairing = curCycle;
			}
		}
	
	}
	
	private Cycle specialCaseDirectNeighbors(ReebGraphVertex r, ReebGraphVertex n) {
		Cycle ret = new Cycle();
		ret.path.add( new STEdge(r,n) );
		ret.path.add( new STEdge(r,n) );
		ret.downFork = r;
		ret.upFork = n;
		return ret;
	}

	public String toString(){
		return pairing.toString();
	}
	
	public ReebGraphVertex getUpFork(){ return (pairing==null)?null:pairing.upFork; }
	public ReebGraphVertex getDownFork(){ return (pairing==null)?null:pairing.downFork; }
	
	private Cycle walkCycle( ReebGraphVertex r, STEdge closure ){
		
		HashSet<STEdge> visited = new HashSet<STEdge>();
		Cycle cycle = new Cycle();
		cycle.downFork = r;
		
		if( walkToRoot( cycle, closure.v0, inST, visited ) &&
			 walkToRoot( cycle, closure.v1, inST, visited ) ) {
			 	cycle.path.add(closure);
			 	return cycle;
		}
		
		
		return null;
	
	}	
	
	private boolean walkToRoot( Cycle cycle, ReebGraphVertex currVert, Vector<STEdge> edges, HashSet<STEdge> visited ){
		if( currVert == cycle.downFork ){
			return true;
		}
		
		for( STEdge e : edges ){
			if( visited.contains(e) ) continue;
			if( e.v0 == currVert ){
				visited.add(e);
				if( walkToRoot( cycle, e.v1, edges, visited ) ){
					if( currVert.getType() == NodeType.SPLIT ) cycle.setUpFork( currVert );
					cycle.path.add( e );
					return true;
				}
			}
			else if( e.v1 == currVert ){
				visited.add(e);
				if( walkToRoot( cycle, e.v0, edges, visited ) ){
					if( currVert.getType() == NodeType.SPLIT ) cycle.setUpFork( currVert );
					cycle.path.add( e );
					return true;
				}
			}
		}
		return false;
	}	


	class Cycle {
		Vector<STEdge> path = new Vector<STEdge>();
		ReebGraphVertex downFork;
		ReebGraphVertex upFork;
		
		public void setUpFork(ReebGraphVertex currVert) {
			if( upFork == null  || upFork.value() > currVert.value() )
				upFork = currVert;
		}
		
		@Override
		public String toString(){
			String ret = downFork + " || " + upFork + " --> ";
			for(STEdge v : path ){
				ret += "[" + v.v0.getGlobalID() + "," + v.v1.getGlobalID() + "] ";
			}
			return ret;
		}

		
	}
	
	
	class STEdge {
		
		ReebGraphVertex v0,v1;
		
		STEdge( ReebGraphVertex _v0, ReebGraphVertex _v1 ){
			v0 = (_v0.id() < _v1.id()) ? _v0 : _v1;
			v1 = (_v0.id() < _v1.id()) ? _v1 : _v0;
		}
		
		public int hashCode() {
			return v0.hashCode() + v1.hashCode();
		}
		
		public boolean equals(Object obj) {
			if( !(obj instanceof STEdge ) ) return false;
			STEdge o = (STEdge)obj;
			return v0.equals(o.v0) && v1.equals(o.v1);
		}
		
		
	}
	
}
