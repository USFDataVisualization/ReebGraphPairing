package junyi.reebgraph.cmd;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import junyi.reebgraph.cmd.ReebMesh.ReebVertex;
import usf.saav.mesh.Mesh;
import usf.saav.topology.TopoTreeNode;


public class ReebMesh extends Mesh {

	private static final long serialVersionUID = 2799501955753168490L;

	public ReebVertex createVertex( float val, int gid ){ 
		add( new ReebVertex( size(), val, gid ) );
		return (ReebVertex)lastElement();
	}


	public String toDot() {
		StringBuffer dot_node = new StringBuffer( );
		StringBuffer dot_edge = new StringBuffer( );
		for(int i = 0; i < size(); i++){
			ReebVertex curr = (ReebVertex)get(i);

			dot_node.append( "\t" + curr.globalID() + "[label=\"" + curr.toString() + "\"];\n");

			for( int n : curr.neighbors() ){
				//ReebVertex nei = (ReebVertex)get(n);
				for( int j = 0; j < size(); j++){
					ReebVertex nei = (ReebVertex)get(j);
					if( nei.id() == n && nei.value() < curr.value() )
						//if( nei.id() == n )
						dot_edge.append( "\t" + curr.globalID() + " -> " + nei.globalID() + "\n");
				}
			}					
		}
		return "Digraph{\n" + dot_node + dot_edge + "}"; 
	}

	public void resetVisited() {
		for( Vertex v : this ){
			((ReebVertex)v).visted = false;
		}
	}






	public class ReebVertex implements Mesh.Vertex, TopoTreeNode {
		float val;
		int id;
		int gid;
		public boolean essent;
		boolean  visted;
		public ReebVertex topoPartner;


		ArrayList<ReebVertex> neighbors = new ArrayList<ReebVertex>();

		public ReebVertex( int _id, float _val, int _gid ) {
			val = _val;
			id = _id;
			gid = _gid;
			//dfs=false;
			essent=true;
			visted=false;
		}

		public String toString(){
			String ret = globalID() + "/" + id + " (" + value() + ")" + (essent?" e":"");
			for( ReebVertex n : neighbors ){
				ret += " " + n.globalID();
			}
			return ret;
		}


		//@Override
		public int[] neighbors() {
			int [] ret = new int[neighbors.size()];
			for(int i = 0; i < neighbors.size(); i++){
				ret[i] = neighbors.get(i).id;
			}
			return ret;
		}




		//if two neighbors are smaller, then down fork, otherwise, up fork
		public boolean isdownfork( ){
			int cnt=0;
			for( ReebVertex n : neighbors ) {
				if(value()>n.value()) cnt++;		
			}
			if(cnt==2) return true;
			else return false;
		}

		public boolean isupfork( ){
			int cnt=0;
			for( ReebVertex n : neighbors ) {
				if(value()<n.value()) cnt++;		
			}
			if(cnt==2) return true;
			else return false;
		}

		@Override
		public NodeType getType() {
			// TODO Auto-generated method stub
			return null;
		}


		public int globalID(){ return gid; }
		public void addNeighbor(ReebVertex v){
			neighbors.add(v);
		}

		@Override public float value() { return val; }

		@Override public int[] positions() { return null; }
		
		@Override public int id() { return id; }
		
		@Override public TopoTreeNode getPartner() { return topoPartner; }

		@Override public int getPosition() { return gid; }

		@Override public float getBirth() {
			if( topoPartner == null ) return value();
			if( essent ) return Math.max( value(), topoPartner.value() ); 
			return Math.min( value(), topoPartner.value() ); 
		}

		@Override public float getDeath() { 
			if( topoPartner == null ) return Float.POSITIVE_INFINITY;
			if( essent ) return Math.min( value(), topoPartner.value() );
			return Math.max( value(), topoPartner.value() ); 
		}

		@Override public float getPersistence() { return getDeath()-getBirth(); }

	}






	public ReebVertex getByID(int position) {
		for(int j = 0; j < size(); j++ ){
			//System.out.println( get(j).id() + " " + x.getPosition() );
			
			if( get(j).id() == position ){
				return ((ReebVertex)get(j));
			}
		}
	
		return null;
	}		
}		

