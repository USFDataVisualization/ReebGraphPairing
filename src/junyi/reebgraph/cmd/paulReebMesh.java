package src.junyi.reebgraph.cmd;


import java.util.Arrays;


import usf.saav.mesh.Mesh;


public class paulReebMesh extends Mesh {

		private static final long serialVersionUID = 2799501955753168490L;


		public class ReebVertex implements Mesh.Vertex {
			float val;
			int id;
			boolean  b;
			int gid;
			
			int [] n = new int[]{};
			public ReebVertex( int _id, float _val, int _gid ) {
				val = _val;
				id = _id;
				gid = _gid;
				b=false;
			}

			public int globalID(){
				return gid;
			}

			//@Override
			public float value() {
				return val;
			}

			public void addNeighbor(Vertex v){
				n = Arrays.copyOf( n, n.length+1 );
				n[n.length-1] = v.id();
			}
			
			//@Override
			public int[] neighbors() {
				return n;
			}

			//@Override
			public int[] positions() { return null; }
			//@Override
			public int id() { return id; }
			
			public boolean visited() { return b; }
			
			public void setvisit() {b=true;}



			public void setNeighbors(int[] neighbors) {
				n = neighbors.clone();
				
			}
	
		}
		
		public ReebVertex createVertex( int id, float val, int gid ){ 
			add( new ReebVertex( id, val, gid ) );
			return (ReebVertex)lastElement();
		}

		public ReebVertex createVertex(int id, float value, int[] neighbors, int gid ) {
			add( new ReebVertex( id, value, gid ) );
			((ReebVertex)lastElement()).setNeighbors(neighbors);
			return (ReebVertex)lastElement();
		}

		
		
		public String toDot() {
				StringBuffer dot_node = new StringBuffer( );
				StringBuffer dot_edge = new StringBuffer( );
				for(int i = 0; i < size(); i++){
					ReebVertex curr = (ReebVertex)get(i);
					
					dot_node.append( "\t" + curr.globalID() + "[label=\"" + curr.globalID() + " (" + curr.value() + ")\"];\n");
					
					for( int n : curr.neighbors() ){
						//ReebVertex nei = (ReebVertex)get(n);
						for( int j = 0; j < size(); j++){
							ReebVertex nei = (ReebVertex)get(j);
							if( nei.id() == n && nei.value() < curr.value() )
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
		
