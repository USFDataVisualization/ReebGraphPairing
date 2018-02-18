package junyi.reebgraph.pairing.conventional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;

import junyi.reebgraph.ReebGraph;
import junyi.reebgraph.ReebGraph.ReebGraphVertex;


public class EssentialPairing {

	ReebGraphVertex downfork = null, upfork = null;
	ArrayList<ReebGraphVertex> selPath;
	
	EssentialPairing( ReebGraph reebMesh, ReebGraphVertex r ){
		downfork = r;
		
		ReebGraphVertex c0 = null, c1 = null;
		for( ReebGraphVertex n : r.neighbors ){
			if( n.value() < r.value() ){
				if( c0 == null ) { c0 = n; }
				else { c1 = n; }
			}
		}
		
		ArrayList<ReebGraphVertex> path = new ArrayList<ReebGraphVertex>();
		path.add(r);
		
		HashSet<ReebGraphVertex> visited = new HashSet<ReebGraphVertex>();
		visited.add(r);
		
		Queue<LoopWalk> wq = new PriorityQueue<LoopWalk>();
		wq.add( new LoopWalk(c0,visited,path) );
		
		while( !wq.isEmpty() ) {
			LoopWalk cur = wq.poll();
			processWalk( c1, cur.curNode, cur.visited, cur.path, wq );			
		}

	}
	
	public ReebGraphVertex getUpFork(){ return upfork; }
	public ReebGraphVertex getDownFork(){ return downfork; }

		
	private void processWalk(ReebGraphVertex endNode, ReebGraphVertex curNode, HashSet<ReebGraphVertex> visited, ArrayList<ReebGraphVertex> path, Queue<LoopWalk> wq ) {
		
		// upfork may have changed, so check to see if this walk is still a candidate
		if( upfork != null && curNode.value() < upfork.value() ) return;
		
		path.add(curNode);
		
		if( curNode == endNode ) { 
			// reached the end of the walk
			path.add(downfork);			
			ReebGraphVertex upvert = getWalkUpfork(path);
			if( upfork == null || upvert.value() > upfork.value() ) {
				selPath = path;
				upfork = upvert;
			}
		}
		else {
			// keep walking
			visited.add(curNode);
			for( ReebGraphVertex n : curNode.neighbors ) {
				if( upfork != null && n.value() < upfork.value() ) continue;
				if( n.value() > downfork.value() ) continue;
				if( visited.contains(n) ) continue;
				wq.add( new LoopWalk(n,visited,path) );
			}
		}
	}
	

	private ReebGraphVertex getWalkUpfork( ArrayList<ReebGraphVertex> cpath ) {
		ReebGraphVertex upvert = null;
		for( int i = 1; i < cpath.size()-1; i++) {
			float v0 = cpath.get(i-1).value();
			float cv = cpath.get(i).value();
			float v2 = cpath.get(i+1).value();
			// if value lower that vertices before and after, this is an upfork
			if( cv < v0 && cv < v2 ) {
				if( upvert == null || cv < upvert.value() ) {
					upvert = cpath.get(i);
				}
			}
		}
		return upvert;
	}

	
	private class LoopWalk implements Comparable<LoopWalk> {
		
		ReebGraphVertex curNode;
		HashSet<ReebGraphVertex> visited;
		ArrayList<ReebGraphVertex> path;
		
		@SuppressWarnings("unchecked")
		LoopWalk( ReebGraphVertex n, HashSet<ReebGraphVertex> _visited, ArrayList<ReebGraphVertex> _path ){
			curNode = n;
			visited = (HashSet<ReebGraphVertex>)_visited.clone();
			path = (ArrayList<ReebGraphVertex>)_path.clone();
		}
		
		@Override
		public int compareTo( LoopWalk o1) {
			if( curNode.value() < o1.curNode.value() ) return  1;
			if( curNode.value() > o1.curNode.value() ) return -1;
			return 0;
		}
	}
		
}
