package junyi.reebgraph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Vector;

import usf.saav.topology.TopoGraph;
import usf.saav.topology.TopoTreeNode;


public class ReebGraph extends TopoGraph {

	private static final long serialVersionUID = 2799501955753168490L;

	public ReebGraphVertex createVertex( float val, int gid ){ 
		add( new ReebGraphVertex( size(), val, gid ) );
		return (ReebGraphVertex)lastElement();
	}

	
	public void resetInternalIDs() {
		int i = 0;
		for( Vertex v : this ) {
			((ReebGraphVertex)v).id = i++;
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
		int curMax = ((ReebGraphVertex)get(0)).gid;
		for( Vertex v : this ) {
			curMax = Math.max(curMax, ((ReebGraphVertex)v).gid );
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
	
	


	public class ReebGraphVertex implements TopoGraph.Vertex, TopoTreeNode {
		private float val;
		private int id;
		private int gid;

		private ReebGraphVertex topoPartner;
		public ArrayList<ReebGraphVertex> neighbors = new ArrayList<ReebGraphVertex>();

		public ReebGraphVertex( int _id, float _val, int _gid ) {
			val = _val;
			id = _id;
			gid = _gid;
		}

		public String toString(){
			return getGlobalID() + "/" + id + " (" + value() + ")";
		}

		@Override
		public int[] neighbors() {
			int [] ret = new int[neighbors.size()];
			for(int i = 0; i < neighbors.size(); i++){
				ret[i] = neighbors.get(i).id;
			}
			return ret;
		}
		
		@Override public float value() { return val; }
		@Override public int getID() { return id; }
		public int getGlobalID() { return gid; }


		@Override 
		public NodeType getType() {
			int cntLess=0;
			int cntMore=0;
			for( ReebGraphVertex n : neighbors ) {
				if(value()<n.value()) cntLess++;		
				if(value()>n.value()) cntMore++;		
			}
			if( cntLess==0 ) return NodeType.LEAF_MAX;
			if( cntMore==0 ) return NodeType.LEAF_MIN;
			if( cntLess==2 ) return NodeType.SPLIT;
			if( cntMore==2 ) return NodeType.MERGE;
			return null;
		}


		public void addNeighbor(ReebGraphVertex v){
			neighbors.add(v);
		}

		@Override public TopoTreeNode getPartner() { return topoPartner; }
		public void setPartner( ReebGraphVertex p ) { topoPartner = p; }

		
		public boolean isEssential() {
			return (getType() == NodeType.MERGE && topoPartner.getType() == NodeType.SPLIT )
					|| (getType() == NodeType.SPLIT && topoPartner.getType() == NodeType.MERGE );
		}

		@Override public float getBirth() {
			if( topoPartner == null ) 
				return value();
			if( isEssential() ) 
				return Math.max( value(), topoPartner.value() ); 
			return Math.min( value(), topoPartner.value() ); 
		}

		@Override 
		 public float getDeath() { 
			if( topoPartner == null ) 
				return Float.POSITIVE_INFINITY;
			if( isEssential() ) 
				return Math.min( value(), topoPartner.value() );
			return Math.max( value(), topoPartner.value() ); 
		}

		@Override public float getPersistence() { return getDeath()-getBirth(); }


	}

}		

