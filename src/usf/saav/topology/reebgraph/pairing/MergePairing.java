package usf.saav.topology.reebgraph.pairing;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;

import usf.saav.common.HashDisjointSet;
import usf.saav.common.SystemX;
import usf.saav.topology.TopoGraph;
import usf.saav.topology.TopoTreeNode;
import usf.saav.topology.TopoTreeNode.NodeType;
import usf.saav.topology.merge.AugmentedMergeTree;
import usf.saav.topology.merge.AugmentedJoinTree;
import usf.saav.topology.merge.AugmentedSplitTree;
import usf.saav.topology.reebgraph.ReebGraph;
import usf.saav.topology.reebgraph.ReebGraphVertex;

public class MergePairing implements Pairing {
	
	private String output_directory = "";
	private boolean saveTrees = false;
	
	public MergePairing( ) {
		saveTrees = false;
	}
	
	public MergePairing( boolean _saveTrees, String _output_path ) {
		saveTrees = _saveTrees;
		output_directory = _output_path;
	} 
			
	public String getName() { return "Merge Pairing"; }
	
	public void pair(ReebGraph reebMesh) {
		
		HashSet<TopoGraph.Vertex> essential = new HashSet<TopoGraph.Vertex>();
		essential.addAll( reebMesh );

		AugmentedJoinTree mt = new AugmentedJoinTree( reebMesh, true );
		if( saveTrees ) {
			try {
				SystemX.writeStringToFile(mt.toDot(), output_directory + "mt.dot" );
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		AugmentedSplitTree st = new AugmentedSplitTree( reebMesh, true );
		if( saveTrees ) {
			try {
				SystemX.writeStringToFile(st.toDot(), output_directory + "st.dot" );
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		ReebGraphVertex gmin = joinTreePairing( mt, reebMesh, essential );
		ReebGraphVertex gmax = joinTreePairing( st, reebMesh, essential );
		
		gmin.setPartner(gmax);
		gmax.setPartner(gmin);
		
		
		for( TopoGraph.Vertex _v : reebMesh ) {
			ReebGraphVertex rv = (ReebGraphVertex)_v;
			if( rv.getPartner() == null && rv.getType() == NodeType.DOWNFORK ) {
				ReebGraphVertex res = downforkPairing( rv );
				rv.setPartner(res);
				res.setPartner(rv);
			}
		}
		
	}
	

	private ReebGraphVertex joinTreePairing( AugmentedMergeTree jt, ReebGraph reebMesh, HashSet<TopoGraph.Vertex> essential ) {
		ReebGraphVertex gmin=null;
		for(int i = 0; i < jt.size(); i++ ){
			TopoTreeNode    mtv = jt.getNode(i);
			TopoTreeNode    mtp = mtv.getPartner();
			ReebGraphVertex rbv = (ReebGraphVertex)reebMesh.get( mtv.getID() );
			
			essential.remove( rbv );
			if( mtp == null ) 
				gmin = rbv; 
			else
				rbv.setPartner( (ReebGraphVertex)reebMesh.get( mtp.getID() ) );
		}
		return gmin;
		
	}
	
	private ReebGraphVertex downforkPairing(ReebGraphVertex rv) {
		ReebGraphVertex n0 = null, n1 = null;
		for( ReebGraphVertex n : rv.neighbors ) {
			if( n.value() < rv.value() ){
				n1 = n0; 
				n0 = n;
			}
		}
		
		// simple pairing
		if( n0 == n1 ) return n0;
		
		PriorityQueue<ReebGraphVertex> proc = new PriorityQueue<ReebGraphVertex>( new Comparator<ReebGraphVertex>() {
			@Override public int compare(ReebGraphVertex o1, ReebGraphVertex o2) {
				if( o1.value() > o2.value() ) return -1;
				return 1;
			}
		});
		proc.add(rv);
		
		HashDisjointSet<ReebGraphVertex> djs = new HashDisjointSet<ReebGraphVertex>();
		HashSet<ReebGraphVertex> visited = new HashSet<ReebGraphVertex>();
		
		while( !proc.isEmpty() ) {
			ReebGraphVertex curr = proc.poll();
			if( visited.contains(curr) ) continue;
			visited.add(curr);

			for( ReebGraphVertex n : curr.neighbors ) {
				if( n.value() >= rv.value() ) continue;
				if( n.value() > curr.value() ) {
					djs.union( curr, n );
				}
				if( !visited.contains(n) ) proc.add(n);
			}
			
			if( djs.find(n0) == djs.find(n1) ) {
				return curr;
			}
		}
		return null;
	}	
	
	
}
