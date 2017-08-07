package src.junyi.reebgraph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;




class Node implements Serializable, Vertex{
		private static final long serialVersionUID = 1L;

		int id;
		
		float fn;
		//VertexType type;
		ArrayList<Integer> neighbors = new ArrayList<Integer>();
		//ArrayList<Arc> next = new ArrayList<Arc>();
		
		public static class ComparatorValueAscending implements Comparator<Object> {
			
			public int compare(Object o1, Object o2) {
				if( o1 instanceof Node && o2 instanceof Node ){
					if( ((Node)o1).value() > ((Node)o2).value() ) return  1;
					if( ((Node)o1).value() < ((Node)o2).value() ) return -1;
					
				}
				return 0;
			}	
		}

		

		public float value() {
			// TODO Auto-generated method stub
			return fn;
		}

		public ArrayList<Integer> neighbors() {
			// TODO Auto-generated method stub
			return neighbors;
		}
		
		public void addNeighbor(int n) {
			// TODO Auto-generated method stub
			neighbors.add(n);
		}

		public int[] positions() {
			// TODO Auto-generated method stub
			return null;
		}

		public int id() {
			// TODO Auto-generated method stub
			return id;
		}
	}


