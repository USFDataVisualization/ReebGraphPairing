package usf.saav.topology.merge;

import java.util.Comparator;

import usf.saav.topology.TopoGraph;


public class AugmentedMergeTree extends MergeTree {

	protected MergeTree jt;
	
	public AugmentedMergeTree( MergeTree _jt ) {
		super(_jt.sf,_jt.comparator);
		this.jt = _jt;
	}
	
	public AugmentedMergeTree( TopoGraph<? extends TopoGraph.Vertex> sf ) {
		super(sf);
		jt = new MergeTree(sf,this.comparator);
	}
	
	public AugmentedMergeTree( TopoGraph<? extends TopoGraph.Vertex> sf, Comparator<? super JNode> comparator  ) {
		super(sf,comparator);
		jt = new MergeTree(sf,comparator);
	}
	
	
	@Override
	public void run() {
		
		if( operationComplete ) return;

		jt.run();

		head = processTree( jt.head );
		setParents( );
		calculatePersistence();
		
		operationComplete = true;
		
	}
	
	protected AugmentedMergeTreeNode processTree( MergeTreeNode current ){
		
		AugmentedMergeTreeNode ret= null;
		while( current.childCount() == 1 ){
			current = current.getChild(0);
		}
		if( current.childCount() == 0 ){
			ret = new AugmentedMergeTreeNode( current.getID(), current.getValue() );
		}
		if( current.childCount() == 2 ){
			ret = new AugmentedMergeTreeNode( current.getID(), current.getValue(),
							processTree( current.getChild(0) ),
							processTree( current.getChild(1) )  );
		}

		if( ret != null )
			grid.add( ret );
		
		return ret;

	}
	
	

	protected class AugmentedMergeTreeNode extends MergeTreeNode {
		
		private int   location;
		private float value;
		
		
		protected AugmentedMergeTreeNode( int loc, float val ){
			this.location = loc;
			this.value = val;
		}
		
		protected AugmentedMergeTreeNode( int loc, float val, AugmentedMergeTreeNode c0, AugmentedMergeTreeNode c1 ){
			this.location = loc;
			this.value = val;
			this.addChild(c0);
			this.addChild(c1);
		}
		
		
		@Override public int	getID() { return location; }
		@Override public float	getValue() { 	return value;	 }


	}
	
	
	
	
}
