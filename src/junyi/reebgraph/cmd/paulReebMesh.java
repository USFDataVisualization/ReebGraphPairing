package src.junyi.reebgraph.cmd;


import java.util.Arrays;


import usf.saav.mesh.Mesh;

public class paulReebMesh extends Mesh {

		private static final long serialVersionUID = 2799501955753168490L;


		public class ReebVertex implements Mesh.Vertex {
			float val;
			int id;
			boolean  b;
			
			int [] n = new int[]{};
			public ReebVertex( int _id, float _val) {
				val = _val;
				id = _id;
				b=false;
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
			
			//public Vertex getV(int id) { return id; }
			
			
		}
		
		public ReebVertex createVertex( int id, float val){ 
			add( new ReebVertex( id, val ) );
			return (ReebVertex)lastElement();
		}
		
}		
		
