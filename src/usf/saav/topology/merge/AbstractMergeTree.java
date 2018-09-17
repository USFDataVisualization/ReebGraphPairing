package usf.saav.topology.merge;

import java.util.ArrayList;

import usf.saav.topology.TopoTree;

public abstract class AbstractMergeTree implements TopoTree<AbstractMergeTreeNode> {

	
	protected AbstractMergeTreeNode head = null;
	protected ArrayList<AbstractMergeTreeNode> grid = new ArrayList<AbstractMergeTreeNode>();
	

	public AbstractMergeTreeNode getRoot( ){
		return head;
	}
		
	public String toString( ){
		if( head == null ){ return "<empty>"; }
		return head.toString();
	}
	
	public int size() {
		return grid.size();
	}

	public float getBirth(int i) {
		return grid.get(i).getBirth();
	}

	public float getDeath(int i) {
		return grid.get(i).getDeath();
	}

	public float getPersistence(int i) {
		return grid.get(i).getPersistence();
	}

	public AbstractMergeTreeNode getNode(int i) {
		return grid.get(i);
	}	

	public ArrayList<AbstractMergeTreeNode> getAll() {
		return grid;
	}	
	

	
	protected void setParents( ) {
		for( AbstractMergeTreeNode curr : grid ) {
			for( AbstractMergeTreeNode child : curr.getChildren() ) {
				child.setParent( curr );
			}
		}		
	}
	
	
	/*

	public String toDot( int maxdepth ){
		if( head == null ){ return "Digraph{\n}"; }
		else {
			StringBuffer dot_node = new StringBuffer( );
			StringBuffer dot_edge = new StringBuffer( );
			head.toDot( dot_node, dot_edge, maxdepth );
			return "Digraph{\n" + dot_node + dot_edge + "}"; 
		}
	}*/

	

	
	
	@Override
	public void setPersistentSimplification(float threshold) {
		// TODO Auto-generated method stub
		// FIX LATER
	}

	@Override
	public float getPersistentSimplification() {
		// TODO Auto-generated method stub
		// FIX LATER
		return 0;
	}

	@Override
	public boolean isActive(int i) {
		// TODO Auto-generated method stub
		// FIX LATER
		return true;
	}

	@Override
	public float getMaxPersistence() {
		// TODO Auto-generated method stub
		// FIX LATER
		return 0;
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
	
	
	
	

	public class JNode extends AbstractMergeTreeNode {

		private int   position;
		private float value;

		public JNode( float value, int position, Object creator ) {
			super(creator);
			this.position = position;
			this.value 	  = value;
		}

		@Override public float getValue( ){ return value; }
		@Override public int   getID( ){ return position; }


	}



}
