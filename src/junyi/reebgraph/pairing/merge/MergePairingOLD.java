package junyi.reebgraph.pairing.merge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Vector;

import junyi.reebgraph.ReebGraph;
import junyi.reebgraph.ReebGraph.ReebGraphVertex;
import usf.saav.mesh.Mesh.Vertex;
import usf.saav.topology.TopoTreeNode.NodeType;

public class MergePairingOLD {

	HashMap<ReebGraphVertex,HashSet<ReebGraphVertex>> inLabels = new HashMap<ReebGraphVertex,HashSet<ReebGraphVertex>>();
	Queue<VEdge> virtEdges = new PriorityQueue<VEdge>();
	
	public MergePairingOLD( ReebGraph reebMesh ) throws Exception {

		for( Vertex v : reebMesh ) {
			inLabels.put( (ReebGraphVertex)v, new HashSet<ReebGraphVertex>() );
		}

		for( ReebGraphVertex v : reebMesh.getNodesSortedByValue() ) {
			
			/*
			inLabels.get(v).sort( new Comparator<ReebGraphVertex>() {
				public int compare(ReebGraphVertex o1, ReebGraphVertex o2) {
					if( o1.value() < o2.value() ) return -1;
					if( o1.value() > o2.value() ) return  1;
					return 0;
				}
			});
			*/
		
			switch( v.getType() ) {
				case LEAF_MAX:	processMax(v);	break;
				case MERGE:		processMerge(v);	break;
				case LEAF_MIN:	processMin(v);	break;
				case SPLIT:		processSplit(v);	break;
				default: throw new Exception();
			}
			

			System.out.print( v.gid + " " + v.getType().toString() + " : " );
			for( ReebGraphVertex y : inLabels.get(v) ) {
				if( y.topoPartner!=null) System.out.print("*");
				System.out.print( y.gid + ", " );
			}
			System.out.println();
			
			for( VEdge e : virtEdges ) {
				System.out.println( "    " + e );
			}
			System.out.println();
		}
		
		
	}
	
	private void processMax(ReebGraphVertex v) {
		
		ReebGraphVertex maxSaddle = null;
		ReebGraphVertex minMin    = null;
		for( ReebGraphVertex n : inLabels.get(v) ) {
			if( n.topoPartner != null ) continue;
			if( n.getType() == NodeType.SPLIT ) {
				if( maxSaddle == null || n.value() > maxSaddle.value() )
					maxSaddle = n;
			}
			if( n.getType() == NodeType.LEAF_MIN ) {
				if( minMin == null || minMin.value() > n.value() )
					minMin = n;
			}
		}

		// remove virtual edges that have terminated
		while( !virtEdges.isEmpty() && virtEdges.peek().n0 == v ) {
			virtEdges.poll();
		}
		
		if( maxSaddle!= null) {
			v.topoPartner = maxSaddle;
			maxSaddle.topoPartner = v;		
		}
		else {
			v.topoPartner = minMin;
			minMin.topoPartner = v;		
		}
	}


	private void processSplit(ReebGraphVertex v) {
		ReebGraphVertex n0 = null, n1 = null;
		for( ReebGraphVertex n : v.neighbors ) {
			if( n.value() > v.value() ) {
				if( n0 == null ) n0 = n;
				else n1 = n;
			}
		}
		
		// forward virtual edges
		while( !virtEdges.isEmpty() && virtEdges.peek().n0 == v ) {
			VEdge e = virtEdges.poll();
			virtEdges.add( new VEdge(e.gen,n0,e.lr0,e.n1,e.lr1) );
			virtEdges.add( new VEdge(e.gen,n1,e.lr0,e.n1,e.lr1) );
		}
		// create new split
		virtEdges.add( new VEdge(v,n0,0,n1,1) );
		
		for( ReebGraphVertex n : v.neighbors ) {
			if( n.value() > v.value() ) {
				inLabels.get(n).add( v );
				inLabels.get(n).addAll( inLabels.get(v) );
			}
		}
	}
	
	private void processMin(ReebGraphVertex v) {
		for( ReebGraphVertex n : v.neighbors ) {
			inLabels.get(n).add(v);
		}
	}

	private void processMerge(ReebGraphVertex v) {
		
		ReebGraphVertex maxMin = null;
		ReebGraphVertex cycle  = null;
		
		Vector<ReebGraphVertex> outlist = new Vector<ReebGraphVertex>(); 
		for( ReebGraphVertex n : inLabels.get(v) ) {
			if( n.topoPartner != null ) continue; 
			if( n.getType() == NodeType.LEAF_MIN && n.topoPartner == null ) {
				if( maxMin == null || maxMin.value() < n.value() )
					maxMin = n;
			}
			outlist.add(n);
		}
		
		// see if the link is a closed cycle
		while( !virtEdges.isEmpty() && virtEdges.peek().n0 == v && virtEdges.peek().n1 == v ) {
			VEdge e0 = virtEdges.poll();
			if( e0.gen.topoPartner != null ) continue;
			if( e0.lr0 != e0.lr1 ) {
				cycle = e0.gen;
				System.out.println("   close cycle: " + e0 ); 
			}
			else { System.out.println("   discard: " + e0 ); }
		}
		
		ReebGraphVertex n0 = null;
		for( ReebGraphVertex n : v.neighbors ) {
			if( n.value() > v.value() ) {
				n0 = n;
			}
		}
		
		// forward virtual edges
		ArrayList<VEdge> activeEdges = new ArrayList<VEdge>();
		while( !virtEdges.isEmpty() && virtEdges.peek().n0 == v ) {
			VEdge e = virtEdges.poll();
			if( e.gen.topoPartner != null ) continue;
			virtEdges.add( new VEdge(e.gen,n0,e.lr0,e.n1,e.lr1) );
			activeEdges.add(e);
			for( ReebGraphVertex ov : inLabels.get(v) ) {
				if( ov.getType() == NodeType.LEAF_MIN )
					inLabels.get(e.n1).add( ov );
			}
		}
		for(int i = 0; i < activeEdges.size(); i++) {
			VEdge ei = activeEdges.get(i);
			for(int j = 0; j < activeEdges.size(); j++ ) {
				if(i==j) continue;
				VEdge ej = activeEdges.get(j);
				if( ei.gen.value() < ej.gen.value() ) {
					virtEdges.add( new VEdge(ei.gen,ei.n1,ei.lr0,ej.n1,ei.lr1) );
				}
			}
		}
		
		
		
		System.out.println( "   " + maxMin + " " + cycle );
		
		if( cycle != null ) {
			v.topoPartner = cycle;
			cycle.topoPartner = v;
			outlist.remove(cycle);
		}
		else {
			v.topoPartner = maxMin;
			maxMin.topoPartner = v;
			outlist.remove(maxMin);
		}
		
		for( ReebGraphVertex n : v.neighbors ) {
			if( n.value() > v.value() ) {
				inLabels.get(n).addAll( inLabels.get(v) );
			}
		}		
	}
	
	
	private class VEdge implements Comparable<VEdge> {
		public VEdge(ReebGraphVertex _v, ReebGraphVertex _n0, int _lr0, ReebGraphVertex _n1, int _lr1) {
			gen = _v;
			n0 = (_n0.value()<_n1.value())?_n0:_n1;
			n1 = (_n0.value()<_n1.value())?_n1:_n0;
			lr0 = (_n0.value()<_n1.value())?_lr0:_lr1;
			lr1 = (_n0.value()<_n1.value())?_lr1:_lr0;
		}
		ReebGraphVertex n0, n1;
		int lr0,lr1;
		ReebGraphVertex gen;
		@Override
		public int compareTo(VEdge o) {
			if( n0.value() < o.n0.value() ) return -1;
			if( n0.value() > o.n0.value() ) return  1;
			if( n1.value() < o.n1.value() ) return -1;
			if( n1.value() > o.n1.value() ) return  1;
			return 0;
		}
		public String toString() {
			return n0.gid + " ==> " + gen.gid + "[" + lr0 + "] " + n1.gid + " ==> " + gen.gid + "[" + lr1 + "]";
		}
	}

}
