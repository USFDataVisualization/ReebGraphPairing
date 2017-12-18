package junyi.reebgraph;

import java.util.ArrayList;

import junyi.reebgraph.cmd.ReebGraph;
import junyi.reebgraph.cmd.ReebGraph.ReebGraphVertex;
import usf.saav.mesh.Mesh.Vertex;

public class ReebGraphRegularization {

	public static float EPSILON = 0.001f;
	public static int   GLOBAL_ID_OFFSET = 10000;
	
	
	
	public static ReebGraph regularize( ReebGraph rg ) {
		
		//for( Vertex v : rg ) {
		for(int i = 0; i < rg.size(); i++) {
			ReebGraphVertex rv = (ReebGraphVertex)rg.get(i);
			
			int cntLess=0;
			int cntMore=0;
			for( ReebGraphVertex n : rv.neighbors ) {
				if(rv.value()<n.value()) cntLess++;		
				if(rv.value()>n.value()) cntMore++;		
			}
			
			if( cntMore != 1 && (cntMore%2) == 1 ) { // down fork
				ReebGraphVertex newR = rg.createVertex( rv.value()+EPSILON, rv.globalID() + GLOBAL_ID_OFFSET ); 
				System.out.println("Problem Case (more) " + rv );
				ArrayList<ReebGraphVertex> newN = new ArrayList<ReebGraphVertex>();
				
				for( ReebGraphVertex n : rv.neighbors ) {
					if( n.value() > rv.value() ) {
						n.neighbors.remove(rv);
						n.neighbors.add( newR );
						newR.neighbors.add(n);
					}
					else {
						if( newN.size() < 2 ) {
							newN.add( n );
						}
						else {
							n.neighbors.remove(rv);
							n.neighbors.add( newR );
							newR.neighbors.add(n);
						}
					}
				}
				
				newR.neighbors.add(rv);
				rv.neighbors = newN;
				rv.neighbors.add(newR);
				
			}
			if( cntLess != 1 && (cntLess%2) == 1 ) { // up fork
				System.out.println("Problem Case (less) " + rv );
				ReebGraphVertex newR = rg.createVertex( rv.value()-EPSILON, rv.globalID() + GLOBAL_ID_OFFSET ); 
				ArrayList<ReebGraphVertex> newN = new ArrayList<ReebGraphVertex>();
				
				for( ReebGraphVertex n : rv.neighbors ) {
					if( n.value() < rv.value() ) {
						n.neighbors.remove(rv);
						n.neighbors.add( newR );
						newR.neighbors.add(n);
					}
					else {
						if( newN.size() < 2 ) {
							newN.add( n );
						}
						else {
							n.neighbors.remove(rv);
							n.neighbors.add( newR );
							newR.neighbors.add(n);
						}
					}
				}
				
				newR.neighbors.add(rv);
				rv.neighbors = newN;
				rv.neighbors.add(newR);
				

			}

		}
		
		rg.resetInternalIDs();
		
		return rg;
		
	}
}
