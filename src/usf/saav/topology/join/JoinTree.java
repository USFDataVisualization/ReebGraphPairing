/*
 *     jPSimp - Persistence calculation and simplification of scalar fields.
 *     Copyright (C) 2016 PAUL ROSEN
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *     You may contact the Paul Rosen at <prosen@usf.edu>.
 */
package usf.saav.topology.join;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

import usf.saav.common.algorithm.BinaryMask1D;
import usf.saav.common.algorithm.DisjointSet1D;
import usf.saav.topology.TopoGraph;



public class JoinTree implements Runnable {
 
	private   Comparator<? super Node> comparator;
	private   TopoGraph sf;
	private   int size;
	private   Node head;
	protected boolean operationComplete = false;

	public JoinTree( TopoGraph sf ) {
		this.sf = sf;
		this.comparator = new Node.ComparatorValueAscending();
	}
	
	protected JoinTree( TopoGraph sf, Comparator<? super Node> comparator  ) {
		this.sf = sf;
		this.comparator = comparator;
	}

	
	public Node getRoot( ){
		if( !operationComplete ) return null;
		return head;
	}
		
	public String toString( ){
		if( head == null ){ return "<empty>"; }
		return head.toString();
	}


	@Override
	public void run() {
		
		if( operationComplete ) return;

		this.size = sf.size();
		Node [] grid;
		grid = new Node[size];

		// We first order the points for adding to the tree.
		Queue< Node > tq = new PriorityQueue< Node >( size, comparator );
		for(int i = 0; i < sf.size(); i++ ){
			tq.add( new Node( sf.get(i).value(), i ) );
		}
		
		// Disjoint Set used to mark which set a points belongs to
		DisjointSet1D dj = new DisjointSet1D( sf.size() );
		
		// Mask for marking who has been processed
		BinaryMask1D bm = new BinaryMask1D( sf.size(), false );
		
		// start popping elements off the of the list
		while( tq.size() > 0 ){
			head = tq.poll();
			init_mergeWithNeighbors( grid, head, sf, bm, dj );
			bm.set(head.getID());
		}
		
		operationComplete = true;
		
	}
	

	private void init_mergeWithNeighbors( Node [] grid, Node me, TopoGraph sf, BinaryMask1D bm, DisjointSet1D dj ) {
		
		int [] neighbors = sf.get( me.getID() ).neighbors();
		
		// set any neighbor sets as children
		for( int n : neighbors ){
			if( bm.isSet( n ) ){
				int setIdx = dj.find( n );
				if( !me.isChild( grid[setIdx] ) ){
					me.addChild( grid[setIdx] );
					grid[setIdx].setParent(me);
				}
			}
		}

		// update the disjoint set with new connection
		for( int n : neighbors ){
			if( bm.isSet( n ) ){
				dj.union( me.getID(), n );
			}
		}
		
		// update the root for the set
		grid[ dj.find( me.getID() ) ] = me;

	}
	

	
	public class Node extends JoinTreeNode {

		private int   position;
		private float value;

		
		public Node( float value, int position ) {
			this.position = position;
			this.value 	  = value;
		}


		@Override public float getValue( ){ return value; }
		@Override public int   getID( ){ return position; }

		@Override public NodeType getType() { return NodeType.UNKNOWN; }
		
	}

	
}
