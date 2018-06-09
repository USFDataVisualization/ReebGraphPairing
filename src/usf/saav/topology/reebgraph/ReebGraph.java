package usf.saav.topology.reebgraph;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Vector;

import usf.saav.topology.TopoGraph;


public class ReebGraph extends TopoGraph<ReebGraphVertex> {

	private static final long serialVersionUID = 2799501955753168490L;

	public ReebGraphVertex createVertex( float val, int gid ){ 
		add( new ReebGraphVertex( size(), val, gid ) );
		return (ReebGraphVertex)lastElement();
	}

	
	public void resetInternalIDs() {
		int i = 0;
		for( Vertex v : this ) {
			((ReebGraphVertex)v).setID(i++);
		}		
	}
	
	public Vector<ReebGraphVertex> getNodesSortedByValue() {
		Vector<ReebGraphVertex> sortedNodes = new Vector<ReebGraphVertex>();
		
		for( Vertex v : this ) {
			sortedNodes.add((ReebGraphVertex)v);
		}
		
		sortedNodes.sort( new Comparator<ReebGraphVertex>() {
			public int compare(ReebGraphVertex o1, ReebGraphVertex o2) {
				if( o1.value() < o2.value() ) return -1;
				if( o1.value() > o2.value() ) return  1;
				return 0;
			}
		});
		return sortedNodes;
	}
	
	public int getMaxGlobalID() {
		int curMax = ((ReebGraphVertex)get(0)).getGlobalID();
		for( Vertex v : this ) {
			curMax = Math.max(curMax, ((ReebGraphVertex)v).getGlobalID() );
		}
		return curMax;
	}





	public String toDot() {
		StringBuffer dot_node = new StringBuffer( );
		StringBuffer dot_edge = new StringBuffer( );
		for(int i = 0; i < size(); i++){
			ReebGraphVertex curr = (ReebGraphVertex)get(i);

			dot_node.append( "\t" + curr.getGlobalID() + "[label=\"" + curr.toString() + "\"];\n");

			for( int n : curr.neighbors() ){
				for( int j = 0; j < size(); j++){
					ReebGraphVertex nei = (ReebGraphVertex)get(j);
					if( nei.getID() == n && nei.value() < curr.value() )
						dot_edge.append( "\t" + curr.getGlobalID() + " -> " + nei.getGlobalID() + "\n");
				}
			}					
		}
		return "Digraph{\n" + dot_node + dot_edge + "}"; 
	}


	



	public Vector<ReebGraph> extractConnectedComponents( ){
		
		HashSet<ReebGraphVertex> visited = new HashSet<ReebGraphVertex>();
		
		Vector<ReebGraph> ret = new Vector<ReebGraph>();
		for( Vertex v : this ) {
			ReebGraphVertex rv = (ReebGraphVertex)v;
			if( !visited.contains(rv) ){
				ret.add( findConnectedComponent(rv, visited) );
			}
		}
				
		return ret;
	}

	private static ReebGraph findConnectedComponent( ReebGraphVertex vertex, HashSet<ReebGraphVertex> visited ) {
		ReebGraph newGraph = new ReebGraph();
		dfs( newGraph, vertex, visited );
		newGraph.resetInternalIDs();
		return newGraph;
	}

	private static void dfs( ReebGraph newGraph, ReebGraphVertex curVertex, HashSet<ReebGraphVertex> visited ) {
		visited.add(curVertex);
		newGraph.add(curVertex);
		for( ReebGraphVertex n : curVertex.neighbors) {
			if( visited.contains(n) ) continue;
			dfs(newGraph, n, visited);
		}
	}	
	
	


}		

