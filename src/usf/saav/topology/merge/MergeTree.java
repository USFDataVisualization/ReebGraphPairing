package usf.saav.topology.merge;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Vector;

import usf.saav.common.HashDisjointSet;
import usf.saav.topology.TopoGraph;
import usf.saav.topology.TopoGraph.Vertex;

public class MergeTree extends AbstractMergeTree implements  Runnable {
 
	protected Comparator<? super JNode> comparator;
	protected TopoGraph<? extends TopoGraph.Vertex> sf;
	protected boolean operationComplete = false;

	protected MergeTree( ) { }

	
	public MergeTree( TopoGraph<? extends TopoGraph.Vertex> sf ) {
		this( sf, new JNode.ComparatorValueAscending() );
	}
	
	public MergeTree( TopoGraph<? extends TopoGraph.Vertex> sf, Comparator<? super JNode> comparator  ) {
		this.sf = sf;
		this.comparator = comparator;
	}

	


	@Override
	public void run() {
		
		if( operationComplete ) return;

		int size = sf.size();
		//this.size = sf.getWidth();
		//grid = new Node[size];

		// Order the points for adding to the tree.
		Queue< JNode > tq = new PriorityQueue< JNode >( size, comparator );
		for(int i = 0; i < size; i++ ){
			grid.add( new JNode( sf.get(i).value(), i, sf.get(i) ) );
			tq.add( (JNode)grid.get(i) );
		}

		// Disjoint Set used to mark which set a points belongs to
		HashDisjointSet<JNode> djs = new HashDisjointSet<JNode>( );
		
		// start popping elements off the of the list
		while( tq.size() > 0 ){
			JNode me = tq.poll();

			// set any neighbor sets as children
			for( Vertex _n : sf.get( me.getID() ).neighbors() ){
				JNode n = (JNode)grid.get(_n.getID());
				if( comparator.compare( n, me ) < 0 ) {
					//Node r0 = djs.find( me );
					JNode r1 = djs.find( n );
					if( me != r1 ) {
						djs.union( me, r1 );
						me.addChild( r1 );
					}
					
				}
			}
			head = me;
		}
		
		//System.out.println();
		
		correctMonkeySaddles();
		setParents( );
		//calculatePersistence();
		
		operationComplete = true;
		
	}
	
	
	protected void correctMonkeySaddles( ) {
		Queue<AbstractMergeTreeNode> proc = new LinkedList<AbstractMergeTreeNode>();
		
		proc.add(head);
		while( !proc.isEmpty() ) {
			AbstractMergeTreeNode curr = proc.poll();
			if( curr.childCount() > 2 ) {
				//System.out.println("Monkey Saddle " + curr.getPosition());
				//System.out.println( curr );
				Vector<AbstractMergeTreeNode> oldChildren = curr.children;
				curr.children = new Vector<AbstractMergeTreeNode>();
				
				JNode newNode = new JNode( curr.getValue(), curr.getID(), curr.creator );
				
				curr.children.add( oldChildren.get(0) );
				curr.children.add( newNode );
				
				for(int i = 1; i < oldChildren.size(); i++ ) {
					newNode.addChild( oldChildren.get(i) );
				}
				
				/*
				while( curr.childCount() > 1 ) {
					AbstractMergeTreeNode n = curr.getChild(1);
					curr.removeChild( n );
					newNode.addChild( n );
				}
				curr.addChild(newNode);
				*/
				grid.add(newNode);
				//System.out.println( curr );
			}
			proc.addAll(curr.getChildren());
		}		
	}




	/*
	private MergeTreeNode getNextCritical( MergeTreeNode curr ) {
		while( curr.childCount() == 1 ) { 
			curr = curr.getChild(0); 
		}
		return curr;
	}
	*/

	

	
	
}
