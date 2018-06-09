package junyi.reebgraph.pairing.conventional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import junyi.reebgraph.pairing.conventional.ConventionalPairing.EssentialSaddleGraph;
import junyi.reebgraph.pairing.conventional.ConventionalPairing.EssentialSaddleGraphVertex;
import usf.saav.topology.TopoGraph;
import usf.saav.topology.TopoTreeNode.NodeType;
import usf.saav.topology.reebgraph.ReebGraphVertex;

public class MSTPairing {

	public MSTPairing( EssentialSaddleGraph g ) {
		
		int cnt = 0;
		for( TopoGraph.Vertex _v : g ) {
			EssentialSaddleGraphVertex v = (EssentialSaddleGraphVertex)_v;
			if( v.reebV.getType() == NodeType.DOWNFORK ) {
				//System.out.println(v);
				//System.out.println();
				buildMST( v );
				if( cnt >= 1 ) break;
				cnt++;
				
			}		
		}
	}

	private void buildMST(EssentialSaddleGraphVertex v) {
		HashSet<EssentialSaddleGraphVertex> visited = new HashSet<EssentialSaddleGraphVertex>();
		PriorityQueue<TestEdge> testEdges = new PriorityQueue<TestEdge>( new Comparator<TestEdge>() {
			@Override public int compare(TestEdge o1, TestEdge o2) {
				if( o1.lowV.value() > o2.lowV.value() ) return -1;
				return 1;
			}
		} );
		HashMap<EssentialSaddleGraphVertex,MSTNode> nodeMap = new HashMap<EssentialSaddleGraphVertex,MSTNode>();
		//System.out.println(createNeighbor( v, visited, new HashSet<EssentialSaddleGraphVertex>(), testEdges, nodeMap ));
		createNeighbor( v, v, visited, new HashSet<EssentialSaddleGraphVertex>(), testEdges, nodeMap );
		
		//System.out.println("Test Edges");
		for( TestEdge t : testEdges ) {
			HashSet<EssentialSaddleGraphVertex> p0 = nodeMap.get(t.lowV).path;
			HashSet<EssentialSaddleGraphVertex> p1 = nodeMap.get(t.highV).path;
			
			System.out.println( t.lowV + " <=> " + t.highV );
			System.out.println( "   " + nodeMap.get(t.lowV).path.toString() );
			System.out.println( "   " + nodeMap.get(t.highV).path.toString() );
			//System.out.println( countSetOverlap( p0, p1 ) );
			if( countSetOverlap( p0, p1 ) == 1 ) {
				System.out.println( v + ", " + t.lowV );
				break;
			}
		}
	}
	
	
	
	private int countSetOverlap(HashSet<EssentialSaddleGraphVertex> p0, HashSet<EssentialSaddleGraphVertex> p1) {
		int ret = 0;
		for( EssentialSaddleGraphVertex v : p0 ) {
			if( p1.contains(v) ) ret++;
		}
		return ret;
	}

	private MSTNode createNeighbor(EssentialSaddleGraphVertex root, EssentialSaddleGraphVertex v, HashSet<EssentialSaddleGraphVertex> visited, HashSet<EssentialSaddleGraphVertex> path, PriorityQueue<TestEdge> testEdges, HashMap<EssentialSaddleGraphVertex,MSTNode> nodeMap ) {
		
		MSTNode m = new MSTNode(v);
		m.path.addAll(path);
		visited.add(v);
		nodeMap.put( v, m );
		
		for( ReebGraphVertex _n : v.neighbors ) {
			EssentialSaddleGraphVertex n = (EssentialSaddleGraphVertex)_n;
			if( n.value() > root.value() ) continue;
			if( !visited.contains(n) ) {
				m.neighbors.add( createNeighbor( root, (EssentialSaddleGraphVertex)n, visited, m.path, testEdges, nodeMap) );
			}
			else {
				testEdges.add( new TestEdge(n,v) );
			}
		}
		return m;
	}

	class TestEdge{
		EssentialSaddleGraphVertex lowV,highV;
		public TestEdge(EssentialSaddleGraphVertex _n, EssentialSaddleGraphVertex _v) {
			if( _n.value() < _v.value() ) {
				lowV = _n; highV = _v;
			}
			else {
				lowV = _v; highV = _n;
			}
		}
	}
	
	class MSTNode {
		EssentialSaddleGraphVertex v;
		HashSet<EssentialSaddleGraphVertex> path = new HashSet<EssentialSaddleGraphVertex>(); 
		ArrayList<MSTNode> neighbors = new ArrayList<MSTNode>();
		public MSTNode(EssentialSaddleGraphVertex _v) {
			v = _v;
			path.add(v);
		}
		public String toString() {
			StringBuffer sb = new StringBuffer();
			toString(sb,"");
			return sb.toString();
		}
		private void toString(StringBuffer sb, String spaces) {
			sb.append(spaces);
			sb.append(v);
			sb.append("\n");
			for( MSTNode n : neighbors ) {
				n.toString(sb, spaces+"  ");
			}
		}
	}
}
