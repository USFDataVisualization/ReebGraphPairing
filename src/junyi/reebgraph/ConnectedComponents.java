package src.junyi.reebgraph;

import java.util.Vector;

import src.junyi.reebgraph.cmd.ReebGraph;
import src.junyi.reebgraph.cmd.ReebGraph.ReebGraphVertex;
import usf.saav.mesh.Mesh.Vertex;

public class ConnectedComponents {

	public static Vector<ReebGraph> extractConnectedComponents( ReebGraph base ){
		Vector<ReebGraph> ret = new Vector<ReebGraph>();
		
		
		for( Vertex v : base ) {
			ReebGraphVertex rv = (ReebGraphVertex)v;
			rv.visted = false;
		}
		
		for( Vertex v : base ) {
			ReebGraphVertex rv = (ReebGraphVertex)v;
			if( rv.visted ) continue;
			
			ReebGraph newGraph = new ReebGraph();
			dfs( newGraph, rv );
			newGraph.resetInternalIDs();
			ret.add(newGraph);
		}
				
		return ret;
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
