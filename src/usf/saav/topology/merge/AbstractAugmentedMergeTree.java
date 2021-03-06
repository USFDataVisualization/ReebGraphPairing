package usf.saav.topology.merge;

import java.util.Stack;

public abstract class AbstractAugmentedMergeTree extends AbstractMergeTree {

	protected AbstractAugmentedMergeTree( ) { }

	
	protected void calculatePersistence(){
		//print_info_message( "Finding Persistence");
		
		Stack<AbstractMergeTreeNode> pstack = new Stack<AbstractMergeTreeNode>( );
		//pstack.push( getNextCritical(this.head) );
		pstack.push( this.head );
		
		while( !pstack.isEmpty() ){
			AbstractMergeTreeNode curr = pstack.pop();
			//System.out.println(curr.getID() + " " + curr.getChildCount());
			
			// leaf is only thing in the stack, done
			if( pstack.isEmpty() && curr.childCount() == 0 ) break;			
			
			// 1 child, just push onto stack
			if( curr.childCount() == 1 ) {
				pstack.push( curr.getChild(0) );
			}			
			
			// saddle point, push children onto stack
			if( curr.childCount() == 2 ){
				pstack.push(curr);
				//pstack.push( getNextCritical( curr.getChild(0) ) );
				//pstack.push( getNextCritical( curr.getChild(1) ) );
				pstack.push( curr.getChild(0) );
				pstack.push( curr.getChild(1) );
			}

			// leaf node, 2 options
			if( curr.childCount() == 0 && pstack.size() >= 2 ) {
				AbstractMergeTreeNode sibling = pstack.pop();
				AbstractMergeTreeNode parent  = pstack.pop();
				
				// sibling is a saddle, restack.
				if( sibling.childCount() == 2 ){
					pstack.push( parent );
					pstack.push( curr );
					pstack.push( sibling );
				}
				
				// sibling is a leaf, we can match a partner.
				if( sibling.childCount() == 0 ){
					// curr value is closer to parent than sibling
					if( Math.abs(curr.getValue()-parent.getValue()) < Math.abs(sibling.getValue()-parent.getValue()) ){
						curr.setPartner(parent);
						parent.setPartner(curr);
						pstack.push( sibling );
					}
					// sibling value is closer to parent than curr
					else {
						sibling.setPartner(parent);
						parent.setPartner(sibling);
						pstack.push( curr );
					}
					//max_persistence = Math.max(max_persistence,parent.getPersistence());
				}
			}
		}
	
		
	}
		
	
	
	protected class AugmentedMergeTreeNode extends AbstractMergeTreeNode {
		
		private int   location;
		private float value;
		
		
		protected AugmentedMergeTreeNode( int loc, float val, AbstractMergeTreeNode creator ){
			super(creator);
			this.location = loc;
			this.value = val;
		}
		
		protected AugmentedMergeTreeNode( int loc, float val, AbstractMergeTreeNode creator, AugmentedMergeTreeNode c0, AugmentedMergeTreeNode c1 ){
			super(creator);
			this.location = loc;
			this.value = val;
			this.addChild(c0);
			this.addChild(c1);
		}
		
		
		@Override public int	getID() { return location; }
		@Override public float	getValue() { 	return value;	 }

	}
	
}
