package usf.saav.topology.reebgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import usf.saav.topology.TopoGraph;


public class ReebGraph extends TopoGraph<ReebGraphVertex> {

	private static final long serialVersionUID = 2799501955753168490L;


	public ReebGraph() { }
	
	public ReebGraph(Collection<ReebGraphVertex> verts) {
		this.addAll(verts);
		this.resetInternalIDs();
		this.resetInternalValues();
	}

	public void resetInternalIDs() {
		int i = 0;
		for( TopoGraph.Vertex v : this ) {
			((ReebGraphVertex)v).setID(i++);
		}		
	}
	
	public void resetInternalValues() {
		ArrayList<ReebGraphVertex> sorted = getNodesSortedByValue();
		for(int i = 0; i < sorted.size(); i++) {
			sorted.get(i).setValue(i);
		}
	}
	
	public ArrayList<ReebGraphVertex> getNodesSortedByValue() {
		
		ArrayList<ReebGraphVertex> sortedNodes = new ArrayList<ReebGraphVertex>();
		for( TopoGraph.Vertex v : this ) {
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
	

	public String toDot() {
		StringBuffer dot_node = new StringBuffer( );
		StringBuffer dot_edge = new StringBuffer( );
		for(int i = 0; i < size(); i++){
			ReebGraphVertex curr = (ReebGraphVertex)get(i);

			dot_node.append( "\t" + curr.getID() + "[label=\"" + curr.toString() + "\"];\n");

			for( Vertex n : curr.neighbors() ){
				for( int j = 0; j < size(); j++){
					ReebGraphVertex nei = (ReebGraphVertex)get(j);
					if( nei == n && nei.value() < curr.value() )
						dot_edge.append( "\t" + curr.getID() + " -> " + nei.getID() + "\n");
				}
			}					
		}
		return "Digraph{\n" + dot_node + dot_edge + "}"; 
	}

}		

