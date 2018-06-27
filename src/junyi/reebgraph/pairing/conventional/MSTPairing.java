package junyi.reebgraph.pairing.conventional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import usf.saav.common.SystemX;
import usf.saav.topology.TopoTreeNode.NodeType;
import usf.saav.topology.reebgraph.ReebGraphVertex;

public class MSTPairing {
	
	public MSTPairing( ReebGraphVertex downFork ) {

		HashSet<ReebGraphVertex> visited = new HashSet<ReebGraphVertex>();
		PriorityQueue<MSTEdge> edgeQueue = new PriorityQueue<MSTEdge>( new Comparator<MSTEdge>() {
			@Override public int compare(MSTEdge o1, MSTEdge o2) {
				if( o1.dst.value() > o2.dst.value() ) return -1;
				return 1;
			}
		} );
		
		
		ReebGraphVertex curV = downFork;
		visited.add(curV);
		for( ReebGraphVertex _n : curV.neighbors ) {
			if( _n.value() < downFork.value() ){
				edgeQueue.add( new MSTEdge(curV,_n) );
			}
		}
		
		ReebGraphVertex bestMatch = null;
		ArrayList<MSTEdge> mst = new ArrayList<MSTEdge>();
		ArrayList<MSTEdge> close = new ArrayList<MSTEdge>();
		
		while( !edgeQueue.isEmpty() ) {
			MSTEdge curr = edgeQueue.poll();
			curV = curr.dst;
			if( !visited.contains( curV ) ) {
				mst.add(curr);
			}
			else if( curr.isDownEdge() && curr.dst.getType() == NodeType.UPFORK && curr.dst.getPartner()==null ){
				System.out.println( curr.src + " -> " +  curr.dst );
				close.add(curr);
				if( bestMatch == null || curr.dst.value() > bestMatch.value() )
					bestMatch = curr.dst;
			}
			
			if( visited.contains( curV ) ) continue;
			
			visited.add(curV);
			
			for( ReebGraphVertex _n : curV.neighbors ) {
				if( _n.value() > downFork.value() ) continue;
				if( _n == curr.src ) continue;
				edgeQueue.add( new MSTEdge(curV, _n) );
			}
			
		}
		
		
		try {
			SystemX.writeStringToFile(toDot(mst,close), ConventionalPairing.tmp_directory + "mst.dot" );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		

		
		//System.out.println( "* " + v + ", " + bestMatch );
		if( bestMatch != null ) {
			downFork.setPartner(bestMatch);
			bestMatch.setPartner(downFork);
			//downFork.setPartner(bestMatch);
			//bestMatch.setPartner(downFork);
		}
	}	
	
	public String toDot( ArrayList<MSTEdge> mst, ArrayList<MSTEdge> close ) {
		StringBuffer dot_node = new StringBuffer( );
		StringBuffer dot_edge = new StringBuffer( );
		HashSet<ReebGraphVertex> verts = new HashSet<ReebGraphVertex>();
		
		for( MSTEdge m : mst ){
			dot_edge.append( "\t" + m.src.getID() + " -> " + m.dst.getID() + " [color = black];\n");
			verts.add(m.src);
			verts.add(m.dst);
		}
		for( MSTEdge m : close ){
			dot_edge.append( "\t" + m.src.getID() + " -> " + m.dst.getID() + " [color = red];\n");
			verts.add(m.src);
			verts.add(m.dst);
		}
		
		for( ReebGraphVertex v : verts ) {
			dot_node.append( "\t" + v.getID() + "[label=\"" + v.toString() + "\"];\n");
		}
		return "Digraph{\n" + dot_node + dot_edge + "}"; 
	}	


	class MSTEdge {
		ReebGraphVertex src,dst;
		public MSTEdge(ReebGraphVertex _src, ReebGraphVertex _dst) {
			src = _src;
			dst = _dst;
		}
		public boolean isUpEdge() { return src.value() < dst.value(); }
		public boolean isDownEdge() { return src.value() > dst.value(); }
	}
	
	
}
