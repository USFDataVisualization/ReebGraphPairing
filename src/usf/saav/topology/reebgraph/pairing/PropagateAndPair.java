package usf.saav.topology.reebgraph.pairing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import usf.saav.topology.TopoGraph.Vertex;
import usf.saav.topology.TopoTreeNode.NodeType;
import usf.saav.topology.reebgraph.ReebGraph;
import usf.saav.topology.reebgraph.ReebGraphVertex;




public class PropagateAndPair implements Pairing {

	HashMap<ReebGraphVertex,TreeSet<Label>> inLabels;
	TreeSet<VEdge> virtEdges;
	
	public PropagateAndPair( ) { }
	
	public String getName() { return "Pair and Propagate"; }
	
	@Override
	public void pair(ReebGraph reebMesh) {
		inLabels = new HashMap<ReebGraphVertex,TreeSet<Label>>();
		virtEdges = new TreeSet<VEdge>();

		for( Vertex v : reebMesh ) {
			inLabels.put( (ReebGraphVertex)v, new TreeSet<Label>() );
		}

		for( ReebGraphVertex v : reebMesh.getNodesSortedByValue() ) {
		
			switch( v.getType() ) {
				case LEAF_MAX:	processMax(v);		break;
				case DOWNFORK:	processMerge(v);	break;
				case LEAF_MIN:	processMin(v);		break;
				case UPFORK:	processSplit(v);	break;
				default: System.err.println("Unknown Critical Point Type");
			}
			
		}		
	}
	
	
	private void processMax(ReebGraphVertex v) {
		
		ReebGraphVertex maxSaddle = null;
		ReebGraphVertex minMin    = null;
		for( Label l : inLabels.get(v) ) {
			if( l.isPaired() ) continue;
			
			ReebGraphVertex n = l.vrt;
			if( l.getType() == NodeType.UPFORK ) {
				if( maxSaddle == null || n.value() > maxSaddle.value() )
					maxSaddle = n;
			}
			
			if( l.getType() == NodeType.LEAF_MIN ) {
				if( minMin == null || minMin.value() > n.value() )
					minMin = n;
			}
			
		}

		// remove virtual edges that have terminated
		while( !virtEdges.isEmpty() && virtEdges.first().n0 == v ) {
			virtEdges.pollFirst();
			//virtEdges.poll();
		}
		
		if( maxSaddle!= null) {
			v.setPartner(maxSaddle);
			maxSaddle.setPartner(v);		
		}
		else {
			v.setPartner(minMin);
			minMin.setPartner(v);		
		}
	}


	private void processSplit(ReebGraphVertex v) {
		
		// Find the 2 outgoing edges
		ReebGraphVertex n0 = null, n1 = null;
		for( ReebGraphVertex n : v.neighbors ) {
			if( n.value() > v.value() ) {
				if( n0 == null ) n0 = n;
				else n1 = n;
			}
		}

		// Create new labels
		inLabels.get(n0).add( new Label(v,1) );
		inLabels.get(n1).add( new Label(v,2) );

		// Pass old labels forward
		inLabels.get(n0).addAll( inLabels.get(v) );
		inLabels.get(n1).addAll( inLabels.get(v) );

		// Create virtual edge
		virtEdges.add( new VEdge(v,n0,n1) );

		// Forward old virtual edges
		while( !virtEdges.isEmpty() && virtEdges.first().n0 == v ) {
			VEdge e = virtEdges.pollFirst();
			virtEdges.add( new VEdge(e.gen,n0,e.n1) );
			virtEdges.add( new VEdge(e.gen,n1,e.n1) );
		}
		
	}
	
	private void processMin(ReebGraphVertex v) {
		// Create new label
		for( ReebGraphVertex n : v.neighbors ) {
			inLabels.get(n).add( new Label(v,0) );
		}
	}

	private void processMerge(ReebGraphVertex v) {
		
		// Find outgoing edge
		ReebGraphVertex n0 = null;
		for( ReebGraphVertex n : v.neighbors ) {
			if( n.value() > v.value() ) {
				n0 = n;
			}
		}
		

		// Identify the possible pairing partners. 
		// This will be the highest upfork or highest leaf. 
		ReebGraphVertex maxLeaf = null;
		ReebGraphVertex upfork  = null;
		Label prev = null;
		for( Label curr : inLabels.get(v) ) {
			if( curr.isPaired() ) continue;
			if( curr.getType() == NodeType.LEAF_MIN ) { 
				maxLeaf = curr.vrt;
			}
			else if( curr.getType() == NodeType.UPFORK && prev != null && curr.vrt == prev.vrt ) {
				upfork = curr.vrt;
			}
			prev = curr;
		}	
		
		// If an upfork is found, a cycle is closed. 
		// Otherwise we have nonessential fork.
		if( upfork != null ) {
			v.setPartner(upfork);
			upfork.setPartner(v);
		}
		else {
			v.setPartner(maxLeaf);
			maxLeaf.setPartner(v);
		}		
		
		// Forward virtual edges
		ArrayList<VEdge> activeEdges = new ArrayList<VEdge>();
		while( !virtEdges.isEmpty() && virtEdges.first().n0 == v ) {
			VEdge e = virtEdges.pollFirst();
			
			// both ends of the virtual edge are the current node, skip
			if( e.n1 == v ) continue;
			
			// Skip edges with the duplicate end points (only retain the one with the highest saddle)
			if( !virtEdges.isEmpty() && virtEdges.first().n0 == v && virtEdges.first().n1 == e.n1 )
				continue;

			virtEdges.add( new VEdge(e.gen,n0,e.n1) );
			activeEdges.add(e);
		}

		// Short circuit virtual edges that connect to the current node 
		for(int i = 0; i < activeEdges.size(); i++) {
			VEdge ei = activeEdges.get(i);
			for(int j = i+1; j < activeEdges.size(); j++ ) {
				VEdge ej = activeEdges.get(j);
				if( ei.gen.value() < ej.gen.value() ) { virtEdges.add( new VEdge(ei.gen,ei.n1,ej.n1) ); }
				if( ej.gen.value() < ei.gen.value() ) { virtEdges.add( new VEdge(ej.gen,ej.n1,ei.n1) ); }
			}
		}

		// Forward labels across virtual edges
		for( VEdge ei : activeEdges ) {
			for( Label l : inLabels.get(v) ) {
				if( l.isPaired() ) continue;
				if( l.vrt.value() < ei.gen.value() ) {
					inLabels.get( ei.n1 ).add(l);
				}
			}
		}		
		
		// Forward labels across real edges
		for( Label l : inLabels.get(v) ) {
			if( !l.isPaired() ) {
				inLabels.get(n0).add( l );
			}
		}
				
	}
	
	
	private class Label implements Comparable<Label> {
		ReebGraphVertex vrt;
		int leg;
		
		Label( ReebGraphVertex _vrt, int _leg ){
			vrt = _vrt;
			leg = _leg;
		}
		
		public boolean isPaired() {
			return vrt.getPartner()!=null;
		}

		public int hashCode() {
			return vrt.hashCode() * (leg+13);
		}
		public NodeType getType() {
			return vrt.getType();
		}
		
		public boolean equals(Object obj) {
			if( obj instanceof Label ) {
				Label l = (Label)obj;
				return l.vrt == vrt && l.leg == leg;
			}
			return false;
		}
		public String toString() {
			return vrt.getGlobalID() + "[" + leg + "]";
		}

		@Override
		public int compareTo(Label o) {
			if( vrt.value() < o.vrt.value() ) return -1;
			if( vrt.value() > o.vrt.value() ) return  1;
			if( leg < o.leg ) return -1;
			if( leg > o.leg ) return  1;
			return 0;
		}

	}
	
	private class VEdge implements Comparable<VEdge> {
		ReebGraphVertex n0, n1;
		ReebGraphVertex gen;

		public VEdge(ReebGraphVertex _v, ReebGraphVertex _n0, ReebGraphVertex _n1) {
			gen = _v;
			n0 = (_n0.value()<_n1.value())?_n0:_n1;
			n1 = (_n0.value()<_n1.value())?_n1:_n0;
		}

		@Override
		public int compareTo(VEdge o) {
			if( n0.value() < o.n0.value() ) return -1;
			if( n0.value() > o.n0.value() ) return  1;
			if( n1.value() < o.n1.value() ) return -1;
			if( n1.value() > o.n1.value() ) return  1;
			if( gen.value() < o.gen.value() ) return -1;
			if( gen.value() > o.gen.value() ) return  1;
			return 0;
		}

		@Override
		public String toString() {
			return n0.getGlobalID() + "(" + n0.value() +")" + " ==> " + gen.getGlobalID() 
					+ " " + n1.getGlobalID() + "(" + n1.value() +")" + " ==> " + gen.getGlobalID();
		}
	}


	
}
