package junyi.reebgraph;

import java.util.Vector;

import junyi.reebgraph.ReebGraph.ReebGraphVertex;
import usf.saav.mesh.Mesh.Vertex;

public class ConnectedComponents {

	public static Vector<ReebGraph> extractConnectedComponents( ReebGraph base ){
		
		base.clearVisited();
		
		Vector<ReebGraph> ret = new Vector<ReebGraph>();
		for( Vertex v : base ) {
			ReebGraphVertex rv = (ReebGraphVertex)v;
			if( !rv.visted ){
				ret.add( findConnectedComponent(rv) );
			}
		}
				
		return ret;
	}

	private static ReebGraph findConnectedComponent( ReebGraphVertex vertex ) {
		ReebGraph newGraph = new ReebGraph();
		dfs( newGraph, vertex );
		newGraph.resetInternalIDs();
		return newGraph;
	}

	private static void dfs( ReebGraph newGraph, ReebGraphVertex curVertex ) {
		curVertex.visted = true;
		newGraph.add(curVertex);
		for( ReebGraphVertex n : curVertex.neighbors) {
			if( n.visted ) continue;
			dfs(newGraph, n);
		}
	}
	
}
