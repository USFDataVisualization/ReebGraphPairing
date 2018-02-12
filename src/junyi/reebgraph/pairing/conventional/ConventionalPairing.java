package junyi.reebgraph.pairing.conventional;

import java.io.IOException;
import java.util.HashSet;

import junyi.reebgraph.ReebGraph;
import junyi.reebgraph.ReebGraph.ReebGraphVertex;
import usf.saav.mesh.Mesh;
import usf.saav.topology.TopoTreeNode;
import usf.saav.topology.TopoTreeNode.NodeType;
import usf.saav.topology.join.AugmentedJoinTree;
import usf.saav.topology.merge.MergeTree;
import usf.saav.topology.split.SplitTree;

public class ConventionalPairing {

	public static String tmp_directory = "/Users/prosen/Code/reebgraphsim/tmp/";
	
	
	public ConventionalPairing( ReebGraph reebMesh ) throws IOException {
		
		HashSet<Mesh.Vertex> essential = new HashSet<Mesh.Vertex>();
		essential.addAll( reebMesh );

		MergeTree mt = new MergeTree( reebMesh );
		mt.run();
		//SystemXv2.writeDot(mt.toDot(), tmp_directory + "mt.dot", tmp_directory + "mt.pdf" );

		SplitTree st = new SplitTree( reebMesh );
		st.run();
		//SystemXv2.writeDot(st.toDot(), tmp_directory + "st.dot", tmp_directory + "st.pdf" );
		
		ReebGraphVertex gmin = JoinTreePairing( mt, reebMesh, essential );
		ReebGraphVertex gmax = JoinTreePairing( st, reebMesh, essential );
		
		gmin.topoPartner = gmax;
		gmax.topoPartner = gmin;

		for( Mesh.Vertex s : essential ) {
			ReebGraphVertex r = (ReebGraphVertex)s;

			if( r.getType() == NodeType.MERGE ) {
				EssentialPairing pairing = new EssentialPairing(reebMesh, r);

				if( pairing.getUpFork() != null && pairing.getDownFork() != null ) {
					pairing.getUpFork().topoPartner = pairing.getDownFork();
					pairing.getDownFork().topoPartner = pairing.getUpFork();
				}
			}
		}
		
	}
	

	private ReebGraphVertex JoinTreePairing( AugmentedJoinTree jt, ReebGraph reebMesh, HashSet<Mesh.Vertex> essential ) {
		ReebGraphVertex gmin=null;
		for(int i = 0; i < jt.size(); i++ ){
			TopoTreeNode    mtv = jt.getNode(i);
			TopoTreeNode    mtp = mtv.getPartner();
			ReebGraphVertex rbv = reebMesh.getByID( mtv.getPosition() );
			
			essential.remove( rbv );
			if( mtp == null ) 
				gmin = rbv; 
			else
				rbv.topoPartner = reebMesh.getByID( mtp.getPosition() );
		}
		return gmin;
		
	}
	
}