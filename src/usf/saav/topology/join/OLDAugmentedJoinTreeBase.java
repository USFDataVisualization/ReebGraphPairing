package usf.saav.topology.join;

import java.util.Stack;
import java.util.Vector;

import usf.saav.topology.TopoTree;
import usf.saav.topology.TopoTreeNode;
import usf.saav.topology.merge.MergeTreeNode;


@Deprecated
public class OLDAugmentedJoinTreeBase implements TopoTree {

	public	  MergeTreeNode		   head = null;

	protected Vector<MergeTreeNode> nodes = new Vector<MergeTreeNode>();
	protected float				   simplify = 0.0f;
	protected float				   max_persistence = 0.0f;
	
	
	protected OLDAugmentedJoinTreeBase( ){ }
	

	protected void calculatePersistence(){
		
		Stack<MergeTreeNode> pstack = new Stack<MergeTreeNode>( );
		pstack.push( this.head );
		
		while( !pstack.isEmpty() ){
			MergeTreeNode curr = pstack.pop();
			
			// leaf is only thing in the stack, done
			if( pstack.isEmpty() && curr.childCount() == 0 ) break;
			
			// saddle point, push children onto stack
			if( curr.childCount() == 2 ){
				pstack.push(curr);
				pstack.push((MergeTreeNode)curr.getChild(0));
				pstack.push((MergeTreeNode)curr.getChild(1));
			}
			
			// leaf node, 2 options
			if( curr.childCount() == 0 ) {
				MergeTreeNode sibling = pstack.pop();
				MergeTreeNode parent  = pstack.pop();
				
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
					max_persistence = Math.max(max_persistence,parent.getPersistence());
				}
				
			}
		}
	
		
	}
	
	public String toString( ){
		if( head == null ){ return "<empty>"; }
		return head.toString();
	}
	
	public String toDot( ){
		if( head == null ){ return "Digraph{\n}"; }
		else {
			StringBuffer dot_node = new StringBuffer( );
			StringBuffer dot_edge = new StringBuffer( );
			head.toDot( dot_node, dot_edge );
			return "Digraph{\n" + dot_node + dot_edge + "}"; 
		}
	}

	public String toDot( int maxdepth ){
		if( head == null ){ return "Digraph{\n}"; }
		else {
			StringBuffer dot_node = new StringBuffer( );
			StringBuffer dot_edge = new StringBuffer( );
			head.toDot( dot_node, dot_edge, maxdepth );
			return "Digraph{\n" + dot_node + dot_edge + "}"; 
		}
	}

	

	
	@Override public float getMaxPersistence(){ return max_persistence; }
	
	@Override 
	public void setPersistentSimplification( float threshold ){
		simplify = threshold;
	}

	@Override
	public float getPersistentSimplification( ){
		return simplify;
	}

	@Override
	public boolean isActive(int i){
		return getPersistence(i) >= simplify;
	}

	@Override
	public int size() {
		return nodes.size();
	}

	@Override
	public float getBirth(int i) {
		return nodes.get(i).getBirth();
	}

	@Override
	public float getDeath(int i) {
		return nodes.get(i).getDeath();
	}

	@Override
	public float getPersistence(int i) {
		return nodes.get(i).getPersistence();
	}

	public TopoTreeNode getNode(int i) {
		return nodes.get(i);
	}
}

