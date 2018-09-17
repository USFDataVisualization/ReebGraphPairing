package usf.saav.topology.merge;

import usf.saav.topology.TopoGraph;

public class JoinTree extends MergeTree {

	public JoinTree( TopoGraph<? extends TopoGraph.Vertex> sf ){
		super( sf, new AbstractMergeTreeNode.ComparatorValueAscending() );
	}

	public JoinTree( TopoGraph<? extends TopoGraph.Vertex> sf, boolean run ){
		super( sf, new AbstractMergeTreeNode.ComparatorValueAscending() );
		if( run ) this.run();
	}


}
