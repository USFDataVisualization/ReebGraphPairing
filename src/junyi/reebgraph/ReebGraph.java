package junyi.reebgraph;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import usf.saav.mesh.Mesh;
import usf.saav.topology.TopoTreeNode;


public class ReebGraph extends Mesh {

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


	
	
	public ReebGraph Normalize( float epsilon ) {
		
		ReebGraph rg = this;
		Queue<Mesh.Vertex> proc = new LinkedList<Mesh.Vertex>();
		proc.addAll(rg);
		
		while( !proc.isEmpty() ) {
			ReebGraphVertex rv = (ReebGraphVertex)proc.poll();
			
			int cntLess=0;
			int cntMore=0;
			for( ReebGraphVertex n : rv.neighbors ) {
				if(rv.value()<n.value()) cntLess++;		
				if(rv.value()>n.value()) cntMore++;		
			}
			
			// mixed upfork and downfork
			if( cntLess>=2 && cntMore>=2 ) {
				ReebGraphVertex newR = rg.createVertex( rv.value()+epsilon, rg.getMaxGlobalID()+1 );
				for( ReebGraphVertex n : rv.neighbors ) {
					if(rv.value()<n.value()) {
						newR.addNeighbor(n);
						n.neighbors.remove(rv);
						n.neighbors.add(newR);
					}
				}
				rv.neighbors.removeAll(newR.neighbors);
				rv.neighbors.add(newR);
				newR.neighbors.add(rv);
				proc.add(rv);
				proc.add(newR);
				continue;
			}
			
			// downfork with more than 2 connections
			if( cntLess==1 && cntMore>2 ) {
				ReebGraphVertex newR = rg.createVertex( rv.value()-epsilon, rg.getMaxGlobalID()+1 );
				int rcnt = 0;
				for( ReebGraphVertex n : rv.neighbors ) {
					if(rv.value()>n.value()) {
						if( rcnt>0 ) {
							newR.addNeighbor(n);
							n.neighbors.remove(rv);
							n.neighbors.add(newR);
						}
						rcnt++;
					}
				}
				rv.neighbors.removeAll(newR.neighbors);
				rv.neighbors.add(newR);
				newR.neighbors.add(rv);	
				proc.add(newR);
				continue;
			}		
			
			// upfork with more than 2 connections
			if( cntLess>2 && cntMore==1 ) {
				ReebGraphVertex newR = rg.createVertex( rv.value()+epsilon, rg.getMaxGlobalID()+1 );
				int rcnt = 0;
				for( ReebGraphVertex n : rv.neighbors ) {
					if(rv.value()<n.value()) {
						if( rcnt>0 ) {
							newR.addNeighbor(n);
							n.neighbors.remove(rv);
							n.neighbors.add(newR);
						}
						rcnt++;
					}
				}
				rv.neighbors.removeAll(newR.neighbors);
				rv.neighbors.add(newR);
				newR.neighbors.add(rv);
				proc.add(newR);
				continue;
			}		
			// non-critical node
			if( cntLess==1 && cntMore==1 ) {
				ReebGraphVertex n0 = rv.neighbors.get(0);
				ReebGraphVertex n1 = rv.neighbors.get(1);
				n0.neighbors.remove(rv);
				n1.neighbors.remove(rv);
				n0.neighbors.add(n1);
				n1.neighbors.add(n0);
				rg.remove(rv);
				continue;
			}			
			
			if( cntLess>=2 && cntMore==0 ) {
				ReebGraphVertex newR = rg.createVertex( rv.value()-epsilon, rg.getMaxGlobalID()+1 );
				rv.neighbors.add(newR);
				newR.neighbors.add(rv);
				proc.add(rv);
				continue;
			}		
			
			if( cntLess==0 && cntMore>=0 ) {
				ReebGraphVertex newR = rg.createVertex( rv.value()+epsilon, rg.getMaxGlobalID()+1 );
				rv.neighbors.add(newR);
				newR.neighbors.add(rv);
				proc.add(rv);
				continue;
			}				
			
		}
		
		rg.resetInternalIDs();
		
		return rg;
		
	}	

	public String toDot() {
		StringBuffer dot_node = new StringBuffer( );
		StringBuffer dot_edge = new StringBuffer( );
		for(int i = 0; i < size(); i++){
			ReebGraphVertex curr = (ReebGraphVertex)get(i);

			dot_node.append( "\t" + curr.globalID() + "[label=\"" + curr.toString() + "\"];\n");

			for( int n : curr.neighbors() ){
				//ReebVertex nei = (ReebVertex)get(n);
				for( int j = 0; j < size(); j++){
					ReebGraphVertex nei = (ReebGraphVertex)get(j);
					if( nei.id() == n && nei.value() < curr.value() )
						//if( nei.id() == n )
						dot_edge.append( "\t" + curr.globalID() + " -> " + nei.globalID() + "\n");
				}
			}					
		}
		return "Digraph{\n" + dot_node + dot_edge + "}"; 
	}



	public ReebGraphVertex getByID(int position) {
		for(int j = 0; j < size(); j++ ){
			//System.out.println( get(j).id() + " " + x.getPosition() );
			
			if( get(j).id() == position ){
				return ((ReebGraphVertex)get(j));
			}
		}
	
		return null;
	}		

	
	public void printPD() {
		for( Vertex v : this ){
			ReebGraphVertex rv = (ReebGraphVertex)v;
			System.out.println( rv.globalID() + ": [" + rv.getBirth() + ", " + rv.getDeath() + "]");
		}
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
	
	


	public class ReebGraphVertex implements Mesh.Vertex, TopoTreeNode {
		float val;
		int id;
		public int gid;

		public ReebGraphVertex topoPartner;

		public ArrayList<ReebGraphVertex> neighbors = new ArrayList<ReebGraphVertex>();

		public ReebGraphVertex( int _id, float _val, int _gid ) {
			val = _val;
			id = _id;
			gid = _gid;
		}

		public String toString(){
			return globalID() + "/" + id + " (" + value() + ")";
		}

		//@Override
		public int[] neighbors() {
			int [] ret = new int[neighbors.size()];
			for(int i = 0; i < neighbors.size(); i++){
				ret[i] = neighbors.get(i).id;
			}
			return ret;
		}


		
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


		public int globalID(){ return gid; }
		public void addNeighbor(ReebGraphVertex v){
			neighbors.add(v);
		}

		public float value() { return val; }

		 public int[] positions() { return null; }
		
		 public int id() { return id; }
		
		 public TopoTreeNode getPartner() { return topoPartner; }

		 public int getPosition() { return gid; }

		 public float getBirth() {
			if( topoPartner == null ) return value();
			boolean essent = (getType() == NodeType.MERGE && topoPartner.getType() == NodeType.SPLIT )
							|| (getType() == NodeType.SPLIT && topoPartner.getType() == NodeType.MERGE );
			if( essent ) return Math.max( value(), topoPartner.value() ); 
			return Math.min( value(), topoPartner.value() ); 
		}

		 public float getDeath() { 
			if( topoPartner == null ) return Float.POSITIVE_INFINITY;
			boolean essent = (getType() == NodeType.MERGE && topoPartner.getType() == NodeType.SPLIT )
					|| (getType() == NodeType.SPLIT && topoPartner.getType() == NodeType.MERGE );
			if( essent ) return Math.min( value(), topoPartner.value() );
			return Math.max( value(), topoPartner.value() ); 
		}

		 public float getPersistence() { return getDeath()-getBirth(); }

	}

}		

