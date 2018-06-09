package usf.saav.common;

import java.util.HashMap;

public class HashDisjointSet<TYPE extends Object> {


	private HashMap<TYPE,TYPE> elements = new HashMap<TYPE,TYPE>( );

	/**
	 *  Construct a disjoint sets object.
	 **/
	public HashDisjointSet() { }

	public void set( TYPE elem ){
		elements.put(elem, elem);
	}
	
	
	/**
	 *  union() unites two disjoint sets into a single set.  A 
	 *  union-by-lower_id heuristic is used to choose the new root.
	 *
	 *  @param set1 a member of the first set.
	 *  @param set2 a member of the other set.
	 **/
	public void union( TYPE newRoot, TYPE oldRoot ){
		TYPE r0 = find( newRoot );
		TYPE r1 = find( oldRoot );
		if( r0 != r1 ) elements.put( r1, r0 );
	}


	/**
	 *  find() finds the name of the set containing a given element.
	 *  Performs path compression along the way.
	 *
	 *  @param x the element sought.
	 *  @return the set containing x.
	 **/
	public TYPE find(TYPE x) {
		if( !elements.containsKey(x) ) return x;
		if( elements.get(x) == x ) return x;
		TYPE ret = find( elements.get(x) );
		elements.put(x, ret);
		return ret;
	}




	/**
	 *  main() is test code.  All the find()s on the same output line should be
	 *  identical.
	 **/
	public static void main(String[] args) {
		int NumElements = 128;
		int NumInSameSet = 16;

		HashDisjointSet<Integer> s = new HashDisjointSet<Integer>();

		for (int k = 1; k < NumInSameSet; k *= 2) {
			for (int j = 0; j + k < NumElements; j += 2 * k) {
				s.union( j , j+k );
			}
		}

		for (int i = 0; i < NumElements; i++) {
			System.out.print(s.find(i) + "*");
			if (i % NumInSameSet == NumInSameSet - 1) {
				System.out.println();
			}
		}
		System.out.println();

	}

}
