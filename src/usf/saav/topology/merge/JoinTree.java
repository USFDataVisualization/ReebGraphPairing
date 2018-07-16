package usf.saav.topology.merge;

import usf.saav.topology.TopoGraph;

public class JoinTree extends MergeTree {

	public JoinTree( TopoGraph<? extends TopoGraph.Vertex> sf ){
		super( sf, new MergeTreeNode.ComparatorValueAscending() );
	}

	public JoinTree( TopoGraph<? extends TopoGraph.Vertex> sf, boolean run ){
		super( sf, new MergeTreeNode.ComparatorValueAscending() );
		if( run ) this.run();
	}


}
