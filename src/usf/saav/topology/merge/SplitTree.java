package usf.saav.topology.merge;

import usf.saav.topology.TopoGraph;

public class SplitTree extends MergeTree {
	public SplitTree( TopoGraph<? extends TopoGraph.Vertex> sf ){
		super(sf, new MergeTreeNode.ComparatorValueDescending() );
	}
	
	public SplitTree( TopoGraph<? extends TopoGraph.Vertex> sf, boolean run ){
		super(sf, new MergeTreeNode.ComparatorValueDescending() );
		if( run ) this.run();
	}	
}
