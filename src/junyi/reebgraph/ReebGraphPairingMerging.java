package junyi.reebgraph;

import java.util.Comparator;
import java.util.Vector;

import junyi.reebgraph.cmd.ReebGraph;
import junyi.reebgraph.cmd.ReebGraph.ReebGraphVertex;
import usf.saav.mesh.Mesh.Vertex;
import usf.saav.topology.TopoTreeNode.NodeType;

public class ReebGraphPairingMerging {

	public ReebGraphPairingMerging( ReebGraph reebMesh ) {

		Vector<ReebGraphVertex> sortedNodes = new Vector<ReebGraphVertex>();
		
		for( Vertex v : reebMesh ) {
			sortedNodes.add((ReebGraphVertex)v);
		}
		
		sortedNodes.sort( new Comparator<ReebGraphVertex>() {
			public int compare(ReebGraphVertex o1, ReebGraphVertex o2) {
				if( o1.value() < o2.value() ) return -1;
				if( o1.value() > o2.value() ) return  1;
				return 0;
			}
		});
		
		
		for( ReebGraphVertex v : sortedNodes ) {
			Vector<ReebGraphVertex> outlist = new Vector<ReebGraphVertex>();
			
			if( v.getType() == NodeType.ESS_DOWN_FORK || v.getType() == NodeType.MERGE  ) {
				//System.out.println("down: " + v.value());
				processDownFork(v,outlist);
			}
			else if( v.getType() == NodeType.LEAF_MAX ) {
				//System.out.println("local max: " + v.value());
				
				ReebGraphVertex maxSaddle = null;
				for( ReebGraphVertex n : v.in0 ) {
					if( n.topoPartner == null ) {
						maxSaddle = n;
					}
				}
				
				v.topoPartner = maxSaddle;
				maxSaddle.topoPartner = v;
				v.essent = false;
				maxSaddle.essent = false;
			}
			else {
				if( v.in0 != null ) {
					for(ReebGraphVertex n : v.in0 ) {
						if( n.topoPartner == null )
							outlist.add(n);
					}
				}
				outlist.add(v);
			}
			for( ReebGraphVertex n : v.neighbors ) {
				if( n.value() > v.value() ) {
					if( n.in0 == null ) n.in0 = outlist;
					else n.in1 = outlist;
				}
			}
			
			//System.out.println( v.value() + " " + outlist );
			
		}
		
		/*
		for( Vertex v : reebMesh ){
			ReebGraphVertex rv = (ReebGraphVertex)v;
			System.out.println( rv.globalID() + ": [" + rv.getBirth() + ", " + rv.getDeath() + "]");
		}
		*/
	}


	private void processDownFork(ReebGraphVertex v, Vector<ReebGraphVertex> outlist ) {

		int i = 0, j = 0;
		
		ReebGraphVertex maxPair = null;
		ReebGraphVertex maxMin  = null;
		while( i < v.in0.size() && j < v.in1.size() ) {
			if( v.in0.get(i).value() < v.in1.get(j).value() ) {
				if( v.in0.get(i).getType() == NodeType.LEAF_MIN ) maxMin = v.in0.get(i);
				i++;
			}
			else if( v.in0.get(i).value() > v.in1.get(j).value() ) {
				if( v.in1.get(j).getType() == NodeType.LEAF_MIN ) maxMin = v.in1.get(j);
				j++;
			}
			else {
				maxPair = v.in0.get(i);
				i++;
				j++;
			}
		}
		
		ReebGraphVertex partner;
		
		if( maxPair != null ) { 
			// MERGE TO CLOSE CYCLE
			partner = maxPair;
			v.essent = true;
			partner.essent = true;
		}
		else { // REGULAR MERGE
			partner = maxMin;
			v.essent = false;
			partner.essent = false;
		}
			
		v.topoPartner = partner;
		partner.topoPartner = v;
		
		
		i = 0; j = 0;
		while( i < v.in0.size() && j < v.in1.size() ) {
			if( v.in0.get(i).value() < v.in1.get(j).value() ) {
				if( v.in0.get(i).topoPartner == null )
					outlist.add( v.in0.get(i) );
				i++;
			}
			else if( v.in0.get(i).value() > v.in1.get(j).value() ) {
				if( v.in1.get(j).topoPartner == null )
					outlist.add( v.in1.get(j) );
				j++;
			}
			else {
				if( v.in0.get(i).topoPartner == null ) {
					outlist.add( v.in0.get(i) );
				}
				i++;
				j++;
			}
		}
		while( i < v.in0.size() ) {
			if( v.in0.get(i).topoPartner == null )
				outlist.add( v.in0.get(i) );
			i++;
		}
		while( j < v.in1.size() ) {
			if( v.in1.get(j).topoPartner == null )
				outlist.add( v.in1.get(j) );
			j++;
		}
	}

	
}
