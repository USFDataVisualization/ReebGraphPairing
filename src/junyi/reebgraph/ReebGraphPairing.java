package src.junyi.reebgraph;

import java.util.Vector;

import src.junyi.reebgraph.cmd.ReebGraph;
import src.junyi.reebgraph.cmd.ReebSpanningTree;
import src.junyi.reebgraph.cmd.ReebGraph.ReebGraphVertex;
import usf.saav.mesh.Mesh.Vertex;
import usf.saav.topology.TopoTreeNode;
import usf.saav.topology.TopoTreeNode.NodeType;
import usf.saav.topology.merge.MergeTree;
import usf.saav.topology.split.SplitTree;

public class ReebGraphPairing {

	
	public ReebGraphPairing( ReebGraph reebMesh ) {
		

		MergeTree mt = new MergeTree( reebMesh );
		mt.run();


		TopoTreeNode gmax =null, gmin=null;
		for(int i = 0; i < mt.size(); i++ ){
			TopoTreeNode x = mt.getNode(i);
			reebMesh.getByID( x.getPosition() ).essent = false;
			if( x.getPartner() == null ){ 
				gmin = x; 
			}
			else{
				reebMesh.getByID( x.getPosition() ).topoPartner = reebMesh.getByID( x.getPartner().getPosition() );
			}
		}

		//System.out.println(mt.toDot());


		SplitTree st = new SplitTree(reebMesh );
		st.run();
		//System.out.println(st.toDot());
		for(int i = 0; i < st.size(); i++ ){
			TopoTreeNode x = st.getNode(i);
			reebMesh.getByID( x.getPosition() ).essent = false;
			if( x.getPartner() == null ){ 
				gmax = x; 
			}
			else{
				reebMesh.getByID( x.getPosition() ).topoPartner = reebMesh.getByID( x.getPartner().getPosition() );
				//System.out.println( x.getPosition() + " " + x.getPartner().getPosition() );
			}
		}

		reebMesh.getByID( gmin.getPosition() ).topoPartner = reebMesh.getByID( gmax.getPosition() );
		reebMesh.getByID( gmax.getPosition() ).topoPartner = reebMesh.getByID( gmin.getPosition() );

		//System.out.println(reebMesh.toDot());

		//System.out.println();
		//	System.out.println(reebMeshIndex2Id.toDot());
		//System.out.println();


		Vector<ReebGraphVertex> ess = new Vector<ReebGraphVertex>();
		//System.out.println(x.getPosition());
		for(int j = 0; j < reebMesh.size(); j++ ){
			if( ((ReebGraphVertex)reebMesh.get(j)).essent ){
				ess.add( ((ReebGraphVertex)reebMesh.get(j)) );
			}
		}


		for( ReebGraphVertex r : ess ){

			if( r.getType() == NodeType.ESS_DOWN_FORK ) {
				//System.out.println();
				//System.out.print("DOWNFORK -- ");
				//System.out.println( r.toString() + "   " + r.getType() );
				ReebSpanningTree pairing = new ReebSpanningTree(reebMesh, r);

				pairing.getUpFork().topoPartner = pairing.getDownFork();
				pairing.getDownFork().topoPartner = pairing.getUpFork();
				//System.out.println(pairing);
			}
		}


		//print_info_message( "Building tree complete" );
		
	}
}
