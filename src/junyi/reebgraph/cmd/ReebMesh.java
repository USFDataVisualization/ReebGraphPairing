package junyi.reebgraph.cmd;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import junyi.reebgraph.cmd.ReebMesh.ReebVertex;
import usf.saav.mesh.Mesh;


public class ReebMesh extends Mesh {

		private static final long serialVersionUID = 2799501955753168490L;


		public class ReebVertex implements Mesh.Vertex {
			float val;
			int id;
			boolean  dfs;
			int gid;
			public boolean essent;
			boolean  mstVisted;
			
			//int [] n = new int[]{};
			ArrayList<ReebVertex> neighbors = new ArrayList<ReebVertex>();
			
			public ReebVertex( int _id, float _val, int _gid ) {
				val = _val;
				id = _id;
				gid = _gid;
				dfs=false;
				essent=true;
				mstVisted=false;
			}

			public String toString(){
				String ret = globalID() + "/" + id + " (" + value() + ")" + (essent?" e":"");
				for( ReebVertex n : neighbors ){
					ret += " " + n.globalID();
				}
				return ret;
			}
			
			public int globalID(){
				return gid;
			}

			//@Override
			public float value() {
				return val;
			}

			public void addNeighbor(ReebVertex v){
				neighbors.add(v);
				//n = Arrays.copyOf( n, n.length+1 );
				//n[n.length-1] = v.id();
			}
			
			//@Override
			public int[] neighbors() {
				int [] ret = new int[neighbors.size()];
				for(int i = 0; i < neighbors.size(); i++){
					ret[i] = neighbors.get(i).id;
				}
				return ret;
			}

			//@Override
			public int[] positions() { return null; }
			//@Override
			public int id() { return id; }
			
			public boolean visited() { return dfs; }
			
			public boolean essented() { return essent; }
			
			public boolean msted() { return mstVisted; }
			
			public void setvisit() {dfs=true;}			

			public void setess() {essent=true;}
			
			public void setmst() {mstVisted=true;}
			
			public void unsetmst() {mstVisted=false;}

			/*
			public void setNeighbors(int[] neighbors) {
				n = neighbors.clone();
				
			}
			*/
			
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
	
		}
		
		public ReebVertex createVertex( float val, int gid ){ 
			add( new ReebVertex( size(), val, gid ) );
			return (ReebVertex)lastElement();
		}
		
		/*
		public ReebVertex createVertex(int id, float value, int[] neighbors, int gid ) {
			add( new ReebVertex( id, value, gid ) );
			((ReebVertex)lastElement()).setNeighbors(neighbors);
			return (ReebVertex)lastElement();
		}
		*/

		
		
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

/*		
		public String toDot() {
			StringBuffer dot_node = new StringBuffer( );
			StringBuffer dot_edge = new StringBuffer( );
			for(int i = 0; i < size(); i++){
				ReebVertex curr = (ReebVertex)get(i);
				
				dot_node.append( "\t" + curr.id() + "[label=\"" + curr.id() + " (" + curr.value() + ")\"];\n");
				
				for( int n : curr.neighbors() ){
					//ReebVertex nei = (ReebVertex)get(n);
					for( int j = 0; j < size(); j++){
						ReebVertex nei = (ReebVertex)get(j);
						if( nei.id() == n && nei.value() < curr.value() )
							dot_edge.append( "\t" + curr.id() + " -> " + n + "\n");
					}
				}					
			}
			return "Digraph{\n" + dot_node + dot_edge + "}"; 
	}
	*/	
}		
		
