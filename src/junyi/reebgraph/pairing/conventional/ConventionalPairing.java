package junyi.reebgraph.pairing.conventional;

import java.io.IOException;
import java.util.HashSet;

import usf.saav.common.SystemX;
import usf.saav.topology.TopoGraph;
import usf.saav.topology.TopoTreeNode;
import usf.saav.topology.TopoTreeNode.NodeType;
import usf.saav.topology.merge.AugmentedMergeTree;
import usf.saav.topology.merge.JoinTree;
import usf.saav.topology.merge.SplitTree;
import usf.saav.topology.reebgraph.ReebGraph;
import usf.saav.topology.reebgraph.ReebGraphVertex;
import usf.saav.topology.reebgraph.pairing.Pairing;

public class ConventionalPairing implements Pairing {

	public static String tmp_directory = "/Users/prosen/Code/reebgraphsim/tmp/";
	
	
	public ConventionalPairing( ) { }
	
	public String getName() { return "MST Pairing"; }
	
	public void pair( ReebGraph reebMesh ) {
		
		HashSet<TopoGraph.Vertex> essential = new HashSet<TopoGraph.Vertex>();
		essential.addAll( reebMesh );

		JoinTree mt = new JoinTree( reebMesh, true );
		try {
			SystemX.writeStringToFile(mt.toDot(), tmp_directory + "mt.dot" );
		} catch (IOException e) {
			e.printStackTrace();
		}

		SplitTree st = new SplitTree( reebMesh, true );
		try {
			SystemX.writeStringToFile(st.toDot(), tmp_directory + "st.dot" );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ReebGraphVertex gmin = JoinTreePairing( mt, reebMesh, essential );
		ReebGraphVertex gmax = JoinTreePairing( st, reebMesh, essential );
		
		gmin.setPartner(gmax);
		gmax.setPartner(gmin);
		
		for( ReebGraphVertex v : reebMesh ) {
			if( v.getType() == NodeType.DOWNFORK && v.getPartner()==null ) {
				if( v.neighbors.get(0) == v.neighbors.get(1) || v.neighbors.get(0) == v.neighbors.get(2) ) {
					v.setPartner( v.neighbors.get(0) );
					v.neighbors.get(0).setPartner( v );
				}
				else if( v.neighbors.get(1) == v.neighbors.get(2)  ) {
					v.setPartner( v.neighbors.get(1) );
					v.neighbors.get(1).setPartner( v );
				}
				else {
					//System.out.println( v );
					//if( v.getGlobalID()==87 )
						new MSTPairing(v);
				}
			}
		}
		
		/*
		EssentialSaddleGraph esg = new EssentialSaddleGraph(reebMesh);
		try {
			SystemX.writeStringToFile( esg.toDot(), tmp_directory + "es_graph.dot" );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//new MSTPairing(esg);
		//int cnt = 0;
		//for( TopoGraph.Vertex _v : esg ) {
		for( TopoGraph.Vertex _v : esg ) {
			//EssentialSaddleGraphVertex v = (EssentialSaddleGraphVertex)_v;
			EssentialSaddleGraphVertex v = (EssentialSaddleGraphVertex)_v;
			//if( v.getGlobalID() != 59 ) continue;
			//if( v.reebV.getType() == NodeType.DOWNFORK ) {
			if( v.getType() == NodeType.DOWNFORK ) {
				
				if( v.neighbors.get(0) == v.neighbors.get(1) || v.neighbors.get(0) == v.neighbors.get(2) ) {
					v.reebV.setPartner( ((EssentialSaddleGraphVertex)v.neighbors.get(0)).reebV );
					((EssentialSaddleGraphVertex)v.neighbors.get(0)).reebV.setPartner( v.reebV );
					continue;
				}
				if( v.neighbors.get(1) == v.neighbors.get(2)  ) {
					v.reebV.setPartner( ((EssentialSaddleGraphVertex)v.neighbors.get(1)).reebV );
					((EssentialSaddleGraphVertex)v.neighbors.get(1)).reebV.setPartner( v.reebV );
					continue;
				}
				 
				
				//System.out.println(v);
				//System.out.println();
				new MSTPairing( v );
				//if( cnt >= 1 ) break;
				//cnt++;
				
			}		
		}
		System.out.println();
		*/

		/*
		for( TopoGraph.Vertex s : essential ) {
			ReebGraphVertex r = (ReebGraphVertex)s;

			if( r.getType() == NodeType.DOWNFORK ) {
				EssentialPairing pairing = new EssentialPairing(reebMesh, r);

				if( pairing.getUpFork() != null && pairing.getDownFork() != null ) {
					pairing.getUpFork().setPartner(pairing.getDownFork());
					pairing.getDownFork().setPartner(pairing.getUpFork());
				}
				
			}
		}
		*/
		
	}
	
	/*
	private EssentialSaddleGraph buildEssentialSaddleGraph( ReebGraph reeb ) {
		
		return new EssentialSaddleGraph(reeb);
		
	}
	 */
	/*
	
	class EssentialSaddleGraph extends ReebGraph {
		private static final long serialVersionUID = -6195836137369945447L;
		
		public EssentialSaddleGraph( ReebGraph reeb ){
			HashMap< ReebGraphVertex, EssentialSaddleGraphVertex > vmap = new HashMap< ReebGraphVertex, EssentialSaddleGraphVertex >();
			for( Vertex _v : reeb ) {
				ReebGraphVertex v = (ReebGraphVertex)_v;
				if( v.getType() != NodeType.LEAF_MAX && v.getType() != NodeType.LEAF_MIN ) {
					EssentialSaddleGraphVertex newV = new EssentialSaddleGraphVertex( size(), v );
					add(newV);					
					vmap.put( v, newV );
				}
			}
			
			Queue<EssentialSaddleGraphVertex> proc = new LinkedList<EssentialSaddleGraphVertex>();
			for( Vertex _v : this ) {
				EssentialSaddleGraphVertex v = (EssentialSaddleGraphVertex)_v;
				for( ReebGraphVertex n : v.reebV.neighbors ) {
					if( vmap.containsKey(n) ) v.addNeighbor( vmap.get(n) );
				}
				if( v.reebV.getPartner() != null && !v.reebV.isEssential() ) proc.add(v);
			}
			
			while( !proc.isEmpty() ) {
				EssentialSaddleGraphVertex v = proc.poll();
				int cntAbove=0;
				int cntBelow=0;
				for( ReebGraphVertex n : v.neighbors ) {
					if(v.value()<n.value()) {
						cntAbove++;
					}
					if(v.value()>n.value()) {
						cntBelow++;
					}
				}
				
				if( cntAbove==1 && cntBelow==1 ) {
					ReebGraphVertex v0 = v.neighbors.get(0);
					ReebGraphVertex v1 = v.neighbors.get(1);
					v0.neighbors.remove(v);
					v1.neighbors.remove(v);
					v0.neighbors.add(v1);
					v1.neighbors.add(v0);
					this.remove(v);
				}			
				else if( cntAbove==2 && cntBelow==1 ) {
					ReebGraphVertex a0 = v.neighbors.get(0);
					ReebGraphVertex a1 = v.neighbors.get(1);
					ReebGraphVertex a2 = v.neighbors.get(2);
					if( a1.value() > a0.value() ) { ReebGraphVertex t = a0; a0 = a1; a1 = t; }
					if( a2.value() > a0.value() ) { ReebGraphVertex t = a0; a0 = a2; a2 = t; }
					if( a2.value() > a1.value() ) { ReebGraphVertex t = a1; a1 = a2; a2 = t; }
					//System.out.println( a0.value() + " " + a1.value() + " " + b0.value());
					a0.neighbors.remove(v);
					a1.neighbors.remove(v);
					a2.neighbors.remove(v);
					a0.neighbors.add(a1); a1.neighbors.add(a0);
					a1.neighbors.add(a2); a2.neighbors.add(a1);
					this.remove(v);
					//break;
				}		
				else if( (cntAbove+cntBelow)==1 ){
					ReebGraphVertex v0 = v.neighbors.get(0);
					v0.neighbors.remove(v);
					this.remove(v);
				}
				else {
					System.out.println("unsure " + v + " " + cntAbove + " " + cntBelow );
					break;
				}

			}
			this.resetInternalIDs();
		}

		
	}
	
	class EssentialSaddleGraphVertex extends ReebGraphVertex {
		ReebGraphVertex reebV;
		public EssentialSaddleGraphVertex(int _id, ReebGraphVertex from) {
			super( from.value(), from.getRealValue(), from.getGlobalID());
			reebV = from;
		}
	}
	*/


	private ReebGraphVertex JoinTreePairing( AugmentedMergeTree jt, ReebGraph reebMesh, HashSet<TopoGraph.Vertex> essential ) {
		ReebGraphVertex gmin=null;
		for(int i = 0; i < jt.size(); i++ ){
			TopoTreeNode    mtv = jt.getNode(i);
			TopoTreeNode    mtp = mtv.getPartner();
			ReebGraphVertex rbv = (ReebGraphVertex)reebMesh.get( mtv.getID() );
			
			essential.remove( rbv );
			if( mtp == null ) 
				gmin = rbv; 
			else
				rbv.setPartner( (ReebGraphVertex)reebMesh.get( mtp.getID() ) );
		}
		return gmin;
		
	}
	
}
