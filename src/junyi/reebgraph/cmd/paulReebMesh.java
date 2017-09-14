package src.junyi.reebgraph.cmd;

import java.util.Arrays;

import usf.saav.mesh.Mesh;

import usf.saav.topology.merge.MergeTree;
import usf.saav.topology.split.SplitTree;

public class paulReebMesh extends Mesh {

		private static final long serialVersionUID = 2799501955753168490L;


		public class ReebVertex implements Mesh.Vertex {
			float val;
			int id;
			int [] n = new int[]{};
			public ReebVertex( int _id, float _val) {
				val = _val;
				id = _id;
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
			
			//public Vertex getV(int id) { return id; }
			
			
		}
		
		public ReebVertex createVertex( int id, float val){ 
			add( new ReebVertex( id, val ) );
			return (ReebVertex)lastElement();
		}
		
		
		public static void main( String [] args ){
			/*
			paulReebMesh rb = new paulReebMesh();
			
			
			ReebVertex vA = rb.createVertex(1);
			ReebVertex vB = rb.createVertex(2);
			ReebVertex vC = rb.createVertex(3);
			ReebVertex vD = rb.createVertex(4);
			ReebVertex vE = rb.createVertex(5);
			ReebVertex vF = rb.createVertex(6);
			ReebVertex vG = rb.createVertex(7);
			ReebVertex vH = rb.createVertex(8);
			
			vA.addNeighbor(vC); vC.addNeighbor(vA);
			vB.addNeighbor(vD); vD.addNeighbor(vB);
			vC.addNeighbor(vD); vD.addNeighbor(vC);
			vC.addNeighbor(vE); vE.addNeighbor(vC);
			vD.addNeighbor(vF); vF.addNeighbor(vD);
			vE.addNeighbor(vF); vF.addNeighbor(vE);
			vE.addNeighbor(vG); vG.addNeighbor(vE);
			vF.addNeighbor(vH); vH.addNeighbor(vF);
			
			MergeTree mt = new MergeTree(rb);
			mt.run();
			System.out.println(mt.toDot());
			
			SplitTree st = new SplitTree(rb);
			st.run();
			System.out.println(st.toDot());
			*/
			
		}
}
