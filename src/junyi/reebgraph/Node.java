package src.junyi.reebgraph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;




class Node implements Serializable, Simplex {
		private static final long serialVersionUID = 1L;

		int v;
		boolean removed;
		float fn;
		//VertexType type;
		ArrayList<Arc> prev = new ArrayList<Arc>();
		ArrayList<Arc> next = new ArrayList<Arc>();
		
		public static class ComparatorValueAscending implements Comparator<Object> {
			
			public int compare(Object o1, Object o2) {
				if( o1 instanceof Node && o2 instanceof Node ){
					if( ((Node)o1).getValue() > ((Node)o2).getValue() ) return  1;
					if( ((Node)o1).getValue() < ((Node)o2).getValue() ) return -1;
					
				}
				return 0;
			}	
		}

		public float getValue() {
			// TODO Auto-generated method stub
			return fn;
		}
	}


